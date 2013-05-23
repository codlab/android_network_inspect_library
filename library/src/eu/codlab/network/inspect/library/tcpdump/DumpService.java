package eu.codlab.network.inspect.library.tcpdump;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.example.eu.codlab.network.inspect.library.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.util.Log;

public class DumpService extends Service implements TcpDumpListener{
	private static TcpDumpManager _manager;
	private NotificationManager notificationManager;
	private Notification notification;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);
		notificationManager =
				(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		//notification = new Notification(R.drawable.radio,getString(R.string.dump_notification),System.currentTimeMillis());
		start();

		return Service.START_NOT_STICKY;
	}

	public void start(){
		if(_manager == null){
			stopTCP();

			copyBinary();
			setRights();
			//listNet();
			_manager = new TcpDumpManager(this);
			_manager.addListener(this);
			_manager.start();
		}
	}

	public void stopTCP(){
		try{
			Process res = Runtime.getRuntime().exec("su -c /system/bin/ps | grep tcpdump");
			String out = null;
			StringBuffer buffer = new StringBuffer(4096);
			BufferedReader reader = new BufferedReader (new InputStreamReader(res.getInputStream()));

			String line ="";
			try {
				while ((line = reader.readLine ()) != null) {
					buffer.append(line+"\n");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}


			String []strs = buffer.toString().split("\n");
			for(int i=0;i<strs.length;i++){
				String [] tmp = strs[i].split(" ");
				boolean err=true;
				int idx=1;
				while(err && idx<tmp.length){
					try{
						int pid = Integer.parseInt(tmp[idx]);
						if(pid > 0)
							Runtime.getRuntime().exec("su -c /system/bin/kill -9 "+pid);
						err=false;
					}catch(Exception e){
						e.printStackTrace();
					}
					idx++;
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}

		_manager = null;
	}



	public String listNet(){
		String out = null;
		try{
			Process res = Runtime.getRuntime().exec("ls /sys/class/net");
			StringBuffer buffer = new StringBuffer(4096);
			BufferedReader reader = new BufferedReader (new InputStreamReader(res.getInputStream()));

			String line ="";
			try {
				while ((line = reader.readLine ()) != null) {
					buffer.append(line+"\n");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			out = buffer.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return out;
	}

	private void setRights(){
		Process p;
		try {
			String str = "chmod 777 /data/data/"+this.getPackageName()+"/tcpdump";
			p = Runtime.getRuntime().exec(str);
			p.waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void copyBinary(){
		AssetManager assetManager = getAssets();
		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open("tcpdump.jpeg");
			String str = "/data/data/"+this.getPackageName()+"/tcpdump";
			Log.d("APP",str+"2");
			out = new FileOutputStream(str);
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch(IOException e) {
			Log.e("tag", "Failed to copy asset file: ffmpeg", e);
		}       
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onPacket(String src, String dst, PacketType type, int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPacket(String line) {
		// TODO Auto-generated method stub

	}

	int len=0;
	@Override
	public void onPacket(int length) {
		len+=length;
		showNotification(len);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop(int value) {
		// TODO Auto-generated method stub

	}

	/**
	 * Proof of concept about receiving packets length
	 * @param length
	 */
	private void showNotification(int length) {
		
		PendingIntent pendingIntent = PendingIntent.getService(this, 21, new Intent(this, DumpService.class), 0);

		//notification.setLatestEventInfo(this, getString(R.string.dump_notification), ""+length, pendingIntent);
		//notificationManager.notify(42, notification);
	}
	
	/**
	 * Created by the library to show
	 */
	public void startForeground(){
		this.startForeground(42, notification);
	}
	
	public void stopForeground(){
		this.stopForeground(true);
	}

}
