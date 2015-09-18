/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sobot.chat.weight;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 自定义的textview，用来处理复制粘贴的消息
 * 
 */
public class PasteEditText extends EditText {
	@SuppressWarnings("unused")
	private Context context;

	public PasteEditText(Context context) {
		super(context);
		this.context = context;
	}

	public PasteEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public PasteEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public boolean onTextContextMenuItem(int id) {
		if (id == android.R.id.paste) {
			ClipboardManager clip = (ClipboardManager) getContext()
					.getSystemService(Context.CLIPBOARD_SERVICE);
			@SuppressWarnings("unused")
			String text = clip.getText().toString();
			// 需要添加代码的地方 TODO
			/*
			 * if(text.startsWith(ChatActivity.COPY_IMAGE)){ text =
			 * text.replace(ChatActivity.COPY_IMAGE, ""); Intent intent = new
			 * Intent(context,FXAlertDialog.class); intent.putExtra("title",
			 * "发送以下图片？"); intent.putExtra("forwardImage", text);
			 * intent.putExtra("cancel", true);
			 * ((Activity)context).startActivityForResult
			 * (intent,ChatActivity.REQUEST_CODE_COPY_AND_PASTE); }
			 */
		}
		return super.onTextContextMenuItem(id);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start,
			int lengthBefore, int lengthAfter) {
		// TODO 需要代做的地方
		/*
		 * if(!TextUtils.isEmpty(text) &&
		 * text.toString().startsWith(ChatActivity.COPY_IMAGE)){ setText(""); }
		 */
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
	}
}