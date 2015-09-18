package com.sobot.chat.model;

import java.io.Serializable;

/**
 * 机械人回复的实体内容对象
 * 
 * @author jinliang
 * 
 */
public class ZhiChiReplyAnswer implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String msgType;
	private String msg;
	private String duration;
	private String richpricurl; // 富文本的消息
	private String richmoreurl;// 查看更多的地址

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getRichpricurl() {
		return richpricurl;
	}

	public void setRichpricurl(String richpricurl) {
		this.richpricurl = richpricurl;
	}

	public String getRichmoreurl() {
		return richmoreurl;
	}

	public void setRichmoreurl(String richmoreurl) {
		this.richmoreurl = richmoreurl;
	}
}