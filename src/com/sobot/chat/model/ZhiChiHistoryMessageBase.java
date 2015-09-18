package com.sobot.chat.model;

import java.io.Serializable;
import java.util.List;

public class ZhiChiHistoryMessageBase implements Serializable {

	private static final long serialVersionUID = 1L;
	private String date;
	private List<ZhiChiMessageBase> content;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<ZhiChiMessageBase> getContent() {
		return content;
	}

	public void setContent(List<ZhiChiMessageBase> content) {
		this.content = content;
	}
}