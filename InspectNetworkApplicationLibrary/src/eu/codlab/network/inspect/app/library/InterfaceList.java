package eu.codlab.network.inspect.app.library;

import java.util.ArrayList;


public class InterfaceList<I extends InterfaceInfo> extends ArrayList{
	public boolean contains(I object){
		boolean b = false;
		for(InterfaceInfo _if : (ArrayList<InterfaceInfo>)this){
			if(_if.equals(object)){
				b = true;
			}
		}
		return b;
	}
	
	private boolean contains(String name){
		boolean b = false;
		for(InterfaceInfo _if : (ArrayList<InterfaceInfo>)this){
			if(_if.equals(name)){
				b = true;
			}
		}
		return b;
	}
}
