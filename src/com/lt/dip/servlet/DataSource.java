package com.lt.dip.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.lt.dip.utils.JdbcUtils;

public class DataSource extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8807607897499407509L;
	/**
	 * 
	 */
	Logger logger = Logger.getLogger("DipLogger");
	private String localDbInfo;
	/**
	 * Constructor of the object.
	 */
	public DataSource() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8"); 
		PrintWriter out = response.getWriter();
		 
		JSONObject resultJO=new JSONObject();
		Class c = this.getClass();//获得当前类的Class对象
		
		String name = request.getParameter("method");//获取方法名
        if(name == null || name.isEmpty()){
        	resultJO.put("status", "-1");
        	resultJO.put("msg", "没有传递method参数,请给出你想调用的方法");
        	logger.info("没有传递method参数,请给出你想调用的方法");
        }
        else{
            Method method = null;
            try {
                //获得Method对象
                method =  c.getMethod(name, HttpServletRequest.class,HttpServletResponse.class);
                try {
                    String mResult=(String) method.invoke(this, request,response);//反射调用方法
                    resultJO = JSONObject.fromObject(mResult);
                    //resultJO.put("status", "0");
                	//resultJO.put("msg", mResult);
                } catch (Exception e) {
                	resultJO.put("status", "-1");
                	resultJO.put("msg", "你调用的方法"+name+",内部发生了异常");
                	logger.info("你调用的方法"+name+",内部发生了异常");
                	e.printStackTrace();
                }
            } catch (Exception e) {
            	resultJO.put("status", "-1");
            	resultJO.put("msg", "没有找到"+name+"方法，请检查该方法是否存在");
            	logger.info("没有找到"+name+"方法，请检查该方法是否存在");
            	e.printStackTrace(); 
            }
        }
		out.print(resultJO.toString());
		out.flush();
		out.close();
	}

	/**
	 * 创建数据源
	 * @param request(dbInfo)
	 * @param response
	 * @return
	 */
	public String create(HttpServletRequest request, HttpServletResponse response){
		JSONObject resultJO=new JSONObject();
		String dbInfo = request.getParameter("dbInfo");//获取数据库信息
		logger.info("数据库信息："+dbInfo);
		logger.info("数据库操作-新建数据库");
		if(dbInfo==null||dbInfo.equals("")){
			resultJO.put("status", "-1");
        	resultJO.put("msg", "没有传递dbInfo参数,请给出数据源连接信息");
        	logger.info("没有传递dbInfo参数,请给出数据源连接信息");
		}
		else{
			try {
				JSONObject dbJO = JSONObject.fromObject(dbInfo);
				try {
					//创建数据库
					String createDBStr=JdbcUtils.createDb(dbJO.getString("dbHost"), dbJO.getString("dbPort"), dbJO.getString("dbName"),
							dbJO.getString("dbUser"), dbJO.getString("dbPassword"));
					
					////////////////////////
					if(createDBStr.equals("创建数据库成功！")){
						//同时在数据源表中插入数据
						String mem_id = request.getParameter("mem_id"); 
						String status = request.getParameter("status"); 
						String nm_t = request.getParameter("nm_t"); 
						String pro_id = request.getParameter("pro_id"); 
						JSONObject localParam=new JSONObject();
						localParam.put("mem_id", mem_id);
						localParam.put("status", status);
						localParam.put("nm_t", nm_t);
						localParam.put("pro_id", pro_id);
						localParam.put("ds_json", dbInfo);
						String localTN="datasources";
						JSONObject insertResult=JSONObject.fromObject(
								JdbcUtils.insert(localDbInfo, localParam.toString(), localTN));
						
						if(insertResult.getString("status").equals("0")){
							resultJO.put("status", "0");
							resultJO.put("msg", createDBStr);
						}
						else{
							JdbcUtils.dropDb(dbJO.getString("dbHost"), dbJO.getString("dbPort"), dbJO.getString("dbName"),
									dbJO.getString("dbUser"), dbJO.getString("dbPassword"));
							resultJO.put("status", "-1");
							resultJO.put("msg", "保存数据源出错！");
						}
					}
					else{
						resultJO.put("status", "-1");
						resultJO.put("msg", createDBStr);
					}
		        	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultJO.put("status", "-1");
		        	resultJO.put("msg", "创建数据库异常！");
		        	logger.info("创建数据库异常！");
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
	 * 删除数据库
	 * @param request(dbInfo)
	 * @param response
	 * @return
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response){
		 
		JSONObject resultJO=new JSONObject();
		//通过id查询数据源信息
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		logger.info("数据库信息："+dbInfo);
		logger.info("数据库操作-删除数据库");
		if(dbInfo==null||dbInfo.equals("")){
			resultJO.put("status", "-1");
			resultJO.put("msg", "未查询到dbInfo参数");
			logger.info("未查询到dbInfo参数");
		}
		else{
			try {
				JSONObject dbJO = JSONObject.fromObject(dbInfo);
				try {
					//删除本地表中记录
					String localTN="datasources";
					JSONObject  paramJO = new JSONObject();
					paramJO.put("id", dat_id);
					JSONObject dropResult=JSONObject.fromObject(
							JdbcUtils.delete(localDbInfo, paramJO.toString(), localTN));
					if(dropResult.getString("status").equals("0")){
						//删除实体数据库
						String dropDBStr=JdbcUtils.dropDb(dbJO.getString("dbHost"), dbJO.getString("dbPort"), dbJO.getString("dbName"),
								dbJO.getString("dbUser"), dbJO.getString("dbPassword"));
						if(dropDBStr.equals("数据库已删除！")){
							resultJO.put("status", "0");
						}
						else{
							resultJO.put("status", "-1");
						}
						resultJO.put("msg", dropDBStr);
					}
					else{
						resultJO=dropResult;
					}
				 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultJO.put("status", "-1");
					resultJO.put("msg", "删除数据库异常！");
					logger.info("删除数据库异常！");
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
	 * 查询列表
	 * @param request(dbInfo,param)
	 * @param response
	 * @return
	 */
	public String query(HttpServletRequest request, HttpServletResponse response){
		
		//String dbs_id = request.getParameter("dbs_id");//获取数据库信息
		//String dbInfo = JdbcUtils.getDbInfoByDbsid(dbs_id);//获取数据库信息
		String param = request.getParameter("param");//获取数据库信息
		return JdbcUtils.query(localDbInfo, param, "DATASOURCES");
	}
	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		
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
			localDbInfo=dbJO.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("本地数据源配置错误！");
			e.printStackTrace();
		} 
				
	}

}
