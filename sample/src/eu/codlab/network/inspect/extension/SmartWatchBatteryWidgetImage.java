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

import com.sonyericsson.extras.liveware.extension.util.widget.SmartWatchWidgetImage;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import eu.codlab.network.inspect.app.R;

/**
 * The class decorates a widget image with sample UI components.
 */
public class SmartWatchBatteryWidgetImage extends SmartWatchWidgetImage {

    private String mName;
    private String mValue;

    /**
     * @param context The context.
     * @param name The name.
     * @param value The value.
     */
    public SmartWatchBatteryWidgetImage(final Context context, final String name, final String value) {
        super(context);
        setInnerLayoutResourceId(R.layout.smart_watch_sample_widget);
        mName = name;
        mValue = value;
    }

    @Override
    protected void applyInnerLayout(LinearLayout innerLayout) {
        // Set time
        ((TextView)innerLayout.findViewById(R.id.smart_watch_sample_widget_name)).setText(mName);
        ((TextView)innerLayout.findViewById(R.id.smart_watch_sample_widget_value)).setText(mValue.replaceAll("/","\n"));
    }

}
