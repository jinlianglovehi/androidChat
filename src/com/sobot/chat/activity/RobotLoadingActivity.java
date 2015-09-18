package com.sobot.chat.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.sobot.chat.activity.base.TitleActivity;
import com.sobot.chat.model.Information;
import com.sobot.chat.model.ZhiChiInitModeBase;
import com.sobot.chat.model.ZhiChiInitModel;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.GsonUtil;
import com.sobot.chat.utils.HttpUtilsTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiApi;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.weight.PowerImageView;

@SuppressLint("ShowToast")
public class RobotLoadingActivity extends TitleActivity {

	private Information info;
	private String sysNum, partnerId, color, nickName, phone, email;
	private TextView textReConnect;
	private PowerImageView image_view;
	private TextView txt_loading;
	private ImageView icon_nonet;
	private Button btn_reconnect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResourceUtils.getIdByName(this, "layout",
				"loding_layout"));
		info = (Information) getIntent().getBundleExtra("bundle")
				.getSerializable("info");
		sysNum = info.getAppKey();
		partnerId = info.getUid();
		color = info.getColor();
		nickName = info.getNickName();
		phone = info.getPhone();
		email = info.getEmail();

		LogUtils.i("sysNum--->" + sysNum);

		if (partnerId.length() == 0) {
			partnerId = CommonUtils.getPartnerId(RobotLoadingActivity.this);
		}
		LogUtils.i("partnerId--->" + partnerId);
		if (color.length() == 0) {
			color = "#09aeb0";
		}
		LogUtils.i("color--->" + color);
		SharedPreferencesUtil.saveStringData(this, "robot_current_themeColor",
				color);
		SharedPreferencesUtil.saveStringData(this, "sysNum", sysNum);

		relative.setBackgroundColor(Color.parseColor(color));
		initView();
		initData();
	}

	/**
	 * 项目初始化
	 */
	private void initView() {
		txt_loading = (TextView) findViewById(ResourceUtils.getIdByName(this,
				"id", "txt_loading"));

		textReConnect = (TextView) findViewById(ResourceUtils.getIdByName(this,
				"id", "textReConnect"));

		image_view = (PowerImageView) findViewById(ResourceUtils.getIdByName(
				this, "id", "image_view"));
		icon_nonet = (ImageView) findViewById(ResourceUtils.getIdByName(this,
				"id", "icon_nonet"));
		btn_reconnect = (Button) findViewById(ResourceUtils.getIdByName(this,
				"id", "btn_reconnect"));

		btn_reconnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				initData();
			}
		});

		setShowNetRemind(false);
		setTitle("提示");
		icon_nonet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// initData();
			}
		});
		textReConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// initData();
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mBackwardbButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void initData() {

		// 初始化
		Map<String, String> map = new HashMap<String, String>();
		map.put("sysNum", sysNum);
		map.put("partnerId", partnerId);
		map.put("way", 3 + "");
		map.put("from", "2");
		map.put("version", CommonUtils.getVersion(RobotLoadingActivity.this)
				+ "");
		map.put("system", "android" + android.os.Build.VERSION.RELEASE);
		LogUtils.i("当前版本是" + "android" + android.os.Build.VERSION.RELEASE);

		if (nickName.length() != 0) {
			map.put("uname", nickName);
		}

		if (phone.length() != 0) {
			map.put("tel", phone);
		}

		if (email.length() != 0) {
			map.put("email", email);
		}

		// 添加from客户端的方式
		HttpUtilsTools.getData(HttpMethod.POST, ZhiChiApi.api_robot_chat_init,
				map, new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 修改初始化的界面
						LogUtils.i("arg1--->" + arg1 + "---arg0--->" + arg0);
						txt_loading.setVisibility(View.GONE);
						image_view.setVisibility(View.GONE);
						textReConnect.setVisibility(View.VISIBLE);
						icon_nonet.setVisibility(View.VISIBLE);
						btn_reconnect.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						super.onLoading(total, current, isUploading);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						LogUtils.i("initData:responseInfo--" + arg0.result);
						ZhiChiInitModel result = (ZhiChiInitModel) GsonUtil
								.jsonToBean(arg0.result, ZhiChiInitModel.class);
						if (ZhiChiConstant.result_success_code == Integer
								.parseInt(result.getCode())) {
							ZhiChiInitModeBase initModel = result.getData();
							initModel.setColor(color);
							SharedPreferencesUtil.saveStringData(
									RobotLoadingActivity.this,
									"robot_initResult", arg0.result);
							try {
								startActivity(new Intent(
										RobotLoadingActivity.this,
										SobotChatActivity.class));
								finish();
							} catch (Exception e) {
							}
						} else {
							Toast.makeText(RobotLoadingActivity.this,
									"请输入您的AppKey", 100).show();
							finish();
						}
					}
					//
				});
	}

	@Override
	public void forwordMethod() {

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}