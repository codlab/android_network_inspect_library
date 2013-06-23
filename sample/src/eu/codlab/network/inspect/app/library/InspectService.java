package eu.codlab.network.inspect.app.library;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import eu.codlab.network.inspect.app.R;
import eu.codlab.network.inspect.library.bdd.InterfacesManager;
import eu.codlab.network.inspect.library.kernel.DumpKernelVariableHelper;
import eu.codlab.network.inspect.library.kernel.NetCfg;
import eu.codlab.network.inspect.library.kernel.NetCfg.FastInfos;
import eu.codlab.network.inspect.library.kernel.RmnetStatisticsInfo;

public class InspectService extends Service {
    private static InspectService _this;

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
	private LinearLayout _top_view_name;
	private LinearLayout _top_view_data;
	private Handler _scanner;
	private ArrayList<InterfaceInfo> _interfaces;
    public ArrayList<InterfaceInfo> getCurrentInterfaces(){
        if(_interfaces == null)_interfaces = new ArrayList<InterfaceInfo>();
        return _interfaces;
    };

    public static InspectService getInstance(){
        return _this;
    }

    public int getInterfaceSize(){
        return getCurrentInterfaces().size() +1;
    }

    public String[] getInterfaceString(int i){
        if(i == 0 || i > _interfaces.size()){
            return new String[]{_batterie.name,_batterie.percent+"%"};
        }else{
            i--;
            InterfaceInfo _if = getCurrentInterfaces().get(i);
            String res = _if.name;
            String val=getNormalized(_if.if_scan.getTXBytes())+"/"+getNormalized(_if.if_scan.getRXBytes());
            return new String[]{res, val};
        }
    }

    private Batterie _batterie;

	private final static int REFRESH_INTERVAL_MS = 2500;

	private void addView(ViewGroup root, View view){
		root.addView(view);
	}

	private void removeView(ViewGroup root, View view){
		root.removeView(view);
	}

	private class NewThread extends Thread{
		public void run(){
			try{
				Thread.sleep(REFRESH_INTERVAL_MS);
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
				ArrayList<FastInfos> _if_list = conf.getNetCfgInterfacesUpPair();
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
			d=(value*10./1048576.);
			return (Math.round(d)*1./10)+"M";
		}else if(value > 1024){
			d=(value*10./1024);
			return (Math.round(d)*1./10)+"K";
		}
		return value+"";
	}

	private void manageGetInfos(){
		manageGetInfos(false);
	}

    private void manageGetInfosBatterie(boolean save){
        if(_batterie.just_changed || save == true){
            //need store
            getManager().addData(getManager().getInterface(_batterie.name), (long)_batterie.percent, 0);
            _batterie.just_changed = false;
        }

        if(_batterie.added == true){
            String name = _batterie.name+" ";
            String val=" "+_batterie.percent+"%";
            _batterie.view_name.setText(name);
            _batterie.view_data.setText(val);
        }
    }
	private void manageGetInfos(boolean save){
		synchronized(this){
			String val="";
			String name="";

            manageGetInfosBatterie(save);

			for(InterfaceInfo _if : _interfaces){

				if(_if.just_changed || save == true){
					//need store
					if(_if.flag != null){
						getManager().addData(getManager().getInterface(_if.name), _if.if_scan.getTXBytes(), _if.if_scan.getRXBytes());
					}else{
						getManager().addData(getManager().getInterface(_if.name), _if.if_scan.getTXBytes(), _if.if_scan.getRXBytes());
					}
					_if.just_changed = false;
				}

				if(_if.added == true){
					name = _if.name;
					val=" U/D "+getNormalized(_if.if_scan.getTXBytes())+"/"+getNormalized(_if.if_scan.getRXBytes());
					_if.view_name.setText(name);
					_if.view_data.setText(val);
				}
			}
		}

	}

	private void manageClean(){
		log("manageClean");
		synchronized(this){
            removeView(_top_view_name, _batterie.view_name);
            removeView(_top_view_name, _batterie.view_data);
			for(InterfaceInfo _if : _interfaces){
				if(_if.added == true){
					removeView(_top_view_name, _if.view_name);
					removeView(_top_view_data, _if.view_data);
				}
			}
		}
	}

	private void log(String string) {
		Log.d(TAG,string);
	}

    private void manageListBatterie(){
        if(_batterie == null){
            _batterie = new Batterie();
            _batterie.viewed = true;
            _batterie.name = "battery";
            _batterie.view_name = new TextView(this);
            _batterie.view_name.setTextColor(0xffffffff);
            _batterie.view_data = new TextView(this);
            _batterie.view_data.setTextColor(0xffffffff);
            _batterie.added = true;
            addView(_top_view_name, _batterie.view_name);
            addView(_top_view_data, _batterie.view_data);
            _batterie.view_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7);
            _batterie.view_data.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7);

            try{
                _batterie.percent = Double.parseDouble(getBatteryFromKernel());
            }catch(Exception e){}

            _batterie.just_changed = true;

