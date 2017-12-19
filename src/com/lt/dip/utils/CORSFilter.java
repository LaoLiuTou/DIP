package com.lt.dip.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lt.dip.utils.TokenUtils.TokenBean;
 

public class CORSFilter implements Filter {  
	  
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {  
    	HttpServletRequest request = (HttpServletRequest) req;
    	HttpServletResponse response = (HttpServletResponse) res;  
	    response.setCharacterEncoding("UTF-8"); 
		response.setContentType("text/html;charset=UTF-8"); 
	  //CORS跨域
	    response.setHeader("Access-Control-Allow-Origin", "*");  
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");  
	    response.setHeader("Access-Control-Max-Age", "3600");  
	    //response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
	    response.setHeader("Access-Control-Allow-Headers","x-requested-with,token,timesamp,sign");
	    //response.setHeader("Access-Control-Allow-Headers","x-requested-with,content-type，requesttype");
        
	    String uri = request.getServletPath();
	    if( !uri.startsWith("/Members")){
	    	 String token = request.getHeader("token");
	        String timesamp = request.getHeader("timesamp");
	        String sign = request.getHeader("sign");
	        String signMd5 = MD5Encryption.getEncryption(token + timesamp+"!QAZXSW@#C");
	        
	        if(sign!=null&&sign.toLowerCase().equals(signMd5.toLowerCase())){
	        	long removeTime = 60*60*1000;//失效时间
	        	TokenBean tokenBean= TokenUtils.get(token);
	        	if(tokenBean!=null){
	        		String tokenTime = tokenBean.getTimesamp();
		        	String userName=tokenBean.getUsername();
		        	
		        	if(tokenTime!=null){
		        		
		        		long timesampLong=Long.parseLong(timesamp);
		        		long tokenTimeLong=Long.parseLong(tokenTime);
		        		if((timesampLong-tokenTimeLong)>removeTime){
		        			//token过期
		        			TokenUtils.remove(token);
		        			StringBuffer msg = new StringBuffer("{\"status\":\"expire\",\"msg\":\"token不存在或已经销毁！\"}");
				        	response.getWriter().write(msg.toString()); 
		        		}
		        		else{
		        			
		        			//获取保存的用户名
		        			request.setAttribute("userName", userName); 
		        			 //签名通过
			                chain.doFilter(request, response);	
			                //更新token时间
			                tokenBean.setTimesamp(timesamp);
			                TokenUtils.add(token, tokenBean);
		        		}
		        	}
		        	else{
		        		//token对应时间为空 认为token不存在
		        		StringBuffer msg = new StringBuffer("{\"status\":\"expire\",\"msg\":\"token不存在或已经销毁！\"}");
			        	response.getWriter().write(msg.toString()); 
		        	}
	        	}
	        	else{
	        		//token对应时间为空 认为token不存在
	        		StringBuffer msg = new StringBuffer("{\"status\":\"expire\",\"msg\":\"token不存在或已经销毁！\"}");
		        	response.getWriter().write(msg.toString()); 
	        	}
	        	
	        }else{
	            //签名不通过，向app后端发送错误信息，提示重新登录
	        	StringBuffer msg = new StringBuffer("{\"status\":\"mismatch\",\"msg\":\"签名错误！\"}");
	        	response.getWriter().write(msg.toString()); 
	        }
	    }
	    else{
		    chain.doFilter(req, res);  
	    }
    }  
  
    public void init(FilterConfig filterConfig) {}  
  
    public void destroy() {}  
  
} 