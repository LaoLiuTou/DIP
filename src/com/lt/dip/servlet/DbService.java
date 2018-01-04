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
import com.lt.dip.utils.TranUtil;

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
		
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		String param = request.getParameter("param"); 
		String tableName = request.getParameter("tableName"); 
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.query(userId,dbInfo, param, tableName);
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
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.insert(userId,dbInfo, param, tableName);
	}
	
	/**
	 * 批量插入数据
	 * @param request(dbInfo,param,tableName)
	 * @param response
	 * @return
	 */
	public String mulInsert(HttpServletRequest request, HttpServletResponse response){
		
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		String param = request.getParameter("param"); 
		String tableName = request.getParameter("tableName"); 
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.mulInsert(userId,dbInfo, param, tableName);
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
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.update(userId,dbInfo, param,condition, tableName);
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
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.delete(userId,dbInfo,condition, tableName);
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
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.selectColumn(userId,dbInfo, tableName);
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
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.selectPKey(userId,dbInfo, tableName);
	}
	/**
	 * 事物处理多步操作
	 * @param request(dbInfo,param,tableName)
	 * @param response
	 * @return
	 */
	public String tran(HttpServletRequest request, HttpServletResponse response){
		
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		String mulParam = request.getParameter("mulParam"); 
		String userId = (String) request.getAttribute("userId");//用户id
		return TranUtil.tran(userId,dbInfo, mulParam);
	}
	/**
	 * 执行sql
	 * @param request(dbInfo,type,sql)
	 * @param response
	 * @return
	 */
	public String execute(HttpServletRequest request, HttpServletResponse response){
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		String type = request.getParameter("type"); 
		String sql = request.getParameter("sql"); 
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.execute(userId,dbInfo, type,sql);
	}
	
	/**
	 * 新建外键
	 * @param request
	 * @param response
	 * @return
	 */
	public String fkey(HttpServletRequest request, HttpServletResponse response){
		String dat_id = request.getParameter("dat_id");//获取数据库信息
		String dbInfo = JdbcUtils.getDbInfoByDatid(dat_id);//获取数据库信息
		String tableNameS = request.getParameter("tableNameS"); 
		String tableNameP = request.getParameter("tableNameP"); 
		String colS = request.getParameter("colS"); 
		String colP = request.getParameter("colP");
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.createFK(userId,dbInfo, tableNameS,colS,tableNameP,colP);
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
		String dbNm_t = JdbcUtils.getDbnameByDatid(dat_id);//获取数据库信息
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.backUp(userId,dbInfo,dbNm_t);
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
		String dbNm_t = JdbcUtils.getDbnameByDatid(dat_id);//获取数据库信息
		String userId = (String) request.getAttribute("userId");//用户id
		return JdbcUtils.restore(userId,dbInfo,filePath,dbNm_t);
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