            getManager().serviceNew(_batterie);
        }else{
            try{
            _batterie.percent = Double.parseDouble(DumpKernelVariableHelper.dump("/sys/class/power_supply/battery/capacity"));
            }catch(Exception e){}
        }
    }

    public static String getBatteryFromKernel(){
        return DumpKernelVariableHelper.dump("/sys/class/power_supply/battery/capacity");
    }

	/*
	 * scenarios
	 * interface > presente dans vue
	 *           > non presente
	 */
	private void manageList(ArrayList<FastInfos> _if_list){
		synchronized(this){

            manageListBatterie();
			if(_if_list.size() > 0){

				/*
				 * check for each up interface if it is here
				 */
				String address = "";
				for(FastInfos pair : _if_list){
					if(pair.up && pair.device.indexOf("lo") != 0 && pair.device.indexOf("p2p") != 0){
						InterfaceInfo tmp = new InterfaceInfo();
						tmp.name = pair.device;
						tmp.address = pair.address;
						tmp.flag = pair.flag;
						if(_interfaces.contains(tmp)){
							Log.d("InspectService","known "+tmp.name);
							//if(_interfaces.contains(name)){
							InterfaceInfo _interface = _interfaces.get(_interfaces.indexOf(tmp));
							_interface.viewed = true;

							_interface.name = tmp.name;
							_interface.address = tmp.address;
							_interface.flag = tmp.flag;

							if(_interface.view_name == null && _interface.view_data == null){
								_interface.view_name = new TextView(this);
								_interface.view_data = new TextView(this);
								_interface.view_name.setTextColor(0xffffffff);
								_interface.view_data.setTextColor(0xffffffff);
							}
							if(_interface.if_scan == null)
								_interface.if_scan = new RmnetStatisticsInfo(_interface.name);
							if(_interface.added == false){
								Log.d("InspectService","interface added = false");
								_interface.added = true;

								addView(_top_view_name, _interface.view_name);
								addView(_top_view_data, _interface.view_data);

								_interface.view_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7);
								_interface.view_data.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7);

								_interface.just_changed = true;
							}

							getManager().serviceUp(_interface);
						}else{
							InterfaceInfo _interface = new InterfaceInfo();
							_interface.viewed = true;
							_interface.name = tmp.name;
							_interface.address = tmp.address;
							_interface.flag = tmp.flag;
							_interface.view_name = new TextView(this);
							_interface.view_name.setTextColor(0xffffffff);
							_interface.view_data = new TextView(this);
							_interface.view_data.setTextColor(0xffffffff);
							_interface.if_scan = new RmnetStatisticsInfo(_interface.name);
							_interface.added = true;
							addView(_top_view_name, _interface.view_name);
							addView(_top_view_data, _interface.view_data);
							_interfaces.add(_interface);
							_interface.view_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7);
							_interface.view_data.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7);

							_interface.just_changed = true;

							getManager().serviceNew(_interface);
							Log.d("InspectService","unknown "+tmp.name+" "+tmp.address);
						}
					}
				}

				/*
				 * check for each "unappeared interface
				 */
				for(InterfaceInfo _if : _interfaces){
					if(_if.viewed == false){
						if(_if.added == true){
							Log.d("View?","removing ...");
							removeView(_top_view_data, _if.view_data);
							removeView(_top_view_name, _if.view_name);

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

        _this = this;

		_delta_scan = 0;
		_delta_save = 0;
		_scanner = new Handler();

		_vibrate = new Vibrate(this);


		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		LayoutInflater inflater = (LayoutInflater)getSystemService(this.LAYOUT_INFLATER_SERVICE);
		_top_view = (LinearLayout)inflater.inflate(R.layout.linearlayout, null);
		_params.gravity = Gravity.TOP | Gravity.LEFT;
		_params.x = 0;
		_params.y = 100;
		windowManager.addView(_top_view, _params);

		_top_view_name = (LinearLayout)_top_view.findViewById(R.id.service_names);
		_top_view_data = (LinearLayout)_top_view.findViewById(R.id.service_data);

		conf = new NetCfg();
		_interfaces = new ArrayList<InterfaceInfo>();




		TextView view = null;

		ArrayList<FastInfos> _if_list = conf.getNetCfgInterfacesUpPair();
		manageList(_if_list);

		if(_scanner != null)
			_scanner.post(send_new_scan);

        _state = RUNNING;
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
				changed = true;// useless here as we are already running since the create method (_state != RUNNING);
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
            Intent serviceIntent = new Intent(this, InspectService.class);
			if(getState() == RUNNING){
				remoteViews.setTextViewText(R.id.widget_text,getString(R.string.stop));
                serviceIntent.putExtra("state", 0);
			}else{
				remoteViews.setTextViewText(R.id.widget_text,getString(R.string.start));
                serviceIntent.putExtra("state", 1);
			}
            // Prepare intent to launch on widget click
            // Launch intent on widget click
            PendingIntent pendingIntent = PendingIntent.getService(this, 1, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_service, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.widget_text, pendingIntent);

			appWidgetManager.updateAppWidget(widgetId, remoteViews);

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		_state = STOPPED;
        _this = null;

		manageClean();
		updateWidgets();
		if (_top_view != null) windowManager.removeView(_top_view);
		_scanner.removeCallbacks(send_new_scan);
		_scanner = null;
	}

}
