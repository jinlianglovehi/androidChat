package com.sobot.chat.weight;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.sobot.chat.activity.SobotChatActivity;
import com.sobot.chat.model.BaseCode;
import com.sobot.chat.utils.GsonUtil;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ZhiChiApi;

public class RobotDialog extends Dialog {

	public RobotDialog(Context context, int theme) {
		super(context, theme);
	}

	public RobotDialog(Context context, SobotChatActivity m, String robotComm) {
		super(context);
	}

	public static class Builder implements android.view.View.OnClickListener {

		private Context context;
		private String positiveButtonText, negativeButtonText, btnOkStr,
				btnNoStr, str1, str2, str3, str4, btnSubmitStr;
		private View contentView;

		private LinearLayout hideLayout;/* 点击"否"按钮以后显示的布局 */
		private boolean isChecked = true;/* 评价语是否被选中 */
		private Button btnJump, btnCancle, btnOk, btnNo, btnSubmit;/*
																	 * 解决问题，没有解决，
																	 * 提交评价
																	 */
		private TextView et1, et2, et3, et4;/* 评价语 */
		private boolean yn1 = false, yn2 = false, yn3 = false, yn4 = false;/* 评价与被选中 */
		private boolean isShow = false;
		private StringBuilder problem = new StringBuilder();/* 存放选中的评价语 */
		private EditText addContent;
		private SobotChatActivity mm;
		private ThankDialog d = null;
		private String color = null;

		public View.OnClickListener onClickListener;

		public Builder(Context context, SobotChatActivity m, String color) {
			this.context = context;
			this.mm = m;
			this.color = color;
		}

		public Builder setBtnOk(String btnOk, View.OnClickListener listener) {
			this.btnOkStr = btnOk;
			this.onClickListener = listener;
			return this;
		}

		public Builder setBtnNo(String btnNo, View.OnClickListener listener) {
			this.btnNoStr = btnNo;
			this.onClickListener = listener;
			return this;
		}

		public Builder setStr1(String str1, View.OnClickListener listener) {
			this.str1 = str1;
			this.onClickListener = listener;
			return this;
		}

		public Builder setStr2(String str2, View.OnClickListener listener) {
			this.str2 = str2;
			this.onClickListener = listener;
			return this;
		}

		public Builder setStr3(String str3, View.OnClickListener listener) {
			this.str3 = str3;
			this.onClickListener = listener;
			return this;
		}

		public Builder setStr4(String str4, View.OnClickListener listener) {
			this.str4 = str4;
			this.onClickListener = listener;
			return this;
		}

		public Builder setBtnSubmitStr(String str, View.OnClickListener listener) {
			this.btnSubmitStr = str;
			this.onClickListener = listener;
			return this;
		}

