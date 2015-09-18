package com.sobot.chat.utils;

import java.io.File;
import java.net.URL;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;

public class HtmlTools {
     private ImageGetter imageGetterLocal;
     private String  textImagePath = ZhiChiConstant.imagePositionPath;
     private  BitmapUtils  bitmapUtils;
     private  HttpUtils  httpUtils;
     private TextView textView;
     private String htmlContent;
	public HtmlTools() {
		super();
		if(httpUtils==null){
			httpUtils = new HttpUtils();
		}
	}

	// 显示本地图片o
	public ImageGetter getImageGetterFromLocal() {
		//避免重复创建
		if(imageGetterLocal==null){
			  imageGetterLocal = new Html.ImageGetter() {
				@Override
				public Drawable getDrawable(String source) {
					Drawable drawable = null;
					drawable = Drawable.createFromPath(source); // Or fetch it from
					// Important
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight());
					return drawable;
				}
			};
		}
		return imageGetterLocal;
	}


	public void handTextWithPic( TextView textView ,final String htmlContent){
		this.textView =textView;
		this.htmlContent = htmlContent;
		textView.setText(Html.fromHtml(htmlContent, imageGetterNet, null));
		
	}
	
	public  ImageGetter imageGetterNet = new Html.ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			 Drawable drawable=null;
			  String fileString=textImagePath+String.valueOf(source.hashCode());
	            if (new File(fileString).exists()) {
	                LogUtils.i(" 网络下载 文本中的图片信息  "+ fileString+"  eixts");
	                //获取本地文件返回Drawable
	                drawable=Drawable.createFromPath(fileString);
	                //设置图片边界
	                LogUtils.i(" 图文并茂中 图片的 大小 width： "+drawable.getIntrinsicWidth() + "--height:"+drawable.getIntrinsicWidth() );
	                drawable.setBounds(0, 0, drawable.getIntrinsicWidth()*8, drawable.getIntrinsicHeight()*8);
	                return drawable;
	            }else {
	                LogUtils.i( fileString+" Do not eixts");
	                loadPic(source,htmlContent,fileString);
	                return drawable;
	            }
		}
		
	};
	
	public void loadPic(String source,final String htmlContent,String fileString){
		 //启动新线程下载
        httpUtils.download(source, fileString, true,true, new RequestCallBack<File>() {
			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				textView.setText(Html.fromHtml(htmlContent, imageGetterNet, null));
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				LogUtils.i(" 文本图片的下载失败");
			}
		});
	}
	
}
