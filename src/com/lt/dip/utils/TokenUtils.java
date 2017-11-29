package com.lt.dip.utils;

 
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author lt
 * @version 1.0 
 */
public class TokenUtils {
    public static Map<String,String> map=new ConcurrentHashMap<String, String>();
    public static void add(String token,String timesamp){
        map.put(token,timesamp);
    }
    public static String get(String token){
       return map.get(token);
    }
 
    public static void remove(String token){
    	 map.remove(token);
    }
    
    
    @SuppressWarnings("rawtypes")
	public static void main(String[] s){
    	System.out.println(get("b6f71e0e-6634-4529-aee5-324cecdb1fbe"));
    	for (Map.Entry entry:map.entrySet()){
            
            System.out.println(entry.getValue()+":"+entry.getKey());
        }
    }

}
