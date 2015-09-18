package com.sobot.chat.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.sobot.chat.activity.base.TitleActivity;

public class HttpUtilsTools {
	   
	
	
    //#####################   网络请求      #########################
    public static void getData(HttpMethod httpMethod,String url,Map<String,String> map,RequestCallBack<String> callBack) {
     	HttpUtils httpUtils = new HttpUtils();
     	httpUtils.configDefaultHttpCacheExpiry(1);
     	// 配置http请求的配置
        HttpParams paramsConfig = httpUtils.getHttpClient().getParams();
     	HttpConnectionParams.setSocketBufferSize(paramsConfig, 1024 * 80);
    	RequestParams params = new RequestParams();
     	List<NameValuePair> arrayList = new ArrayList<NameValuePair>();
	    for (String key : map.keySet()) {
			BasicNameValuePair nameValuePair = new BasicNameValuePair(key,map.get(key));
			arrayList.add(nameValuePair);
        }
		params.addBodyParameter(arrayList);
		httpUtils.send(httpMethod,url,params,callBack);
	}
}
