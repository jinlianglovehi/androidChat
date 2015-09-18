package com.sobot.chat.activity.base;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.sobot.chat.adapter.base.MessageAdapter;
import com.sobot.chat.model.ZhiChiInitModeBase;
import com.sobot.chat.model.ZhiChiMessage;
import com.sobot.chat.model.ZhiChiMessageBase;
import com.sobot.chat.model.ZhiChiReplyAnswer;
import com.sobot.chat.utils.AudioTools;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.GsonUtil;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SendPictureUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiApi;
import com.sobot.chat.utils.ZhiChiConstant;

@SuppressLint("NewApi")
public abstract class TitleActivity extends FragmentActivity implements
		OnClickListener {

	public LinearLayout back;
	public TextView mTitleTextView;
	public Button mBackwardbButton;
	private Button mForwardButton;
	private FrameLayout mContentLayout;
	public RelativeLayout relative;
	public RelativeLayout net_status_remide;
	protected SharedPreferencesUtil sp;
	protected ExecutorService executorService;
	public static String sysNum;
	protected static final String PATH = ZhiChiConstant.imagePositionPath;
	/*
	 * 请求成功
	 */
	public static final int result_ok = 0;
	/**
	 * 请求失败
	 */
	public static final int result_fail = 1;

	public boolean isAllowLogI = true;
	protected ZhiChiInitModeBase initModel;
	protected File cameraFile;
	protected String currentUserName;
	/**
	 * 定时任务的处理 用户的定时任务
	 */
	protected  int type =-1 ;
	protected Timer timerUserInfo;
	protected TimerTask taskUserInfo;
	protected int noReplyTimeUserInfo = 0; // 用户已经无应答的时间
	/**
	 * 客服的定时任务
	 */
	protected Timer timerCustom;
	protected TimerTask taskCustom;
	protected int noReplyTimeCustoms = 0;// 客服无应答的时间

	/**
	 * 录音的定时
	 */
	protected Timer voiceTimer;
	protected TimerTask voiceTimerTask;
	protected int voiceTimerLong = 0;
	protected String voiceTimeLongStr = "00";// 时间的定时的任务

	protected int timeInterval = 3;
	protected int current_client_model = ZhiChiConstant.client_model_robot;
	
	public boolean is_startCustomTimerTask=false;
	
	
	public boolean is_startTask = true;
	public boolean is_start_push = true;
	/**
	 * 工具类
	 */
	protected SendPictureUtils sendPictureUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setupViews(); // 加载 activity_title 布局 ，并获取标题及两侧按�?
		LogUtils.allowI = true;
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	@SuppressLint("NewApi")
	private void setupViews() {
		super.setContentView(ResourceUtils.getIdByName(this, "layout",
				"activity_title"));
		relative = (RelativeLayout) findViewById(ResourceUtils.getIdByName(
				this, "id", "layout_titlebar"));

		mTitleTextView = (TextView) findViewById(ResourceUtils.getIdByName(
				this, "id", "text_title"));
		mContentLayout = (FrameLayout) findViewById(ResourceUtils.getIdByName(
				this, "id", "layout_content"));
		mBackwardbButton = (Button) findViewById(ResourceUtils.getIdByName(
				this, "id", "button_backward"));
		net_status_remide = (RelativeLayout) findViewById(ResourceUtils
				.getIdByName(this, "id", "net_status_remide"));
		net_status_remide.setBackgroundColor(Color.rgb(33, 35, 36));
		net_status_remide.setAlpha(80);
		//
		// 创建线程池
		initThreadPools();
		mForwardButton = (Button) findViewById(ResourceUtils.getIdByName(this,
				"id", "button_forward"));
		mForwardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				forwordMethod();
			}
		});

		back = (LinearLayout) findViewById(ResourceUtils.getIdByName(this, "id", "back"));
	}

	// 初始化 线程池
	public void setShowNetRemind(boolean isShow) {
		net_status_remide.setVisibility(isShow == true ? View.VISIBLE
				: View.GONE);
	}

	private void initThreadPools() {
		if (executorService == null) {
			executorService = Executors.newFixedThreadPool(5);
		}
	}

	public abstract void forwordMethod();

	/**
	 * 是否显示返回按钮
	 * 
	 * @param backwardResid
	 *            文字
	 * @param show
	 *            true则显�?
	 */
	protected void showLeftView(int backwardResid, boolean show) {
		if (mBackwardbButton != null) {
			if (show) {
				mBackwardbButton.setText(backwardResid);
				mBackwardbButton.setVisibility(View.VISIBLE);
			} else {
				mBackwardbButton.setVisibility(View.INVISIBLE);
			}
		} // else ignored
	}

	/**
	 * 提供是否显示提交按钮
	 * 
	 * @param forwardResId
	 *            文字
	 * @param show
	 *            true则显�?
	 */
	protected void showRightView(int forwardResId, boolean show) {
		if (mForwardButton != null) {
			if (show) {
				mForwardButton.setVisibility(View.VISIBLE);
				mForwardButton.setText(forwardResId);
			} else {
				mForwardButton.setVisibility(View.INVISIBLE);
			}
		} // else ignored
	}

	// 设置标题内容
	@Override
	public void setTitle(int titleId) {
		mTitleTextView.setText(titleId);
	}

	// 设置标题内容
	@Override
	public void setTitle(CharSequence title) {
		mTitleTextView.setText(title);
	}

	// 设置标题文字颜色
	@Override
	public void setTitleColor(int textColor) {
		mTitleTextView.setTextColor(textColor);
	}

	// 取出FrameLayout并调用父类removeAllViews()方法
	@Override
	public void setContentView(int layoutResID) {
		mContentLayout.removeAllViews();
		View.inflate(this, layoutResID, mContentLayout);
		onContentChanged();
	}

	@Override
	public void setContentView(View view) {
		mContentLayout.removeAllViews();
		mContentLayout.addView(view);
		onContentChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#setContentView(android.view.View,
	 * android.view.ViewGroup.LayoutParams)
	 */
	@Override
	public void setContentView(View view, LayoutParams params) {
		mContentLayout.removeAllViews();
		mContentLayout.addView(view, params);
		onContentChanged();
	}

	@Override
	public abstract void onClick(View v);

	// ##################### 网络请求 #########################
	public void getData(HttpMethod httpMethod, String url,
			Map<String, String> map, RequestCallBack<String> callBack) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configDefaultHttpCacheExpiry(1);
		// 配置top请求的配置
		HttpParams paramsConfig = httpUtils.getHttpClient().getParams();
		HttpConnectionParams.setSocketBufferSize(paramsConfig, 1024 * 80);
		RequestParams params = new RequestParams();
		List<NameValuePair> arrayList = new ArrayList<NameValuePair>();
		for (String key : map.keySet()) {
			BasicNameValuePair nameValuePair = new BasicNameValuePair(key,
					map.get(key));
			arrayList.add(nameValuePair);
		}
		// 添加from客户端的方式
		BasicNameValuePair fromMethod = new BasicNameValuePair("from", "2");
		arrayList.add(fromMethod);
		// 添加版本号
		BasicNameValuePair versionStr = new BasicNameValuePair("version",
				CommonUtils.getVersion(TitleActivity.this) + "");
		arrayList.add(versionStr);
		params.addBodyParameter(arrayList);
		httpUtils.send(httpMethod, url, params, callBack);
	}

	public void sendPicture(Map<String, String> map, final String filePath,
			final Handler handler, final String id) {

		RequestParams params = new RequestParams();
		// 基本传递参数
		List<NameValuePair> arrayList = new ArrayList<NameValuePair>();
		for (String key : map.keySet()) {
			BasicNameValuePair nameValuePair = new BasicNameValuePair(key,
					map.get(key));
			arrayList.add(nameValuePair);
		}
		// 添加发送参数 以及发送的版本号
		// 添加from客户端的方式
		BasicNameValuePair fromMethod = new BasicNameValuePair("from", "2");
		arrayList.add(fromMethod);
		// 添加版本号
		BasicNameValuePair versionStr = new BasicNameValuePair("version",
				CommonUtils.getVersion(TitleActivity.this) + "");
		arrayList.add(versionStr);

		params.addBodyParameter(arrayList);
		// 文件流传递参数
		// params.addBodyParameter(filePath.replace("/", ""), new
		// File(filePath));
		params.addBodyParameter("file", new File(filePath));
		// 添加图片文件的字符流
		getDataWithParam(HttpMethod.POST,
				ZhiChiApi.api_sendFile_to_customeService, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						LogUtils.i("发送图片error:" + arg1.toString()
								+ "exception:" + arg0);
						if (id != null) {
							Message message = handler.obtainMessage();
							message.what = ZhiChiConstant.hander_sendPicStatus_fail;
							message.obj = id;
							handler.sendMessage(message);
						}
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						super.onLoading(total, current, isUploading);
						LogUtils.i("发送图片 进度:" + current + "/" + total);
						if (id != null) {
							Message message = handler.obtainMessage();
							message.what = ZhiChiConstant.hander_sendPicIsLoading;
							message.obj = id;
							// 设置消息的进度
							message.arg1 = Integer.parseInt(String
									.valueOf(current * 100 / total));
							handler.sendMessage(message);
						}
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						LogUtils.i("返回图片发送成功success后的操作：" + arg0.result);
						ZhiChiMessage result = (ZhiChiMessage) GsonUtil
								.jsonToBean(arg0.result, ZhiChiMessage.class);
						if (ZhiChiConstant.result_success_code == Integer
								.parseInt(result.getCode())) {
							if (id != null) {
								Message message = handler.obtainMessage();
								message.what = ZhiChiConstant.hander_sendPicStatus_success;
								message.obj = id;
								handler.sendMessage(message);
							}
						}
					}
				});

	}

	public void getDataWithParam(HttpMethod httpMethod, String url,
			RequestParams params, RequestCallBack<String> callBack) {
		HttpUtils httpUtils = new HttpUtils(1000 * 30);
		httpUtils.send(httpMethod, url, params, callBack);
	}

	//

	public void showText(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public void showText(Context context, int resourceId) {
		Toast.makeText(context, resourceId, Toast.LENGTH_SHORT).show();
	}

	// ##################### 更新界面的ui ###############################

	/**
	 * handler 消息实体message 更新ui界面
	 * 
	 * @param messageAdapter
	 * @param msg
	 */
	public void updateUiMessage(MessageAdapter messageAdapter, Message msg) {
		ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
		messageAdapter.addData(myMessage);
		messageAdapter.notifyDataSetChanged();
	}

	public void updateTextMessageStatus(MessageAdapter messageAdapter,
			Message msg) {
		ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
		messageAdapter.updateVoiceStatusById(myMessage.getId(),
				myMessage.getSendSuccessState());
		messageAdapter.notifyDataSetChanged();
	}

	public void updateVoiceStatusMessage(MessageAdapter messageAdapter,
			Message msg) {
		ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
		messageAdapter.updateVoiceStatusById(myMessage.getId(),
				myMessage.getSendSuccessState());
		messageAdapter.notifyDataSetChanged();
	}

	/**
	 * 通过消息实体 zhiChiMessage进行封装
	 * 
	 * @param messageAdapter
	 * @param zhichiMessage
	 */
	public void updateUiMessage(MessageAdapter messageAdapter,
			ZhiChiMessageBase zhichiMessage) {
		messageAdapter.addData(zhichiMessage);
		messageAdapter.notifyDataSetChanged();
	}

	/**
	 * 通过消息实体 zhiChiMessage进行封装
	 * 
	 * @param messageAdapter
	 * @param zhichiMessage
	 */
	public void updateUiMessageBefore(MessageAdapter messageAdapter,
			ZhiChiMessageBase zhichiMessage) {
		messageAdapter.addDataBefore(zhichiMessage);
		messageAdapter.notifyDataSetChanged();
	}

	@SuppressWarnings("unchecked")
	public void updateUiMessageList(MessageAdapter messageAdapter, Message msg) {
		List<ZhiChiMessageBase> msgList = (List<ZhiChiMessageBase>) msg.obj;
		messageAdapter.addData(msgList);
		messageAdapter.notifyDataSetChanged();
	}
	

	/**
	 * 
	 * @param msgContent
	 * @param handler
	 */
	public void updateUiMessageStatus(MessageAdapter messageAdapter, String id,
			int status, int progressBar) {
		messageAdapter.updateMsgInfoById(id, status, progressBar);
		messageAdapter.notifyDataSetChanged();
	}

	// ################### 发送 消息 通知handler #######################
	// 文本通知
	public void sendTextMessageToHandler(String id, String msgContent,
			Handler handler, int isSendStatus, boolean isUpdate) {
		ZhiChiMessageBase myMessage = new ZhiChiMessageBase();
		myMessage.setId(id);
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		reply.setMsg(msgContent);
		reply.setMsgType(ZhiChiConstant.message_type_text + "");
		myMessage.setAnswer(reply);
		myMessage.setSenderType(ZhiChiConstant.message_type_wo_text + "");
		myMessage.setSendSuccessState(isSendStatus);
		if (isSendStatus == 0) { // 文本发送失败
			myMessage.setTextFailTimes(1);
		}
		Message handMyMessage = handler.obtainMessage();
		if (!isUpdate) {// 显示发送成功的状态
			myMessage.setSendMessageTime(0);
			// myMessage.setFirstSend(true);
			handMyMessage.what = ZhiChiConstant.hander_my_senderMessage;

		} else if (isUpdate) {// 发送失败的状态
			handMyMessage.what = ZhiChiConstant.hander_my_update_senderMessageStatus;
		}

		handMyMessage.obj = myMessage;
		handler.sendMessage(handMyMessage);
	}

	// 图片通知
	public void sendImageMessageToHandler(String imageUrl, Handler handler,
			String id) {
		ZhiChiMessageBase zhichiMessage = new ZhiChiMessageBase();
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		reply.setMsg(imageUrl);
		zhichiMessage.setAnswer(reply);
		zhichiMessage.setId(id);
		// zhichiMessage.setMsgType(ZhiChiConstant.message_type_pic+"");
		zhichiMessage.setSenderType(ZhiChiConstant.message_type_wo_sendImage
				+ "");
		Message message = new Message();
		message.what = ZhiChiConstant.message_type_wo_sendImage;//
		message.obj = zhichiMessage;
		handler.sendMessage(message);
	}

	// 发送语音消息
	/**
	 * 
	 * @param id
	 *            语音暂时产生唯一标识符
	 * @param voiceUrl
	 *            语音的地址
	 * @param voiceTimeLongStr
	 *            语音的时长
	 * @param isSendSuccess
	 * @param isUpdate
	 * @param handler
	 */
	public void sendVoiceMessageToHandler(String id, String voiceUrl,
			String voiceTimeLongStr, int isSendSuccess, boolean isUpdate,
			final Handler handler) {
		ZhiChiMessageBase zhichiMessage = new ZhiChiMessageBase();
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		reply.setMsg(voiceUrl);
		reply.setDuration(voiceTimeLongStr);
		zhichiMessage.setAnswer(reply);
		zhichiMessage
				.setSenderType(ZhiChiConstant.message_type_send_voice + "");
		zhichiMessage.setId(id);
		zhichiMessage.setSendSuccessState(isSendSuccess);
		// zhichiMessage.setMsgType(ZhiChiConstant.message_type_voice+"");
		// 设置语音的时长的操作

		Message message = handler.obtainMessage();
		if (isUpdate) {// 更新界面布局
			message.what = ZhiChiConstant.message_type_update_voice;
		} else {
			message.what = ZhiChiConstant.message_type_send_voice;
		}

		message.obj = zhichiMessage;
		handler.sendMessage(message);
	}

	// ###################### 获取应用常量 #############################
	/**
	 * 获取应用的名称
	 * 
	 * @return
	 */
	public String getApplicationName() {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);

		return applicationName;
	}

	private int remindRobotMessageTimes  =0;
	public void remindRobotMessage(MessageAdapter messageAdapter , Handler  handler ){
		// 修改提醒的信息
		remindRobotMessageTimes = remindRobotMessageTimes+1;
		if(remindRobotMessageTimes==1){
			ZhiChiMessageBase base = new ZhiChiMessageBase();
	
			base.setSenderType(ZhiChiConstant.message_type_remide_info + "");
	
			ZhiChiReplyAnswer reply1 = new ZhiChiReplyAnswer();
			reply1.setMsg(CommonUtils.getCurrentDateTime());
			base.setAnswer(reply1);
			// 长时间没有
			base.setReconnectCustom(false);
			// 更新界面的操作
			updateUiMessage(messageAdapter, base);
			/**
			 * 首次的文化语
			 */
			ZhiChiMessageBase robot = new ZhiChiMessageBase();
			ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
			reply.setMsg(initModel.getRobotHelloWord());
			reply.setMsgType(ZhiChiConstant.message_type_text + "");
			robot.setAnswer(reply);
			robot.setSender(initModel.getRobotName());
			robot.setSenderType(ZhiChiConstant.message_type_robot_text + "");
			robot.setSenderName(initModel.getRobotName());
			Message message = handler.obtainMessage();
			message.what = ZhiChiConstant.hander_robot_message;
			message.obj = robot;
			handler.sendMessage(message);
		}
	}
	/**
	 * 
	 * @param context
	 * @param initModel
	 * @param handler
	 * @param type
	 * @throws Exception
	 */
	public void sendMessage(String id, String context,
			ZhiChiInitModeBase initModel, final Handler handler, int type)
			throws Exception {
		try {
			// URLEncoder.encode(context,"UTF-8")
			Map<String, String> map = new HashMap<String, String>();
			map.put("uid", initModel.getUid());
			map.put("cid", initModel.getCid());
			// map.put("sysNum", sysNum);
			if (ZhiChiConstant.client_model_robot == type) { // 客户和机械人进行聊天
				map.put("requestText", context);
				sendHttpRobotMessage(id, map, handler);
				LogUtils.i("发送消息内容：(机械人模式)" + "原内容：" + context + "  加密后内容："
						+ URLEncoder.encode(context, "UTF-8"));
			} else if (ZhiChiConstant.client_model_customService == type) {
				map.put("content", context);
				sendHttpCustomServiceMessage(map, handler, id);
				LogUtils.i("发送消息内容：(客服模式)" + "uid:" + initModel.getUid()
						+ "---cid:" + initModel.getCid() + "content:"
						+ URLEncoder.encode(context, "UTF-8"));
			}
		} catch (Exception e) {
		}
	}

	// 发送文件消息
	public void sendFileMessage(String context, ZhiChiInitModeBase initModel,
			final Handler handler, String id) throws Exception {
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("uid", initModel.getUid());
			map.put("cid", initModel.getCid());
			map.put("content", context);
			sendHttpCustomServiceMessage(map, handler, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 人与机械人进行聊天
	// isdelaySend 是否是延迟7 秒后重新的发送

	public void sendHttpRobotMessage(final String id, Map<String, String> map,
			final Handler handler) {
		// 获取发送的消息的应答语
		getData(HttpMethod.POST, ZhiChiApi.api_robot_chat_sendMessage, map,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						LogUtils.i("text:" + arg1.toString());
						// 显示信息发送失败
						sendTextMessageToHandler(id, null, handler, 0, true);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						LogUtils.i("############## 机械人返回的消息 ####################："
								+ arg0.result);
						// / 获取应答语： 文本消息
						ZhiChiMessage result = (ZhiChiMessage) GsonUtil
								.jsonToBean(arg0.result, ZhiChiMessage.class);
						if (ZhiChiConstant.result_success_code == Integer
								.parseInt(result.getCode())) {
							// 机械人的回答语
							sendTextMessageToHandler(id, null, handler, 1, true);
							String id = System.currentTimeMillis() + "";
							// 必须添加这一行
							ZhiChiMessageBase simpleMessage = result.getData();

							simpleMessage.setId(id);
							// simpleMessage.setMsg(null);
							simpleMessage.setSenderName(initModel
									.getRobotName());
							simpleMessage.setSender(initModel.getRobotName());
							// simpleMessage.setMsgType(null);
							simpleMessage
									.setSenderType(ZhiChiConstant.message_type_robot_reply
											+ "");
							Message message = handler.obtainMessage();
							message.what = ZhiChiConstant.hander_robot_message;
							message.obj = simpleMessage;
							handler.sendMessage(message);
						} else {
							sendTextMessageToHandler(id, null, handler, 0, true);
						}
					}
				});
	}

	public void sendHttpCustomServiceMessage(Map<String, String> map,
			final Handler handler, final String id) {
		// 获取发送的消息的应答语
		getData(HttpMethod.POST, ZhiChiApi.api_sendmessage_to_customService,
				map, new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						LogUtils.i("error:" + arg1.toString());
						sendTextMessageToHandler(id, null, handler, 0, true);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// {"status":1} 代表发送成功
						LogUtils.i("人与客服进行聊天success:" + arg0.result);
						ZhiChiMessage result = (ZhiChiMessage) GsonUtil
								.jsonToBean(arg0.result, ZhiChiMessage.class);
						if (ZhiChiConstant.result_success_code == Integer
								.parseInt(result.getCode())) {
							sendTextMessageToHandler(id, null, handler, 1, true);
							ZhiChiMessageBase base = result.getData();
							if (base != null) {// 返回结果成功
								if (id != null) {
									LogUtils.i("----文件发送成功");
									// 当发送成功的时候更新自己的ui界面
									// 更新ui界面
									Message message = handler.obtainMessage();
									message.what = ZhiChiConstant.hander_sendPicStatus_success;
									message.obj = id;
									handler.sendMessage(message);
								}
							} else {
								// 返回失败的结果
								if (id != null) {
									LogUtils.i("-----文件发送失败");
									Message message = handler.obtainMessage();
									message.what = ZhiChiConstant.hander_sendPicStatus_fail;
									message.obj = id;
									handler.sendMessage(message);
								}
							}
						} else {
							sendTextMessageToHandler(id, null, handler, 0, true);
						}

					}
				});
	}

	// 发送图片 整体接口 (发送服务器， 更新界面)

	/**
	 * 发送语音消息
	 * 
	 * @param map
	 * @param fileName
	 * @param handler
	 */
	public void sendVoice(final String id, final String voiceTimeLongStr,
			Map<String, String> map, final String filePath,
			final Handler handler) {
		RequestParams params = new RequestParams();
		// 基本传递参数
		List<NameValuePair> arrayList = new ArrayList<NameValuePair>();
		for (String key : map.keySet()) {
			BasicNameValuePair nameValuePair = new BasicNameValuePair(key,
					map.get(key));
			arrayList.add(nameValuePair);
		}
		params.addBodyParameter(arrayList);
		// 条件way以及版本的信息

		// 文件流传递参数
		params.addBodyParameter("file", new File(filePath));
		// 添加图片文件的字符流
		getDataWithParam(HttpMethod.POST,
				ZhiChiApi.api_sendFile_to_customeService, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						LogUtils.i("发送语音error:" + arg1.toString()
								+ "exception:" + arg0);
						sendVoiceMessageToHandler(id, filePath.toString(),
								voiceTimeLongStr, 0, true, handler);
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						super.onLoading(total, current, isUploading);
						LogUtils.i("发送语音 进度:" + current + "/" + total);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						LogUtils.i("返回语音发送成功success后的操作：" + arg0.result);
						if (arg0 != null && arg0.result != null) {
							ZhiChiMessage result = (ZhiChiMessage) GsonUtil
									.jsonToBean(arg0.result,
											ZhiChiMessage.class);
							if (ZhiChiConstant.result_success_code == Integer
									.parseInt(result.getCode())) {
								// 语音发送成功
								restartMyTimeTask(handler);
								sendVoiceMessageToHandler(id,
										filePath.toString(), voiceTimeLongStr,
										1, true, handler);
							} else {
								// 语音发送失败
								sendVoiceMessageToHandler(id,
										filePath.toString(), voiceTimeLongStr,
										1, true, handler);
							}
						}
					}
				});
	}

	/**
	 * 退出方法
	 * 
	 * @param map
	 */
	public void loginOut() {

		Map<String, String> map = new HashMap<String, String>();
		map.put("cid", initModel.getCid());
		map.put("uid", initModel.getUid());

		getData(HttpMethod.POST, ZhiChiApi.api_login_out, map,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						if (arg0 != null && TextUtils.isEmpty(arg0.toString())) {
							ZhiChiMessage result = (ZhiChiMessage) GsonUtil
									.jsonToBean(arg0.result,
											ZhiChiMessage.class);
							// 返回的正常的数据
							if (ZhiChiConstant.result_success_code == Integer
									.parseInt(result.getCode())) {
								ZhiChiMessageBase zhichiMessage = result
										.getData();
								// 判断下线成功
								if (ZhiChiConstant.result_success_code == Integer
										.parseInt(zhichiMessage.getStatus())) {
									Toast.makeText(TitleActivity.this, "下线成功",
											Toast.LENGTH_SHORT).show();

								} else {
									Toast.makeText(TitleActivity.this,
											"下线失败  ,5min 后 自动下线 。",
											Toast.LENGTH_SHORT).show();
								}

							}
						}

					}
				});

		// 停止语音的播放
		MediaPlayer mMediaPlayer = AudioTools.getInstance();
		if (mMediaPlayer != null) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
		}

	}

	/**
	 * 用户的定时任务的处理
	 */
	public void startUserInfoTimeTask(final Handler handler) {
		if(current_client_model ==ZhiChiConstant.client_model_customService){
			stopUserInfoTimeTask();
			timerUserInfo = new Timer();
			taskUserInfo = new TimerTask() {
				@Override
				public void run() {
					// 需要做的事:发送消息
					sendHandlerUserInfoTimeTaskMessage(handler);
				}
			};
			// TODO 需要计算计时的频率
			timerUserInfo.schedule(taskUserInfo, 1000, 1000);
	   }
	}

	public void stopUserInfoTimeTask() {

		if (timerUserInfo != null) {
			timerUserInfo.cancel();
			timerUserInfo = null;
		}
		if (taskUserInfo != null) {
			taskUserInfo.cancel();
			taskUserInfo = null;
		}
		noReplyTimeUserInfo = 0;
	}

	// ################# 处理定时任务 开始 #####################

	/**
	 * 客服的定时处理
	 */
	public void startCustomTimeTask(final Handler handler) {
		if(current_client_model ==ZhiChiConstant.client_model_customService){
			if(!is_startCustomTimerTask){
				stopCustomTimeTask();
				is_startCustomTimerTask = true;
				timerCustom = new Timer();
				taskCustom = new TimerTask() {
					@Override
					public void run() {
						// 需要做的事:发送消息
						sendHandlerCustomTimeTaskMessage(handler);
					}
				};
				// TODO 需要设置计算机时间的频率
				timerCustom.schedule(taskCustom, 1000, 1000);
			}
		}
	}

	public void stopCustomTimeTask() {
		is_startCustomTimerTask = false;
		if (timerCustom != null) {
			timerCustom.cancel();
			timerCustom = null;
		}
		if (taskCustom != null) {
			taskCustom.cancel();
			taskCustom = null;
		}
		noReplyTimeCustoms = 0;
	}

	/**
	 * 录音的时间控制
	 */
	public void startVoiceTimeTask(final Handler handler) {
		voiceTimerLong = 0;
		stopVoiceTimeTask();
		voiceTimer = new Timer();
		voiceTimerTask = new TimerTask() {
			@Override
			public void run() {
				// 需要做的事:发送消息
				sendVoiceTimeTask(handler);
			}
		};
		// TODO 需要设置计算机时间的频率
		// 500ms进行定时任务
		voiceTimer.schedule(voiceTimerTask, 0, 500);

	}

	/**
	 * 发送声音的定时的任务
	 * 
	 * @param handler
	 */
	public void sendVoiceTimeTask(Handler handler) {
		Message message = handler.obtainMessage();
		message.what = ZhiChiConstant.voiceIsRecoding;
		voiceTimerLong = voiceTimerLong + 500;
		message.obj = voiceTimerLong;
		handler.sendMessage(message);
	}

	public void stopVoiceTimeTask() {
		if (voiceTimer != null) {
			voiceTimer.cancel();
			voiceTimer = null;
		}
		if (voiceTimerTask != null) {
			voiceTimerTask.cancel();
			voiceTimerTask = null;
		}
		voiceTimerLong = 0;
	}

	/**
	 * 客服的定时任务处理
	 */
	public void sendHandlerCustomTimeTaskMessage(Handler handler) {
		noReplyTimeCustoms++;
		// 用户和人工进行聊天 超长时间没有哦发起对话
		LogUtils.i("  客服 ---的定时任务--监控--："+noReplyTimeCustoms );
		// 妹子忙翻了
		if (noReplyTimeCustoms == Integer.parseInt(initModel.getAdminTipTime()) * 60) {
			ZhiChiMessageBase result = new ZhiChiMessageBase();
			ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();

			// 发送我的语音的消息
			result.setSenderName(currentUserName); // 当前的用户

			result.setSenderType(ZhiChiConstant.message_type_robot_text + "");
			reply.setMsg(initModel.getAdminTipWord());
			reply.setMsgType(ZhiChiConstant.message_type_text + "");
			result.setAnswer(reply);

			Message message = handler.obtainMessage();
			message.what = ZhiChiConstant.hander_timeTask_custom_isBusying;
			message.obj = result;
			handler.sendMessage(message);
		}
	}

	/**
	 * 客户的定时任务处理
	 * 
	 * @param handler
	 */
	private void sendHandlerUserInfoTimeTaskMessage(Handler handler) {

		noReplyTimeUserInfo++;
		 LogUtils.i(" 客户的定时任务--监控--："+noReplyTimeUserInfo );
		// 用户几分钟没有说话
		if (current_client_model == ZhiChiConstant.client_model_customService) {
			if (noReplyTimeUserInfo == (Integer.parseInt(initModel
					.getAdminTipTime()) * 60)) {
				// TODO 进行消息的封装
				ZhiChiMessageBase base = new ZhiChiMessageBase();
				// 设置
				base.setSenderType(ZhiChiConstant.message_type_robot_text + "");
				ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();

				reply.setMsgType(ZhiChiConstant.message_type_text + "");
				// 根据当前的模式
				base.setSenderName(currentUserName);
				reply.setMsg(initModel.getUserTipWord());
				base.setAnswer(reply);
				Message message = handler.obtainMessage();
				message.what = ZhiChiConstant.hander_timeTask_userInfo;
				message.obj = base;
				handler.sendMessage(message);
			}
		}
	}

	// ################### 处理定时任务 结束 #######################
	/**
	 * 设置定时任务
	 */
	public void setTimeTaskMethod(Handler handler) {
		if (is_startTask) {
			 LogUtils.i(" 定时任务的计时的操作：" + current_client_model);
			// 断开我的计时任务
			if (current_client_model == ZhiChiConstant.client_model_robot) {
				//stopUserInfoTimeTask();
				//startUserInfoTimeTask(handler);
			} else if (current_client_model == ZhiChiConstant.client_model_customService) {
				if(!is_startCustomTimerTask){
					stopUserInfoTimeTask();
					startCustomTimeTask(handler);
				}
			}
		}
	}

	public void restartMyTimeTask(Handler handler) {
		if (is_startTask) {
			// 断开我的计时任务
			if (current_client_model == ZhiChiConstant.client_model_customService) {
				if(!is_startCustomTimerTask){
					stopUserInfoTimeTask();
					startCustomTimeTask(handler);
				}
			}
		}
	}

	public ZhiChiMessageBase reRemindTimeMessage(String timeStr) {
		// 修改提醒的信息
		ZhiChiMessageBase base = new ZhiChiMessageBase();

		base.setSenderType(ZhiChiConstant.message_type_remide_info + "");

		ZhiChiReplyAnswer reply1 = new ZhiChiReplyAnswer();
		reply1.setMsg(timeStr);
		base.setAnswer(reply1);
		// 长时间没有
		base.setReconnectCustom(false);
		return base;
		// 更新界面的操作
	}
}