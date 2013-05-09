package eu.codlab.network.inspect.app.library;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class ServiceWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int []appWidgetId) {
    	
    	 
        // Prepare widget views
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_service);
 
        if(InspectService.getState() == InspectService.RUNNING){
            views.setTextViewText(R.widget.text, context.getString(R.string.stop));
        }else{
            views.setTextViewText(R.widget.text, context.getString(R.string.start));
        }
        
        // Prepare intent to launch on widget click
		Intent serviceIntent = new Intent(context, InspectService.class);
		serviceIntent.putExtra("state", 2);
        // Launch intent on widget click
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.widget.service, pendingIntent);
        views.setOnClickPendingIntent(R.widget.text, pendingIntent);
 
        for(int i=0;appWidgetId != null && i<appWidgetId.length;i++)
        	appWidgetManager.updateAppWidget(appWidgetId[i], views);
    }}
