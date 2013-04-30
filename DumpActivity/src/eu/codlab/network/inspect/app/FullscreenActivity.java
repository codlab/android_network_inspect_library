package eu.codlab.network.inspect.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import eu.codlab.network.inspect.library.kernel.NetCfg;
import eu.codlab.network.inspect.library.kernel.WifiStatisticsInfo;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//proof root ability<s
		//Intent i = new Intent(this, DumpService.class);
		//this.startService(i);
		
		
		NetCfg cfg = new NetCfg();
		Log.d("INFO",cfg.getNetCfgDumpUp());
		
		setContentView(R.layout.activity_fullscreen);

		final View start = findViewById(R.id.service_start);
		start.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(FullscreenActivity.this, InspectService.class);
				startService(i);
			}
			
		});
		final View stop = findViewById(R.id.service_stop);
		stop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(FullscreenActivity.this, InspectService.class);
				stopService(i);
			}
			
		});

	}

}
