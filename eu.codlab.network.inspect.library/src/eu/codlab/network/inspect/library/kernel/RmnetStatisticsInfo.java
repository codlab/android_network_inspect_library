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
	
	private int getInt(String toInt){
		int res = -1;
		try{
			res = Integer.parseInt(toInt.replace("\n", ""));
		}catch(Exception e){
			
		}
		return res;
	}


	/**
	 * Number of Bytes Read since start
	 * @return number of bytes
	 */
	public int getRXBytes(){
		return getInt(dump("rx_bytes"));
	}

	public int getRXCompressed(){
		return getInt(dump("rx_compressed"));
	}

	public int getRXCRCErrors(){
		return getInt(dump("rx_crc_errors"));
	}

	public int getRXDropped(){
		return getInt(dump("rx_dropped"));
	}

	public int getRXErrors(){
		return getInt(dump("rx_errors"));
	}

	public int getRXFifoErrors(){
		return getInt(dump("rx_fifo_errors"));
	}

	public int getRXFrameErrors(){
		return getInt(dump("rx_frame_errors"));
	}
	
	public int getRXLengthErrors(){
		return getInt(dump("rx_length_errors"));
	}
	
	public int getRXMissedErrors(){
		return getInt(dump("rx_missed_errors"));
	}
	
	public int getRXOverErrors(){
		return getInt(dump("rx_over_errors"));
	}
	
	public int getRXPackets(){
		return getInt(dump("rx_packets"));
	}
	

	public int getTXMissedErrors(){
		return getInt(dump("tx_aborted_errors"));
	}
	
	/**
	 * Number of Bytes Sent since start
	 * @return number of bytes
	 */
	public int getTXBytes(){
		return getInt(dump("tx_bytes"));
	}

	public int getTXCarrierErrors(){
		return getInt(dump("tx_carrier_errors"));
	}
	
	public int getTXCompressed(){
		return getInt(dump("tx_compressed"));
	}

	public int getTXDropped(){
		return getInt(dump("tx_dropped"));
	}

	public int getTXErrors(){
		return getInt(dump("tx_errors"));
	}

	public int getTXFifoErrors(){
		return getInt(dump("tx_fifo_errors"));
	}

	public int getTXHeartBeatErrors(){
		return getInt(dump("tx_heartbeat_errors"));
	}
	
	public int getTXPackets(){
		return getInt(dump("tx_packets"));
	}

	public int getTXWindowErrors(){
		return getInt(dump("tx_window_errors"));
	}
	
}
