package eu.codlab.network.inspect.library.kernel;

import java.util.ArrayList;

import eu.codlab.network.inspect.library.CommandResult;
import eu.codlab.network.inspect.library.SH;

public class NetCfg {
	private SH _sh;
	
	public static class FastInfos{
		public String device;
		public boolean up;
		public String address;
		public String flag;
	}
	public NetCfg(){
		_sh = new SH("sh");
	}

	/**
	 * Get the content of the NetCfg command line
	 * Infos about interface state address etc... \n
	 * Need INTERNET permission
	 * @return the content
	 */
	public String getNetCfgDump(){
		CommandResult res = _sh.runWaitFor("netcfg");
		return DumpKernelVariableHelper.dump_trim_out(res.stdout);//res.stderr+ " "+res.stdout;
	}
	

	/**
	 * Get the interfaces of the NetCfg command line
	 * Need INTERNET permission
	 * @return the list of all every up interfaces
	 */
	public ArrayList<String> getNetCfgInterfacesDown(){
		CommandResult res = _sh.runWaitFor("netcfg");
		String stdout = DumpKernelVariableHelper.dump_trim_out(res.stdout);//res.stderr+ " "+res.stdout;
		ArrayList<String> ret = new ArrayList<String>();
		String [] stdoutsplitted = stdout.split("\n");
		for(int i = 0; i < stdoutsplitted.length; i ++){
			String [] linesplitted = stdoutsplitted[i].split(" ");
			if(linesplitted.length > 1 && "DOWN".equals(linesplitted[1]))
				ret.add(linesplitted[0]);
		}
		return ret;
	}
	

	/**
	 * Get the interfaces of the NetCfg command line
	 * Need INTERNET permission
	 * @return the list of all every up interfaces
	 */
	public ArrayList<String> getNetCfgInterfacesUp(){
		CommandResult res = _sh.runWaitFor("netcfg");
		String stdout = DumpKernelVariableHelper.dump_trim_out(res.stdout);//res.stderr+ " "+res.stdout;
		ArrayList<String> ret = new ArrayList<String>();
		String [] stdoutsplitted = stdout.split("\n");
		for(int i = 0; i < stdoutsplitted.length; i ++){
			String [] linesplitted = stdoutsplitted[i].split(" ");
			if(linesplitted.length > 1 && "UP".equals(linesplitted[1]))
				ret.add(linesplitted[0]);
		}
		return ret;
	}
	
	public ArrayList<FastInfos> getNetCfgInterfacesUpPair(){
		CommandResult res = _sh.runWaitFor("netcfg");
		String stdout = DumpKernelVariableHelper.dump_trim_out(res.stdout);
		ArrayList<FastInfos> ret = new ArrayList<FastInfos>();
		String [] stdoutsplitted = stdout.split("\n");
		FastInfos p = null;
		for(int i = 0; i < stdoutsplitted.length; i ++){
			String [] linesplitted = stdoutsplitted[i].split(" ");
			if(linesplitted.length > 1){
				p = new FastInfos();
				p.up = "UP".equals(linesplitted[1]) ? true : false;
				p.address = linesplitted[linesplitted.length-1];
				p.device = linesplitted[0];
				p.flag = DumpKernelVariableHelper.dump("/sys/class/net/"+p.device+"/flags");
				ret.add(p);
			}
		}
		return ret;
	}

	/**
	 * Get the content of the NetCfg command line
	 * Infos about interface state address etc... \n
	 * Need INTERNET permission
	 * @return the content = UP
	 */
	public String getNetCfgDumpUp(){
		CommandResult res = _sh.runWaitFor("netcfg");
		String stdout = DumpKernelVariableHelper.dump_trim_out(res.stdout);//res.stderr+ " "+res.stdout;
        StringBuffer ret = new StringBuffer(4096);
		String [] stdoutsplitted = stdout.split("\n");
		for(int i = 0; i < stdoutsplitted.length; i ++){
			String [] linesplitted = stdoutsplitted[i].split(" ");
			if(linesplitted.length > 1 && "UP".equals(linesplitted[1])){
				ret.append(stdoutsplitted[i]+"\n");
			}
		}
		return ret.toString();
	}

	/**
	 * Get the content of the NetCfg command line
	 * Infos about interface state address etc... \n
	 * Need INTERNET permission
	 * @return the content where state = DOWN
	 */
	public String getNetCfgDumpDown(){
		CommandResult res = _sh.runWaitFor("netcfg");
		String stdout = DumpKernelVariableHelper.dump_trim_out(res.stdout);//res.stderr+ " "+res.stdout;
		StringBuffer ret = new StringBuffer(4096);
		String [] stdoutsplitted = stdout.split("\n");
		for(int i = 0; i < stdoutsplitted.length; i ++){
			String [] linesplitted = stdoutsplitted[i].split(" ");
			if(linesplitted.length > 1 && "DOWN".equals(linesplitted[1]))
				ret.append(stdoutsplitted[i]+"\n");
		}
		return ret.toString();
	}
}
