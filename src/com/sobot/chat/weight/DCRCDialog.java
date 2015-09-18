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
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.sobot.chat.activity.SobotChatActivity;
import com.sobot.chat.activity.StartActivity;
import com.sobot.chat.model.BaseCode;
import com.sobot.chat.utils.GsonUtil;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ZhiChiApi;

/**
 * 人工客服评价Dialog
 * 
 * @author WangShuai
 * 
 */
public class DCRCDialog extends Dialog {

	public DCRCDialog(Context context, int theme) {
		super(context, theme);
	}

	public DCRCDialog(Context context, StartActivity m, String robotComm) {
		super(context);
	}

	public static class Builder implements View.OnClickListener {

		private Context context;
		private String positiveButtonText, negativeButtonText, str1, str2,
				str3, str4, btnSubmitStr;

		private LinearLayout hideLayout;/* 点击"否"按钮以后显示的布局 */
		private Button btnJump, btnCancle, btnSubmit;/*
													 * 解决问题，没有解决， 提交评价
													 */
		private RatingBar ratingBar;
		private TextView et5, et6, et7, et8;;/* 评价语 */
		private boolean yn5 = false, yn6 = false, yn7 = false, yn8 = false;/* 评价语被选中 */
		private StringBuilder problem = new StringBuilder();/* 存放选中的评价语 */
		private boolean isShow = false;
		private EditText addContent;
		private SobotChatActivity mm;
		private ThankDialog d = null;
		private String color = null;
		private float score = -1f;

		public View.OnClickListener onClickListener;

		public Builder(Context context, SobotChatActivity m, String color) {
			this.context = context;
			this.mm = m;
			this.color = color;
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
		public DCRCDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final DCRCDialog dialog = new DCRCDialog(context,
					ResourceUtils.getIdByName(context, "style", "Dialog"));
			dialog.setCanceledOnTouchOutside(false);

			View layout = inflater.inflate(ResourceUtils.getIdByName(context,
					"layout", "dcrc_dialog_layout"), null);

			hideLayout = (LinearLayout) layout.findViewById(ResourceUtils
					.getIdByName(context, "id", "hide_layout"));

			ratingBar = (RatingBar) layout.findViewById(ResourceUtils
					.getIdByName(context, "id", "ratingBar"));
			ratingBar
					.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
						@Override
						public void onRatingChanged(RatingBar arg0, float arg1,
								boolean arg2) {
							score = ratingBar.getRating();
							if (0 < score && score < 5) {
								hideLayout.setVisibility(View.VISIBLE);
							} else {
								hideLayout.setVisibility(View.GONE);
								comment("", score + "", "", 0);

								GradientDrawable myGrad = (GradientDrawable) et5
										.getBackground();
								myGrad.setColor(Color.parseColor("#ffffff"));
								et5.setBackgroundResource(ResourceUtils
										.getIdByName(context, "drawable",
												"dcrc_dialog_textview_shape"));
								et6.setBackgroundResource(ResourceUtils
										.getIdByName(context, "drawable",
												"dcrc_dialog_textview_shape"));
								et7.setBackgroundResource(ResourceUtils
										.getIdByName(context, "drawable",
												"dcrc_dialog_textview_shape"));
								et8.setBackgroundResource(ResourceUtils
										.getIdByName(context, "drawable",
												"dcrc_dialog_textview_shape"));

								dialog.dismiss();
							}
						}
					});

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
						GradientDrawable myGrad = (GradientDrawable) et5
								.getBackground();
						myGrad.setColor(Color.parseColor("#ffffff"));

