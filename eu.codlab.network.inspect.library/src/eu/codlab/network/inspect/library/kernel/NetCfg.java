package eu.codlab.network.inspect.library.kernel;

import java.util.ArrayList;

import eu.codlab.network.inspect.library.CommandResult;
import eu.codlab.network.inspect.library.SH;

public class NetCfg {
	private SH _sh;
	
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

	/**
	 * Get the content of the NetCfg command line
	 * Infos about interface state address etc... \n
	 * Need INTERNET permission
	 * @return the content = UP
	 */
	public String getNetCfgDumpUp(){
		CommandResult res = _sh.runWaitFor("netcfg");
		String stdout = DumpKernelVariableHelper.dump_trim_out(res.stdout);//res.stderr+ " "+res.stdout;
		String ret = "";
		String [] stdoutsplitted = stdout.split("\n");
		for(int i = 0; i < stdoutsplitted.length; i ++){
			String [] linesplitted = stdoutsplitted[i].split(" ");
			if(linesplitted.length > 1 && "UP".equals(linesplitted[1])){
				ret+=stdoutsplitted[i]+"\n";
			}
		}
		return ret;
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
		String ret = "";
		String [] stdoutsplitted = stdout.split("\n");
		for(int i = 0; i < stdoutsplitted.length; i ++){
			String [] linesplitted = stdoutsplitted[i].split(" ");
			if(linesplitted.length > 1 && "DOWN".equals(linesplitted[1]))
				ret+=stdoutsplitted[i]+"\n";
		}
		return ret;
	}
}
