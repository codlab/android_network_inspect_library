package eu.codlab.network.inspect.app.library;

import android.widget.TextView;
import eu.codlab.network.inspect.library.kernel.RmnetStatisticsInfo;

public class InterfaceInfo{
	public String name;
	public TextView view;
	public RmnetStatisticsInfo if_scan;
	public boolean added;
	public boolean viewed;
	
	public boolean just_changed;

	
	@Override
	public boolean equals(Object object){
		if(object instanceof InterfaceInfo)
			return equals((InterfaceInfo)object);
		return false;
	}
	public boolean equals(InterfaceInfo info){
		return equals(info.name);
	}

	public boolean equals(String n){
		if(this.name == null && n == null)
			return true;
		return (this.name.equals(n));
	}
}