						et5.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable",
								"dcrc_dialog_textview_shape"));
						et6.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable",
								"dcrc_dialog_textview_shape"));
						et7.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable",
								"dcrc_dialog_textview_shape"));
						if(isShow)
						et8.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable",
								"dcrc_dialog_textview_shape"));
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

						GradientDrawable myGrad = (GradientDrawable) et5
								.getBackground();
						myGrad.setColor(Color.parseColor("#ffffff"));
						et5.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable",
								"dcrc_dialog_textview_shape"));
						et6.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable",
								"dcrc_dialog_textview_shape"));
						et7.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable",
								"dcrc_dialog_textview_shape"));
						if(isShow)
						et8.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable",
								"dcrc_dialog_textview_shape"));

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

			if (str1 != null) {
				et5 = (TextView) layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "every_case5"));
				et5.setText(str1);
				et5.setOnClickListener(this);
			}

			if (str2 != null) {
				et6 = (TextView) layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "every_case6"));
				et6.setText(str2);
				et6.setOnClickListener(this);
			}

			if (str3 != null) {
				et7 = (TextView) layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "every_case7"));
				et7.setText(str3);
				et7.setOnClickListener(this);
			}

			if (str4 != null) {
				isShow = true;
				et8 = (TextView) layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "every_case8"));
				et8.setText(str4);
				et8.setOnClickListener(this);
			}else
				layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "every_case8")).setVisibility(View.GONE);

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

						if (yn5 == true) {
							problem.append(et5.getText().toString() + ";");
						}
						if (yn6 == true) {
							problem.append(et6.getText().toString() + ";");
						}
						if (yn7 == true) {
							problem.append(et7.getText().toString() + ";");
						}
						if (yn8 == true) {
							problem.append(et8.getText().toString() + ";");
						}

						comment(problem + "", score + "", suggest, 1);
						hideLayout.setVisibility(View.GONE);
						GradientDrawable myGrad = (GradientDrawable) et5
								.getBackground();
						myGrad.setColor(Color.parseColor("#ffffff"));
						et5.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable",
								"dcrc_dialog_textview_shape"));
						et6.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable",
								"dcrc_dialog_textview_shape"));
						et7.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable",
								"dcrc_dialog_textview_shape"));
						if(isShow)
						et8.setBackgroundResource(ResourceUtils.getIdByName(
								context, "drawable",
								"dcrc_dialog_textview_shape"));
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

		private void comment(String problem, String score, String suggest,
				int isresolve) {
			Map<String, String> map = new HashMap<String, String>();
			LogUtils.i("CID==" + mm.cid + ";uid==>" + mm.uid + ";problem==>"
					+ problem + ";suggest==>" + suggest + ";isresolve==>"
					+ isresolve + ";score==>" + score);
			map.put("cid", mm.cid);
			map.put("userId", mm.uid);
			map.put("type", "0");
			map.put("problem", problem + "");
			map.put("suggest", suggest);
			map.put("isresolve", isresolve + "");
			map.put("source", score);

			mm.getData(HttpMethod.POST, ZhiChiApi.api_chat_comment, map,
					new RequestCallBack<String>() {

						@SuppressWarnings("unchecked")
						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							LogUtils.i("arg0.result===>" + arg0.result);
							if (!TextUtils.isEmpty(arg0.result)) {
								try {
									BaseCode<String> code = (BaseCode<String>) GsonUtil
											.jsonToBean(arg0.result,
													BaseCode.class);
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
			if (v == et5) {
				if (!yn5) {
					et5.setTextColor(Color.parseColor("#ffffff"));
					myGrad = (GradientDrawable) et5.getBackground();
					myGrad.setColor(Color.parseColor(color));
				} else {
					et5.setTextColor(Color.parseColor("#656565"));
					myGrad = (GradientDrawable) et5.getBackground();
					myGrad.setColor(Color.parseColor("#ffffff"));
				}
				et5.setBackgroundResource(ResourceUtils.getIdByName(context,
						"drawable", "dcrc_dialog_textview_shape"));
				yn5 = !yn5;
			}
			if (v == et6) {
				if (!yn6) {
					et6.setTextColor(Color.parseColor("#ffffff"));
					myGrad = (GradientDrawable) et6.getBackground();
					myGrad.setColor(Color.parseColor(color));
				} else {
					et6.setTextColor(Color.parseColor("#656565"));
					myGrad = (GradientDrawable) et6.getBackground();
					myGrad.setColor(Color.parseColor("#ffffff"));
				}
				et6.setBackgroundResource(ResourceUtils.getIdByName(context,
						"drawable", "dcrc_dialog_textview_shape"));
				yn6 = !yn6;
			}

			if (v == et7) {
				if (!yn7) {
					et7.setTextColor(Color.parseColor("#ffffff"));
					myGrad = (GradientDrawable) et7.getBackground();
					myGrad.setColor(Color.parseColor(color));
				} else {
					et7.setTextColor(Color.parseColor("#656565"));
					myGrad = (GradientDrawable) et7.getBackground();
					myGrad.setColor(Color.parseColor("#ffffff"));
				}
				et7.setBackgroundResource(ResourceUtils.getIdByName(context,
						"drawable", "dcrc_dialog_textview_shape"));
				yn7 = !yn7;
			}

			if (v == et8) {
				if (!yn8) {
					et8.setTextColor(Color.parseColor("#ffffff"));
					myGrad = (GradientDrawable) et8.getBackground();
					myGrad.setColor(Color.parseColor(color));
				} else {
					et8.setTextColor(Color.parseColor("#656565"));
					myGrad = (GradientDrawable) et8.getBackground();
					myGrad.setColor(Color.parseColor("#ffffff"));
				}
				et8.setBackgroundResource(ResourceUtils.getIdByName(context,
						"drawable", "dcrc_dialog_textview_shape"));
				yn8 = !yn8;
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