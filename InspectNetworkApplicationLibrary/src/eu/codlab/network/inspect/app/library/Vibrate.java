package eu.codlab.network.inspect.app.library;

import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;

public class Vibrate{
	Vibrator _vibrator;

	public Vibrate(Context context){
		try{
			_vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
		}catch(Exception e){ }
	}
	public boolean hasVibrator(){
		if(Build.VERSION.SDK_INT >= 11){
			return _vibrator.hasVibrator();
		}else{
			return _vibrator != null;
		}
	}

	public void shortVibration(){
		if(hasVibrator()){
			_vibrator.vibrate(200);
		}
	}
	
	public void longVibration(){
		if(hasVibrator()){
			_vibrator.vibrate(1000);
		}
	}

}
