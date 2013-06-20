package eu.codlab.network.inspect.app.library;

import android.widget.TextView;

import eu.codlab.network.inspect.library.kernel.RmnetStatisticsInfo;

public class Batterie {
	public String name;
	public TextView view_name;
	public TextView view_data;
	public boolean added;
	public boolean viewed;
	public double percent;

	public boolean just_changed;

	@Override
	public String toString(){
		return ""+name+" "+percent;
	}
	
	@Override
	public boolean equals(Object object){
		if(object instanceof Batterie)
			return equals((Batterie)object);
		return false;
	}
	public boolean equals(Batterie info){
		if(info.name == null && name == null)
			return true;
		if(name == null || info.name == null)
			return false;
		
		if(! (name.equals(info.name)))
			return false;
		return true;
	}
}