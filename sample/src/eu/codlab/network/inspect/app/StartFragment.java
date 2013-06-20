package eu.codlab.network.inspect.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import eu.codlab.network.inspect.app.library.InspectService;
import eu.codlab.network.inspect.library.kernel.NetCfg;

public class StartFragment extends SherlockFragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.activity_fullscreen, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		final View start = view.findViewById(R.id.service_start);
		start.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(), InspectService.class);
                i.putExtra("state",1);
				getActivity().startService(i);
			}
			
		});
		final View stop = view.findViewById(R.id.service_stop);
		stop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(), InspectService.class);
                i.putExtra("state",0);
				getActivity().stopService(i);
			}
			
		});

	}
}
