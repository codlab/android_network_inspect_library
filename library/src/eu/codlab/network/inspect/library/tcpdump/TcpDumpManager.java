package eu.codlab.network.inspect.library.tcpdump;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import eu.codlab.network.inspect.library.SH;

import android.content.Context;

public class TcpDumpManager implements TcpDumpListener{
	private static Thread _tcpdump_thread;
	private String _cmd;
	private Context _context;
	private ArrayList<TcpDumpListener> _listeners;

	public TcpDumpManager(Context context){
		_context = context;
		_listeners = new ArrayList<TcpDumpListener>();
	}

	public boolean addListener(TcpDumpListener listener){
		boolean b = false;
		synchronized(this){
			if(!_listeners.contains(listener))
				b = _listeners.add(listener);
		}
		return b;
	}

	public boolean removeListener(TcpDumpListener listener){
		boolean b = false;
		synchronized(this){
			b = _listeners.remove(listener);
		}
		return b;
	}

	public void onPacket(String line){
		String [] lines = line.split("length ");
		if(lines.length == 2){
			int length = -1;
			try{
				length = Integer.parseInt(lines[1]);
			}catch(Exception e){
				e.printStackTrace();
			}
			if(length > -1)
				onPacket(length);
		}
	}

	public void onPacket(int length){
		synchronized(this){
			for(TcpDumpListener listener : _listeners){
				listener.onPacket(length);
			}
		}
	}

	public void onPacket(String src, String dst, PacketType type, int length){
		synchronized(this){
			for(TcpDumpListener listener : _listeners){
				listener.onPacket(src, dst, type, length);
			}
		}
	}


	@Override
	public void onStart() {
		synchronized(this){
			for(TcpDumpListener listener : _listeners){
				listener.onStart();
			}
		}
	}

	@Override
	public void onStop(int value) {
		synchronized(this){
			for(TcpDumpListener listener : _listeners){
				listener.onStop(value);
			}
		}
	}

	public void start(){
		Thread t = new Thread(){

			public void run(){

				try{

					SH sh = new SH("su");
					Process process = sh.runWaitForProcess("/data/data/"+_context.getPackageName()+"/tcpdump -i any");

					onStart();

					String out = null;
					StringBuffer buffer = null;
					BufferedReader reader = new BufferedReader (new InputStreamReader(process.getInputStream()));

					String line ="";
					try {
						while ((line = reader.readLine ()) != null) {
							onPacket(line);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					onStop(0);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
}
