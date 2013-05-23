package eu.codlab.network.inspect.app.library;

import android.widget.TextView;
import eu.codlab.network.inspect.library.kernel.RmnetStatisticsInfo;

public class InterfaceInfo{
	public String name;
	public String address;
	public String flag;
	public TextView view_name;
	public TextView view_data;
	public RmnetStatisticsInfo if_scan;
	public boolean added;
	public boolean viewed;
	
	public boolean just_changed;

	@Override
	public String toString(){
		return ""+name+" "+flag+" "+address;
	}
	
	@Override
	public boolean equals(Object object){
		if(object instanceof InterfaceInfo)
			return equals((InterfaceInfo)object);
		return false;
	}
	public boolean equals(InterfaceInfo info){
		if(info.name == null && name == null)
			return true;
		if(name == null || info.name == null)
			return false;
		
		if(! (name.equals(info.name)))
			return false;
		return true;
	}
}