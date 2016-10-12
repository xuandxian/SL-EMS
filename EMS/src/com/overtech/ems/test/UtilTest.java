package com.overtech.ems.test;


import org.junit.Test;

import com.overtech.ems.utils.AppUtils;

import android.test.AndroidTestCase;

public class UtilTest extends AndroidTestCase {

	@Test
	public void testIsNumberOrCharac() {
		boolean b=AppUtils.isValidTagAndAlias("哈 哈 我来 了   ");
	
		assertEquals(true, b);
	}

}
