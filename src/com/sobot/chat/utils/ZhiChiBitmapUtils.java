package com.sobot.chat.utils;

import android.content.Context;

import com.lidroid.xutils.BitmapUtils;

/**
 * 加载图片的共有的配置公共类
 * @author jinliang
 */
public  class ZhiChiBitmapUtils {
    
	
	public static BitmapUtils bitmapUtils;
    public static Context context;
	public ZhiChiBitmapUtils(Context context) {
		super();
		this.context=context;
	}
	public static BitmapUtils getInstance(){
		if(bitmapUtils==null){
			bitmapUtils = new BitmapUtils(context);
			// 配置图片的加载的最大的大小的尺寸
			bitmapUtils.configDefaultBitmapMaxSize(200, 300);
			bitmapUtils.configDiskCacheEnabled(true);
			bitmapUtils.configDefaultAutoRotation(true);
			//bitmapUtils.configMemoryCacheEnabled(false);
		}
		return bitmapUtils;
	}
	
}
