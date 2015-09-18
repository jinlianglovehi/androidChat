package com.sobot.chat.model;

import java.io.Serializable;

/**
 *  history sdkMsg;
 * @author jinliang
 *
 */
public class ZhiChiHistorySDKMsg implements Serializable {

	private static final long serialVersionUID = 1L;

	private ZhiChiReplyAnswer answer;
	private String[] sugguestions;
	private String answerType;
	private String stripe;
	
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
	
	
}
