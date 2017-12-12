package com.lt.dip.utils;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
 

public class HttpServletUtil {
    private static CloseableHttpClient httpclient;
    private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
 
    /**
     * Post
     * 
     * @param params
     *            向服务器端传的参数
     * @param url
     * @return String 数据字符串
     * @throws Exception
     */
    public static String doPost(List<NameValuePair> params, String url) throws Exception {
        String result = null;
        httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
        //设置请求和传输超时时间
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse httpResp = httpclient.execute(httpPost);
        try {
            int statusCode = httpResp.getStatusLine().getStatusCode();
            // 判断是够请求成功
            if (statusCode == HttpStatus.SC_OK) {
                //System.out.println("状态码:" + statusCode);
                //System.out.println("请求成功!");
                // 获取返回的数据
                result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
            } else {
                System.out.println("状态码:"
                        + httpResp.getStatusLine().getStatusCode());
                System.out.println("HttpPost方式请求失败!");
            }
        } finally {
            httpResp.close();
            httpclient.close();
        }
        return result;
    }
 
    /**
     * Get
     * 
     * @param url
     * @return String 数据字符串
     * @throws Exception
     */
     public static String doGet(String url) throws Exception{
        String result = null;
        httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        //设置请求和传输超时时间
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse httpResp = httpclient.execute(httpGet);
        try {
            int statusCode = httpResp.getStatusLine().getStatusCode();
            // 判断是够请求成功
            if (statusCode == HttpStatus.SC_OK) {
                //System.out.println("状态码:" + statusCode);
                //System.out.println("请求成功!");
                // 获取返回的数据
                result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
            } else {
                System.out.println("状态码:"
                        + httpResp.getStatusLine().getStatusCode());
                System.out.println("HttpGet方式请求失败!");
            }
        } finally {
            httpResp.close();
            httpclient.close();
        }
        return result;
     }
     
     
     /**
      * POST
      * 
      * @param url
      * @return json json数据字符串
      * @throws Exception
      */
     public static String HttpPostWithJson(String url, String json,Header[] headers) {
 		String returnValue = "wrong!";
 		CloseableHttpClient httpClient = HttpClients.createDefault();
 		ResponseHandler<String> responseHandler = new BasicResponseHandler();
 		try{
 		    httpClient = HttpClients.createDefault();
 	        HttpPost httpPost = new HttpPost(url);
 	        StringEntity requestEntity = new StringEntity(json,"utf-8");
 	        requestEntity.setContentEncoding("UTF-8");    	         
 	        httpPost.setHeaders(headers); 
 	        httpPost.setEntity(requestEntity);
 	       
 	        //第四步：发送HttpPost请求，获取返回值
 	        returnValue = httpClient.execute(httpPost,responseHandler); //调接口获取返回值时，必须用此方法
 	        CloseableHttpResponse httpResonse = httpClient.execute(httpPost);
	        int statusCode = httpResonse.getStatusLine().getStatusCode();
 	        if(statusCode!=200)
 	        {  
 	        	returnValue = httpResonse.getStatusLine().toString();	  
 	        }
 	       
 	      
 		}
 		 catch(Exception e)
 		{
 			 e.printStackTrace();
 		}
 		
 		finally {
 	       try {
 			httpClient.close();
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 	    }
 		 
 	     return returnValue;
 	}
  
     
}
