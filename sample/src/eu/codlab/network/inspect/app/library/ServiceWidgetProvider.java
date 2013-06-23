package eu.codlab.network.inspect.app.library;

import eu.codlab.network.inspect.app.R;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

public class ServiceWidgetProvider extends AppWidgetProvider {
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int []appWidgetId) {


		// Prepare widget views
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_service);


        Intent serviceIntent = new Intent(context, InspectService.class);
		if(InspectService.getState() == InspectService.RUNNING){
			views.setTextViewText(R.id.widget_text, context.getString(R.string.stop));
            serviceIntent.putExtra("state", 0);
		}else{
			views.setTextViewText(R.id.widget_text, context.getString(R.string.start));
            serviceIntent.putExtra("state", 1);
		}

		// Prepare intent to launch on widget click
		// Launch intent on widget click
		PendingIntent pendingIntent = PendingIntent.getService(context, 1, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		views.setOnClickPendingIntent(R.id.widget_service, pendingIntent);
		views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);

		for(int i=0;appWidgetId != null && i<appWidgetId.length;i++){
			update(appWidgetManager, appWidgetId[i], views);
		}
	}

	@SuppressLint("NewApi")
	private void update(AppWidgetManager appWidgetManager, int id, RemoteViews views){
		if(Build.VERSION.SDK_INT >= 16){
			Bundle options = appWidgetManager.getAppWidgetOptions(id);

			if(Build.VERSION.SDK_INT >= 17){
				int category = options.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);
				boolean isLockScreen = category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;
			}
		}

		appWidgetManager.updateAppWidget(id, views);
	}
}