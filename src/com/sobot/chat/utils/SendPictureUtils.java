package com.sobot.chat.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.sobot.chat.activity.base.TitleActivity;
import com.sobot.chat.model.ZhiChiMessage;

public class SendPictureUtils {
	
	private static SendPictureUtils  instance;
	private Context context;
	public SendPictureUtils getInstance(Context context){
		this.context =context;
		if(instance==null){
			return instance = new SendPictureUtils();
		}
		return instance;
	}
	
	/*public void sendPicture(Map<String, String> map, final String filePath,
			final Handler handler, final String id) {

		RequestParams params = new RequestParams();
		// 基本传递参数
		List<NameValuePair> arrayList = new ArrayList<NameValuePair>();
		for (String key : map.keySet()) {
			BasicNameValuePair nameValuePair = new BasicNameValuePair(key,
					map.get(key));
			arrayList.add(nameValuePair);
		}
		// 添加发送参数 以及发送的版本号
		// 添加from客户端的方式
		BasicNameValuePair fromMethod = new BasicNameValuePair("from", "2");
		arrayList.add(fromMethod);
		// 添加版本号
		BasicNameValuePair versionStr = new BasicNameValuePair("version",
				CommonUtils.getVersion(context) + "");
		arrayList.add(versionStr);

		params.addBodyParameter(arrayList);
		// 文件流传递参数
		// params.addBodyParameter(filePath.replace("/", ""), new
		// File(filePath));
		params.addBodyParameter("file", new File(filePath));
		// 添加图片文件的字符流
		getDataWithParam(HttpMethod.POST,
				ZhiChiApi.api_sendFile_to_customeService, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						LogUtils.i("发送图片error:" + arg1.toString()
								+ "exception:" + arg0);
						if(id!=null){
							Message message = handler.obtainMessage();
							message.what = ZhiChiConstant.hander_sendPicStatus_fail;
							message.obj = id;
							handler.sendMessage(message);
						}
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						super.onLoading(total, current, isUploading);
						LogUtils.i("发送图片 进度:" + current + "/" + total);
						if(id!=null){
							Message message = handler.obtainMessage();
							message.what = ZhiChiConstant.hander_sendPicIsLoading;
							message.obj = id;
							// 设置消息的进度
							message.arg1= Integer.parseInt(String.valueOf(current*100/total));
							handler.sendMessage(message);
						}
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						LogUtils.i("返回图片发送成功success后的操作：" + arg0.result);
						ZhiChiMessage result = (ZhiChiMessage) GsonUtil.jsonToBean(arg0.result, ZhiChiMessage.class);
						if(ZhiChiConstant.result_success_code==Integer.parseInt(result.getCode())){
							if(id!=null){
								Message message = handler.obtainMessage();
								message.what = ZhiChiConstant.hander_sendPicStatus_success;
								message.obj = id;
								handler.sendMessage(message);
							}
						}
					}
				});
	}
	
	public void getDataWithParam(HttpMethod httpMethod, String url,
			RequestParams params, RequestCallBack<String> callBack) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(httpMethod, url, params, callBack);
	}
*/
}
