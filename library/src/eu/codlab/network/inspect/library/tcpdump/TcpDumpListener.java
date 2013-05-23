package eu.codlab.network.inspect.library.tcpdump;

public interface TcpDumpListener {
	public void onPacket(String src, String dst, PacketType type, int length);
	public void onPacket(String line);
	public void onPacket(int length);
	public void onStart();
	public void onStop(int value);
}
