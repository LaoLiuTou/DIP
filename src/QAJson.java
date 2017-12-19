import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.lt.dip.utils.HttpServletUtil;


public class QAJson {

	
	public static void main(String[] args){
		String url="https://xc.wenav.net/api/user/questionnaire";
		
		List<NameValuePair> params= new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sid", "db8afa337f38465aa1fef92d3851291e"));    
		params.add(new BasicNameValuePair("winzoom", "1"));    
		try {
			String qResult = HttpServletUtil.doPost(params, url);
			mateQuestion(qResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 根据问题返回对应答案json格式
	 * @param qResult 问题json
	 * @return 答案json
	 */
	@SuppressWarnings("unused")
	private static String mateQuestion(String qResult){
		String aResult="";
		try {
			JSONObject qJO = JSONObject.fromObject(qResult);
			if(qJO.getString("message").equals("success")){
				//返回结果 
				JSONObject answerJO=new JSONObject();
				
				//result节点
				JSONObject resultJO=qJO.getJSONObject("result");
				//result-questions 节点
				JSONArray questionsJA=resultJO.getJSONArray("questions");
				//循环所有问题分类
				for(int index=0;index<questionsJA.size();index++){
					JSONObject questionsJO=(JSONObject) questionsJA.get(index);
					//问题id
					String qId=questionsJO.getString("id");
					//result-questions-selecter 节点
					JSONArray selecterJA=resultJO.getJSONArray("questions");
					//循环 selecter
					for(int i=0;i<selecterJA.size();i++){
						JSONObject selecterJO=(JSONObject) selecterJA.get(index);
						//selecter id
						String sId=questionsJO.getString("sid");
						//问题类型
						String qType=questionsJO.getString("qtype");
						
						
						
					}
					
					
					
				}
				
			}
			else{
				aResult="接口返回结果：失败！";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			aResult="JSON格式解析错误！";
			e.printStackTrace();
		}
		return aResult;
	}
	
}
