package com.example.gurpreetsingh.project.framework;

import java.util.Locale;

/**
 * Created by Gurpreet on 15-01-2017.
 */

public final class ApplicationConstants {

    // Private constructor so that this class cannot be initiated as this is a singleton
    private ApplicationConstants() { }

    // The default encoding of the app
    public static final String DEFAULT_ENCODING = "UTF-8";

    // The default Locale of the app
    public static final String DEFAULT_LOCALE_KEY = Locale.getDefault().toString();

    // The heading font
    public static final String HEADING_FONT = "fonts/Brandon_bold.otf" ;

    // The heading font (lighter version)
    public static final String HEADING_FONT_LIGHT = "fonts/Brandon_light.otf" ;

    // The body font
    public static final String BODY_FONT ="fonts/Brandon_reg.otf" ;
}
