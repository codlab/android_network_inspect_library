package eu.codlab.network.inspect.library.kernel;

/**
 * Class to get info about packets received/sent via the internal Radio
 * 
 * @author codlab
 *
 */
public class RmnetStatisticsInfo {
	private String _cmd_base = "/sys/class/net/%s/statistics/";
	private String _interface_info;
	public RmnetStatisticsInfo(String interface_name){
		if(interface_name == null)
			throw new NullPointerException("Dafuk did youd do? You were the chosen one! :'(");
		
		
		_interface_info = interface_name;
		_cmd_base = _cmd_base.replace("%s",_interface_info);
		
	}
	
	private String dump(String file){
		return DumpKernelVariableHelper.dump(_cmd_base+file);
	}
	
	private long getLong(String toLong){
		long res = -1;
		try{
			res = Long.parseLong(toLong.replace("\n", ""));
		}catch(Exception e){
			
		}
		return res;
	}


	/**
	 * Number of Bytes Read since start
	 * @return number of bytes
	 */
	public long getRXBytes(){
		return getLong(dump("rx_bytes"));
	}

	public long getRXCompressed(){
		return getLong(dump("rx_compressed"));
	}

	public long getRXCRCErrors(){
		return getLong(dump("rx_crc_errors"));
	}

	public long getRXDropped(){
		return getLong(dump("rx_dropped"));
	}

	public long getRXErrors(){
		return getLong(dump("rx_errors"));
	}

	public long getRXFifoErrors(){
		return getLong(dump("rx_fifo_errors"));
	}

	public long getRXFrameErrors(){
		return getLong(dump("rx_frame_errors"));
	}
	
	public long getRXLengthErrors(){
		return getLong(dump("rx_length_errors"));
	}
	
	public long getRXMissedErrors(){
		return getLong(dump("rx_missed_errors"));
	}
	
	public long getRXOverErrors(){
		return getLong(dump("rx_over_errors"));
	}
	
	public long getRXPackets(){
		return getLong(dump("rx_packets"));
	}
	

	public long getTXMissedErrors(){
		return getLong(dump("tx_aborted_errors"));
	}
	
	/**
	 * Number of Bytes Sent since start
	 * @return number of bytes
	 */
	public long getTXBytes(){
		return getLong(dump("tx_bytes"));
	}

	public long getTXCarrierErrors(){
		return getLong(dump("tx_carrier_errors"));
	}
	
	public long getTXCompressed(){
		return getLong(dump("tx_compressed"));
	}

	public long getTXDropped(){
		return getLong(dump("tx_dropped"));
	}

	public long getTXErrors(){
		return getLong(dump("tx_errors"));
	}

	public long getTXFifoErrors(){
		return getLong(dump("tx_fifo_errors"));
	}

	public long getTXHeartBeatErrors(){
		return getLong(dump("tx_heartbeat_errors"));
	}
	
	public long getTXPackets(){
		return getLong(dump("tx_packets"));
	}

	public long getTXWindowErrors(){
		return getLong(dump("tx_window_errors"));
	}
	
}
