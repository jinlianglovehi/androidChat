package com.sobot.chat.adapter.base;

import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.BaseAdapter;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ZhiChiBitmapUtils;

/**
 * @author jinliang
 * @param <T>
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
	
    protected List<T> list;
    protected Context context;
    protected BitmapUtils bitmapUtils;
    protected MediaPlayer mMediaPlayer = null;
    protected HttpUtils httpUtils;
    protected HtmlTools htmlTools;
	public MyBaseAdapter(Context context,List<T> list) {
		super();
		this.list = list;
		this.context =context;
		initBitMapUtils();
	}
	
	@SuppressWarnings("static-access")
	private void initBitMapUtils() {
		 if(bitmapUtils==null){
			 bitmapUtils =new ZhiChiBitmapUtils(context).getInstance();
		 }
		 if(httpUtils==null){
			 httpUtils = new HttpUtils();
			 //配置过期的时间
			 httpUtils.configTimeout(30*1000); 
		 }
		 if(htmlTools==null){
			 htmlTools = new HtmlTools();
		 }
		
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

}
