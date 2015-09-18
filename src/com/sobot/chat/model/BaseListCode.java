package com.sobot.chat.model;

import java.util.List;

public class BaseListCode<T> {

	private String code;
	private List<T> data;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
}