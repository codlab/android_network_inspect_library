package eu.codlab.network.inspect.library;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SH {

	private String SHELL = "sh";

	public SH(String SHELL_in) {
		SHELL = SHELL_in;
	}

	public Process run(String s) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(SHELL);
			DataOutputStream toProcess = new DataOutputStream(process.getOutputStream());
			toProcess.writeBytes("exec " + s + "\n");
			toProcess.flush();
		} catch(Exception e) {
			process = null;
			e.printStackTrace();
		}
		return process;
	}

	private String getStreamLines(InputStream is) {
		String out = null;
		StringBuffer buffer = new StringBuffer(4096);
		BufferedReader reader = new BufferedReader (new InputStreamReader(is));

		String line ="";
		try {
			while ((line = reader.readLine ()) != null) {
				buffer.append(line+"\n");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (buffer != null)
			out = buffer.toString();
		return out;
	}


	public CommandResult runWaitFor(String s) {
		Process process = run(s);
		Integer exit_value = null;
		String stdout = null;
		String stderr = null;
		if (process != null) {
			try {

				stdout = getStreamLines(process.getInputStream());

				exit_value = process.waitFor();
			} catch(InterruptedException e) {
				e.printStackTrace();
			} catch(NullPointerException e) {
				e.printStackTrace();
			}
		}
		return new CommandResult(exit_value, stdout, stderr);
	}
	public Process runWaitForProcess(String s) {
		Process process = run(s);
		Integer exit_value = null;
		String stdout = null;
		String stderr = null;
		return process;
	}
}
