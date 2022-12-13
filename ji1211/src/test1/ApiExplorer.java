package test1;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.awt.*;
import javax.swing.*;

import org.json.simple.JSONArray;
//import org.json.simple.JSONArray.put;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.*;
import org.json.*;
import org.json.simple.parser.JSONParser;


public class ApiExplorer {
	String forSb;
	String covidstr;
	public ApiExplorer(){
		Api_SpecialWeatherReport SpecialWeather = new Api_SpecialWeatherReport();
		// weather weather2=new weather();
		int size=SpecialWeather.WeatherAPI();
		
		StringBuilder sb=new StringBuilder();
		
		
		for(int i=0;i<size;i++)
		{
			System.out.println(SpecialWeather.sentence[i]);
			sb.append(SpecialWeather.sentence[i]+"\n");
		}
		
		
		forSb=sb.toString();
		corona corona = new corona();
		covidstr=corona.covid();
		
	}
				
		
		public String getForsbandcovidstr() { //api에 관한 패널을 생성.
			
			String composite=forSb+"///"+covidstr;
			return composite;
		}
		
		
	
}


class JiGui extends JFrame{
	String strr;
	String covidstr;
	public JiGui(String strr, String covidstr)
	{
		this.strr=strr;
		this.covidstr=covidstr;
	}
	public JPanel createPanel()
	{
	{
		JPanel jp=new JPanel();
		
		//setTitle("공공데이터를 활용해보자");
		jp.setSize(400, 100);
		
		jp.setLayout(null);
		JLabel label1 = new JLabel("오늘의 기상특보");
		// label1.setBounds();
		label1.setSize(200, 30);
		label1.setHorizontalAlignment(JLabel.CENTER); //글자 중앙정렬.
		jp.add(label1);
		add(label1);
		
		JLabel label3 = new JLabel("오늘의 코로나 정보");
		
		label3.setSize(200, 30);
		label3.setHorizontalAlignment(JLabel.CENTER);
		jp.add(label3);
		
		System.out.println("strr:"+strr); //출력잘됨.
		String[] strry=strr.split("\n");
		
		TextArea textarea = new TextArea(strry[1]+"\n\n"+strry[0], 10, 10, TextArea.SCROLLBARS_VERTICAL_ONLY);
		textarea.setSize(200, 70);
		textarea.setEditable(false);
		/*
		JScrollPane scrollPane = new JScrollPane(textarea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(0, 100, 400, 200);
		*/
		jp.add(textarea);
		
		String[] strry2=covidstr.split(" ");
		
		for(int j=0;j<strry2.length;j++)
		{
			System.out.println(strry2[j]);
			
		}
		
		String[] savedKey= {"데이터 조회일시:", "일일확진:", "일일사망:"};
		JPanel coronaPanel=new JPanel();
		coronaPanel.setSize(200, 150);
		for(int i=0;i<strry2.length;i++)
		{
			JLabel label2=new JLabel(savedKey[i]+strry2[i]);
			label2.setSize(200, 70);
			
			
			coronaPanel.add(label2);
			
			
		}
		jp.add(coronaPanel);
		return jp;
	}
	}

}

class corona {
	
	String covid(){
		
	
	{
		try {
			StringBuilder urlBuilder = new StringBuilder(
					"http://apis.data.go.kr/1790387/covid19CurrentStatusKorea/covid19CurrentStatusKoreaJason"); /*
																												 * URL
																												 */
			urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8")
					+ "=M03zsufFW7hqsm3aTffU%2BAHzeFOOx%2FKhA4P75mH9zz6aG%2F9KrOrDUNe2pC%2F6yh%2By1Rp9qXqrf%2FEa0SN%2F56HeNg%3D%3D"); /*
																																		 * Service
																																		 * Key
																																		 */
			URL url = new URL(urlBuilder.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");
			System.out.println("Response code: " + conn.getResponseCode());
			BufferedReader rd;
			if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			conn.disconnect();
			// System.out.println(sb.toString());

			String result = sb.toString();

			JSONParser parser = new JSONParser();

			JSONObject jobj = (JSONObject) parser.parse(result);
			JSONObject jobj2 = (JSONObject) jobj.get("response");
			String str1 = jobj2.toString();
			JSONObject jobj3 = (JSONObject) parser.parse(str1);
			JSONArray jarr = (JSONArray) jobj3.get("result");
			
			String save="";
			
			for (int i = 0; i < jarr.size(); i++) {
				JSONObject obj = (JSONObject) jarr.get(i);
				String mmddhh = (String) obj.get("mmddhh"); // 데이터 조회일시
				String cnt_confirmations = (String) obj.get("cnt_confirmations"); // 일일확진
				String cnt_deaths = (String) obj.get("cnt_deaths"); // 일일사망
				
				
				System.out.println("데이터조회일시:" + mmddhh + " 일일확진:" + cnt_confirmations +" 일일사망:" + cnt_deaths);
				String subsave=mmddhh+" "+cnt_confirmations+" "+cnt_deaths;
				save=save+subsave;
			}
			return save;
			
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}

	}}
}

class Api_SpecialWeatherReport {
	
