package com.lt.dip.utils;

 
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author lt
 * @version 1.0 
 */
public class TokenUtils {
    public static Map<String,TokenBean> map=new ConcurrentHashMap<String, TokenBean>();
    public static void add(String token,TokenBean tokenBean){
        map.put(token,tokenBean);
    }
    public static TokenBean get(String token){
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
    
    
    
    public class TokenBean { 
    	private String timesamp;
    	private String username;
    	private String userid;
		public String getTimesamp() {
			return timesamp;
		}
		public void setTimesamp(String timesamp) {
			this.timesamp = timesamp;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getUserid() {
			return userid;
		}
		public void setUserid(String userid) {
			this.userid = userid;
		}
    	
       
    } 
}
