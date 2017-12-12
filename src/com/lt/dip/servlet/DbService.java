package com.lt.dip.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.lt.dip.utils.JdbcUtils;

public class DbService extends HttpServlet {
	
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = -4958352598894257551L;
	Logger logger = Logger.getLogger("DipLogger");
	/**
	 * Constructor of the object.
	 */
	public DbService() {
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
	 * 查询列表
	 * @param request(dbInfo,param,tableName)
	 * @param response
	 * @return
	 */
	public String query(HttpServletRequest request, HttpServletResponse response){
		
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		String param = request.getParameter("param"); 
		String tableName = request.getParameter("tableName"); 
		return JdbcUtils.query(dbInfo, param, tableName);
	}
	/**
	 * 插入数据
	 * @param request(dbInfo,param,tableName)
	 * @param response
	 * @return
	 */
	public String insert(HttpServletRequest request, HttpServletResponse response){
		
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		String param = request.getParameter("param"); 
		String tableName = request.getParameter("tableName"); 
		return JdbcUtils.insert(dbInfo, param, tableName);
	}
	
	/**
	 * 修改数据
	 * @param request(dbInfo,param,tableName)
	 * @param response
	 * @return
	 */
	public String update(HttpServletRequest request, HttpServletResponse response){
		
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		String param = request.getParameter("param"); 
		String condition = request.getParameter("condition"); 
		String tableName = request.getParameter("tableName"); 
		return JdbcUtils.update(dbInfo, param,condition, tableName);
	}
	/**
	 * 删除数据
	 * @param request(dbInfo,param,tableName)
	 * @param response
	 * @return
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response){
		
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		//String param = request.getParameter("param"); 
		String condition = request.getParameter("condition"); 
		String tableName = request.getParameter("tableName");
		return JdbcUtils.delete(dbInfo,condition, tableName);
	}
	/**
	 * 获取数据表的列
	 * @param request(dbInfo,param,tableName)
	 * @param response
	 * @return
	 */
	public String column(HttpServletRequest request, HttpServletResponse response){
		
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		String tableName = request.getParameter("tableName");
		return JdbcUtils.selectColumn(dbInfo, tableName);
	}
	/**
	 * 获取数据表的主键
	 * @param request(dbInfo,param,tableName)
	 * @param response
	 * @return
	 */
	public String pkey(HttpServletRequest request, HttpServletResponse response){
		
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		String tableName = request.getParameter("tableName");
		return JdbcUtils.selectPKey(dbInfo, tableName);
	}
	
	
	/**
	 * 备份
	 * @param request
	 * @param response
	 * @return
	 */
	public String backUp(HttpServletRequest request, HttpServletResponse response){
		
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		return JdbcUtils.backUp(dbInfo);
	}
	/**
	 * 恢复
	 * @param request
	 * @param response
	 * @return
	 */
	public String restore(HttpServletRequest request, HttpServletResponse response){
		
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		String filePath = request.getParameter("file_path");//备份文件路径
		return JdbcUtils.restore(dbInfo,filePath);
	}
	
	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
		 
	}

	

	
}
