package com.example.gurpreetsingh.project.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.example.gurpreetsingh.project.R;
import com.example.gurpreetsingh.project.framework.ApplicationConstants;

import java.lang.reflect.Field;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Gurpreet on 15-01-2017.
 */
public class AppUtils {

    // The tag declaration
    private static final String TAG = AppUtils.class.getSimpleName();

    /**
     * This method initializes calligraphy(fonts) in the system
     */
    public static void initializeCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(ApplicationConstants.BODY_FONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}

