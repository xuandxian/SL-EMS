package com.overtech.ems.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 *
 */
public class MD5 {
	 protected static MessageDigest messageDigest = null;   
	    static{   
	        try{   
	            messageDigest = MessageDigest.getInstance("MD5");   
	        }catch (NoSuchAlgorithmException e) {   
	            e.printStackTrace();   
	        }   
	    } 
	    
	protected static char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};   

	
    public static String getMD5(String s) {  
        try {  
            MessageDigest md5 = MessageDigest.getInstance("MD5");    
            byte[] byteArray = s.getBytes("UTF-8");  
            byte[] md5Bytes = md5.digest(byteArray);    
            StringBuffer hexValue = new StringBuffer();    
            for (int i = 0; i < md5Bytes.length; i++) {  
                int val = ((int) md5Bytes[i]) & 0xff;  
                if (val < 16)  
                    hexValue.append("0");  
                hexValue.append(Integer.toHexString(val));  
            }    
            return hexValue.toString();    
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }
    
    
   
	public static String getFileMD5String(File file) throws IOException{
		FileInputStream in =null;		
		try {
			in=new FileInputStream(file);
			FileChannel ch = in.getChannel();
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			messageDigest.update(byteBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(in!=null){
				in.close();
			}
		}
		return bufferToHex(messageDigest.digest());
	}
	
	 
    public static String getFileMD5String(String fileName) throws IOException{   
        File f = new File(fileName);   
        return getFileMD5String(f);   
    }  
	
	private static String bufferToHex(byte bytes[]) {
		   return bufferToHex(bytes, 0, bytes.length);
	}
	
	private static String bufferToHex(byte bytes[], int m, int n) {   
	       StringBuffer stringbuffer = new StringBuffer(2 * n);   
	       int k = m + n;   
	       for (int l = m; l < k; l++) {   
	        appendHexPair(bytes[l], stringbuffer);   
	       }   
	       return stringbuffer.toString();   
	 }
	
	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {   
	       char c0 = hexDigits[(bt & 0xf0) >> 4];   
	       char c1 = hexDigits[bt & 0xf];   
	       stringbuffer.append(c0);   
	       stringbuffer.append(c1);   
	 }
}
