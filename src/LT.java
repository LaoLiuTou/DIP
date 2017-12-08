import net.sf.json.JSONObject;

import com.lt.dip.utils.JdbcUtils;
 


public class LT {

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		JSONObject dbJO=new JSONObject();
		dbJO.accumulate("dbType", "mysql");
		dbJO.accumulate("dbUser", "root");
		dbJO.accumulate("dbPassword", "root");
		dbJO.accumulate("dbHost", "127.0.0.1");
		dbJO.accumulate("dbPort", "3306");
		dbJO.accumulate("dbName", "test111");
		
		JdbcUtils ju= new JdbcUtils();
		System.out.println(ju.selectPKey(dbJO.toString(), "user3"));
		//System.out.println(ju.getDbInfoByEid("1"));
/*		JSONObject dbJO=new JSONObject();
		dbJO.accumulate("dbType", "mysql");
		dbJO.accumulate("dbUser", "root");
		dbJO.accumulate("dbPassword", "root");
		dbJO.accumulate("dbHost", "127.0.0.1");
		dbJO.accumulate("dbPort", "3306");
		dbJO.accumulate("dbName", "test111");
 
		String c3p0Key=dbJO.getString("dbType")+":"+dbJO.getString("dbHost")+":"+dbJO.getString("dbPort")+":"+dbJO.getString("dbName");
		
		String createDBStr=JdbcUtils.createDb(dbJO.getString("dbHost"), dbJO.getString("dbPort"), dbJO.getString("dbName"),
				dbJO.getString("dbUser"), dbJO.getString("dbPassword"));
		System.out.println(createDBStr);


       String sql = "create table user3 (userid int(4) primary key not null auto_increment comment 'id', " +
       		"username varchar(16) not null comment '用户名', " +
       		"userpassword varchar(32) not null comment '密码')";
       
       //String sql = "insert into user3 (username,userpassword) values ('1111','2222')";
        ConnectPoolC3P0 cp = ConnectPoolC3P0.getInstance(dbJO.getString("dbType"),
        		dbJO.getString("dbHost"),dbJO.getString("dbPort"),dbJO.getString("dbName"),
        		dbJO.getString("dbUser"),dbJO.getString("dbPassword"));
        
        Connection connection =cp.getConnection(c3p0Key);
     
        try {
        	connection.setAutoCommit(false);
        	
        	
        	cp.execute(connection, sql, null);
			//System.out.println(conn.execute(c3p0Key,sql, null));
			sql = "insert into user3 (username,userpassword) values ('3333','4444')";
			cp.execute(connection, sql, null);
			//System.out.println(conn.execute(c3p0Key,sql, null));
			
			sql = "select * from user";
			java.util.List<Map<String,String>> result =conn.queryForMap(c3p0Key,sql, null);
			System.out.println(result.size());
			connection.commit();//commit the transaction;
			connection.setAutoCommit(true); 
			
			//查询
			sql = "select * from user3 ";
			List<Map<String,String>> list=cp.queryForMap(c3p0Key, sql, null);
			System.out.println(JSONArray.fromObject(list));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			connection.rollback();
			e.printStackTrace();
		}
        finally{
        	cp.removeConnection(c3p0Key, connection);
        }*/
      
        
        
        
        //System.out.println(conn.count(sql, null));
        
        /*long start = System.currentTimeMillis();
        ConnectionPool pool = null;

        for (int i = 0; i < 100; i++) {
            pool = ConnectionPool.getInstance();
            Connection conn = pool.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
            }
            rs.close();
            stmt.close();
            pool.release(conn);
        }
        pool.closePool();
        System.out.println("经过100次的循环调用，使用连接池花费的时间:" + (System.currentTimeMillis() - start) + "ms\n");

        String hostName = "localhost";
        String driverClass = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://" + "192.168.1.112" + ":3306/flumetest";
        String user = "root";
        String password = "JiubaiSoft123";
        start = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            Class.forName(driverClass);
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        System.out.println("经过100次的循环调用，不使用连接池花费的时间:" + (System.currentTimeMillis() - start) + "ms");*/
    }
}
