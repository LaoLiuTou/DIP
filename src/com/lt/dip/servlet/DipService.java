package com.lt.dip.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.lt.dip.utils.ConfigUtil;
import com.lt.dip.utils.JdbcUtils;

public class DipService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7365017666329529426L;

	Logger logger = Logger.getLogger("DipLogger");
	/**
	 * Constructor of the object.
	 */
	public DipService() {
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
		String userName = (String) request.getAttribute("userName");//用户名
		logger.info("用户("+userName+")执行方法："+getClass().getName()+"-"+name);
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
		
		String param = request.getParameter("param");//获取数据库信息
		String tableName = request.getParameter("tableName");//获取数据库信息
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.query(userId,ConfigUtil.getConfig(), param, tableName);
	}
	/**
	 * 插入数据
	 * @param request(dbInfo,param,tableName)
	 * @param response
	 * @return
	 */
	public String insert(HttpServletRequest request, HttpServletResponse response){
		
		String param = request.getParameter("param");//获取数据库信息
		String tableName = request.getParameter("tableName");//获取数据库信息
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.insert(userId,ConfigUtil.getConfig(), param, tableName);
	}
	/**
	 * 批量插入数据
	 * @param request(dbInfo,param,tableName)
	 * @param response
	 * @return
	 */
	public String mulInsert(HttpServletRequest request, HttpServletResponse response){
		
		String param = request.getParameter("param");//获取数据库信息
		String tableName = request.getParameter("tableName");//获取数据库信息
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.mulInsert(userId,ConfigUtil.getConfig(), param, tableName);
	}
	
	/**
	 * 修改数据
	 * @param request(dbInfo,param,tableName)
	 * @param response
	 * @return
	 */
	public String update(HttpServletRequest request, HttpServletResponse response){
		
		String param = request.getParameter("param"); 
		String condition = request.getParameter("condition"); 
		String tableName = request.getParameter("tableName");//获取数据库信息
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.update(userId,ConfigUtil.getConfig(), param,condition, tableName);
	}
	/**
	 * 删除数据
	 * @param request(dbInfo,param,tableName)
	 * @param response
	 * @return
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response){
		
		String condition = request.getParameter("condition"); 
		String tableName = request.getParameter("tableName");//获取数据库信息
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.delete(userId,ConfigUtil.getConfig(),condition, tableName);
	}
	/**
	 * 执行sql
	 * @param request(dbInfo,type,sql)
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String execute(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		String type = request.getParameter("type"); 
		String sql = request.getParameter("sql");
		sql = new String(sql.getBytes("ISO-8859-1"), "UTF-8");
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.execute(userId,ConfigUtil.getConfig(), type,sql);
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
