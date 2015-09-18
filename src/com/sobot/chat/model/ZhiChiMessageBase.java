package com.sobot.chat.model;

import java.io.Serializable;

/**
 * 历史记录的实体
 * 
 * @author jinliang
 * 
 */
public class ZhiChiMessageBase implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 历史记录
	 */
	private int mysendMessageState = 3; // 不做任何处理
	private String id;
	private String cid;
	private String action;
	private String url;
	private String status;
	private int progressBar;
	private String duration;// 语音的时长
	private boolean reconnectCustom = false;
	// private boolean isFirstSend =true; // 判断是否是第一次发送的消息

	private int sendSuccessState = 1; // 代表发送成功， 0 发送失败 2 发送中
	private int textFailTimes = 0; // 失败的次数

	private boolean voideIsPlaying = false;
	private String context;

	/**
	 * 发送人信息
	 */
	private String sender;
	private String senderName;
	private String senderType;
	private String senderFace;

	private long sendMessageTime = 0;
	/**
	 * 发送消息
	 */
	private String t;
	private String ts;
	/*
	 * private String msgType; private String msg;
	 */
	private ZhiChiHistorySDKMsg sdkMsg;
	private int sugguestionsFontColor;

	public int getSugguestionsFontColor() {
		return sugguestionsFontColor;
	}

	public void setSugguestionsFontColor(int sugguestionsFontColor) {
		this.sugguestionsFontColor = sugguestionsFontColor;
	}

	/**
	 * 接收人信息
	 */
	private String receiver;
	private String receiverName;
	private String receiverType; // 0: machine 1: people 2 客服

	private String offlineType;
	private String receiverFace;

	/**
	 * 机械人的答复：
	 * 
	 * @return
	 */
	private ZhiChiReplyAnswer answer;
	private String[] sugguestions;
	private String answerType;
	private String stripe;

	/**
	 * 富文本的消息内容
	 * 
	 * @return
	 */
	// private String url;
	private String picurl;
	private String rictype;

	
	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getRictype() {
		return rictype;
	}

	public void setRictype(String rictype) {
		this.rictype = rictype;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderType() {
		return senderType;
	}

	public void setSenderType(String senderType) {
		this.senderType = senderType;
	}

	public String getSenderFace() {
		return senderFace;
	}

	public void setSenderFace(String senderFace) {
		this.senderFace = senderFace;
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverType() {
		return receiverType;
	}

	public void setReceiverType(String receiverType) {
		this.receiverType = receiverType;
	}

	public String getOfflineType() {
		return offlineType;
	}

	public void setOfflineType(String offlineType) {
		this.offlineType = offlineType;
	}

	public String getReceiverFace() {
		return receiverFace;
	}

	public void setReceiverFace(String receiverFace) {
		this.receiverFace = receiverFace;
	}

	public ZhiChiReplyAnswer getAnswer() {
		return answer;
	}

	public void setAnswer(ZhiChiReplyAnswer answer) {
		this.answer = answer;
	}

	public String[] getSugguestions() {
		return sugguestions;
	}

	public void setSugguestions(String[] sugguestions) {
		this.sugguestions = sugguestions;
	}

	public String getAnswerType() {
		return answerType;
	}

	public void setAnswerType(String answerType) {
		this.answerType = answerType;
	}

	public String getStripe() {
		return stripe;
	}

	public void setStripe(String stripe) {
		this.stripe = stripe;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(int progressBar) {
		this.progressBar = progressBar;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public boolean isVoideIsPlaying() {
		return voideIsPlaying;
	}

	public void setVoideIsPlaying(boolean voideIsPlaying) {
		this.voideIsPlaying = voideIsPlaying;
	}

	public boolean getReconnectCustom() {
		return reconnectCustom;
	}

	public void setReconnectCustom(boolean reconnectCustom) {
		this.reconnectCustom = reconnectCustom;
	}

	public int getMysendMessageState() {
		return mysendMessageState;
	}

	public void setMysendMessageState(int mysendMessageState) {
		this.mysendMessageState = mysendMessageState;
	}

	public int getSendSuccessState() {
		return sendSuccessState;
	}

	public void setSendSuccessState(int sendSuccessState) {
		this.sendSuccessState = sendSuccessState;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public ZhiChiHistorySDKMsg getSdkMsg() {
		return sdkMsg;
	}

	public void setSdkMsg(ZhiChiHistorySDKMsg sdkMsg) {
		this.sdkMsg = sdkMsg;
	}

	public long getSendMessageTime() {
		return sendMessageTime;
	}

	public void setSendMessageTime(long sendMessageTime) {
		this.sendMessageTime = sendMessageTime;
	}

	public int getTextFailTimes() {
		return textFailTimes;
	}

	public void setTextFailTimes(int textFailTimes) {
		this.textFailTimes = textFailTimes;
	}
	
}