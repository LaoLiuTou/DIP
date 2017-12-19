package com.lt.dip.utils;

import java.util.Properties;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;


public class ConfigUtil {
	
	public static String configCon=null;
    
    public static String getConfig(){
    	if(configCon==null){
    		configCon=readProperties();
    	}
    	else{
    		 
    	}
    	return configCon;
    }
 
    
	public static String readProperties(){
		Logger logger = Logger.getLogger("DipLogger");
		String resultStr="";
		try {
			Properties props = new Properties();  
	      	props.load(ConfigUtil.class.getClassLoader().getResourceAsStream("dbpool.properties"));  
			JSONObject dbJO=new JSONObject();
			dbJO.accumulate("dbType", props.getProperty("dbType").trim());
			dbJO.accumulate("dbUser", props.getProperty("dbUser").trim());
			dbJO.accumulate("dbPassword", props.getProperty("dbPassword").trim());
			dbJO.accumulate("dbHost", props.getProperty("dbHost").trim());
			dbJO.accumulate("dbPort", props.getProperty("dbPort").trim());
			dbJO.accumulate("dbName", props.getProperty("dbName").trim());
			resultStr=dbJO.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("本地数据源配置错误！");
			e.printStackTrace();
		}
		return resultStr;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
