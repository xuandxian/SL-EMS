package com.overtech.ems.activity;

import java.util.LinkedList;
import java.util.List;

import com.overtech.ems.activity.common.photo.util.CustomConstants;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

	public class MyApplication extends Application {
		private  List<Activity> activityList=new LinkedList<Activity>();
	    private static MyApplication instance;
	    public MyApplication()
	    {
	    }
	    public static MyApplication getInstance()
	    {
	        if (null == instance)
	        {
	            instance = new MyApplication();
	        }
	        return instance;
	    }
	    public void addActivity(Activity activity)
	    {
	        activityList.add(activity);
	    }
	    public  void exit()
	    {
	        for (Activity activity : activityList)
	        {
	            activity.finish();
	        }
	        System.exit(0);
	    }
	    @Override
	    public void onCreate() {
	    	removeTempFromPref();
	    }
	    private void removeTempFromPref()
		{
			SharedPreferences sp = getSharedPreferences(
					CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
			sp.edit().remove(CustomConstants.PREF_TEMP_IMAGES).commit();
		}

	}

