package eu.codlab.network.inspect.app.library;

import java.util.ArrayList;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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
import android.widget.RemoteViews;
import android.widget.TextView;
import eu.codlab.network.inspect.library.bdd.InterfacesManager;
import eu.codlab.network.inspect.library.kernel.NetCfg;
import eu.codlab.network.inspect.library.kernel.RmnetStatisticsInfo;
import eu.codlab.network.inspect.library.kernel.WifiStatisticsInfo;

public class InspectService extends Service {
	private InterfacesManager _manager;

	private InterfacesManager getManager(){
		if(_manager == null)
			_manager = new InterfacesManager(this);
		return _manager;
	}
	private final static String TAG = Service.class.toString();

	public final static int RUNNING = 1;
	public final static int STOPPED = 2;
	private static int _state = STOPPED;

	private NetCfg conf;

	private WindowManager.LayoutParams _params = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE,
			WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
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
	private int _delta_save;

	private Runnable send_new_scan = new Runnable(){
		public void run(){
			//TODO implement broadcast receiver for wifi on/off
			if(_delta_scan == 2){
				ArrayList<String> _if_list = conf.getNetCfgInterfacesUp();
				manageList(_if_list);
				_delta_scan = 0;
			}else{
				_delta_scan++;
			}

			//10 sec >> 6 = 1min ==> 60
			if(_delta_save > 60){
				manageGetInfos(true);
				_delta_save = 0;
			}else{
				manageGetInfos();
				_delta_save++;
			}

			Thread t = new NewThread();
			t.start();
		}
	};
	private Vibrate _vibrate;

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
		manageGetInfos(false);
	}

	private void manageGetInfos(boolean save){
		synchronized(this){
			String val="";
			for(InterfaceInfo _if : _interfaces){

				if(_if.just_changed || save == true){
					//need store
					getManager().addData(getManager().getInterface(_if.name), _if.if_scan.getTXBytes(), _if.if_scan.getRXBytes());
					_if.just_changed = false;
				}

				if(_if.added == true){
					val=_if.name+" U/D: "+getNormalized(_if.if_scan.getTXBytes())+"/"+getNormalized(_if.if_scan.getRXBytes());
					_if.view.setText(val);
				}
			}
		}

	}

	private void manageClean(){
		log("manageClean");
		synchronized(this){
			for(InterfaceInfo _if : _interfaces){
				if(_if.added == true){
					removeView(_if.view);
				}
			}
		}
	}

	private void log(String string) {
		Log.d(TAG,string);
	}

	private InterfaceInfo get(String name){
		for(InterfaceInfo _if : _interfaces){
			if(_if.equals(name)){
				return _if;
			}
		}
		return null;
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
					if(name.indexOf("lo") != 0 && name.indexOf("p2p") != 0){
						InterfaceInfo tmp = new InterfaceInfo();
						tmp.name = name;
						if(_interfaces.contains(tmp)){
							Log.d("InspectService","known "+tmp.name);
							//if(_interfaces.contains(name)){
							InterfaceInfo _interface = get(name);
							_interface.viewed = true;
							_interface.name = name;
							if(_interface.view == null){
								_interface.view = new TextView(this);
								_interface.view.setTextColor(0xffffffff);
							}
							if(_interface.if_scan == null)
								_interface.if_scan = new RmnetStatisticsInfo(_interface.name);
							if(_interface.added == false){
								Log.d("InspectService","interface added = false");
								_interface.added = true;
								addView(_interface.view);

								_interface.just_changed = true;
							}

							getManager().serviceUp(_interface);
						}else{
							Log.d("InspectService","unknown "+tmp.name);
							InterfaceInfo _interface = new InterfaceInfo();
							_interface.viewed = true;
							_interface.name = name;
							_interface.view = new TextView(this);
							_interface.view.setTextColor(0xffffffff);
							_interface.if_scan = new RmnetStatisticsInfo(_interface.name);
							_interface.added = true;
							addView(_interface.view);
							_interfaces.add(_interface);

							_interface.just_changed = true;

							getManager().serviceNew(_interface);
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

							_if.just_changed = true;

							getManager().serviceDown(_if);

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

	public static int getState(){
		return _state;
	}
	@Override public void onCreate() {
		super.onCreate();

		_delta_scan = 0;
		_delta_save = 0;
		_scanner = new Handler();

		_vibrate = new Vibrate(this);

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

		updateWidgets();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int e){
		if(intent != null && intent.hasExtra("state")){
			Log.d(TAG, "has state");
			boolean changed = false;
			int state = intent.getIntExtra("state", 0);
			Log.d(TAG, "state = "+state);
			if(state == 0){
				changed = (_state != STOPPED);
				_state = STOPPED;
			}else if(state == 1){
				changed = (_state != RUNNING);
				_state = RUNNING;
			}else if(state == 2){
				//start or stop
				changed = true;
				_state = (this.getState() == RUNNING) ? STOPPED : RUNNING;
			}

			if(changed){
				if(_state == STOPPED){
					manageClean();
					this.stopSelf();
				}else if(_state == RUNNING){
					if(_scanner != null)
						_scanner.post(send_new_scan);
				}
				_vibrate.shortVibration();
			}
			updateWidgets();
		}
		return START_STICKY;
	}

	private void updateWidgets(){
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());


		ComponentName widgetComponentName = new ComponentName(getApplicationContext(), ServiceWidgetProvider.class);
		int[] widgetIds = appWidgetManager.getAppWidgetIds(widgetComponentName);

		for (int widgetId : widgetIds) {
			RemoteViews remoteViews = new RemoteViews(this
					.getApplicationContext().getPackageName(),
					R.layout.widget_service);
			if(getState() == RUNNING){
				remoteViews.setTextViewText(R.widget.text,getString(R.string.stop));
			}else{
				remoteViews.setTextViewText(R.widget.text,getString(R.string.start));
			}
			appWidgetManager.updateAppWidget(widgetId, remoteViews);

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		_state = STOPPED;

		manageClean();
		updateWidgets();
		if (_top_view != null) windowManager.removeView(_top_view);
		_scanner.removeCallbacks(send_new_scan);
		_scanner = null;
	}

}
