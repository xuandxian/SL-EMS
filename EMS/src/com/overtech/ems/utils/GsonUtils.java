package com.overtech.ems.utils;

import com.google.gson.Gson;

public class GsonUtils {
	private GsonUtils() {
	}

	private static Gson gson;

	public static synchronized Gson getInstance() {
		if (gson == null) {
			gson = new Gson();
		}
		return gson;
	}
}
