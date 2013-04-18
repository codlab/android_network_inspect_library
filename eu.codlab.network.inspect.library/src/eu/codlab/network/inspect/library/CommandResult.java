package eu.codlab.network.inspect.library;

/**
 * Can be used to store command line results
 * 
 * @author kevin
 *
 */
public class CommandResult {

	public final String stdout;
	public final String stderr;
	public final Integer exit_value;

	CommandResult(Integer exit_value_in, String stdout_in, String stderr_in)
	{
		exit_value = exit_value_in;
		stdout = stdout_in;
		stderr = stderr_in;
	}

	CommandResult(Integer exit_value_in) {
		this(exit_value_in, null, null);
	}

	public boolean success() {
		return exit_value != null && exit_value == 0;
	}
}
