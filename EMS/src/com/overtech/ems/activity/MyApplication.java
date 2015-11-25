package com.overtech.ems.activity;

import android.app.Application;

	public class MyApplication extends Application {
		private static MyApplication instance=null;
		public MyApplication(){}
	    public static MyApplication getInstance()
	    {
	        if (null == instance)
	        {
	            instance = new MyApplication();
	        }
	        return instance;
	    }
	}

