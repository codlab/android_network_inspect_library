package eu.codlab.network.inspect.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import eu.codlab.network.inspect.library.bdd.Data;
import eu.codlab.network.inspect.library.bdd.DataUpDown;
import eu.codlab.network.inspect.library.bdd.Interface;
import eu.codlab.network.inspect.library.bdd.InterfacesManager;

public class RealTimeLayout extends LinearLayout{
	private InterfacesManager _manager;

	private ArrayList<TimeSeries> timeSeries;
	private XYMultipleSeriesDataset dataset;
	private XYMultipleSeriesRenderer renderer;
	private ArrayList<XYSeriesRenderer> rendererSeries;
	private GraphicalView view;
	private ArrayList<Thread> mThread;
	private final int colors[]=new int[]{
			Color.BLUE,
			Color.RED,
			Color.CYAN,
			Color.GREEN,
			Color.GRAY,
			Color.MAGENTA,
			Color.YELLOW,
			Color.BLACK,
			Color.DKGRAY,
			Color.LTGRAY};
	private Context _context;
	private Activity _parent;

	private boolean _draw;

	private void init(Context context){
		_manager = new InterfacesManager(context);

		mThread = new ArrayList<Thread>();
		_draw = true;
		_context = context;
		timeSeries = new ArrayList<TimeSeries>();
		rendererSeries = new ArrayList<XYSeriesRenderer>();

		dataset = new XYMultipleSeriesDataset();

		renderer = new XYMultipleSeriesRenderer();
		renderer.setAxesColor(Color.DKGRAY);
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitle("Time");
		renderer.setChartTitleTextSize(15);
		renderer.setFitLegend(true);
		renderer.setGridColor(Color.LTGRAY);
		renderer.setPanEnabled(true, true);
		//renderer.setPanLimits(new double[]{0, Double.MAX_VALUE, -1, Double.MAX_VALUE});
		renderer.setPointSize(0);
		renderer.setYAxisMin(0);
		renderer.setLabelsTextSize(18);
		//renderer.setClickEnabled(true);
		renderer.setXTitle("Temps");
		renderer.setYTitle("Nombre");
		//renderer.setMargins( new int []{20, 30, 15, 0});
		renderer.setZoomButtonsVisible(true);
		renderer.setBarSpacing(10);
		renderer.setShowGrid(true);



		view = ChartFactory.getTimeChartView(_context, dataset, renderer,"HH"+":mm MM/dd/yyyy");
		view.refreshDrawableState();
		view.repaint();
		addView(view); 

		Thread t = new Thread(){
			@Override
			public void run(){
				while(_draw){
					try{
						Thread.sleep(1000L);
					}catch(Exception e){

					}

					if(dataset.getSeriesCount()>0){
						view.repaint();
					}
				}
			}
		};
		t.start();
	}
	public RealTimeLayout(Context context) {
		super(context);
		init(context);
	}
	public RealTimeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@Override
	public void onDetachedFromWindow(){
		_draw = false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
		return true;
	}

	private class AbstractUpdateThread extends Thread{
		private TimeSeries _serie_up=null;
		private TimeSeries _serie_down=null;
		private final Random random = new Random();
		private double _last_value;
		private final Handler _handler = new Handler();
		private long _last_date=new Date().getTime();
		private long _last_time;
		private boolean _loading = false;
		private Interface _interface;

		AbstractUpdateThread(TimeSeries serie_up, TimeSeries serie_down, Interface interface_){
			_serie_up = serie_up;
			_serie_down = serie_down;
			_last_time = 0;
			_last_value = 0;
			_interface = interface_;
		}

		public double getLastValue(){
			return _last_value;
		}

		public void setDownload(boolean state){
			_loading = state;
		}

		public void postDelayedDownload(){
			_handler.postDelayed(_run, 5000);
		}

		public void setLastTime(long time){
			this._last_time = time;
		}

		public long getLastTime(){
			return _last_time;
		}

		public void addPoint(long t, double up, double down){
			//if(t>_last_date)
			_serie_up.add(t, up);
			_serie_down.add(t, down);
			//_last_value = v;
			if(t>_last_date)
				_last_date=t;
		}
		private final Runnable _run = new Runnable(){
			@Override
			public void run(){
				Download _dl= new Download(AbstractUpdateThread.this,
						_interface);
				_dl.execute();
			}
		};

		@Override
		public void run(){
			_handler.post(_run);
			/*while(_draw){
				try{
					Thread.sleep(1000L);
				}catch(Exception e){

				}
				_last_date+=1000;
				if(!_loading)
					_serie.add(_last_date, _last_value);
			}*/
		}
	}



	private class InterfaceThread extends AbstractUpdateThread{
		InterfaceThread(TimeSeries serie_up, TimeSeries serie_down, Interface interface_){
			super(serie_up, serie_down, interface_);
		}
	}

	public void addThread(TimeSeries serie_up, TimeSeries serie_down, Interface interface_){
		InterfaceThread t = new InterfaceThread(serie_up, serie_down, interface_);
		mThread.add(t);
		t.start();
	}

	public void add(String name, Interface interface_){
		TimeSeries new_series_up = new TimeSeries(name+" up");
		TimeSeries new_series_down = new TimeSeries(name+" down");
		XYSeriesRenderer new_renderer_up = new XYSeriesRenderer();
		XYSeriesRenderer new_renderer_down = new XYSeriesRenderer();
		//rendererSeries.size() >> avant l'ajout et 0 based donc si modif v&eacute;rifier
		new_renderer_up.setColor(colors[ rendererSeries.size() % colors.length]);
		new_renderer_up.setFillPoints(false);
		new_renderer_up.setPointStyle(PointStyle.POINT);
		new_renderer_up.setLineWidth(2);

		new_renderer_down.setColor(colors[ (rendererSeries.size()+1) % colors.length]);
		new_renderer_down.setFillPoints(false);
		new_renderer_down.setPointStyle(PointStyle.POINT);
		new_renderer_down.setLineWidth(2);
		addThread(new_series_up, new_series_down, interface_);
		
		rendererSeries.add(new_renderer_up);
		rendererSeries.add(new_renderer_down);
		renderer.addSeriesRenderer(new_renderer_up);
		renderer.addSeriesRenderer(new_renderer_down);
		dataset.addSeries(new_series_up);
		dataset.addSeries(new_series_down);
	}

	private class Download extends AsyncTask{
		private Interface _interface;
		private AbstractUpdateThread _parent;

		private Download(){

		}

		public Download(AbstractUpdateThread parent, Interface inter){
			_interface=inter;
			_parent = parent;
		}
		@Override
		protected Object doInBackground(Object... params) {
			try{
				DataUpDown res = null;
				if(_parent.getLastTime() > 0)
					res = _manager.getData(_interface, _parent.getLastTime());
				else
					res = _manager.getData(_interface);

				_parent.setDownload(true);
				for(int i=0;i<res._timestamps.length;i++){
					_parent.addPoint(res._timestamps[i], res._up[i], res._down[i]);
				}
				_parent.setDownload(false);
			}catch(Exception e){
				e.printStackTrace();
			}
			_parent.postDelayedDownload();
			return null;
		}

	}

}
