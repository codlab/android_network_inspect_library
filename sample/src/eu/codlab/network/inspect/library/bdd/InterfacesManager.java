package eu.codlab.network.inspect.library.bdd;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import eu.codlab.network.inspect.app.library.Batterie;
import eu.codlab.network.inspect.app.library.InterfaceInfo;

public class InterfacesManager {
	private Context _context;
	private SGBD _sgbd;
	
	private ArrayList<Interface> _interfaces;
	
	public InterfacesManager(Context context){
		_context = context;
		_sgbd = new SGBD(context);
		_sgbd.open();
		
		_interfaces = new ArrayList<Interface>();
		
		Interface [] interfaces = _sgbd.getInterfaces();
		if(interfaces != null)
		for(int i = 0;i<interfaces.length;i++){
			Log.d("InterfacesManager","getInterfaces "+interfaces[i].name+" "+interfaces[i].id);
			_interfaces.add(interfaces[i]);
		}
	}
	
	public ArrayList<Interface> getInterfaces(){
		return _interfaces;
	}
	
	public DataUpDown getData(Interface interface_object, long time_max){
		return _sgbd.getInterfaceDataSuperior(interface_object.id, time_max);
	}
	public DataUpDown getData(Interface interface_object){
		return _sgbd.getInterfaceData(interface_object.id);
	}
	
	public Data getDataDown(Interface interface_object, long time_max){
		return _sgbd.getInterfaceDataDownSuperior(interface_object.id, time_max);
	}

	public Data getDataDown(Interface interface_object){
		return _sgbd.getInterfaceDataDown(interface_object.id);
	}
	
	public Data getDataUp(Interface interface_object, long time_max){
		return _sgbd.getInterfaceDataUpSuperior(interface_object.id, time_max);
	}

	public Data getDataUp(Interface interface_object){
		return _sgbd.getInterfaceDataUp(interface_object.id);
	}
	
	public boolean hasInterface(String name){
		Log.d("InterfacesManager","hasInterface "+name+" "+_interfaces.size());
		Interface _if = new Interface(new Long(0), name, false, "", "");
		
		for(int i=0;i<_interfaces.size();i++){
			Log.d("INFO", _interfaces.get(i).toString());
		}
		return _interfaces.contains(_if);
	}
	
	public Interface addInterface(String name, boolean up, String flag, String address){
		if(hasInterface(name)){
			return _interfaces.get(_interfaces.indexOf(name));
		}
		
		Interface inter = new Interface(_sgbd.addInterfaces(name, up, flag, address), name, up, flag, address);
		_interfaces.add(inter);
		return inter;
	}

	public Interface getInterface(String name, String flag){
		return getInterface(name);
	}
	
	public Interface getInterface(String name){
		Log.d("getInterface",name);
		if(hasInterface(name)){
			Interface _if = new Interface(new Long(0), name, false, "", "");
			return _interfaces.get(_interfaces.indexOf(_if));
		}
		return null;
	}
	
	public long addData(Interface interface_object, long up, long down){
		Log.d("addData",interface_object.toString()+" "+up+" "+down);
		return _sgbd.addData(interface_object.id, up, down, System.currentTimeMillis(), interface_object.flag, interface_object.address);
	}

    public void serviceNew(Batterie _interface) {
        Log.d("serviceNew",_interface.toString());
        if(this.hasInterface(_interface.name)){
        }else{
            Log.d("serviceNew",_interface.name);
            Interface inter = addInterface(_interface.name, true, "", "");
            inter.up=true;
            _sgbd.updateInterface(inter.id, inter.up);
        }
    }

	public void serviceNew(InterfaceInfo _interface) {
		Log.d("serviceNew",_interface.toString());
		if(this.hasInterface(_interface.name)){
		}else{
			Log.d("serviceNew",_interface.name);
			Interface inter = addInterface(_interface.name, true, _interface.flag, _interface.address);
			inter.up=true;
			_sgbd.updateInterface(inter.id, inter.up);
		}
	}

	public void serviceUp(InterfaceInfo _interface) {
		Log.d("serviceUp",_interface.toString());
		Interface inter = getInterface(_interface.name);
		inter.up=true;
		_sgbd.updateInterface(inter.id, inter.up);
	}

	public void serviceDown(InterfaceInfo _interface) {
		Log.d("serviceDown",_interface.toString());
		Interface inter = getInterface(_interface.name);
		inter.up=false;
		_sgbd.updateInterface(inter.id, inter.up);
	}
}
