package com.lt.dip.utils;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
 

 
import redis.clients.jedis.Jedis;  

import redis.clients.jedis.JedisPool;  
  
import redis.clients.jedis.JedisPoolConfig;  
  
public class RedisUtil {
	
	
	  private static JedisPool pool;      
	  
	  
	  public RedisUtil(String ip,String port) {      
	  
	      try{  
	    	 Properties props = new Properties();  
	      	 props.load(RedisUtil.class.getClassLoader().getResourceAsStream("redis/redis.properties"));  
	     	 //创建jedis池配置实例    
             JedisPoolConfig config = new JedisPoolConfig();     
             //设置池配置项值    
             config.setMaxActive(Integer.valueOf(props.getProperty("jedis.pool.maxActive")));      
             config.setMaxIdle(Integer.valueOf(props.getProperty("jedis.pool.maxIdle")));      
             config.setMaxWait(Long.valueOf(props.getProperty("jedis.pool.maxWait")));      
             config.setTestOnBorrow(Boolean.valueOf(props.getProperty("jedis.pool.testOnBorrow")));      
             config.setTestOnReturn(Boolean.valueOf(props.getProperty("jedis.pool.testOnReturn")));     
             //根据配置实例化jedis池    
             pool = new JedisPool(ip, Integer.valueOf(port));   
	      }catch (Exception e) {  
			 e.printStackTrace();  
		  }  
			  
	    }    
	  
	      
	  
	    /**获得jedis对象*/  
	  
	    public static Jedis getJedisObject(){  
	    	Jedis jedis = null;
	    	try {
				jedis=pool.getResource();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	 
	    	return jedis;
	  
	    }  
	  
	      
	  
	    /**归还jedis对象*/  
	  
	    public static void recycleJedisOjbect(Jedis jedis){  
	  
	     pool.returnResource(jedis);    
	  
	    }  
	  
	    
	    public static String getObject(String id) {
	    	Jedis jedis = getJedisObject();//获得jedis实例 
	        String info = jedis.get(id);
	        recycleJedisOjbect(jedis); //将 获取的jedis实例对象还回迟中  
	        return info;

	   }
	    public static void getList(String prev) {
	    	Jedis jedis = getJedisObject();//获得jedis实例 
	    	 Set<String> keys = jedis.keys(prev+"*"); 
	         Iterator<String> it=keys.iterator() ;   
	         while(it.hasNext()){   
	             String key = it.next();   
	             System.out.println(key +":"+jedis.get(key));   
	         }
	    	
	    }
	    
	    public static void setObject(String id,String value) {
	    	Jedis jedis = getJedisObject();//获得jedis实例 
	        try {
				jedis.set(id, value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        recycleJedisOjbect(jedis); //将 获取的jedis实例对象还回迟中  
	    }
	    
	    public static void delOject(String key){
	    	Jedis jedis = getJedisObject();//获得jedis实例 
	        boolean isExist=jedis.exists(key);
	        if(isExist){
	            jedis.del(key);
	        }
	        recycleJedisOjbect(jedis); //将 获取的jedis实例对象还回迟中  
	    }      
	     
	    /**  
	 
	     * 测试jedis池方法  
	 
	     */    
	  
	    @SuppressWarnings("static-access")
		public static void main(String[] args) {  
	  
	    	
	    	Jedis jedis=new RedisUtil("192.168.1.152", "6379").getJedisObject();
	    	if(jedis==null){
	    		System.out.println("获取连接失败");
	    	}
	    	else{
	    		
	    		System.out.println(getObject("JC:members")); 
	    	}
	      // Jedis jedis = getJedisObject();//获得jedis实例                    
	  
	       
	       
	        //获取jedis实例后可以对redis服务进行一系列的操作    
	  
	        /*jedis.set("name", "zhuxun");    
	  
	        System.out.println(jedis.get("name"));    
	  
	        jedis.del("name");    
	  
	        System.out.println(jedis.exists("name"));    */
	        //{"T":"1","UN":"李三","UI":"002","UH":""}
	       /*delOject("102");
	        delOject("144");*/
	         //delOject("area", "1000");
        	//getList("prod");
	        //getList("JC");
        	
	        //System.out.println(getObject("JC", "members")); 
	        //recycleJedisOjbect(jedis); //将 获取的jedis实例对象还回迟中  
	  
	    }    
	  

}

