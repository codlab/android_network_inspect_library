package eu.codlab.network.inspect.app;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import eu.codlab.network.inspect.library.bdd.Interface;
import eu.codlab.network.inspect.library.bdd.InterfacesManager;

public class GraphFragment extends SherlockFragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.activity_graph, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		final RealTimeLayout real = (RealTimeLayout)view.findViewById(R.id.graph);
        real.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                real.onClick();
            }
        });
        InterfacesManager manager = new InterfacesManager(getActivity());
		ArrayList<Interface> _interfaces = manager.getInterfaces();
		for(Interface _if : _interfaces){
			real.add(_if.name, _if);
		}
	}
}
