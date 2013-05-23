package eu.codlab.network.inspect.library.bdd;

import android.util.Log;

public class Interface {
	public Long id;
	public String name;
	public boolean up;
	public String flag;
	public String address;
	
	@Override
	public String toString(){
		return ""+name+"@"+id;
	}


	public Interface(Long id, String name, boolean up, String flag, String address){
		this.id = id;
		this.name = name;
		this.up = up;
		this.flag = flag;
		this.address = address;

	}

	@Override
	public int hashCode(){
		int i = (int) (id+0);
		return i;
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof String)
			return equals((String)object);
		else if(object instanceof Long)
			return equals((Long)object);
		else if(object instanceof Interface)
			return equals((Interface)object);
		return false;
	}
	public boolean equals(Interface interface_){
		return equals(interface_.name);
	}

	public boolean equals(String name_param){
		return name.equals(name_param);
	}

	public boolean equals(Long id){
		return this.id == id;
	}

}
