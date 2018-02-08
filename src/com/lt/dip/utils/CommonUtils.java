package com.lt.dip.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CommonUtils {

	public static boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}
	
	public static JSONArray subArray(JSONArray array,String value) {  
	    try {  
	        JSONArray subArray = new JSONArray();  
	        JSONObject obj = null;  
	        JSONObject obj2 = null;  
	        JSONArray aArray = new JSONArray();  
	        for (int i = 0, size = array.size(); i < size; i++) {  
	            obj = array.getJSONObject(i);  
	            if (i + 1 < size) {  
	                if (i - 1 < 0) {  
	                    subArray = new JSONArray();  
	                    subArray.add(obj);  
	                } else {  
	                    obj2 = array.getJSONObject(i - 1);  
	                    if (obj.getString(value).equals(obj2.getString(value))) {  
	                        subArray.add(obj);  
	                    } else {  
	                        aArray.add(subArray);  
	                        subArray = new JSONArray();  
	                        subArray.add(obj);  
	                    }  
	  
	                }  
	            } else {  
	                if (size + 1 > 0) {  
	                    obj2 = array.getJSONObject(i - 1);  
	                    if (obj.getString(value).equals(obj2.getString(value))) {  
	                        subArray.add(obj);  
	                        if (size - 1 == i) {  
	                            aArray.add(subArray);  
	                        }  
	                    } else {  
	                        aArray.add(subArray);  
	                        subArray = new JSONArray();  
	                        subArray.add(obj);  
	                        if (i + 1 == size) {  
	                            aArray.add(subArray);  
	                        }  
	                    }  
	  
	                } else {  
	                    subArray.add(obj);  
	                    aArray.add(subArray);  
	                }  
	  
	            }  
	  
	        }  
	        return aArray;  
	    }catch (Exception e){  
	        e.printStackTrace();  
	        return null;  
	    }  
	} 
}
