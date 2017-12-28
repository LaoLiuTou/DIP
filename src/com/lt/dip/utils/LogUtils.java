package com.lt.dip.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class LogUtils {

	/**
	 * 
	 * @param mem_id
	 * @param planame
	 * @param objects
	 * @param operations
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static void log(String mem_id,String planame,String objects,String operations,String status){
		Logger logger = Logger.getLogger("DipLogger");
		try {
			JSONObject dbJO = JSONObject.fromObject(ConfigUtil.configCon);
			
			String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
			ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
					dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
					dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
			String insertSql = "INSERT INTO "+"SYS_SQLLOGS"+" ";
			JSONObject  paramJO = new JSONObject();	
			paramJO.accumulate("mem_id", mem_id);
			paramJO.accumulate("planame", planame);
			paramJO.accumulate("objects", objects);
			paramJO.accumulate("operations", operations);
			paramJO.accumulate("status", status);
			if(paramJO!=null){
				List<String> colList = new ArrayList<String>();
				List<String> valueList = new ArrayList<String>();
				Iterator iterator = paramJO.keys();
				while(iterator.hasNext()){
					String key=(String) iterator.next();
					String value=paramJO.getString(key).toLowerCase();
					colList.add(key);
					valueList.add("'"+value+"'");
				}
				if(colList.size()>0&&valueList.size()>0){
					insertSql+=" ("+StringUtils.join(colList,",")+") ";
					insertSql+=" VALUES ("+StringUtils.join(valueList,",")+") ";
				}
				//返回自增主键
				int result=cp.insert(c3p0Key, insertSql);
				//日志
				logger.info("执行SQL："+insertSql);
			}
			
				 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("日志存储失败！");
			e.printStackTrace();
		}

	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
