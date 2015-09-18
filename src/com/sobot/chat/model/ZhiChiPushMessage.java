package com.sobot.chat.model;

/**
 * 消息推送的格式信息
 * 
 * @author jinliang
 * 
 */
public class ZhiChiPushMessage {

	private int type;
	private String aname;
	private String aface;
	private String content;
	private String status;
	private String msgType;
	private String count;
    private boolean quitRongConnect;
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getAname() {
		return aname;
	}

	public void setAname(String aname) {
		this.aname = aname;
	}

	public String getAface() {
		return aface;
	}

	public void setAface(String aface) {
		this.aface = aface;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public boolean isQuitRongConnect() {
		return quitRongConnect;
	}

	public void setQuitRongConnect(boolean quitRongConnect) {
		this.quitRongConnect = quitRongConnect;
	}

}
