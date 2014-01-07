
/*
 * Copyright 2012 David Cesarino de Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itcorea.coreonmobile;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerDialogFragment extends DialogFragment {
    
    public static final String YEAR = "Year";
    public static final String MONTH = "Month";
    public static final String DATE = "Day";
    
    private OnDateSetListener mListener;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mListener = (OnDateSetListener) activity;
    }
    
    @Override
    public void onDetach() {
        this.mListener = null;
        super.onDetach();
    }
    
    @TargetApi(11)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle b = getArguments();
        int y = b.getInt(YEAR);
        int m = b.getInt(MONTH);
        int d = b.getInt(DATE);
        
        final DatePickerDialog picker = new DatePickerDialog(getActivity(), 
                getConstructorListener(), y, m, d);
        
        if (hasJellyBeanAndAbove()) {
            picker.setButton(DialogInterface.BUTTON_POSITIVE, 
                    getActivity().getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatePicker dp = picker.getDatePicker();
                    mListener.onDateSet(dp, 
                            dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                }
            });
            picker.setButton(DialogInterface.BUTTON_NEGATIVE,
                    getActivity().getString(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });
        }
        return picker;
    }
    
    private static boolean hasJellyBeanAndAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
    
    private OnDateSetListener getConstructorListener() {
        return hasJellyBeanAndAbove() ? null : mListener;
    }
}