	String[] sentence;
	public int WeatherAPI(){
	
		
		{
		try {
    StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/WthrWrnInfoService/getPwnStatus"); /*URL*/
    urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=M03zsufFW7hqsm3aTffU%2BAHzeFOOx%2FKhA4P75mH9zz6aG%2F9KrOrDUNe2pC%2F6yh%2By1Rp9qXqrf%2FEa0SN%2F56HeNg%3D%3D"); /*Service Key*/
    urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
    urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
    urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON)Default: XML*/
    //urlBuilder.append("&" + URLEncoder.encode("stnId","UTF-8") + "=" + URLEncoder.encode("108", "UTF-8")); /*지점코드 *하단 지점코드 자료 참조*/
    //urlBuilder.append("&" + URLEncoder.encode("fromTmFc","UTF-8") + "=" + URLEncoder.encode("20170601", "UTF-8")); /*시간(년월일)(데이터 생성주기 : 시간단위로 생성)*/
    //urlBuilder.append("&" + URLEncoder.encode("toTmFc","UTF-8") + "=" + URLEncoder.encode("20170607", "UTF-8")); /*시간(년월일) (데이터 생성주기 : 시간단위로 생성)*/
    URL url = new URL(urlBuilder.toString());
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Content-type", "application/json");
    System.out.println("Response code: " + conn.getResponseCode());
    BufferedReader rd;
    if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
        rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
    } else {
        rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
    }
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
        sb.append(line);
    }
    
    //System.out.println(sb.toString());
    
    
   
    JSONParser parser=new JSONParser();
    
    JSONObject job=(JSONObject)parser.parse(sb.toString());
    //System.out.println(job.getClass().getName());
    
    
    //System.out.println(job.get("response"));
    
    JSONObject job1=(JSONObject) job.get("response"); //키로부터얻은 value가 제이슨오브젝트가 아니다.
    String str1=job1.toString();
    
    
    //System.out.println(job1);
    //System.out.println(job1.getClass().getName());
    
    
   JSONObject job2=(JSONObject)parser.parse(str1);
   JSONObject job3=(JSONObject) job2.get("body");
   String str2=job3.toString();
   // System.out.println(str2);   //body의 벨류값 출력.
   JSONObject job4=(JSONObject)parser.parse(str2);
   JSONObject job5=(JSONObject) job4.get("items");
   String str3=job5.toString(); 
   //System.out.println(str3); //items의 벨류값 출력.
   JSONObject job6=(JSONObject)parser.parse(str3); //
   
   JSONArray jarr=(JSONArray) job6.get("item"); //제이슨오브젝트형태로 array를 얻음.
   //System.out.println(jarr); //list의 값들 출력.
  
   
  
    
    
    
    //JSONObject obj=(JSONObject)jarr.get(0);
    /*
    String weatherContent=(String)obj.get("t6");  //기상특보
    //String preWeatherContent=(String)obj.get("t6"); //예비특보
    String timeAt=(String)obj.get("tmfc"); //발표시각
    
    String timeAtYear=timeAt.substring(0, 4); //발표시각의 년도
    String timeAtMonth=timeAt.substring(4, 2);//발표시각의 월.
    String timeAtDay=timeAt.substring(6, 2);//발표시각의 일.
    String timeAtHour=timeAt.substring(8, 2);//발표시각의 시.
    String timeAtMinute=timeAt.substring(10, 2);//발표시각의 분.
    
    System.out.println("기상특보->"+weatherContent+"\n발표시각->"+ timeAtYear+"년"+ timeAtMonth+"월"+ timeAtDay+"일"+ timeAtHour+"시"+ timeAtMinute+"분");
    
    */
    		/*new JSONArray();
    jarr.add(job);*/
    
   sentence=new String[jarr.size()];
   
  
    
    
    for(int i=0;i<jarr.size();i++)
    {
    	JSONObject JSONElement=(JSONObject)jarr.get(i);
    	 String weatherContent=(String)JSONElement.get("t6");  //기상특보
         //String preWeatherContent=(String)obj.get("t6"); //예비특보
         String timeAt=(String)JSONElement.get("tmEf"); //특보발효시각
         
         String timeAtYear=timeAt.substring(0, 4); //특보발효시각의 년도
         String timeAtMonth=timeAt.substring(4, 6);//특보발효시각의 월.
         String timeAtDay=timeAt.substring(6, 8);//특보발효시각의 일.
         String timeAtHour=timeAt.substring(8, 10);//특보발효시각의 시.
         String timeAtMinute=timeAt.substring(10, 12);//특보발효시각의 분.
         
         /*
         String timeAt2=(String)JSONElement.get("tmFc"); //발표시각
         
         String timeAtYear2=timeAt2.substring(0, 4); //발표시각의 년도
         String timeAtMonth2=timeAt2.substring(4, 6);//발표시각의 월.
         String timeAtDay2=timeAt2.substring(6, 8);//발표시각의 일.
         String timeAtHour2=timeAt2.substring(8, 10);//발표시각의 시.
         String timeAtMinute2=timeAt2.substring(10, 12);//발표시각의 분.
    	*/
         String str=""+weatherContent+"\n특보발효현황시각->"+ timeAtYear+"년"+ timeAtMonth+"월"+ timeAtDay+"일"+ timeAtHour+"시"+ timeAtMinute+"분 이후";
         System.out.println(str);
    	 sentence[i]=new String(str);
    	
    	
    	
    	
    }
    
    
    rd.close();
    conn.disconnect();
    return jarr.size();
    
    }catch(Exception e)
    {
    	e.printStackTrace();
    	return -1;
    }
	
	}
}

private void Api_SpecialWeatherReport() {
	// TODO Auto-generated method stub
	
}
}