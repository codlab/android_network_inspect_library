package eu.codlab.network.inspect.app;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.codlab.network.inspect.library.kernel.NetCfg;
import eu.codlab.network.inspect.library.kernel.RmnetStatisticsInfo;
import eu.codlab.network.inspect.library.kernel.WifiStatisticsInfo;

public class InspectService extends Service {
	private NetCfg conf;

	private WindowManager.LayoutParams _params = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE,
			WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
			WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
			WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
			PixelFormat.TRANSLUCENT);
	private WindowManager windowManager;
	private LinearLayout _top_view;//master of all da views \o/
	private TextView _wifi;
	private TextView _3g;
	private WifiStatisticsInfo _info;
	private Handler _scanner;
	private ArrayList<InterfaceInfo> _interfaces;



	private void addView(View view){
		_top_view.addView(view);
	}

	private void removeView(View view){
		_top_view.removeView(view);
	}
	private class InterfaceInfo{
		public String name;
		public TextView view;
		public RmnetStatisticsInfo if_scan;
		public boolean added;
		public boolean viewed;

		public boolean equals(InterfaceInfo info){
			return equals(info.name);
		}

		public boolean equals(String n){
			if(this.name == null && n == null)
				return true;
			return (this.name.equals(n));
		}
	}

	private class NewThread extends Thread{
		public void run(){
			try{
				Thread.sleep(10000);
			}catch(Exception e){};

			if(_scanner != null)
				_scanner.post(send_new_scan);
		}
	}

	private int _delta_scan;
	private Runnable send_new_scan = new Runnable(){
		public void run(){
			manageGetInfos();


			//TODO implement broadcast receiver for wifi on/off
			if(_delta_scan == 2){
				ArrayList<String> _if_list = conf.getNetCfgInterfacesUp();
				manageList(_if_list);
				_delta_scan = 0;
			}else{
				_delta_scan++;
			}

			Thread t = new NewThread();
			t.start();
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	private String getNormalized(long value){
		double d;
		if(value >= 1048576){
			d=(value/1048576)*10;
			return (d*1./10)+"Mio";
		}else if(value > 1024){
			d=(value/1024)*10;
			return (d*1./10)+"Kio";
		}
		return value+"o";
	}
	
	private void manageGetInfos(){
		synchronized(this){
			String val="";
			for(InterfaceInfo _if : _interfaces){
				if(_if.added == true){
					Log.d("RX",""+_if.if_scan.getRXBytes());
					val=_if.name+" U/D: "+getNormalized(_if.if_scan.getTXBytes())+"/"+getNormalized(_if.if_scan.getRXBytes());
					_if.view.setText(val);
				}
			}
		}
	}

	private void manageClean(){
		synchronized(this){
			for(InterfaceInfo _if : _interfaces){
				if(_if.added == true){
					removeView(_if.view);
				}
			}
		}
	}

	private InterfaceInfo get(String name){
		for(InterfaceInfo _if : _interfaces){
			if(_if.equals(name)){
				return _if;
			}
		}
		return null;
	}
	private boolean contains(String name){
		boolean b = false;
		for(InterfaceInfo _if : _interfaces){
			if(_if.equals(name)){
				b = true;
			}
		}
		return b;
	}
	/*
	 * scenarios
	 * interface > presente dans vue
	 *           > non presente
	 */
	private void manageList(ArrayList<String> infos){
		synchronized(this){
			if(infos.size() > 0){

				/*
				 * check for each up interface if it is here
				 */
				for(String name : infos){
					if(name.indexOf("rmnet") >= 0 || name.indexOf("wlan") >=0){
						if(contains(name)){
							//if(_interfaces.contains(name)){
							InterfaceInfo _interface = get(name);
							_interface.viewed = true;
							_interface.name = name;
							if(_interface.view == null)
								_interface.view = new TextView(this);
							if(_interface.if_scan == null)
								_interface.if_scan = new RmnetStatisticsInfo(_interface.name);
							if(_interface.added == false){
								_interface.added = true;
								addView(_interface.view);
							}
						}else{
							InterfaceInfo _interface = new InterfaceInfo();
							_interface.viewed = true;
							_interface.name = name;
							_interface.view = new TextView(this);
							_interface.if_scan = new RmnetStatisticsInfo(_interface.name);
							_interface.added = true;
							addView(_interface.view);
							_interfaces.add(_interface);
						}
					}
				}

				/*
				 * check for each "unappeared interface
				 */
				for(InterfaceInfo _if : _interfaces){
					if(_if.viewed == false){
						if(_if.added == true){
							removeView(_if.view);
							_if.added = false;
						}
					}else{
						_if.viewed = false;
					}
				}

				/*
				 * set viwed = false for next loop 
				 */

				//for(InterfaceInfo _if : _interfaces){
				//	_if.viewed=false;
				//}
			}
		}
	}

	@Override public void onCreate() {
		super.onCreate();

		_scanner = new Handler();

		_delta_scan = 0;

		_info = new WifiStatisticsInfo("wlan0");

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


		LayoutInflater inflater = (LayoutInflater)getSystemService(this.LAYOUT_INFLATER_SERVICE);
		_top_view = (LinearLayout)inflater.inflate(R.layout.linearlayout, null);
		_params.gravity = Gravity.TOP | Gravity.LEFT;
		_params.x = 0;
		_params.y = 100;
		windowManager.addView(_top_view, _params);

		conf = new NetCfg();
		_wifi = new TextView(this);
		_interfaces = new ArrayList<InterfaceInfo>();




		TextView view = null;

		ArrayList<String> _if_list = conf.getNetCfgInterfacesUp();
		manageList(_if_list);

		if(_scanner != null)
			_scanner.post(send_new_scan);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		manageClean();
		if (_top_view != null) windowManager.removeView(_top_view);
		_scanner.removeCallbacks(send_new_scan);
		_scanner = null;
	}

}
