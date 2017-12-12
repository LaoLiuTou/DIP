package com.lt.dip.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.lt.dip.servlet.DataSource;

public class JdbcUtils {

	
	/**
	* 创建数据库
	* 
	* @param dbHost
	* @param dbName
	* @param dbPort
	* @param dbUser
	* @param dbPassword
	* @throws Exception
	*/
	public static String  createDb(String dbType, String dbHost,String dbPort,String dbName,
			String dbUser,String dbPassword) throws Exception {
		String resultstr="创建数据库失败！";
		try {
			
			if(dbType.equals("oracle")){
				
        	}
        	else if(dbType.equals("sqlserver")){
        		Class.forName("com.mysql.jdbc.Driver");
    			Class.forName("com.mysql.jdbc.Driver").newInstance();
    			String connStr = "jdbc:mysql://" + dbHost + ":" + dbPort + "?user="
    			+ dbUser + "&password=" + dbPassword
    			+ "&characterEncoding=UTF8";
    			Connection conn = DriverManager.getConnection(connStr);
    			Statement stat = conn.createStatement();
    			if (conn != null) {   
    				 System.out.println("连接成功！");
    			}
    			String sql = "SELECT * FROM MASTER.DBO.SYSDATABASES WHERE NAME='"+dbName+"'";
    			PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
    			ResultSet rs = pstmt.executeQuery();
    			//rs.last();  rs.getRow()>0
    			//int col = rs.getMetaData().getColumnCount();
    		    if(rs.next()){
    				resultstr="数据库已存在！"; 
    		    }
    		    else{
    				sql = "CREATE DATABASE " + dbName ;
    				stat.execute(sql);
    				resultstr="创建数据库成功！";
    		    }
    			stat.close();
    			conn.close();
        	}
        	else if(dbType.equals("mysql")){
        		Class.forName("com.mysql.jdbc.Driver");
    			Class.forName("com.mysql.jdbc.Driver").newInstance();
    			String connStr = "jdbc:mysql://" + dbHost + ":" + dbPort + "?user="
    			+ dbUser + "&password=" + dbPassword
    			+ "&characterEncoding=UTF8";
    			Connection conn = DriverManager.getConnection(connStr);
    			Statement stat = conn.createStatement();
    			if (conn != null) {   
    				 System.out.println("连接成功！");
    			}
    			String sql = "SELECT * FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME='"+dbName+"'";
    			PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
    			ResultSet rs = pstmt.executeQuery();
    			//rs.last();  rs.getRow()>0
    			//int col = rs.getMetaData().getColumnCount();
    		    if(rs.next()){
    				resultstr="数据库已存在！"; 
    		    }
    		    else{
    				sql = "CREATE DATABASE " + dbName + " CHARACTER SET UTF8";
    				stat.execute(sql);
    				resultstr="创建数据库成功！";
    		    }
    			stat.close();
    			conn.close();
        	}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return resultstr;
	}
	/**
	 * 删除数据库
	 * 
	 * @param dbHost
	 * @param dbName
	 * @param dbPort
	 * @param dbUser
	 * @param dbPassword
	 * @throws Exception
	 */
	public static String  dropDb(String dbHost,String dbPort,String dbName,
			String dbUser,String dbPassword) throws Exception {
		String resultstr="删除数据库失败！";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String connStr = "jdbc:mysql://" + dbHost + ":" + dbPort + "?user="
					+ dbUser + "&password=" + dbPassword
					+ "&characterEncoding=UTF8";
			Connection conn = DriverManager.getConnection(connStr);
			Statement stat = conn.createStatement();
			if (conn != null) {   
				System.out.println("连接成功！");
			}
			String sql = "SELECT * FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME='"+dbName+"'";
			PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			//rs.last();  rs.getRow()>0
			//int col = rs.getMetaData().getColumnCount();
			if(rs.next()){
				sql = "drop database " + dbName;
				stat.execute(sql);
				resultstr="数据库已删除！"; 
			}
			else{
				
				resultstr="数据库不存在！";
			}
			stat.close();
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultstr;
	}
	
	
	/**
	 * 新建数据表
	 * @param (dbInfo,param,tableName)
	 * @return
	 */
	public static String createTable(String dbInfo,String param,String tableName){
		Logger logger = Logger.getLogger("DipLogger");
		JSONObject resultJO=new JSONObject();
		logger.info("数据库信息："+dbInfo);
		logger.info("数据库操作-新建实体("+tableName+")");
		if(dbInfo==null||dbInfo.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递dbInfo参数,请给出数据源连接信息");
			logger.info("没有传递dbInfo参数,请给出数据源连接信息");
		}
		else if(tableName==null||tableName.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递tableName参数,请给出数据实体名称");
			logger.info("没有传递tableName参数,请给出数据实体名称");
		}
		else{
			try {
				JSONObject dbJO = JSONObject.fromObject(dbInfo);
				try {
					String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
					ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
							dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
							dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
					String createSql = "CREATE TABLE "+tableName;
					if(param!=null&&!param.equals("")){
						JSONArray  paramJA = JSONArray.fromObject(param);
						//参数
						List<String> paramList = new ArrayList<String>();
						//是否有错误参数
						boolean flag=true;
						//动态添加新建参数
						for(int index=0;index<paramJA.size();index++){
							JSONObject paramJO = (JSONObject) paramJA.get(index);
							String colStr = "";
						     
							if(paramJO!=null){
								//列名
								 if(paramJO.containsKey("cn")&&!paramJO.getString("cn").equals("")){
									 colStr+=paramJO.getString("cn")+" ";
								 }else{
									 flag=false;
									 break;
								 }
								 //数据类型/长度
								 if(paramJO.containsKey("tp")&&paramJO.containsKey("lt")&&
										 !paramJO.getString("tp").equals("")&&!paramJO.getString("lt").equals("")){
									 colStr+=paramJO.getString("tp")+"("+paramJO.getString("lt")+") ";
								 }else{
									 flag=false;
									 break;
								 }
								 //主键 Y/N
								 if(paramJO.containsKey("pk")&&paramJO.getString("pk").equals("Y")){
									 colStr+=" PRIMARY KEY ";
								 }
								 //非空 Y/N
								 if(paramJO.containsKey("nn")&&paramJO.getString("nn").equals("Y")){
									 colStr+=" NOT NULL ";
								 }
								 //自增 Y/N
								 if(paramJO.containsKey("ai")&&paramJO.getString("ai").equals("Y")){
									 colStr+=" AUTO_INCREMENT ";
								 }
								 //备注
								 if(paramJO.containsKey("cm")){
									 colStr+=" COMMENT '"+paramJO.getString("cm")+"'";
								 }
								 paramList.add(colStr) ;
							}
						}
						if(flag){
							createSql+="("+StringUtils.join(paramList,",")+")";
							cp.execute(c3p0Key, createSql, null);
							resultJO.put("status", "0");
							resultJO.put("msg", "创建成功！");
							logger.info("创建数据表"+tableName+"成功！");
						}
						else{
							resultJO.put("status", "-1");
							resultJO.put("msg", "参数格式不正确！");
						}
					}
					else{
						resultJO.put("status", "-1");
						resultJO.put("msg", "参数格式不正确！");
						logger.info("查询失败！");
					}
					//日志
					logger.info("执行SQL："+createSql);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultJO.put("status", "-1");
					resultJO.put("msg", "新建失败！");
					logger.info("新建失败！"+e.getLocalizedMessage());
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
	/**
	 * 删除数据表
	 * @param (dbInfo,param,tableName)
	 * @return
	 */
	public static String deleteTable(String dbInfo,String tableName){
		Logger logger = Logger.getLogger("DipLogger");
		JSONObject resultJO=new JSONObject();
		logger.info("数据库信息："+dbInfo);
		logger.info("数据库操作-删除实体("+tableName+")");
		if(dbInfo==null||dbInfo.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递dbInfo参数,请给出数据源连接信息");
			logger.info("没有传递dbInfo参数,请给出数据源连接信息");
		}
		else if(tableName==null||tableName.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递tableName参数,请给出数据实体名称");
			logger.info("没有传递tableName参数,请给出数据实体名称");
		}
		else{
			try {
				JSONObject dbJO = JSONObject.fromObject(dbInfo);
				try {
					String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
					ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
							dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
							dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
					String dropSql = "DROP TABLE "+tableName;
					cp.execute(c3p0Key, dropSql, null);
					resultJO.put("status", "0");
					resultJO.put("msg", "删除成功！");
					//日志
					logger.info("执行SQL："+dropSql);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultJO.put("status", "-1");
					resultJO.put("msg", "删除失败！");
					logger.info("删除失败！"+e.getLocalizedMessage());
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
	
	

	/**
	 * 查询数据
	 * @param (dbInfo,param,tableName)
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String query(String dbInfo,String param,String tableName){
		Logger logger = Logger.getLogger("DipLogger");
		JSONObject resultJO=new JSONObject();
		logger.info("数据库信息："+dbInfo);
		logger.info("数据库操作-查询数据("+tableName+")");
		if(dbInfo==null||dbInfo.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递dbInfo参数,请给出数据源连接信息");
			logger.info("没有传递dbInfo参数,请给出数据源连接信息");
		}
		else if(tableName==null||tableName.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递tableName参数,请给出数据实体名称");
			logger.info("没有传递tableName参数,请给出数据实体名称");
		}
		else{
			try {
				JSONObject dbJO = JSONObject.fromObject(dbInfo);
				try {
					String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
					ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
			        		dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
			        		dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
					
					String countSql = "SELECT COUNT(*) FROM "+tableName+" ";
					String querySql = "SELECT * FROM "+tableName+" ";
					 
					/*String countSql = "select count(ID) from datasources where PROJECTS_ID=?,unix_timestamp(C_DT) >= unix_timestamp(?)," +
							"unix_timestamp(C_DT) <= unix_timestamp(?),unix_timestamp(UP_DT) >= unix_timestamp(?)," +
							"unix_timestamp(UP_DT) <= unix_timestamp(?),DS_JSON=?,C_ID=?";*/
					if(param!=null&&!param.equals("")){
						 
						JSONObject  paramJO = JSONObject.fromObject(param);
						//动态添加查询参数
						//StringBuffer paramSb = new StringBuffer(" where ");
						if(paramJO!=null){
							
							//分页
							if(paramJO.containsKey("page")&&paramJO.containsKey("size")){
								String page=paramJO.getString("page");
								String size=paramJO.getString("size");
								if(dbJO.getString("dbType").equals("oracle")){
							    	querySql="SELECT * FROM ( SELECT A.*, ROWNUM RN FROM (SELECT * FROM "+tableName+" ";
					        	}
					        	else if(dbJO.getString("dbType").equals("sqlserver")){
					        		querySql="SELECT * FROM ( SELECT TOP ("+Integer.parseInt(size)+") * FROM (SELECT TOP ("+
					        				Integer.parseInt(page)*Integer.parseInt(size)+") * FROM "+tableName+" ";
					        	}
					        	else if(dbJO.getString("dbType").equals("mysql")){
					        		 
					        	}
							}
							
							
							List<String> paramList = new ArrayList<String>();
							Iterator iterator = paramJO.keys();
							while(iterator.hasNext()){
								String key=(String) iterator.next();
								if(!key.equals("order")&&!key.equals("page")&&!key.equals("size")){
									String value=paramJO.getString(key);
									if(value.startsWith("%|")){
										paramList.add(key+" LIKE '"+value.split("\\|")[1]+"' ");
									}
									else if(value.startsWith("!|")){
										paramList.add(key+" <> '"+value.split("\\|")[1]+"' ");
									}
									else if(value.startsWith(">|")||value.startsWith("<|")||
											value.startsWith(">=|")||value.startsWith("<=|")){
										paramList.add(key+value.split("\\|")[0]+" '"+value.split("\\|")[1]+"' ");
									}
									else{
										paramList.add(key+"='"+value+"' ");
									}
									
								}
							}
							if(paramList.size()>0){
								countSql+=" WHERE "+StringUtils.join(paramList," AND ");
								querySql+=" WHERE "+StringUtils.join(paramList," AND ");
							}
							
							//排序
							if(paramJO.containsKey("order")){
								querySql+=paramJO.getString("order");
							}
							/*else{
								querySql+=" ORDER BY ID DESC ";
							}*/
							//分页
							if(paramJO.containsKey("page")&&paramJO.containsKey("size")){
								String page=paramJO.getString("page");
								String size=paramJO.getString("size");
								if(CommonUtils.isNumeric(page)&&CommonUtils.isNumeric(size)){
									if(dbJO.getString("dbType").equals("oracle")){
										querySql+=" ) A WHERE ROWNUM <= "+Integer.parseInt(page)*Integer.parseInt(size)+
												" )   WHERE RN > "+(Integer.parseInt(page)-1)*Integer.parseInt(size)+" ";
							    	}
							    	else if(dbJO.getString("dbType").equals("sqlserver")){
							    		//querySql+=" ) TEMP1 ORDER BY TEMP1."+pKey.toUpperCase()+" ASC )  TEMP2 ";
							    		querySql+=" ) TEMP1 )  TEMP2 ";
							    		querySql+="		ORDER BY TEMP2.SMSINDEX DESC ";
							    	}
							    	else if(dbJO.getString("dbType").equals("mysql")){
							    		querySql+=" LIMIT "+(Integer.parseInt(page)-1)*Integer.parseInt(size)+" , "+size;
							    	}
									 
								}
							}
						}
					}
					
					//查询总记录数
					int count = cp.count(c3p0Key, countSql, null);
					List<Map<String, String>> resultMap = cp.queryForMap(c3p0Key, querySql, null);
					JSONObject tempJO = new JSONObject();
					tempJO.put("num", count);
					tempJO.put("data", JSONArray.fromObject(resultMap));
					
					resultJO.put("status", "0");
					resultJO.put("msg", tempJO);
					//日志
					logger.info("执行SQL："+querySql);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultJO.put("status", "-1");
					resultJO.put("msg", "查询失败！");
					logger.info("查询失败！"+e.getLocalizedMessage());
					e.printStackTrace();
				}
				finally{
					//cp.removeConnection(c3p0Key, null);
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
	
	/**
	 * 新增数据
	 * @param (dbInfo,param,tableName)
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String insert(String dbInfo,String param,String tableName){
		Logger logger = Logger.getLogger("DipLogger");
		JSONObject resultJO=new JSONObject();
		logger.info("数据库信息："+dbInfo);
		logger.info("数据库操作-新建数据("+tableName+")");
		if(dbInfo==null||dbInfo.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递dbInfo参数,请给出数据源连接信息");
			logger.info("没有传递dbInfo参数,请给出数据源连接信息");
		}
		else if(tableName==null||tableName.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递tableName参数,请给出数据实体名称");
			logger.info("没有传递tableName参数,请给出数据实体名称");
		}
		else{
			try {
				JSONObject dbJO = JSONObject.fromObject(dbInfo);
				try {
					String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
					ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
							dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
							dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
					
					String insertSql = "INSERT INTO "+tableName+" ";
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
							//返回自增主键
							int result=cp.insert(c3p0Key, insertSql);
							
							resultJO.put("status", "0");
							resultJO.put("msg", result);
							//日志
							logger.info("执行SQL："+insertSql);
					 
						}
					}
					else{
						resultJO.put("status", "-1");
						resultJO.put("msg", "参数格式不正确！");
						logger.info("新建失败！");
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultJO.put("status", "-1");
					resultJO.put("msg", "新建失败！");
					logger.info("新建失败！"+e.getLocalizedMessage());
					e.printStackTrace();
				}
				finally{
					//cp.removeConnection(c3p0Key, null);
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
	/**
	 * 更新数据
	 * @param (dbInfo,param,condition,tableName)
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String update(String dbInfo,String param,String condition,String tableName){
		Logger logger = Logger.getLogger("DipLogger");
		JSONObject resultJO=new JSONObject();
		logger.info("数据库信息："+dbInfo);
		logger.info("数据库操作-修改数据("+tableName+")");
		if(dbInfo==null||dbInfo.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递dbInfo参数,请给出数据源连接信息");
			logger.info("没有传递dbInfo参数,请给出数据源连接信息");
		}
		else if(tableName==null||tableName.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递tableName参数,请给出数据实体名称");
			logger.info("没有传递tableName参数,请给出数据实体名称");
		}
		else{
			try {
				JSONObject dbJO = JSONObject.fromObject(dbInfo);
				try {
					String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
					ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
							dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
							dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
					
					String updateSql = "UPDATE "+tableName+" ";
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
								
								int result=cp.execute(c3p0Key, updateSql,null); 
								logger.info("修改数据"+result+"条。");
								resultJO.put("status", "0");
								resultJO.put("msg", "更新成功！");
								//日志
								logger.info("执行SQL："+updateSql);
								
							}
						}
					}
					else{
						resultJO.put("status", "-1");
						resultJO.put("msg", "参数格式不正确！");
						logger.info("更新失败！");
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultJO.put("status", "-1");
					resultJO.put("msg", "更新失败！");
					logger.info("更新失败！"+e.getLocalizedMessage());
					e.printStackTrace();
				}
				finally{
					//cp.removeConnection(c3p0Key, null);
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
	
	
	/**
	 * 删除数据
	 * @param (dbInfo,param,tableName)
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String delete(String dbInfo,String param,String tableName){
		Logger logger = Logger.getLogger("DipLogger");
		JSONObject resultJO=new JSONObject();
		logger.info("数据库信息："+dbInfo);
		logger.info("数据库操作-删除数据("+tableName+")");
		if(dbInfo==null||dbInfo.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递dbInfo参数,请给出数据源连接信息");
			logger.info("没有传递dbInfo参数,请给出数据源连接信息");
		}
		else if(tableName==null||tableName.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递tableName参数,请给出数据实体名称");
			logger.info("没有传递tableName参数,请给出数据实体名称");
		}
		else{
			try {
				JSONObject dbJO = JSONObject.fromObject(dbInfo);
				try {
					String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
					ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
							dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
							dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
					
					String deleteSql = "DELETE FROM "+tableName+" ";
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
							int result=cp.execute(c3p0Key, deleteSql,null);
							logger.info("删除数据"+result+"条。");
							resultJO.put("status", "0");
							resultJO.put("msg", "删除成功！");
							  
							//日志
							logger.info("执行SQL："+deleteSql);
					 
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
					resultJO.put("msg", "删除失败！");
					logger.info("删除失败！"+e.getLocalizedMessage());
					e.printStackTrace();
				}
				finally{
					//cp.removeConnection(c3p0Key, null);
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
	
	/**
	 * 根据id查询数据源信息
	 * @param dbs_id
	 * @return
	 */
	public static String getDbInfoByDatid(String dat_id){
		Logger logger = Logger.getLogger("DipLogger");
		String result="";
		try {
			Properties props = new Properties();  
	      	props.load(DataSource.class.getClassLoader().getResourceAsStream("dbpool.properties"));  
			JSONObject dbJO=new JSONObject();
			dbJO.accumulate("dbType", props.getProperty("dbType").trim());
			dbJO.accumulate("dbUser", props.getProperty("dbUser").trim());
			dbJO.accumulate("dbPassword", props.getProperty("dbPassword").trim());
			dbJO.accumulate("dbHost", props.getProperty("dbHost").trim());
			dbJO.accumulate("dbPort", props.getProperty("dbPort").trim());
			dbJO.accumulate("dbName", props.getProperty("dbName").trim());
			
			String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
			ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
	        		dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
	        		dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
			String querySql = "SELECT * FROM SYS_DATASOURCES WHERE ID="+dat_id;
			List<Map<String, String>> resultMap = cp.queryForMap(c3p0Key, querySql, null);
			if(resultMap.size()>0){
				
				result=resultMap.get(0).get("ds_json");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("本地数据源配置错误！");
			e.printStackTrace();
		} 
		
		return result;
	}
	/**
	 * 根据实体id查询数据源信息
	 * @param dbs_id
	 * @return
	 */
	public static String getDbInfoByEid(String e_id){
		Logger logger = Logger.getLogger("DipLogger");
		String result="";
		try {
			Properties props = new Properties();  
	      	props.load(DataSource.class.getClassLoader().getResourceAsStream("dbpool.properties"));  
			JSONObject dbJO=new JSONObject();
			dbJO.accumulate("dbType", props.getProperty("dbType").trim());
			dbJO.accumulate("dbUser", props.getProperty("dbUser").trim());
			dbJO.accumulate("dbPassword", props.getProperty("dbPassword").trim());
			dbJO.accumulate("dbHost", props.getProperty("dbHost").trim());
			dbJO.accumulate("dbPort", props.getProperty("dbPort").trim());
			dbJO.accumulate("dbName", props.getProperty("dbName").trim());
			
			String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
			ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
	        		dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
	        		dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
			String querySql = "SELECT * FROM SYS_ENTITIES WHERE ID="+e_id;
			List<Map<String, String>> resultDMap = cp.queryForMap(c3p0Key, querySql, null);
			if(resultDMap.size()>0){ 
				querySql = "SELECT * FROM SYS_DATASOURCES WHERE ID="+resultDMap.get(0).get("dat_id");
				List<Map<String, String>> resultEMap = cp.queryForMap(c3p0Key, querySql, null);
				if(resultEMap.size()>0){ 
					result=resultEMap.get(0).get("ds_json");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("本地数据源配置错误！");
			e.printStackTrace();
		} 
		
		return result;
	}
	
	/**
	 * 登录获取token
	 * @param dbs_id
	 * @return
	 */
	public static String getToken(String username,String password){
		Logger logger = Logger.getLogger("DipLogger");
		JSONObject resultJO=new JSONObject();
		try {
			Properties props = new Properties();  
	      	props.load(DataSource.class.getClassLoader().getResourceAsStream("dbpool.properties"));  
			JSONObject dbJO=new JSONObject();
			dbJO.accumulate("dbType", props.getProperty("dbType").trim());
			dbJO.accumulate("dbUser", props.getProperty("dbUser").trim());
			dbJO.accumulate("dbPassword", props.getProperty("dbPassword").trim());
			dbJO.accumulate("dbHost", props.getProperty("dbHost").trim());
			dbJO.accumulate("dbPort", props.getProperty("dbPort").trim());
			dbJO.accumulate("dbName", props.getProperty("dbName").trim());
			
			String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
			ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
	        		dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
	        		dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
			String querySql = "SELECT * FROM SYS_MEMBERS WHERE USERNAME='"+username+"'";
			List<Map<String, String>> resultDMap = cp.queryForMap(c3p0Key, querySql, null);
			if(resultDMap.size()>0){ 
				String localpass=resultDMap.get(0).get("userpwd");
				if(localpass.toLowerCase().equals(MD5Encryption.getEncryption(password).toLowerCase())){
					String userToken= UUID.randomUUID().toString();
					resultJO.put("status", "0");
					resultJO.put("msg", userToken); 
  					TokenUtils.add(userToken, System.currentTimeMillis()+"");
  					logger.info("用户"+username+"登录成功！分配Token："+userToken);
				}
				else{
					resultJO.put("status", "-1");
					resultJO.put("msg","密码错误！"); 
					logger.info("用户"+username+"登录失败！错误密码："+password);
				}
				 
			}
			else{
				resultJO.put("status", "-1");
				resultJO.put("msg","用户不存在！"); 
				logger.info("用户"+username+"登录失败！不存在的用户！");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			resultJO.put("status", "-1");
			resultJO.put("msg","查询用户异常！"); 
			logger.info("本地数据源配置错误！");
			e.printStackTrace();
		} 
		
		return resultJO.toString();
	}
	
	
	
	/**
	 * 查询数据表的列
	 * @param (dbInfo,param,tableName)
	 * @return
	 */
	public static String selectColumn(String dbInfo,String tableName){
		Logger logger = Logger.getLogger("DipLogger");
		JSONObject resultJO=new JSONObject();
		logger.info("数据库信息："+dbInfo);
		logger.info("数据库操作-查询数据表的列("+tableName+")");
		if(dbInfo==null||dbInfo.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递dbInfo参数,请给出数据源连接信息");
			logger.info("没有传递dbInfo参数,请给出数据源连接信息");
		}
		else if(tableName==null||tableName.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递tableName参数,请给出数据实体名称");
			logger.info("没有传递tableName参数,请给出数据实体名称");
		}
		else{
			try {
				JSONObject dbJO = JSONObject.fromObject(dbInfo);
				try {
					String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
					ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
							dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
							dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
					String selectSql ="";
					
					if(dbJO.getString("dbType").equals("oracle")){
						selectSql="SELECT T.COLUMN_NAME,T.DATA_TYPE,C.COMMENTS FROM USER_TAB_COLUMNS T,USER_COL_COMMENTS C " +
					    		"WHERE T.TABLE_NAME = C.TABLE_NAME AND T.COLUMN_NAME = C.COLUMN_NAME AND T.TABLE_NAME ='"+tableName+"'";
			    	}
			    	else if(dbJO.getString("dbType").equals("sqlserver")){
			    		selectSql="SELECT A.NAME COLUMN_NAME,  B.NAME AS DATA_TYPE,  CAST(G.[VALUE] AS VARCHAR(500)) AS COLUMN_COMMENT FROM SYSCOLUMNS A LEFT JOIN SYSTYPES B ON A.XTYPE=B.XUSERTYPE "+
            					"INNER JOIN SYSOBJECTS D ON A.ID=D.ID AND D.XTYPE='U' AND D.NAME<>'DTPROPERTIES' LEFT JOIN SYS.EXTENDED_PROPERTIES G "+
            					"ON A.ID=G.MAJOR_ID AND A.COLID = G.MINOR_ID WHERE D.[NAME] ='"+tableName+"' ORDER BY A.ID,A.COLORDER ";
			    	}
			    	else if(dbJO.getString("dbType").equals("mysql")){
			    		selectSql ="SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '"
				        		+tableName+"' AND TABLE_SCHEMA ='"+dbJO.getString("dbName")+"'";
			    	}
					
						
					List<Map<String, String>> resultMap = cp.queryForMap(c3p0Key, selectSql, null);
					
					resultJO.put("status", "0");
					resultJO.put("msg", JSONArray.fromObject(resultMap));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultJO.put("status", "-1");
					resultJO.put("msg", "查询失败！");
					logger.info("查询失败！"+e.getLocalizedMessage());
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
	
	/**
	 * 查询数据表的列
	 * @param (dbInfo,param,tableName)
	 * @return
	 */
	public static String selectPKey(String dbInfo,String tableName){
		Logger logger = Logger.getLogger("DipLogger");
		JSONObject resultJO=new JSONObject();
		logger.info("数据库信息："+dbInfo);
		logger.info("数据库操作-查询数据表的主键("+tableName+")");
		if(dbInfo==null||dbInfo.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递dbInfo参数,请给出数据源连接信息");
			logger.info("没有传递dbInfo参数,请给出数据源连接信息");
		}
		else if(tableName==null||tableName.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递tableName参数,请给出数据实体名称");
			logger.info("没有传递tableName参数,请给出数据实体名称");
		}
		else{
			try {
				JSONObject dbJO = JSONObject.fromObject(dbInfo);
				try {
					String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
					ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
							dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
							dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
					String selectSql ="";
					
					if(dbJO.getString("dbType").equals("oracle")){
						selectSql="SELECT CU.* FROM USER_CONS_COLUMNS CU, USER_CONSTRAINTS AU " +
				        		"WHERE CU.CONSTRAINT_NAME = AU.CONSTRAINT_NAME AND AU.CONSTRAINT_TYPE = 'P' AND AU.TABLE_NAME ='"+tableName+"'";
					}
					else if(dbJO.getString("dbType").equals("sqlserver")){
						selectSql="SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_NAME='"+tableName+"'";
					}
					else if(dbJO.getString("dbType").equals("mysql")){
						selectSql ="SELECT COLUMN_KEY,COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='"
				        		+tableName+"' AND COLUMN_KEY='PRI'";
					}
					
					
					List<Map<String, String>> resultMap = cp.queryForMap(c3p0Key, selectSql, null);
					if(resultMap.size()>0){
						
						resultJO.put("status", "0");
						resultJO.put("msg", resultMap.get(0).get("column_name"));
					}
					else{
						resultJO.put("status", "-1");
						resultJO.put("msg", "不存在主键！");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultJO.put("status", "-1");
					resultJO.put("msg", "查询失败！");
					logger.info("查询失败！"+e.getLocalizedMessage());
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
	/**
	 * 备份数据库
	 * @param (dbInfo,param,tableName)
	 * @return
	 */
	public static String backUp(String dbInfo){
		Logger logger = Logger.getLogger("DipLogger");
		JSONObject resultJO=new JSONObject();
		logger.info("数据库信息："+dbInfo);
		logger.info("数据库操作-备份数据库");
		if(dbInfo==null||dbInfo.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递dbInfo参数,请给出数据源连接信息");
			logger.info("没有传递dbInfo参数,请给出数据源连接信息");
		}
		else{
			try {
				JSONObject dbJO = JSONObject.fromObject(dbInfo);
				if(dbJO.getString("dbType").equals("oracle")){
					resultJO.put("status", "-1");
					resultJO.put("msg", "正在开发中！");
	        	}
	        	else if(dbJO.getString("dbType").equals("sqlserver")){
	        		resultJO.put("status", "-1");
					resultJO.put("msg", "正在开发中！");
	        	}
	        	else if(dbJO.getString("dbType").equals("mysql")){
	        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        		Properties props = new Properties();  
	    	      	props.load(DataSource.class.getClassLoader().getResourceAsStream("dbpool.properties"));  
	    			String backupDir=props.getProperty("backupDir")+"/"+sdf.format(new Date());
	    			String fileName=UUID.randomUUID()+".sql";
	    			
	        		if (exportMysql(dbJO.getString("dbHost"),dbJO.getString("dbUser"),dbJO.getString("dbPassword"), 
	        				backupDir, fileName, dbJO.getString("dbName"))) {  
	        			resultJO.put("status", "0");
	    				resultJO.put("msg", backupDir+"/"+fileName);  
	                } else { 
	                	resultJO.put("status", "-1");
	    				resultJO.put("msg", "数据库备份失败！");
	                } 
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
	/**
	 * 还原数据库
	 * @param (dbInfo,param,tableName)
	 * @return
	 */
	public static String restore(String dbInfo,String filePath){
		Logger logger = Logger.getLogger("DipLogger");
		JSONObject resultJO=new JSONObject();
		logger.info("数据库信息："+dbInfo);
		logger.info("数据库操作-恢复数据库");
		if(dbInfo==null||dbInfo.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "没有传递dbInfo参数,请给出数据源连接信息");
			logger.info("没有传递dbInfo参数,请给出数据源连接信息");
		}
		else if(filePath==null||filePath.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "恢复文件路径为空！");
			logger.info("恢复文件路径为空！");
		}
		else{
			try {
				JSONObject dbJO = JSONObject.fromObject(dbInfo);
				if(dbJO.getString("dbType").equals("oracle")){
					resultJO.put("status", "-1");
					resultJO.put("msg", "正在开发中！");
				}
				else if(dbJO.getString("dbType").equals("sqlserver")){
					resultJO.put("status", "-1");
					resultJO.put("msg", "正在开发中！");
				}
				else if(dbJO.getString("dbType").equals("mysql")){
					 
					
					if (importMysql(dbJO.getString("dbHost"),dbJO.getString("dbUser"),dbJO.getString("dbPassword"), 
							filePath, dbJO.getString("dbName"))) {  
						resultJO.put("status", "0");
						resultJO.put("msg", "数据库恢复成功！");  
					} else { 
						resultJO.put("status", "-1");
						resultJO.put("msg", "数据库恢复失败！");
					} 
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
	
	  /** 
     * Mysql数据库导出 
     * @param hostIP MySQL数据库所在服务器地址IP 
     * @param userName 进入数据库所需要的用户名 
     * @param password 进入数据库所需要的密码 
     * @param savePath 数据库导出文件保存路径 
     * @param fileName 数据库导出文件文件名 
     * @param databaseName 要导出的数据库名 
     * @return 返回true表示导出成功，否则返回false。 
     */  
    public static boolean exportMysql(String hostIP, String userName, String password,
    		String savePath, String fileName, String databaseName) throws InterruptedException {  
        
    	Logger logger = Logger.getLogger("DipLogger");
    	File saveFile = new File(savePath);  
        if (!saveFile.exists()) {// 如果目录不存在  
            saveFile.mkdirs();// 创建文件夹  
        }  
        if(!savePath.endsWith(File.separator)){  
            savePath = savePath + File.separator;  
        }  
          
        PrintWriter printWriter = null;  
        BufferedReader bufferedReader = null;  
        try {  
        	
            printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(savePath + fileName), "utf8"));  
            String cmdStr="";
            if(password.equals("")){
            	cmdStr="mysqldump -h" + hostIP + " -u" + userName + " --default-character-set=utf8 --set-charset=1 " + databaseName;
            }
            else{
            	cmdStr="mysqldump -h" + hostIP + " -u" + userName + " -p" + password + " --default-character-set=utf8 --set-charset=1 " + databaseName;
            }
            logger.info("备份命令："+cmdStr);
            Process process = Runtime.getRuntime().exec(cmdStr);  
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "utf8");  
            bufferedReader = new BufferedReader(inputStreamReader);  
            String line;  
            while((line = bufferedReader.readLine())!= null){  
                printWriter.println(line);  
            }  
            printWriter.flush();  
            if(process.waitFor() == 0){//0 表示线程正常终止。  
                return true;  
            }  
        }catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (bufferedReader != null) {  
                    bufferedReader.close();  
                }  
                if (printWriter != null) {  
                    printWriter.close();  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return false;  
    }  
    
    /** 
     * 还原D:db.sql到erp数据库 
     * @return 
     */  
    public static boolean importMysql(String hostIP, String userName, String password,
    		String filePath, String databaseName) {//还原  
    	Logger logger = Logger.getLogger("DipLogger");
        try {  
               
            Runtime rt = Runtime.getRuntime();    
            String cmdStr="";
            if(password.equals("")){
            	cmdStr="mysql -h" + hostIP + " -u" + userName + " "+databaseName;
            }
            else{
            	cmdStr="mysql -h" + hostIP + " -u" + userName + " -p" + password + " "+databaseName;
            }
           
            logger.info("还原命令："+cmdStr);
            logger.info("还原文件路径："+filePath);
            // 调用 mysql 的 cmd:    
            Process child = rt.exec(cmdStr);    
            OutputStream out = child.getOutputStream();//控制台的输入信息作为输出流    
            String inStr;    
            StringBuffer sb = new StringBuffer("");    
            String outStr;    
            BufferedReader br = new BufferedReader(new InputStreamReader(    
                    new FileInputStream(filePath), "utf8"));    
            while ((inStr = br.readLine()) != null) {    
                sb.append(inStr + "\r\n");    
            }    
            outStr = sb.toString();    
        
            OutputStreamWriter writer = new OutputStreamWriter(out, "utf8");    
            writer.write(outStr);    
            // 注：这里如果用缓冲方式写入文件的话，会导致中文乱码，用flush()方法则可以避免    
            writer.flush();    
            // 别忘记关闭输入输出流    
            out.close();    
            br.close();    
            writer.close();    
            return true;     
        } catch (Exception e) {    
            e.printStackTrace();    
        }    
        return false;    
    }     
    
    /** 
     * Oracle数据库导出 
     *  
     * @param hostIP 数据库地址 
     * @param userName 进入数据库所需要的用户名 
     * @param password 进入数据库所需要的密码 
     * @param SID 用户所在的SID 
     * @param savePath 数据库导出文件保存路径 
     * @param fileName 数据库导出文件文件名 
     * @return 返回true表示导出成功，否则返回false。 
     */  
    public static boolean exportOracle(String hostIP,String userName, String password,  String savePath, String fileName,String SID) throws InterruptedException {  
        File saveFile = new File(savePath);  
        if (!saveFile.exists()) {// 如果目录不存在  
            saveFile.mkdirs();// 创建文件夹  
        }  
        try {  
        	//exp anjian/anjian@124.234.102.102/orcl owner=anjian file=D:\anjian.dmp
            Process process = Runtime.getRuntime().exec("exp "+userName+"/"+password+"@"+
        	hostIP+"/"+SID+" owner="+userName+" file="+savePath+"/"+fileName+".dmp");  
            if(process.waitFor() == 0){//0 表示线程正常终止。   
                return true;  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return false;  
    }  
     
}
