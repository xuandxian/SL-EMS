package com.overtech.ems.activity;

import com.overtech.ems.config.SystemConfig;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

	public class MyApplication extends Application {
		private static MyApplication instance=null;
		private  static SharedPreferences sp;
		public MyApplication(){}
	    public static MyApplication getInstance()
	    {
	        if (null == instance)
	        {
	            instance = new MyApplication();
	        }
	        return instance;
	    }
	    @Override
	    public void onCreate() {
	    	// TODO Auto-generated method stub
	    	super.onCreate();
	    	if(sp==null){
	    		sp=getSharedPreferences(SystemConfig.PREFERENCES_NAME, Context.MODE_PRIVATE);
	    	}
	    }
	    public static SharedPreferences getSharePreference(){
	    	return sp;
	    }
	    
	}

