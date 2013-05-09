package eu.codlab.network.inspect.library.kernel;

public class AbstractStatisticsInfo {
	protected String _cmd_base;
	protected String _interface_info;
	
	protected AbstractStatisticsInfo(String interface_name){
		if(interface_name == null)
			throw new NullPointerException("Dafuk did youd do? You were the chosen one! :'(");
		
		
		_interface_info = interface_name;
		_cmd_base = "/sys/class/net/%s/statistics/".replace("%s",_interface_info);
	}
	
	protected String dump(String file){
		return DumpKernelVariableHelper.dump(_cmd_base+file);
	}
	
	protected long getLong(String toLong){
		long res = -1;
		try{
			res = Long.parseLong(toLong.replace("\n", ""));
		}catch(Exception e){
			
		}
		return res;
	}
	
}
