package com.sobot.chat.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 历史记录的实体
 * 
 * @author jinliang
 * 
 */
@SuppressLint("ParcelCreator")
public class ZhiChiMessage extends BaseCode<ZhiChiMessageBase> implements
		Parcelable {

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {

	}
}