		/**
		 * Set a custom content view for the Dialog. If a message is set, the
		 * contentView is not added to the Dialog...
		 * 
		 * @param v
		 * @return
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText,
				View.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.onClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText,
				View.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.onClickListener = listener;
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		@SuppressWarnings("deprecation")
		public RobotDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final RobotDialog dialog = new RobotDialog(context,
					ResourceUtils.getIdByName(context, "style", "Dialog"));
			dialog.setCanceledOnTouchOutside(false);

			View layout = inflater.inflate(ResourceUtils.getIdByName(context,
					"layout", "robot_dialog_layout"), null);
			hideLayout = (LinearLayout) layout.findViewById(ResourceUtils
					.getIdByName(context, "id", "hide_layout"));

			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			// set the cancel button
			if (negativeButtonText != null) {
				btnJump = (Button) layout.findViewById(ResourceUtils
						.getIdByName(context, "id", "negativeButton"));
				btnJump.setText(negativeButtonText);
				btnJump.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
						GradientDrawable myGrad = (GradientDrawable) et1
								.getBackground();
						myGrad.setColor(Color.parseColor("#F5F5F5"));
						et1.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable", "login_edit_nomal"));
						et2.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable", "login_edit_nomal"));
						et3.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable", "login_edit_nomal"));
						if(isShow)
						et4.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable", "login_edit_nomal"));
					}
				});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(
						ResourceUtils.getIdByName(context, "id",
								"negativeButton")).setVisibility(View.GONE);
			}

			// set the confirm button
			if (positiveButtonText != null) {
				btnCancle = (Button) layout.findViewById(ResourceUtils
						.getIdByName(context, "id", "positiveButton"));
				btnCancle.setText(positiveButtonText);
				btnCancle.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
						mm.finish();
					}
				});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(
						ResourceUtils.getIdByName(context, "id",
								"positiveButton")).setVisibility(View.GONE);
			}

			if (contentView != null) {
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "content"))).removeAllViews();
				((LinearLayout) layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "content"))).addView(contentView,
						new LayoutParams(LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
			}

			if (btnOkStr != null) {
				btnOk = (Button) layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "btn_ok_robot"));
				btnOk.setText(btnOkStr);
				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						GradientDrawable myGrad = (GradientDrawable) et1
								.getBackground();
						comment("", "", 0);
						myGrad.setColor(Color.parseColor("#F5F5F5"));
						et1.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable", "login_edit_nomal"));
						et2.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable", "login_edit_nomal"));
						et3.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable", "login_edit_nomal"));
						et4.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable", "login_edit_nomal"));
						dialog.dismiss();
					}
				});
			}

			if (btnNoStr != null) {
				btnNo = (Button) layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "btn_no_robot"));
				GradientDrawable myGrad = (GradientDrawable) btnNo
						.getBackground();
				myGrad.setColor(Color.parseColor(color));
				btnNo.setText(btnNoStr);
				btnNo.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (isChecked) {
							hideLayout.setVisibility(View.VISIBLE);
						} else {
							hideLayout.setVisibility(View.GONE);
						}
						isChecked = !isChecked;
					}
				});
			}

			if (str1 != null) {
				et1 = (TextView) layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "every_case1"));
				et1.setText(str1);
				et1.setOnClickListener(this);
			}

			if (str2 != null) {
				et2 = (TextView) layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "every_case2"));
				et2.setText(str2);
				et2.setOnClickListener(this);
			}

			if (str3 != null) {
				et3 = (TextView) layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "every_case3"));
				et3.setText(str3);
				et3.setOnClickListener(this);
			}

			if (str4 != null) {
				isShow = true;
				et4 = (TextView) layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "every_case4"));
				et4.setText(str4);
				et4.setOnClickListener(this);
			}else
				layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "every_case4")).setVisibility(View.GONE);

			if (btnSubmitStr != null) {
				btnSubmit = (Button) layout.findViewById(ResourceUtils
						.getIdByName(context, "id", "btn_submit"));
				btnSubmit.setText(btnSubmitStr);
				addContent = (EditText) layout.findViewById(ResourceUtils
						.getIdByName(context, "id", "add_content"));
				btnSubmit.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						String suggest = addContent.getText().toString();
						if (yn1 == true) {
							problem.append(et1.getText().toString() + ";");
						}
						if (yn2 == true) {
							problem.append(et2.getText().toString() + ";");
						}
						if (yn3 == true) {
							problem.append(et3.getText().toString() + ";");
						}
						if (yn4 == true) {
							problem.append(et4.getText().toString() + ";");
						}
						hideLayout.setVisibility(View.GONE);
						
						comment(problem + "", suggest, 1);
						
						GradientDrawable myGrad = (GradientDrawable) et1
								.getBackground();
						myGrad.setColor(Color.parseColor("#F5F5F5"));
						et1.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable", "login_edit_nomal"));
						et2.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable", "login_edit_nomal"));
						et3.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable", "login_edit_nomal"));
						if(isShow)
						et4.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable", "login_edit_nomal"));
						
						dialog.dismiss();
					}
				});
				btnSubmit.setFocusable(true);
				GradientDrawable myGrad = (GradientDrawable) btnSubmit
						.getBackground();
				myGrad.setColor(Color.parseColor(color));
				btnSubmit.setBackgroundResource(ResourceUtils.getIdByName(
						context, "drawable", "subbutton_shap"));
			}
			dialog.setContentView(layout);
			return dialog;
		}

		private void comment(String problem, String suggest, int isresolve) {
			Map<String, String> map = new HashMap<String, String>();
			LogUtils.i("CID==" + mm.cid + ";uid==>" + mm.uid + ";problem==>"
					+ problem + ";suggest==>" + suggest + ";isresolve==>"
					+ isresolve);
			map.put("cid", mm.cid);
			map.put("userId", mm.uid);
			map.put("type", "0");
			map.put("problem", problem + "");
			map.put("suggest", suggest);
			map.put("isresolve", isresolve + "");

			mm.getData(HttpMethod.POST, ZhiChiApi.api_chat_comment, map,
					new RequestCallBack<String>() {

						@SuppressWarnings("unchecked")
						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							LogUtils.i("arg0.result===>" + arg0.result);
							if (!TextUtils.isEmpty(arg0.result)) {
								try {
									BaseCode<String> code = (BaseCode<String>) GsonUtil
											.jsonToBean(arg0.result,BaseCode.class);
									String resultCode = code.getCode();
									if (resultCode.trim().equals("1")) {
										LogUtils.i("评论成功*****");
										showThankDialog();
									}
								} catch (Exception e) {
								}
							}
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							LogUtils.i("失败失败失败失败失败" + arg1 + "***"
									+ arg0.toString());
						}
					});
		}

		@SuppressWarnings("deprecation")
		private void showThankDialog() {
			ThankDialog.Builder customBuilder = new ThankDialog.Builder(context);
			customBuilder.setMessage("感谢您的反馈＾―＾!");
			d = customBuilder.create();
			d.show();
			
			int width = getScreenWidth(context);
			WindowManager windowManager = mm.getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = d.getWindow().getAttributes();
			if (width == 480) {
				lp.width = (int) (display.getWidth() - 80); // 设置宽度
			} else {
				lp.width = (int) (display.getWidth() - 120); // 设置宽度
			}
			d.getWindow().setAttributes(lp);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(2000);
						d.dismiss();
						mm.finish();
					} catch (Exception e) {
					}
				}
			}).start();
		}

		@Override
		public void onClick(View v) {
			GradientDrawable myGrad = null;
			if (v == et1) {
				if (!yn1) {
					et1.setTextColor(Color.parseColor("#ffffff"));
					myGrad = (GradientDrawable) et1.getBackground();
					myGrad.setColor(Color.parseColor(color));
				} else {
					et1.setTextColor(Color.parseColor("#656565"));
					myGrad = (GradientDrawable) et1.getBackground();
					myGrad.setColor(Color.parseColor("#F5F5F5"));
				}
				et1.setBackgroundResource(ResourceUtils.getIdByName(context,
						"drawable", "login_edit_nomal"));
				yn1 = !yn1;
			}
			if (v == et2) {
				if (!yn2) {
					et2.setTextColor(Color.parseColor("#ffffff"));
					myGrad = (GradientDrawable) et2.getBackground();
					myGrad.setColor(Color.parseColor(color));
				} else {
					et2.setTextColor(Color.parseColor("#656565"));
					myGrad = (GradientDrawable) et2.getBackground();
					myGrad.setColor(Color.parseColor("#F5F5F5"));
				}
				et2.setBackgroundResource(ResourceUtils.getIdByName(context,
						"drawable", "login_edit_nomal"));
				yn2 = !yn2;
			}

			if (v == et3) {
				if (!yn3) {
					et3.setTextColor(Color.parseColor("#ffffff"));
					myGrad = (GradientDrawable) et3.getBackground();
					myGrad.setColor(Color.parseColor(color));
				} else {
					et3.setTextColor(Color.parseColor("#656565"));
					myGrad = (GradientDrawable) et3.getBackground();
					myGrad.setColor(Color.parseColor("#F5F5F5"));
				}
				et3.setBackgroundResource(ResourceUtils.getIdByName(context,
						"drawable", "login_edit_nomal"));
				yn3 = !yn3;
			}

			if (v == et4) {
				if (!yn4) {
					et4.setTextColor(Color.parseColor("#ffffff"));
					myGrad = (GradientDrawable) et4.getBackground();
					myGrad.setColor(Color.parseColor(color));
				} else {
					et4.setTextColor(Color.parseColor("#656565"));
					myGrad = (GradientDrawable) et4.getBackground();
					myGrad.setColor(Color.parseColor("#F5F5F5"));
				}
				et4.setBackgroundResource(ResourceUtils.getIdByName(context,
						"drawable", "login_edit_nomal"));
				yn4 = !yn4;
			}
		}

		/**
		 * 得到设备屏幕的宽度
		 */
		private int getScreenWidth(Context context) {
			return context.getResources().getDisplayMetrics().widthPixels;
		}
	}
}