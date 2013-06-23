/*
 Copyright (c) 2011, Sony Ericsson Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB nor the names
 of its contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.codlab.network.inspect.extension;

import com.sonyericsson.extras.liveware.aef.widget.Widget;
import com.sonyericsson.extras.liveware.extension.util.SmartWatchConst;
import com.sonyericsson.extras.liveware.extension.util.widget.WidgetExtension;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import eu.codlab.network.inspect.app.R;
import eu.codlab.network.inspect.app.library.InspectService;

/**
 * The sample widget handles the widget on an accessory. This class exists in
 * one instance for every supported host application that we have registered to.
 */
class SampleWidget extends WidgetExtension {
    private int _select_index;

    public static final int WIDTH = 128;

    public static final int HEIGHT = 110;

    private static final long UPDATE_INTERVAL = 60 * DateUtils.SECOND_IN_MILLIS;

    /**
     * Create sample widget.
     *
     * @param hostAppPackageName Package name of host application.
     * @param context The context.
     */
    SampleWidget(final String hostAppPackageName, final Context context) {
        super(context, hostAppPackageName);
        _select_index = 0;
    }

    /**
     * Start refreshing the widget. The widget is now visible.
     */
    @Override
    public void onStartRefresh() {
        Log.d(SampleExtensionService.LOG_TAG, "startRefresh");
        // Update now and every 60th second
        cancelScheduledRefresh(SampleExtensionService.EXTENSION_KEY);
        scheduleRepeatingRefresh(System.currentTimeMillis(), UPDATE_INTERVAL,
                SampleExtensionService.EXTENSION_KEY);
    }

    /**
     * Stop refreshing the widget. The widget is no longer visible.
     */
    @Override
    public void onStopRefresh() {
        Log.d(SampleExtensionService.LOG_TAG, "stopRefesh");

        // Cancel pending clock updates
        cancelScheduledRefresh(SampleExtensionService.EXTENSION_KEY);
    }

    @Override
    public void onScheduledRefresh() {
        Log.d(SampleExtensionService.LOG_TAG, "scheduledRefresh()");
        updateWidget();
    }

    /**
     * Unregister update clock receiver, cancel pending updates
     */
    @Override
    public void onDestroy() {
        Log.d(SampleExtensionService.LOG_TAG, "onDestroy()");
        onStopRefresh();
    }

    /**
     * The widget has been touched.
     *
     * @param type The type of touch event.
     * @param x The x position of the touch event.
     * @param y The y position of the touch event.
     */
    @Override
    public void onTouch(final int type, final int x, final int y) {
        Log.d(SampleExtensionService.LOG_TAG, "onTouch() " + type);
        if (!SmartWatchConst.ACTIVE_WIDGET_TOUCH_AREA.contains(x, y)) {
            Log.d(SampleExtensionService.LOG_TAG, "Ignoring touch outside active area x: " + x
                    + " y: " + y);
            return;
        }

        if (type == Widget.Intents.EVENT_TYPE_SHORT_TAP) {
            if(_select_index==-1)_select_index=0;
            _select_index++;
            if(InspectService.getInstance() == null){
                _select_index = -1;
            }else{
                if(_select_index > InspectService.getInstance().getInterfaceSize()){
                    _select_index =0;
                }

            }
            updateWidget();
        }
    }

    /**
     * Update the widget.
     */
    private void updateWidget() {
        Log.d(SampleExtensionService.LOG_TAG, "updateWidget");
        if(_select_index == -1 || InspectService.getInstance() == null){
            showBitmap(new SmartWatchBatteryWidgetImage(mContext, "battery", InspectService.getBatteryFromKernel().replace("\n","")+"%").getBitmap());
        }else{
            String [] res = InspectService.getInstance().getInterfaceString(_select_index);
            showBitmap(new SmartWatchBatteryWidgetImage(mContext, res[0], res[1]).getBitmap());
        }
    }
}
