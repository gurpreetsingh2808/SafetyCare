package com.example.gurpreetsingh.project;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Gurpreet Singh on 10/25/2015.
 */
public class VolleySingleton {
    private static VolleySingleton ourInstance = new VolleySingleton();
    private RequestQueue mRequestQueue;

    public static VolleySingleton getInstance() {
        return ourInstance;
    }

    private VolleySingleton() {
        mRequestQueue= Volley.newRequestQueue(SafetyCareApplication.getAppContext());
    }

    public RequestQueue getmRequestQueue()  {
        return mRequestQueue;
    }

}
