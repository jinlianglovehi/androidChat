package com.sobot.chat.application;

import io.rong.imlib.RongIMClient;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;

public  abstract class SobotApplication extends Application {
	
    @Override
    public void onCreate() {
    	super.onCreate();
       //SharedPreferencesUtil.saveStringData(this,CommonUtils.getSobotCloudChatAppKey(getApplicationContext()),null);
    	String sobotCloudAppkey = SharedPreferencesUtil.getStringData(this, CommonUtils.getSobotCloudChatAppKey(getApplicationContext()),null);
    	//获取主进程的名称
    	String mainProcess  = CommonUtils.getPackageName(getApplicationContext());
		if(sobotCloudAppkey!=null){
	        if(mainProcess.equals(getCurProcessName(getApplicationContext())) ||
	        		"com.sobot.chat.push".equals(getCurProcessName(getApplicationContext()))) {
	            RongIMClient.init(this,sobotCloudAppkey);
	            if (mainProcess.equals(getCurProcessName(getApplicationContext()))) {
	            	RongCloudEvent.init(this);
	            }
	        }
		}
    }
	public  abstract void oncreateApp();
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
