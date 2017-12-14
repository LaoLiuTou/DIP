package com.lt.dip.utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 返回sql
 * @author Administrator
 *
 */
public class TranUtil {

	
	
	/**
	 * 新增数据
	 * @param (dbInfo,param,tableName)
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String insert(String param,String tableName){
		Logger logger = Logger.getLogger("DipLogger");
		logger.info("组装sql语句-新建数据("+tableName+")");
		String insertSql="-1";
		try { 
				insertSql = "INSERT INTO "+tableName+" ";
				/*INSERT INTO tbl_name (col1,col2) VALUES(15,col1*2); */
				if(param!=null&&!param.equals("")){
					JSONObject  paramJO = JSONObject.fromObject(param);
					//动态添加查询参数
					if(paramJO!=null){
						List<String> colList = new ArrayList<String>();
						List<String> valueList = new ArrayList<String>();
						Iterator iterator = paramJO.keys();
						while(iterator.hasNext()){
							String key=(String) iterator.next();
							String value=paramJO.getString(key);
							colList.add(key);
							valueList.add("'"+value+"'");
						}
						if(colList.size()>0&&valueList.size()>0){
							insertSql+=" ("+StringUtils.join(colList,",")+") ";
							insertSql+=" VALUES ("+StringUtils.join(valueList,",")+") ";
						}
						//日志
						logger.info("组装SQL："+insertSql);
					}
				}
				else{
					insertSql="-1";
					logger.info("参数格式不正确！");
				}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			insertSql="-1";
			logger.info("组装新建sql语句错误！");
			e.printStackTrace();
		}
		
		return insertSql;
	}
	/**
	 * 更新数据
	 * @param (param,condition,tableName)
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String update(String param,String condition,String tableName){
		Logger logger = Logger.getLogger("DipLogger");
		String updateSql="-1";
		try {
			updateSql = "UPDATE "+tableName+" ";
			/*String sql = "update person set sex=?,name=? where id=?";*/
			if(param!=null&&!param.equals("")){
				JSONObject  paramJO = JSONObject.fromObject(param);
				//动态添加查询参数
				if(paramJO!=null){
					List<String> colList = new ArrayList<String>();
					Iterator iterator = paramJO.keys();
					while(iterator.hasNext()){
						String key=(String) iterator.next();
						String value=paramJO.getString(key);
						colList.add(key+"='"+value+"'");
					}
					if(colList.size()>0){
						updateSql+=" SET "+StringUtils.join(colList,",");
						
						if(condition!=null&&!condition.equals("")){
							JSONObject  conditionJO = JSONObject.fromObject(condition);
							if(conditionJO!=null){
								List<String> conditionList = new ArrayList<String>();
								Iterator it = conditionJO.keys();
								while(it.hasNext()){
									String key=(String) it.next();
									String value=conditionJO.getString(key);
									conditionList.add(key+"='"+value+"'");
								}
								if(colList.size()>0){
									updateSql+=" WHERE "+StringUtils.join(conditionList," AND ");
								}
							}
						}
						//日志
						logger.info("组装SQL："+updateSql);
					}
				}
			}
			else{
				updateSql="-1";
				logger.info("参数格式不正确！");
			}
		} catch (Exception e) {
			updateSql="-1";
			// TODO Auto-generated catch block
			logger.info("组装更新sql语句错误！");
			e.printStackTrace();
		}
		return updateSql;
	}
	
	
	/**
	 * 删除数据
	 * @param (param,tableName)
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String delete(String param,String tableName){
		Logger logger = Logger.getLogger("DipLogger");
		String deleteSql="-1";
		try {
			deleteSql = "DELETE FROM "+tableName+" ";
			/* String sql = "delete from myuser where userID=5"; */
			if(param!=null&&!param.equals("")){
				JSONObject  paramJO = JSONObject.fromObject(param);
				//动态添加查询参数
				if(paramJO!=null){
					List<String> colList = new ArrayList<String>();
					Iterator iterator = paramJO.keys();
					while(iterator.hasNext()){
						String key=(String) iterator.next();
						String value=paramJO.getString(key);
						colList.add(key+"='"+value+"'");
					}
					if(colList.size()>0){
						deleteSql+=" WHERE "+StringUtils.join(colList," AND ");
					}
				 
					//日志
					logger.info("执行SQL："+deleteSql);
			 
				}
			}
			else{
				deleteSql="-1";
				logger.info("参数格式不正确！");
			}
		
		} catch (Exception e) {
			deleteSql="-1";
			// TODO Auto-generated catch block
			logger.info("组装删除sql语句错误！");
			e.printStackTrace();
		}
			
	
		return deleteSql;
	}
	
	/**
	 * 事务处理多步操作
	 * @param (dbInfo,mulParam) mulParam:[{type:insert,tableName:user,param{}},{}]
	 * @return
	 */
	public static String tran(String dbInfo,String mulParam){
		Logger logger = Logger.getLogger("DipLogger");
		JSONObject resultJO=new JSONObject();
		logger.info("数据库信息："+dbInfo);
		logger.info("数据库操作-事务处理多步操作");
		if(dbInfo==null||dbInfo.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递dbInfo参数,请给出数据源连接信息");
			logger.info("没有传递dbInfo参数,请给出数据源连接信息");
		}
		else{
			try {
				JSONObject dbJO = JSONObject.fromObject(dbInfo);
				try {
					String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
					ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
							dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
							dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
					Connection connection =cp.getConnection(c3p0Key);
					if(mulParam!=null&&!mulParam.equals("")){
						JSONArray  mulParamJA = JSONArray.fromObject(mulParam);
						 try {
							logger.info("事物开始");
				        	connection.setAutoCommit(false);
				        	//存储公共参数
				        	Map<String,String> publicParam=new HashMap<String,String>();
				        	  
				        	for(int i=0;i<mulParamJA.size();i++){
				        		JSONObject temp=(JSONObject) mulParamJA.get(i);
				        		String type=temp.getString("type");
				        		String tableName=temp.getString("tableName");
				        		String param=temp.getString("param");
				        		//替换参数中的值
				        		for (Entry<String, String> entry : publicParam.entrySet()) {  
					        	    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
					        		if(param.contains(entry.getKey())){
					        			param.replaceAll(entry.getKey(), entry.getValue());
					        		}
					        	}
				        		if(type.equals("insert")){
				        			String sql=insert(param, tableName);
				        			int pKey=cp.insert(connection, sql, null);
				        			if(pKey>0){
				        				publicParam.put(tableName+"_pKey", pKey+"");
				        			}
				        			logger.info("事物执行SQL："+sql);
				        		}
				        		else if(type.equals("update")){
				        			String condition=temp.getString("condition");
				        			//替换参数中的值
					        		for (Entry<String, String> entry : publicParam.entrySet()) {  
						        	    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
						        		if(condition.contains(entry.getKey())){
						        			condition.replaceAll(entry.getKey(), entry.getValue());
						        		}
						        	}
				        			String sql=update(param,condition, tableName);
				        			int result =cp.execute(connection, sql, null);
				        			logger.info("事物执行SQL："+sql+",更新"+result+"条记录。");
				        		}
				        		else if(type.equals("delete")){
				        			String sql=delete(param, tableName);
				        			int result =cp.execute(connection, sql, null);
				        			logger.info("事物执行SQL："+sql+",删除"+result+"条记录。");
				        		}
				        		else{
				        			 
				        		}
				        		
				        		 
				        		
				        	}
						
							connection.commit();//commit the transaction;
							connection.setAutoCommit(true); 

							resultJO.put("status", "0");
							resultJO.put("msg", "事物执行成功！");
							  
						 
						} catch (Exception e) {
							// TODO Auto-generated catch block
							connection.rollback();
							resultJO.put("status", "-1");
							resultJO.put("msg", "出现异常，事物回滚！");
							logger.info("出现异常，事物回滚！");
							e.printStackTrace();
							
							
						}
						 
					}
					else{
						resultJO.put("status", "-1");
						resultJO.put("msg", "参数格式不正确！");
						logger.info("删除失败！");
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultJO.put("status", "-1");
					resultJO.put("msg", "事务执行失败！");
					logger.info("事务执行失败！"+e.getLocalizedMessage());
					e.printStackTrace();
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				resultJO.put("status", "-1");
				resultJO.put("msg", "数据源连接信息格式错误！");
				logger.info("数据源连接信息格式错误！");
				e.printStackTrace();
			}
			
		}
		return resultJO.toString();
	}
	
	
}
