package com.gulusoft.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.alidns.model.v20150109.AddDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.AddDomainRecordResponse;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.auth.Credential;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpRequest;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.reader.JsonReader;
import com.aliyuncs.utils.Base64Helper;
import com.aliyuncs.utils.ParameterHelper;


public class TestDomainRev {

	
	
	static String HTTP_METHOD = "GET";
	
	protected String AccessKeyId = "";	
	protected String AccessKeySec = "";
	protected String rootDomainName = "";
	protected String hostName = "";
	
	public static void main(String[] argc) {
		
		try {
			if(argc.length != 4) {
				System.out.println("Usage Parameters:  accessKeyId AccessKeySec rootDomainName hostName");
				System.exit(0);
			}
			TestDomainRev ins = new TestDomainRev();
			ins.setAccessKeyId(argc[0]);
			ins.setAccessKeySec(argc[1]);
			ins.setRootDomainName(argc[2]);
			ins.setHostName(argc[3]);
			ins.test();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	public String getAccessKeyId() {
		return AccessKeyId;
	}



	public void setAccessKeyId(String accessKeyId) {
		AccessKeyId = accessKeyId;
	}



	public String getAccessKeySec() {
		return AccessKeySec;
	}



	public void setAccessKeySec(String accessKeySec) {
		AccessKeySec = accessKeySec;
	}



	public String getRootDomainName() {
		return rootDomainName;
	}



	public void setRootDomainName(String rootDomainName) {
		this.rootDomainName = rootDomainName;
	}



	public String getHostName() {
		return hostName;
	}



	public void setHostName(String hostName) {
		this.hostName = hostName;
	}



	@Test
	public void test() throws InvalidKeyException, NoSuchAlgorithmException, IOException, ServerException, ClientException {
		
		String domainName = "gulusoft.com";
		String homeRecordRr = "home";
		
		IClientProfile clientProfile = DefaultProfile.getProfile("cn-hangzhou", this.AccessKeyId, this.AccessKeySec );  
		DefaultAcsClient client = new DefaultAcsClient(clientProfile);  
		DescribeDomainRecordsRequest sdkReq = new DescribeDomainRecordsRequest();  
		sdkReq.setDomainName(domainName);  
		DescribeDomainRecordsResponse response = client.getAcsResponse(sdkReq);  
		List<DescribeDomainRecordsResponse.Record> list = response.getDomainRecords(); 
		
		DescribeDomainRecordsResponse.Record homeRecord = null;
		
		
		String ip = getIP();
		
		for(DescribeDomainRecordsResponse.Record record : list) {
			if(record.getRR().toString().equals(homeRecordRr)) {
				homeRecord = record;
			}
			System.out.println(record.getRR() + " | " + record.getType() + " | " + record.getValue());
		}
		if(homeRecord == null) {
	        AddDomainRecordRequest addReq = new AddDomainRecordRequest();
	        addReq.setDomainName(domainName);
	        addReq.setRR(homeRecordRr);
	        addReq.setType("A");
	        addReq.setValue(ip);
	        AddDomainRecordResponse addResponse = client.getAcsResponse(addReq);
	        System.out.println(addResponse.getRecordId());
		} else {
			if(ip.equals(homeRecord.getValue()) == false) {
				
				UpdateDomainRecordRequest updateReq = new UpdateDomainRecordRequest();
				updateReq.setRecordId(homeRecord.getRecordId());
				updateReq.setValue(ip);
				
				UpdateDomainRecordResponse updateResponse = client.getAcsResponse(updateReq);
				System.out.println(updateResponse.getRecordId());
				
			} else {
				System.out.println("IP STILL:  " + ip);
			}
		}
		
		
		fail("Not yet implemented");
	}
	
	protected String getIP() {
		String ip = "";
		try {
			
		
		String url = "http://expired.gulusoft.com/api.php?act=getIP";
		HttpRequest request = new HttpRequest(url);
		request.setMethod(MethodType.GET);
		request.setEncoding("UTF-8");
		HttpURLConnection httpConn = request.getHttpConnection();

		
				int responseCode = httpConn.getResponseCode();   
				  
				   
				if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功   
					StringBuffer sb = new StringBuffer();   
					String readLine;   
					BufferedReader responseReader;   
					 responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));   
					while ((readLine = responseReader.readLine()) != null) {   
						sb.append(readLine).append("\n");   
					}   
					responseReader.close(); 
					ip = sb.toString().trim();
				} 
		
		
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return ip;
	}
	
    private static final String ENCODING = "UTF-8";

    private static String percentEncode(String value)
            throws UnsupportedEncodingException{
        return value != null ?
                URLEncoder.encode(value, ENCODING).replace("+", "%20")
                .replace("*", "%2A").replace("%7E", "~")
                : null;
    }
}
