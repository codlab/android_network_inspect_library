package eu.codlab.network.inspect.library.kernel;

import eu.codlab.network.inspect.library.SH;

public class DumpKernelVariableHelper {
	private static SH _sh;
	private static Object _this = new Object();
	
	/**
	 * Dump a file via the cat cmd line
	 * @param file the file
	 * @return the content
	 */
	public static String dump(String file){
		String res = "";
		synchronized(_this){
			if(_sh == null)_sh = new SH("sh");
			res = _sh.runWaitFor("cat "+file).stdout;
		}
		return res;
	}
	
	/**
	 * Erase every unusefull space character in a file content
	 * @param file the file
	 * @return the stripped content
	 */
	public static String dump_trim(String file){
		return dump_trim_out(dump(file));
	}

	/**
	 * 
	 * @param res
	 * @return
	 */
	public static String dump_trim_out(String res){
		while(res.indexOf("  ") >=0){res = res.replace("  "," ");}
		
		return res;
	}
}
