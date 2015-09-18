package com.sobot.chat.activity;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.sobot.chat.activity.base.TitleActivity;
import com.sobot.chat.adapter.base.MessageAdapter;
import com.sobot.chat.application.RongCloudEvent;
import com.sobot.chat.model.ZhiChiHistoryMessage;
import com.sobot.chat.model.ZhiChiHistoryMessageBase;
import com.sobot.chat.model.ZhiChiInitModel;
import com.sobot.chat.model.ZhiChiMessage;
import com.sobot.chat.model.ZhiChiMessageBase;
import com.sobot.chat.model.ZhiChiPushMessage;
import com.sobot.chat.model.ZhiChiReplyAnswer;
import com.sobot.chat.utils.AudioTools;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.EmojiFilter;
import com.sobot.chat.utils.ExtAudioRecorder;
import com.sobot.chat.utils.GsonUtil;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.TimeTools;
import com.sobot.chat.utils.ZhiChiApi;
import com.sobot.chat.utils.ZhiChiBitmapUtils;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.weight.ContainsEmojiEditText;
import com.sobot.chat.weight.DCRCDialog;
import com.sobot.chat.weight.OtherDialog;
import com.sobot.chat.weight.RobotDialog;
import com.sobot.chat.weight.XListView;
import com.sobot.chat.weight.XListView.IXListViewListener;

/**
 * 智齿主界面
 * 
 * @author jinliang
 */

@SuppressLint({ "HandlerLeak", "ShowToast", "SimpleDateFormat", "SdCardPath",
		"NewApi" })
public class SobotChatActivity extends TitleActivity implements
		IXListViewListener  ,SensorEventListener{

	// 下推消息的广播
	public static final String brocastContantant = "com.sobot.chat.activity.MainActivity";
	// 问答语的发送消息广播
	public static final String sendMyMessageBrocast = "com.sobot.chat.activity.MainActivity.sendMyMessageBrocast";
	// 重新上传图片广播
	public static final String picReUploadBrocast = "com.sobot.chat.activity.MainActivity.picReUploadBrocast";
	// 语音的发送广播
	public static final String voiceReUploadBrocast = "com.sobot.chat.activity.MainActivity.voiceReUploadBrocast";

	public static final String ResendTextBrocast = "com.sobot.chat.activity.MainActivity.ResendTextBrocast";
	// 重新连接广播
	public static final String reConnectCustom = "com.sobot.chat.activity.MainActivity.ReConnectCustom";
	
	public static final String quitSobotConnection ="com.sobot.chat.activity.MainActivity.quit" ;
	private static final SobotChatActivity MainActivity = null;

	private XListView lv_message;/* 带下拉的ListView */

	private ContainsEmojiEditText et_sendmessage;// 当前用户输入的信息

	private Button btn_send; // 发送消息按钮

	private Button btn_set_mode_rengong; // 转人工button

	private Button btn_upload_view; // 上传图片
	private TextView voice_time_long;
	//
	private LinearLayout voice_top_image;
	private ImageView image_endVoice;
	private ImageView mic_image;
	private ImageView mic_image_animate; // 图片的动画
	private ImageView recording_timeshort;// 语音太短的图片

	private Button btn_model_edit; // 编辑模式

	private Button btn_model_voice;// 语音模式

	private TextView btn_picture; // 发送图片
	private TextView btn_take_picture;// 发送照相的图片
	private TextView txt_speak_content; // 发送语音的文字
	/**
	 * 语音的动画
	 */
	private AnimationDrawable animationDrawable;
	private LinearLayout moreFunction; // 显示更多功能

	private LinearLayout btn_press_to_speak; // 说话view ;
	private RelativeLayout edittext_layout; // 输入框view;
	private LinearLayout recording_container;// 语音上滑的动画
	private TextView recording_hint;// 上滑的显示文本；
	public String time = null;/* 当天日期 */
	private boolean isCustom = false;/* 是否是人工模式 */
	@SuppressWarnings("unused")
	private boolean isPicShow = false;/* 是否是发送图片模式 */
	public int pageNow = 1;/* 当前页 */
	public String uid = null;
	public String cid = null;
	private String flag = null;
	private boolean isNoMoreHistoryMsg = false;
	private boolean isOneMinute = false, isAboveZero = false;/*
															 * 时间是否大于一分钟，消息数是否大于0
															 */
	private TimeCount isPastOneMinute;/* 计时一分钟 */
	private View.OnClickListener listener;
	private String color;

	// 消息列表展示
	private List<ZhiChiMessageBase> messageList = new ArrayList<ZhiChiMessageBase>();
	private MessageAdapter messageAdapter;
	private boolean isFirstTransCustom = false;
	/* 融云的连接接口 */
	// private String token =
	// "ZC0tAJEOZL6cUmud8fNTYtQp5c8nb9aA8P92V8mb8WYASrbTPWiPepuHCRAzXq6vGBEgTo2FR8IxxGPwJzahF1nCZeCSh1oP";
	private String token = "c9U52TIr7+v1YEOWs+f9M9Qp5c8nb9aA8P92V8mb8WYASrbTPWiPeoU+V0ZuhwVtSXmB8cqT0KC0mrt2GWEyKQh72wyY83Wi5iKHdoUxwaM=";
	private boolean rongyun_is_connect = false,
			rongyun_is_connect_is_frist = false;
	private int currentVoiceLong = 0;
	/**
	 * 发送语音方法
	 */
	private String mFileName = null;
	/** 用于语音播放 */
	@SuppressWarnings("unused")
	private MediaPlayer mPlayer = null;
	/** 用于完成录音 */
	@SuppressWarnings("unused")
	private MediaRecorder mRecorder = null;
	private MyMessageReceiver receiver;

	// listview 滚动定位
	public int scrollPos;
	public int scrollTop;
	private ExtAudioRecorder extAudioRecorder;
	private int paiduiTimes = 0;

	/**
	 * 听筒模式转换
	 */
	private AudioManager audioManager = null; // 声音管理器  
    private SensorManager _sensorManager = null; // 传感器管理器  
    private Sensor mProximiny = null; // 传感器实例  
    private float f_proximiny; // 当前传感器距离  
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResourceUtils.getIdByName(this, "layout",
				"activity_main"));
		initViewNew();
		/* 给前天日期赋值 */
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		time = df.format(new Date());// new Date()为获取当前系统时间
		initBrocastReceiver();
		color = SharedPreferencesUtil.getStringData(this,
				"robot_current_themeColor", "#09aeb0");
		isPastOneMinute = new TimeCount(60000, 1000);
		isPastOneMinute.start();
		// 乐视TV的 sysNum ： df60ea2a8fa74440aacd687cb2de62f8
		// sysNum = "0a88b1e206574af09ecd608b8e927a81";
		// sysNum = "be99cce4876c41c1854a275c55b8e68b";
		initMessageList();
		initData();
		initLinster();
		if (is_startTask) {
			// 开发用户的定时任务
			startUserInfoTimeTask(handler);
		}
		initAudioManager();
	}

	//设置听筒模式或者是正常模式的转换
	private void initAudioManager() {
	    audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);  
        _sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);  
        mProximiny = _sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
	}

	private void initViewNew() {

		lv_message = (XListView) findViewById(ResourceUtils.getIdByName(this,
				"id", "lv_message"));

		et_sendmessage = (ContainsEmojiEditText) findViewById(ResourceUtils
				.getIdByName(this, "id", "et_sendmessage"));
		btn_send = (Button) findViewById(ResourceUtils.getIdByName(this, "id",
				"btn_send"));
		btn_set_mode_rengong = (Button) findViewById(ResourceUtils.getIdByName(
				this, "id", "btn_set_mode_rengong"));
		btn_upload_view = (Button) findViewById(ResourceUtils.getIdByName(this,
				"id", "btn_upload_view"));
		btn_model_edit = (Button) findViewById(ResourceUtils.getIdByName(this,
				"id", "btn_model_edit"));
		btn_model_voice = (Button) findViewById(ResourceUtils.getIdByName(this,
				"id", "btn_model_voice"));
		btn_picture = (TextView) findViewById(ResourceUtils.getIdByName(this,
				"id", "btn_picture"));
		btn_take_picture = (TextView) findViewById(ResourceUtils.getIdByName(
				this, "id", "btn_take_picture"));
		moreFunction = (LinearLayout) findViewById(ResourceUtils.getIdByName(
				this, "id", "moreFunction"));
		btn_press_to_speak = (LinearLayout) findViewById(ResourceUtils
				.getIdByName(this, "id", "btn_press_to_speak"));
		edittext_layout = (RelativeLayout) findViewById(ResourceUtils
				.getIdByName(this, "id", "edittext_layout"));
		recording_hint = (TextView) findViewById(ResourceUtils.getIdByName(
				this, "id", "recording_hint"));
		recording_container = (LinearLayout) findViewById(ResourceUtils
				.getIdByName(this, "id", "recording_container"));

		// 开始语音的布局的信息
		voice_top_image = (LinearLayout) findViewById(ResourceUtils
				.getIdByName(this, "id", "voice_top_image"));

		// 停止语音的
		image_endVoice = (ImageView) findViewById(ResourceUtils.getIdByName(
				this, "id", "image_endVoice"));
		// 动画的效果
		mic_image_animate = (ImageView) findViewById(ResourceUtils.getIdByName(
				this, "id", "mic_image_animate"));

		// 时长的界面
		voice_time_long = (TextView) findViewById(ResourceUtils.getIdByName(
				this, "id", "voiceTimeLong"));
		//
		txt_speak_content = (TextView) findViewById(ResourceUtils.getIdByName(
				this, "id", "txt_speak_content"));
		txt_speak_content.setText("按住 说话");

		recording_timeshort = (ImageView) findViewById(ResourceUtils
				.getIdByName(this, "id", "recording_timeshort"));

		mic_image = (ImageView) findViewById(ResourceUtils.getIdByName(this,
				"id", "mic_image"));

	}

	@Override
	protected void onResume() {
		super.onResume();
		initBrocastReceiver();
		_sensorManager.registerListener(this, mProximiny,  
                SensorManager.SENSOR_DELAY_NORMAL);  
	}

	@Override
	protected void onPause() {
		super.onPause();
		   // 取消注册传感器  
        _sensorManager.unregisterListener(this);  
	}

	/**
	 * 初始化广播接受者
	 */
	@SuppressWarnings("static-access")
	private void initBrocastReceiver() {
		if (receiver == null) {
			receiver = new MyMessageReceiver();
		}
		// 创建过滤器，并指定action，使之用于接收同action的广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(MainActivity.brocastContantant);
		filter.addAction(MainActivity.sendMyMessageBrocast);
		filter.addAction(MainActivity.picReUploadBrocast);
		filter.addAction(MainActivity.reConnectCustom);
		filter.addAction(MainActivity.voiceReUploadBrocast);
		filter.addAction(MainActivity.ResendTextBrocast);
		filter.addAction(MainActivity.quitSobotConnection);//监听融云的退出时间
		
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // 检测网络的状态
		// 注册广播接收器
		registerReceiver(receiver, filter);
	}

	private void initMessageList() {
		messageAdapter = new MessageAdapter(SobotChatActivity.this, messageList);
		lv_message.setAdapter(messageAdapter);
		lv_message.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

		lv_message.setPullLoadEnable(false);// 设置上拉加载更多
		lv_message.setPullRefreshEnable(true);// 设置下拉刷新列表
		lv_message.setXListViewListener(this);// 设置列表监听
	}

	@SuppressWarnings("static-access")
	private void initLinster() {

		back.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		btn_set_mode_rengong.setOnClickListener(this);
		btn_upload_view.setOnClickListener(this);
		btn_picture.setOnClickListener(this);
		btn_take_picture.setOnClickListener(this);
		btn_model_edit.setOnClickListener(this);
		btn_model_voice.setOnClickListener(this);

		et_sendmessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				moreFunction.setVisibility(View.GONE);
				isPicShow = false;
			}
		});

		et_sendmessage.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					moreFunction.setVisibility(View.GONE);
					isPicShow = false;
				}
			}
		});

		et_sendmessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (isCustom) {
					if (arg0.length() > 0) {
						btn_send.setVisibility(View.VISIBLE);
						btn_upload_view.setVisibility(View.GONE);
						moreFunction.setVisibility(View.GONE);
					} else {
						btn_upload_view.setVisibility(View.VISIBLE);
						btn_send.setVisibility(View.GONE);
					}
				} else {
					if (arg0.length() > 0) {
						btn_send.setVisibility(View.VISIBLE);
						btn_upload_view.setVisibility(View.GONE);
						moreFunction.setVisibility(View.GONE);
					} else {
						btn_send.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}
			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

		btn_press_to_speak.setOnTouchListener(new PressToSpeakListen());

		lv_message.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0); // 强制隐藏键盘
					moreFunction.setVisibility(View.GONE);
					return false;
				case MotionEvent.ACTION_MOVE:
					imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0); // 强制隐藏键盘
					moreFunction.setVisibility(View.GONE);
					return false;
				}
				return false;
			}
		});

		lv_message.setOnScrollListener(new PauseOnScrollListener(
				new ZhiChiBitmapUtils(SobotChatActivity.this).getInstance(),
				false, true));
		// 给ListView设置监听器 【用来恢复ListView位置的】
		lv_message.setOnScrollListener(scrollListener);
	}

	private OnScrollListener scrollListener = new OnScrollListener() {

		@Override
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				// scrollPos记录当前可见的List顶端的一行的位置
				scrollPos = lv_message.getFirstVisiblePosition();
			}
			if (messageList != null) {

				View v = lv_message.getChildAt(lv_message
						.getFirstVisiblePosition());

				scrollTop = (v == null) ? 0 : v.getTop();
			}
			LogUtils.i("scrollPos:" + scrollPos + "---scrollTop:" + scrollTop);
		}

	};

	class PressToSpeakListen implements View.OnTouchListener {
		@SuppressLint({ "ClickableViewAccessibility", "Wakelock" })
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			boolean isCutVoice = false;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				btn_upload_view.setClickable(false);
				btn_model_edit.setClickable(false);
				btn_upload_view.setBackgroundResource(ResourceUtils
						.getIdByName(SobotChatActivity.this, "drawable",
								"picture_pressed"));
				btn_model_edit
						.setBackgroundResource(ResourceUtils.getIdByName(
								SobotChatActivity.this, "drawable",
								"keyboard_pressed"));
				stopVoiceTimeTask();
				v.setPressed(true);
				voice_time_long.setText("00" + "''");
				voiceTimeLongStr = "00";
				voiceTimerLong = 0;
				currentVoiceLong = 0;
				recording_container.setVisibility(View.VISIBLE);
				voice_top_image.setVisibility(View.VISIBLE);
				mic_image.setVisibility(View.VISIBLE);
				mic_image_animate.setVisibility(View.VISIBLE);
				voice_time_long.setVisibility(View.VISIBLE);
				recording_timeshort.setVisibility(View.GONE);
				image_endVoice.setVisibility(View.GONE);
				txt_speak_content.setText("松开 发送");
				// 设置语音的定时任务

				mic_image_animate.setImageResource(ResourceUtils.getIdByName(
						SobotChatActivity.this, "drawable", "voice_animation"));
				animationDrawable = (AnimationDrawable) mic_image_animate
						.getDrawable();
				animationDrawable.start();
				recording_hint
						.setText(getString(ResourceUtils.getIdByName(
								SobotChatActivity.this, "string",
								"move_up_to_cancel")));
				recording_hint.setBackgroundColor(Color.GRAY);

				LogUtils.i("录音Action     Down---");
				startVoice();
				// restartMyTimeTask(handler);
				return true;
			case MotionEvent.ACTION_MOVE: {
				if (is_startCustomTimerTask == false) {
					noReplyTimeUserInfo = 0;
				}
				// restartMyTimeTask(handler);
				if (event.getY() < 10) {
					// 取消界面的显示
					voice_top_image.setVisibility(View.GONE);
					image_endVoice.setVisibility(View.VISIBLE);
					mic_image.setVisibility(View.GONE);
					mic_image_animate.setVisibility(View.GONE);
					recording_timeshort.setVisibility(View.GONE);
					txt_speak_content.setText("松开手指 取消发送");
					// 停止语音的播放时间
					mic_image_animate.setImageResource(ResourceUtils
							.getIdByName(SobotChatActivity.this, "drawable",
									"voice_animation"));
					animationDrawable = (AnimationDrawable) mic_image_animate
							.getDrawable();
					animationDrawable.start();
					recording_hint.setText(getString(ResourceUtils.getIdByName(
							SobotChatActivity.this, "string",
							"release_to_cancel")));
					recording_hint.setBackgroundResource(ResourceUtils
							.getIdByName(SobotChatActivity.this, "drawable",
									"recording_text_hint_bg"));
				} else {

					txt_speak_content.setText("松开 发送");
					voice_top_image.setVisibility(View.VISIBLE);
					mic_image_animate.setVisibility(View.VISIBLE);
					mic_image_animate.setImageResource(ResourceUtils
							.getIdByName(SobotChatActivity.this, "drawable",
									"voice_animation"));
					animationDrawable = (AnimationDrawable) mic_image_animate
							.getDrawable();
					animationDrawable.start();
					image_endVoice.setVisibility(View.GONE);
					mic_image.setVisibility(View.VISIBLE);
					recording_timeshort.setVisibility(View.GONE);
					recording_hint.setText(getString(ResourceUtils.getIdByName(
							SobotChatActivity.this, "string",
							"move_up_to_cancel")));
					recording_hint.setBackgroundColor(Color.GRAY);
					// 录音的时间超过一分钟的时间切断进行发送语音
					if (voiceTimerLong > 60 * 1000) {
						isCutVoice = true;
						voiceCuttingMethod();
						voiceTimerLong = 0;
						// recording_container.setVisibility(View.VISIBLE);
						// voice_top_image.setVisibility(View.VISIBLE);
						recording_hint.setText("语音不能太长");
						recording_timeshort.setVisibility(View.VISIBLE);
						mic_image.setVisibility(View.GONE);
						mic_image_animate.setVisibility(View.GONE);
						closeVoiceWindows();
						v.setPressed(false);
						// restartMyTimeTask(handler);
						currentVoiceLong = 0;
					}
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				btn_upload_view.setClickable(true);
				btn_model_edit.setClickable(true);
				btn_upload_view.setBackgroundResource(ResourceUtils
						.getIdByName(SobotChatActivity.this, "drawable",
								"picture_normal"));
				btn_model_edit.setBackgroundResource(ResourceUtils.getIdByName(
						SobotChatActivity.this, "drawable", "keyboard_normal"));
				v.setPressed(false);
				txt_speak_content.setText("按住 说话");
				stopVoiceTimeTask();
				stopVoice();
				if (recording_container.getVisibility() == View.VISIBLE
						&& !isCutVoice) {
					moreFunction.setVisibility(View.GONE);
					animationDrawable.stop();
					LogUtils.i("录音Action     up---");
					voice_time_long.setText("00" + "''");
					voice_time_long.setVisibility(View.INVISIBLE);
					if (event.getY() < 0) {
						recording_container.setVisibility(View.GONE);
						return true;
						// 取消发送语音
					} else {
						// 发送语音
						if (currentVoiceLong < 1 * 1000) {
							LogUtils.i("asfafdas时间不能少于一秒钟" + currentVoiceLong);
							// recording_container.setVisibility(View.VISIBLE);
							voice_top_image.setVisibility(View.VISIBLE);
							recording_hint.setText("语音不能少于一秒钟");
							recording_timeshort.setVisibility(View.VISIBLE);
							mic_image.setVisibility(View.GONE);
							mic_image_animate.setVisibility(View.GONE);
						} else if (currentVoiceLong < 60 * 1000) {
							recording_container.setVisibility(View.GONE);
							sendVoiceMap();
							return true;
						} else if (currentVoiceLong > 60 * 1000) {
							LogUtils.i("时间不能大于60秒");
							voice_top_image.setVisibility(View.VISIBLE);
							recording_hint.setText("语音不能太长");
							recording_timeshort.setVisibility(View.VISIBLE);
							mic_image.setVisibility(View.GONE);
							mic_image_animate.setVisibility(View.GONE);
						}
					}
					currentVoiceLong = 0;
					closeVoiceWindows();
				}

				voiceTimerLong = 0;
				restartMyTimeTask(handler);
				// mFileName
				return true;
			default:
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
							Message message = handler.obtainMessage();
							message.what = ZhiChiConstant.hander_close_voice_view;
							handler.sendMessage(message);
						} catch (Exception e) {
							e.printStackTrace();
							recording_container.setVisibility(View.GONE);
						}
					}
				}).start();
				return false;
			}
		}
	}

	public void closeVoiceWindows() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					Message message = handler.obtainMessage();
					message.what = ZhiChiConstant.hander_close_voice_view;
					handler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
					recording_container.setVisibility(View.GONE);
				}
			}
		}).start();
	}

	// 当时间超过1秒的时候自动发送
	public void voiceCuttingMethod() {
		LogUtils.i("超过一秒钟的定时发送的发放的调用：" + voiceTimerLong);
		stopVoice();
		sendVoiceMap();
		voice_time_long.setText("00" + "''");
	}

	/**
	 * 开始录音
	 */
	private void startVoice() {
		try {

			/*
			 * mFileName = PATH + UUID.randomUUID().toString() + ".amr"; String
			 * state = android.os.Environment.getExternalStorageState(); if
			 * (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			 * LogUtils.i("SD Card is not mounted,It is  " + state + "."); }
			 * File directory = new File(mFileName).getParentFile(); if
			 * (!directory.exists() && !directory.mkdirs()) {
			 * LogUtils.i("Path to file could not be created"); }
			 * LogUtils.i("---startVoice----"); mRecorder = new MediaRecorder();
			 * mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			 * mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			 * mRecorder.setOutputFile(mFileName);
			 * mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			 * mRecorder.prepare(); mRecorder.start();
			 */
			stopVoice();
			mFileName = PATH + UUID.randomUUID().toString() + ".wav";
			String state = android.os.Environment.getExternalStorageState();
			if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
				LogUtils.i("SD Card is not mounted,It is  " + state + ".");
			}
			File directory = new File(mFileName).getParentFile();
			if (!directory.exists() && !directory.mkdirs()) {
				LogUtils.i("Path to file could not be created");
			}
			extAudioRecorder = ExtAudioRecorder.getInstanse(false);
			extAudioRecorder.setOutputFile(mFileName);
			extAudioRecorder.prepare();
			extAudioRecorder.start();
			startVoiceTimeTask(handler);
		} catch (Exception e) {
			LogUtils.i("prepare() failed");
		}
	}

	/**
	 * 停止录音
	 */
	private void stopVoice() {

		/**
		 * 布局的变化
		 */
		/*
		 * try { if (mRecorder != null) { mRecorder.stop(); mRecorder.release();
		 * mRecorder = null; } } catch (Exception e) { mRecorder = null; }
		 * 
		 * LogUtils.i("---endVoice----"); // mFileName 文件保存的位置
		 * LogUtils.i("--录音结束 产生的文件--" + mFileName);
		 */
		try {
			if (extAudioRecorder != null) {
				stopVoiceTimeTask();
				extAudioRecorder.stop();
				extAudioRecorder.release();
			}
		} catch (Exception e) {
			mRecorder = null;
		}

	}

	// 发送语音的方式
	private void sendVoiceMap() {
		// 发送语音的界面
		//
		String id = System.currentTimeMillis() + "";
		sendVoiceMessageToHandler(id, mFileName.toString(), voiceTimeLongStr,
				2, false, handler);
		// 发送http 返回发送成功的按钮
		Map<String, String> map = new HashMap<String, String>();
		map.put("cid", initModel.getCid());
		map.put("uid", initModel.getUid());
		map.put("duration", voiceTimeLongStr);
		sendVoice(id, voiceTimeLongStr, map, mFileName, handler);
	}

	/**
	 * 项目初始化
	 */
	private void initData() {
		String robot_initResult = SharedPreferencesUtil.getStringData(
				SobotChatActivity.this, "robot_initResult", null);

		if (robot_initResult != null) {
			ZhiChiInitModel result = (ZhiChiInitModel) GsonUtil.jsonToBean(
					robot_initResult, ZhiChiInitModel.class);
			if (ZhiChiConstant.result_success_code == Integer.parseInt(result
					.getCode())) {

				initModel = result.getData();
				uid = initModel.getUid();
				cid = initModel.getCid();
				if (SharedPreferencesUtil
						.getStringData(
								SobotChatActivity.this,
								CommonUtils
										.getSobotCloudChatAppKey(getApplicationContext()),
								null) == null) {
					RongIMClient.init(SobotChatActivity.this,
							initModel.getAppKey());
					RongCloudEvent.init(this);
					SharedPreferencesUtil
							.saveStringData(
									SobotChatActivity.this,
									CommonUtils
											.getSobotCloudChatAppKey(getApplicationContext()),
									initModel.getAppKey());

				}
				token = initModel.getToken();
				// conntRongYun(token);
				LogUtils.i("初始化 token :" + initModel.getToken());

				// 切换模式
				type = Integer.parseInt(initModel.getType());
				// type=4;
				setTitle(initModel.getRobotName()); // 设置标题
				initView();

				if (color != null && color.trim().length() != 0) {
					relative.setBackgroundColor(Color.parseColor(color));
				}

				LogUtils.i("uid---" + uid);
				LogUtils.i("cid---" + cid);
				if (type != 4) {
					/**
					 * 时间的提醒
					 */
					remindRobotMessage(messageAdapter, handler);
					conntRongYun(token);
				}
			}
		}
	}

	/* 初始化连接融云机制 */
	private void connectRongYun() {
		LogUtils.i(" rongyun_is_connect: " + rongyun_is_connect);
		btn_set_mode_rengong.setEnabled(false);
		btn_set_mode_rengong.setAlpha(0.2f);
		if (rongyun_is_connect) {
			// 转人工服务
			new Thread(new Runnable() {
				@Override
				public void run() {
					zhichi_TransferCustomer_Service(btn_set_mode_rengong,
							initModel.getUid(), initModel.getCid(), handler,
							messageAdapter);
					// 开始计时任务
					// stopCustomTimeTask();
				}
			}).start();
		} else {
			// token 强制刷新
			Map<String, String> map = new HashMap<String, String>();
			map.put("sysNum", SharedPreferencesUtil.getStringData(
					SobotChatActivity.this, "sysNum", null));
			map.put("partnerId",
					CommonUtils.getPartnerId(SobotChatActivity.this));
			map.put("way", 3 + "");
			map.put("from", "2");
			map.put("version", CommonUtils.getVersion(SobotChatActivity.this)
					+ "");
			getData(HttpMethod.POST, ZhiChiApi.api_token_reflesh, map,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							ZhiChiInitModel result = (ZhiChiInitModel) GsonUtil
									.jsonToBean(arg0.result,
											ZhiChiInitModel.class);
							btn_set_mode_rengong.setEnabled(true);
							btn_set_mode_rengong.setAlpha(1f);
							if (ZhiChiConstant.result_success_code == Integer
									.parseInt(result.getCode())) {
								if (result.getData() != null) {
									final String token = result.getData()
											.getToken();
									SharedPreferencesUtil.saveStringData(
											SobotChatActivity.this,
											CommonUtils
													.getSobotCloudChatAppKey(getApplicationContext()),
											result.getData().getAppKey());
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											conntRongYun(token);
											rongyun_is_connect_is_frist = true;
										}
									});
								}

							}
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							btn_set_mode_rengong.setEnabled(true);
							btn_set_mode_rengong.setAlpha(1f);
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if (rongyun_is_connect_is_frist) {
										Toast toast = Toast.makeText(
												SobotChatActivity.this,
												"暂时无法转接人工服务，请稍后再试",
												Toast.LENGTH_SHORT);
										// 可以控制toast显示的位置
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 10);
										toast.show();
									}
								}
							});

						}

					});
		}
	}

	/* 处理消息 */
	public Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(final android.os.Message msg) {
			switch (msg.what) {
			case ZhiChiConstant.hander_history:
				List<ZhiChiMessageBase> msgList = (List<ZhiChiMessageBase>) msg.obj;
				messageAdapter.addData(msgList);
				messageAdapter.notifyDataSetChanged();
				post(new Runnable() {
					@Override
					public void run() {
						lv_message.setSelection(16);
					}
				});
				break;
			case ZhiChiConstant.hander_my_senderMessage:/* 我的文本消息 */
				updateUiMessage(messageAdapter, msg);
				et_sendmessage.setText("");
				isAboveZero = true;
				break;
			case ZhiChiConstant.hander_my_update_senderMessageStatus:
				updateTextMessageStatus(messageAdapter, msg);
				break;
			case ZhiChiConstant.hander_robot_message:
				updateUiMessage(messageAdapter, msg);
				break;
			case ZhiChiConstant.message_type_remide_info:
				if (msg.arg1 == ZhiChiConstant.transfer_customeServeive_success) {
					// 机械人转人工 转成功后 我就不提醒消息了
					// robotToCustomServiceModel();
					// isAboveZero = false;
					// isOneMinute = false;
					// isPastOneMinute.start();

					// 返回正常的时候去掉
					// robotToCustomServiceModel();
				} else {
					if (msg.arg1 == ZhiChiConstant.transfer_customeServeive_alreay) {
						robotToCustomServiceModel();
						isAboveZero = false;
						isOneMinute = false;
						isPastOneMinute.start();
					}
					updateUiMessage(messageAdapter, msg);
				}
				//
				break;
			case ZhiChiConstant.message_type_wo_sendImage: // 我发送图片 更新ui
				// 加载更过view隐藏
				updateUiMessage(messageAdapter, msg);
				isAboveZero = true;
				break;

			case ZhiChiConstant.message_type_send_voice: // 发送语音
				isAboveZero = true;
				updateUiMessage(messageAdapter, msg);
				isAboveZero = true;
				break;
			// 修改语音的发送状态
			case ZhiChiConstant.message_type_update_voice:
				updateVoiceStatusMessage(messageAdapter, msg);
				break;
			case ZhiChiConstant.hander_sendPicStatus_success:
				String id = (String) msg.obj;
				updateUiMessageStatus(messageAdapter, id,
						ZhiChiConstant.result_success_code, 0);
				break;
			case ZhiChiConstant.hander_sendPicStatus_fail:
				String resultId = (String) msg.obj;
				updateUiMessageStatus(messageAdapter, resultId,
						ZhiChiConstant.result_fail_code, 0);
				break;
			case ZhiChiConstant.hander_sendPicIsLoading:
				String loadId = (String) msg.obj;
				int uploadProgress = msg.arg1;
				updateUiMessageStatus(messageAdapter, loadId,
						ZhiChiConstant.hander_sendPicIsLoading, uploadProgress);
				break;
			case ZhiChiConstant.hander_timeTask_custom_isBusying: // 客服的定时任务
																	// --客服忙碌
				updateUiMessage(messageAdapter, msg);
				LogUtils.i("客服的定时任务:" + noReplyTimeCustoms);
				if (is_startTask) {
					stopCustomTimeTask();
				}
				break;
			case ZhiChiConstant.hander_timeTask_custom_finish:
				updateUiMessage(messageAdapter, msg);
				if (is_startTask) {
					stopUserInfoTimeTask();
					// 界面切换模式
					customServiceToRobotModel();
				}
				LogUtils.i("长时间没有回答  结束转为机械人聊天模式:" + noReplyTimeCustoms);
				break;
			case ZhiChiConstant.hander_timeTask_userInfo:// 客户的定时任务
				updateUiMessage(messageAdapter, msg);
				LogUtils.i("客户的定时任务的时间  停止定时任务：" + noReplyTimeUserInfo);
				break;
			case ZhiChiConstant.voiceIsRecoding:
				final int time = Integer.parseInt(msg.obj.toString());
				LogUtils.i("录音定时任务的时长：" + time);
				currentVoiceLong = time;
				if (time < 50 * 1000) {
					if (time % 1000 == 0) {
						voiceTimeLongStr = TimeTools.instance
								.calculatTime(time);
						voice_time_long.setText(voiceTimeLongStr + "''");
					}
				} else if (time < 60 * 1000) {
					if (time % 1000 == 0) {
						voiceTimeLongStr = TimeTools.instance
								.calculatTime(time);
						voice_time_long.setText("倒计时：" + (60 * 1000 - time)
								/ 1000);
					}
				} else {
					voice_time_long.setText(ResourceUtils.getIdByName(
							SobotChatActivity.this, "string", "voiceTooLong"));
				}
				break;
			case ZhiChiConstant.hander_close_voice_view:
				txt_speak_content.setText("按住 说话");

				currentVoiceLong = 0;
				recording_container.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		};
	};

	// 机械人人转换到人工模式
	private void robotToCustomServiceModel() {
		if (et_sendmessage.getText().toString().length() > 0) {
			btn_upload_view.setVisibility(View.GONE);
			btn_set_mode_rengong.setVisibility(View.GONE);
			btn_model_voice.setVisibility(View.VISIBLE);
			isCustom = true;
		} else {
			// mTitleTextView.setText(initModel.getCompanyName());
			current_client_model = ZhiChiConstant.client_model_customService;
			btn_set_mode_rengong.setVisibility(View.GONE);
			btn_upload_view.setVisibility(View.VISIBLE);
			moreFunction.setVisibility(View.GONE);
			btn_model_voice.setVisibility(View.VISIBLE);
			isCustom = true;
		}
	}

	// 人工 to 机械人模式的模式
	private void customServiceToRobotModel() {
		setTitle(initModel.getRobotName());
		mTitleTextView.setText(initModel.getRobotName());
		current_client_model = ZhiChiConstant.client_model_robot;
		btn_set_mode_rengong.setVisibility(View.VISIBLE);
		edittext_layout.setVisibility(View.VISIBLE);
		btn_press_to_speak.setVisibility(View.GONE);
		btn_upload_view.setVisibility(View.GONE);
		moreFunction.setVisibility(View.GONE);
		btn_model_voice.setVisibility(View.GONE);
		btn_model_edit.setVisibility(View.GONE);
		// 显示输入框的内容
		current_client_model = ZhiChiConstant.client_model_robot;
		isCustom = false;
		btn_set_mode_rengong.setEnabled(true);
		btn_set_mode_rengong.setAlpha(1f);
		//
	}

	@Override
	public void forwordMethod() {
	}

	@Override
	public void onClick(View view) {
		if (view == btn_send) {// 发送消息按钮
			SharedPreferencesUtil.saveStringData(SobotChatActivity.this,
					pageNow + "", "");
			// 对emoji 表情的过滤
			final String message_result = et_sendmessage.getText().toString()
					.trim();
			if (message_result.length() > 0) {
				new Thread() {
					public void run() {
						try {
							/* 获取发送的消息 */
							LogUtils.i("发送的消息内容：" + et_sendmessage.getText());
							String message_result =EmojiFilter.filterEmoji(et_sendmessage.getText().toString().trim());
							// 通知Handler更新 我的消息ui
							String id = System.currentTimeMillis() + "";
							sendTextMessageToHandler(id, message_result,
									handler, 1, false);
							/*
							 * if(current_client_model==ZhiChiConstant.
							 * client_model_robot){ // 发送测试数据
							 * sendTextRobotReply(0,"文本"); sendTextRobotReply(1,
							 * "http://img2.imgtn.bdimg.com/it/u=2199180302,1055708820&fm=21&gp=0.jpg"
							 * ); sendTextRobotReply(2,
							 * "http://img.sobot.com/chatres/298/msg/20150827/294d9c52922147bf88b417c8fa07a19f.amr"
							 * ); sendTextRobotReply(4,"富文本中有图片");
							 * sendTextRobotReply(5, "富文本中纯文字");
							 * //sendTextRobotReply(6, "富文本中有视频"); }
							 */
							LogUtils.i("当前发送消息模式：" + current_client_model);
							if (is_startTask) {
								setTimeTaskMethod(handler);
							}
							sendMessage(id, message_result, initModel, handler,
									current_client_model);
						} catch (Exception e) {
							e.printStackTrace();
						}
					};
				}.start();
			}
		}

		if (view == btn_set_mode_rengong) {// 设置转人工的按钮
			isFirstTransCustom = true;
			connectRongYun();

		}

		if (view == btn_upload_view) {// 显示上传view
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			moreFunction.setVisibility(View.VISIBLE != moreFunction
					.getVisibility() ? View.VISIBLE : View.GONE);
			// restartMyTimeTask(handler);

			if (moreFunction.getVisibility() == 0) {
				isPicShow = false;
				moreFunction.setVisibility(View.VISIBLE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
			}else{
				moreFunction.setVisibility(View.GONE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}

			if (btn_press_to_speak.isShown()) {
				btn_model_edit.setVisibility(View.GONE);
				btn_model_voice.setVisibility(View.VISIBLE);
				btn_press_to_speak.setVisibility(View.GONE);
				edittext_layout.setVisibility(View.VISIBLE);
				moreFunction.setVisibility(View.VISIBLE);
			}

			if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
				getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				moreFunction.setVisibility(View.VISIBLE);
			}

			if (!isPicShow) {
				moreFunction.setVisibility(View.VISIBLE);
				et_sendmessage.setInputType(InputType.TYPE_CLASS_TEXT);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
			} else {
				moreFunction.setVisibility(View.GONE);
				et_sendmessage.setInputType(InputType.TYPE_CLASS_TEXT);
				edittext_layout.requestFocus();
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
			isPicShow = !isPicShow;
		}

		if (view == btn_picture) {// 图库 上传图片
//			isPicShow = false;
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
			// restartMyTimeTask(handler);
			// stopUserInfoTimeTask();
			selectPicFromLocal(); //
		}

		if (view == btn_take_picture) {
			// restartMyTimeTask(handler);
//			isPicShow = false;
			// stopUserInfoTimeTask();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(view, InputMethodManager.HIDE_NOT_ALWAYS);
			selectPicFromCamera(); // 拍照 上传
		}

		if (view == btn_model_edit) {// 从编辑模式转换到语音
			// 软件盘的处理
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); // 强制隐藏键盘
			// restartMyTimeTask(handler);
			editModelToVoice(View.GONE);// 编辑模式隐藏 ，语音模式显示
		}

		if (view == btn_model_voice) { // 从语音转换到编辑模式
			et_sendmessage.setText("");
//			isPicShow = false;
			// restartMyTimeTask(handler);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
			editModelToVoice(View.VISIBLE);// 编辑模式显示
		}

		if (view == mBackwardbButton) {// 返回按钮
			if (isAboveZero && isOneMinute) {
				showDialog();
			} else {
				showOtherDialog();
			}
		}

		if (view == back) {
			if (isAboveZero && isOneMinute) {
				showDialog();
			} else {
				showOtherDialog();
			}
		}
	}

	public void sendTextRobotReply(int type, String msgContent) {
		ZhiChiMessageBase simpleMessage = new ZhiChiMessageBase();

		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		reply.setMsgType(type + "");
		reply.setRichpricurl("http://img1.imgtn.bdimg.com/it/u=1713787744,1555345659&fm=21&gp=0.jpg");

		if (2 != type) {
			reply.setRichmoreurl("http://www.sobot.com/img/logo.png");
		}
		reply.setDuration("08:30");
		reply.setMsg(msgContent);
		if (2 != type && 1 != type) {
			String[] sugges = new String[] { "C1S盒子及C1盒子怎么升级", "T1S产品介绍",
					"T1S机型及价格", "T1S产品性能简述 " };
			simpleMessage.setSugguestions(sugges);
		}

		simpleMessage.setAnswer(reply);

		simpleMessage.setId(System.currentTimeMillis() + "");

		// simpleMessage.setMsg(null);
		simpleMessage.setSenderName(initModel.getRobotName());
		simpleMessage.setSender(initModel.getRobotName());
		// simpleMessage.setMsgType(null);
		simpleMessage.setSenderType(ZhiChiConstant.message_type_robot_reply
				+ "");
		Message message = handler.obtainMessage();
		message.what = ZhiChiConstant.hander_robot_message;
		message.obj = simpleMessage;
		handler.sendMessage(message);
	}

	/* 下拉查看更多聊天记录 王帅 */
	@Override
	public void onRefresh() {
		flag = "flag";
		getHistoryMessage(flag, pageNow);
		pageNow++;
	}

	private void onLoad() {
		lv_message.setRefreshTime(time);
		lv_message.stopRefresh();
		lv_message.stopLoadMore();
	}

	@Override
	public void onLoadMore() {
	}

	/* 获取聊天记录 */
	private void getHistoryMessage(String flag, final int pageNow) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", initModel.getUid());
		map.put("cid", initModel.getCid());
		map.put("pageNow", pageNow + "");
		map.put("pageSize", "15");
		String json = SharedPreferencesUtil.getStringData(
				SobotChatActivity.this, pageNow + "", null);
		LogUtils.i("  获取历史记录的接口文档   ：" + json);

		/*********** 这是从内存中获取存储的聊天记录 *****************/
		getData(HttpMethod.POST, ZhiChiApi.api_robot_chat_historyMessage, map,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						LogUtils.i("error:" + arg1.toString());
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						LogUtils.i("----历史记录是 ：" + arg0.result);
						if (!TextUtils.isEmpty(arg0.result)
								&& arg0.result.trim().length() > 21) {
							ZhiChiHistoryMessage zhichi = (ZhiChiHistoryMessage) GsonUtil
									.jsonToBean(arg0.result,ZhiChiHistoryMessage.class);
							List<ZhiChiHistoryMessageBase> result = zhichi.getData();

							List<ZhiChiMessageBase> msgLists = new ArrayList<ZhiChiMessageBase>();
							if (result != null && result.size() > 0) {
								List<ZhiChiMessageBase> msgList = new ArrayList<ZhiChiMessageBase>();
								for (int i = 0; i < result.size(); i++) {
									ZhiChiHistoryMessageBase historyMsg = result.get(i);

									isNoMoreHistoryMsg = true;

									msgList = historyMsg.getContent();

									for (ZhiChiMessageBase base : msgList) {
										base.setSugguestionsFontColor(1);
										if (base.getSdkMsg() != null) {
											ZhiChiReplyAnswer answer = base
													.getSdkMsg().getAnswer();
											if (answer != null
													&& answer.getMsgType() == null) {
												answer.setMsgType("0");
											}
											if (ZhiChiConstant.message_type_robot_text == Integer
													.parseInt(base
															.getSenderType())) {
												base.setSenderName(base
														.getSenderName() == null ? initModel
														.getRobotName() : base
														.getSenderName());
											}
											base.setAnswer(answer);
											base.setSugguestions(base
													.getSdkMsg()
													.getSugguestions());
											base.setStripe(base.getSdkMsg()
													.getStripe());
											base.setAnswerType(base.getSdkMsg()
													.getAnswerType());
										}
									}
									msgLists.addAll(msgList);
								}
							}

							if (msgLists.size() > 0) {

								// add time reminde()
								msgLists.add(0,reRemindTimeMessage(msgLists.get(0).getTs()));
								Message message = new Message();
								message.what = ZhiChiConstant.hander_history;
								message.obj = msgLists;
								message.arg1 = msgLists.size();
								handler.sendMessage(message);
							}

						} else {
							isNoMoreHistoryMsg = false;
						}

						if (!isNoMoreHistoryMsg) {
							ZhiChiMessageBase base = new ZhiChiMessageBase();

							base.setSenderType(ZhiChiConstant.message_type_remide_info
									+ "");

							ZhiChiReplyAnswer reply1 = new ZhiChiReplyAnswer();
							reply1.setMsg("已无更多纪录");
							base.setAnswer(reply1);
							// 长时间没有
							base.setReconnectCustom(false);
							// 更新界面的操作
							updateUiMessageBefore(messageAdapter, base);

							handler.post(new Runnable() {
								@Override
								public void run() {
									lv_message.setSelection(0);
								}
							});

							lv_message.setPullRefreshEnable(false);// 设置下拉刷新列表
						}
					}
				});
		// }
		if (null != flag) {
			onLoad();
		}
	};

	/* 显示对话框 王帅 */
	@SuppressWarnings("deprecation")
	private void showDialog() {
		int width = getScreenWidth(this);
		/* 当前模式机器人 */
		if (current_client_model == ZhiChiConstant.client_model_robot) {

			String data[] = convertStrToArray(initModel.getRobotCommentTitle());
			RobotDialog dialog = null;
			RobotDialog.Builder customBuilder = null;

			if (data.length == 3) {
				customBuilder = new RobotDialog.Builder(SobotChatActivity.this,
						SobotChatActivity.this, color);
				customBuilder.setBtnOk("是", listener).setBtnNo("否", listener)
						.setStr1(data[0], listener).setStr2(data[1], listener)
						.setStr3(data[2], listener)
						.setBtnSubmitStr("提交评价", listener)
						.setNegativeButton("取消", listener)
						.setPositiveButton("立即结束", listener);
			}

			if (data.length == 4) {
				customBuilder = new RobotDialog.Builder(SobotChatActivity.this,
						SobotChatActivity.this, color);
				customBuilder.setBtnOk("是", listener).setBtnNo("否", listener)
						.setStr1(data[0], listener).setStr2(data[1], listener)
						.setStr3(data[2], listener).setStr4(data[3], listener)
						.setBtnSubmitStr("提交评价", listener)
						.setNegativeButton("取消", listener)
						.setPositiveButton("立即结束", listener);
			}

			dialog = customBuilder.create();
			dialog.show();
			WindowManager windowManager = getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			if (width == 480) {
				lp.width = (int) (display.getWidth() - 40); // 设置宽度
			} else {
				lp.width = (int) (display.getWidth() - 120); // 设置宽度
			}
			dialog.getWindow().setAttributes(lp);
		} else {
			String data[] = convertStrToArray(initModel.getManualCommentTitle());
			DCRCDialog dcrc = null;
			DCRCDialog.Builder dcrcDialog = null;

			if (data.length == 3) {
				dcrcDialog = new DCRCDialog.Builder(SobotChatActivity.this,
						SobotChatActivity.this, color);
				dcrcDialog.setStr1(data[0], listener)
						.setStr2(data[1], listener).setStr3(data[2], listener)
						.setBtnSubmitStr("提交评价", listener)
						.setNegativeButton("取消", listener)
						.setPositiveButton("立即结束", listener);
			}

			if (data.length == 4) {
				dcrcDialog = new DCRCDialog.Builder(SobotChatActivity.this,
						SobotChatActivity.this, color);
				dcrcDialog.setStr1(data[0], listener)
						.setStr2(data[1], listener).setStr3(data[2], listener)
						.setStr4(data[3], listener)
						.setBtnSubmitStr("提交评价", listener)
						.setNegativeButton("取消", listener)
						.setPositiveButton("立即结束", listener);
			}

			dcrc = dcrcDialog.create();
			dcrc.show();
			WindowManager windowManager = getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = dcrc.getWindow().getAttributes();
			if (width == 480) {
				lp.width = (int) (display.getWidth() - 40); // 设置宽度
			} else {
				lp.width = (int) (display.getWidth() - 120); // 设置宽度
			}
			dcrc.getWindow().setAttributes(lp);
		}
	};

	@SuppressWarnings("deprecation")
	private void showOtherDialog() {
		int width = getScreenWidth(this);
		int widths = 0;
		if (width == 480) {
			widths = 80;
		} else {
			widths = 120;
		}
		OtherDialog dialog = null;
		OtherDialog.Builder otherDialog = new OtherDialog.Builder(this, this,
				color);
		otherDialog.setMessage("结束本次对话？").setNegativeButton("否", listener)
				.setPositiveButton("是", listener);
		dialog = otherDialog.create();
		dialog.show();
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int) (display.getWidth() - widths); // 设置宽度
		dialog.getWindow().setAttributes(lp);
	}

	// 键盘编辑模式转换为语音模式
	private void editModelToVoice(int typeModel) {
		btn_model_edit.setVisibility(View.GONE == typeModel ? View.GONE
				: View.VISIBLE); // 键盘编辑隐藏
		btn_model_voice.setVisibility(View.VISIBLE != typeModel ? View.VISIBLE
				: View.GONE);// 语音模式开启
		btn_press_to_speak.setVisibility(View.GONE != typeModel ? View.VISIBLE
				: View.GONE);
		edittext_layout.setVisibility(View.VISIBLE == typeModel ? View.GONE
				: View.VISIBLE);
		// 设置editText内部的光标
		if (View.VISIBLE == typeModel) { // 编辑模式隐藏
			et_sendmessage.setInputType(InputType.TYPE_NULL);
		} else {
			et_sendmessage.setInputType(InputType.TYPE_CLASS_TEXT);
			edittext_layout.requestFocus();
		}
		moreFunction.setVisibility(View.GONE);
		btn_send.setVisibility(View.GONE);
	}

	/**
	 * 通过照相上传图片
	 */
	private void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			Toast.makeText(getApplicationContext(), "SD卡不存在，不能拍照",
					Toast.LENGTH_SHORT).show();
			return;
		}

		String path = CommonUtils.getSDCardRootPath() + "/"
				+ getApplicationName()+"/" + System.currentTimeMillis() + ".jpg";
		// 创建图片文件存放的位置
		cameraFile = new File(path);
		cameraFile.getParentFile().mkdirs();
		LogUtils.i("cameraPath:" + path);
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				ZhiChiConstant.REQUEST_CODE_makePictureFromCamera);
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			// crop为true是设置在开启的intent中设置显示的view可以剪裁
			/*
			 * intent.putExtra("crop", "true"); // aspectX aspectY 是宽高的比例
			 * intent.putExtra("aspectX", 1); intent.putExtra("aspectY", 1); //
			 * outputX,outputY 是剪裁图片的宽高 intent.putExtra("outputX", 200);
			 * intent.putExtra("outputY", 200); intent.putExtra("return-data",
			 * true);
			 */
		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, ZhiChiConstant.REQUEST_CODE_picture);
	}

	/**
	 * 返回数据
	 */
	@SuppressWarnings("unused")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i("多媒体返回的结果：" + requestCode + "--" + resultCode + "--" + data);

		if (resultCode == RESULT_OK) {
			if (requestCode == ZhiChiConstant.REQUEST_CODE_picture) { // 发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						// ???/
						LogUtils.i("发送图片的地址信息:" + selectedImage.toString());
						// 通知handler更新图片
						sendPicByUri(selectedImage);
						stopUserInfoTimeTask();
						setTimeTaskMethod(handler);
						restartMyTimeTask(handler);
						LogUtils.i("发送图片的地址信息:" + selectedImage.toString());
					}
				} else {

					Toast toast = Toast.makeText(SobotChatActivity.this,
							"未获取图片的地址", Toast.LENGTH_LONG);
					// 可以控制toast显示的位置
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 10);
					toast.show();

				}
			} else if (requestCode == ZhiChiConstant.REQUEST_CODE_makePictureFromCamera) {
				if (cameraFile != null && cameraFile.exists())
					LogUtils.i("cameraFile.getAbsolutePath()------>>>>"
							+ cameraFile.getAbsolutePath());
				// 限制文件的上传的大小
				long size = CommonUtils.getFileSize(cameraFile
						.getAbsolutePath());
				DecimalFormat df = new DecimalFormat("#.00");
				if (size < 3145728) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("cid", initModel.getCid());
					map.put("uid", initModel.getUid());
					String id = System.currentTimeMillis() + "";
					sendImageMessageToHandler(cameraFile.getAbsolutePath()
							.toString(), handler, id);
					sendPicture(map, cameraFile.getAbsolutePath(), handler, id);
				} else {
					// 设置限制的提醒
					Toast toast = Toast.makeText(SobotChatActivity.this,
							"图片大小需小于3M", Toast.LENGTH_LONG);
					// 可以控制toast显示的位置
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 10);
					toast.show();
				}
				stopUserInfoTimeTask();
				setTimeTaskMethod(handler);
				restartMyTimeTask(handler);
			}
			moreFunction.setVisibility(View.GONE);
		}
	}

	private void sendPicByUri(Uri selectedImage) {
		Cursor cursor = getContentResolver().query(selectedImage, null, null,
				null, null);
		// 配置请求参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("cid", initModel.getCid());
		map.put("uid", initModel.getUid());

		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;
			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			String id = System.currentTimeMillis() + "";
			sendPicLimitBySize(0, id, picturePath, map);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			String id = System.currentTimeMillis() + "";
			sendPicLimitBySize(1, id, file.getAbsolutePath(), map);
		}
	}

	public void sendPicLimitBySize(int type, String id, String filePath,
			Map<String, String> map) {
		long size = CommonUtils.getFileSize(filePath);
		@SuppressWarnings("unused")
		DecimalFormat df = new DecimalFormat("#.00");
		if (size < 3145728) {
			if (type == 0) { // 从相册中获取
				sendImageMessageToHandler(filePath, handler, id);
				sendPicture(map, filePath, handler, id);
			} else if (type == 1) {// 从相机中获取
				sendPicture(map, filePath, handler, id);
			}
		} else {
			Toast toast = Toast.makeText(SobotChatActivity.this, "图片大小需小于3M",
					Toast.LENGTH_LONG);
			// 可以控制toast显示的位置
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 10);
			toast.show();
		}

	}

	/**
	 * 广播接受者：
	 */
	public class MyMessageReceiver extends BroadcastReceiver {

		@SuppressWarnings("static-access")
		@Override
		public void onReceive(Context context, Intent intent) {
			String msg = (String) intent.getExtras().get("msgContent");

			// 客服返回消息 ，开始计算客户的反应时间
			if (is_start_push) {
				if (MainActivity.brocastContantant.equals(intent.getAction())) {
					// 接受下推的消息
					ZhiChiPushMessage pushMessage = new ZhiChiPushMessage();
					pushMessage = (ZhiChiPushMessage) GsonUtil.jsonToBean(msg,
							ZhiChiPushMessage.class);
					ZhiChiMessageBase base = new ZhiChiMessageBase();
					base.setSenderName(pushMessage.getAname());

					// 建立会话连接
					if (ZhiChiConstant.push_message_createChat == pushMessage
							.getType()) {
						btn_set_mode_rengong.setClickable(true);
						btn_set_mode_rengong.setAlpha(1f);
						customeCreateSession(base, pushMessage);
						if (is_startTask) {
							stopUserInfoTimeTask();
							is_startCustomTimerTask = false;
							current_client_model = ZhiChiConstant.client_model_customService;
							startUserInfoTimeTask(handler);
						}
						// 应用内的消息广播
					} else if (ZhiChiConstant.push_message_paidui == pushMessage
							.getType()) {// 排队的消息类型
						// base.setMsgType(ZhiChiConstant.message_type_text+"");
						base.setSenderType(ZhiChiConstant.message_type_remide_info
								+ "");
						ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
						reply.setMsg("排队中,您在队伍中的第" + pushMessage.getCount()
								+ "个");
						/*
						 * base.setMsg("排队中,您在队伍中的第" + pushMessage.getCount() +
						 * "个");
						 */
						base.setAnswer(reply);
						// 更新界面的操作
						updateUiMessage(messageAdapter, base);
						// 排队的时候 转人工的按钮 是不能够点击的
						paiduiTimes = paiduiTimes + 1;
						if (type == 4) {
							if (paiduiTimes == 1) {
								remindRobotMessage(messageAdapter, handler);
							}
							// 转到机械人的模式
							customServiceToRobotModel();

						}
						btn_set_mode_rengong.setClickable(false);
						btn_set_mode_rengong.setAlpha(0.2f);
						// 更具ui 操作的界面
						// customServiceToRobotModel();
					} else if (ZhiChiConstant.push_message_receverNewMessage == pushMessage
							.getType()) {// 接收到新的消息
						if (current_client_model == ZhiChiConstant.client_model_customService) {
							base.setSender(pushMessage.getAname());
							base.setSenderName(pushMessage.getAname());
							base.setSenderType(ZhiChiConstant.message_type_robot_text
									+ "");
							ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
							reply.setMsgType(pushMessage.getMsgType() + "");
							reply.setMsg(pushMessage.getContent());
							base.setAnswer(reply);
							// 更新界面的操作
							updateUiMessage(messageAdapter, base);
							if (is_startTask) {
								stopCustomTimeTask();
								startUserInfoTimeTask(handler);
							}
						}

					} else if (ZhiChiConstant.push_message_outLine == pushMessage
							.getType()) {// 用户被下线

						// 修改提醒的信息
						base.setSenderType(ZhiChiConstant.message_type_remide_info
								+ "");

						int type = Integer.parseInt(pushMessage.getStatus());
						ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
						reply.setMsg(getMessageContentByOutLineType(
								pushMessage.getAname(), type));
						/*
						 * base.setMsg(getMessageContentByOutLineType(
						 * pushMessage.getAname(), type));
						 */
						base.setAnswer(reply);
						// 长时间没有

						if (6 != type) { // 打开新窗口的不用提示

							if (1 == type) {
								base.setReconnectCustom(true);
							} else if (2 == type) {
								base.setReconnectCustom(true);
							} else if (3 == type) {
								base.setReconnectCustom(true);
							} else if (4 == type) {
								base.setReconnectCustom(true);
							} else if (6 == type) {
								base.setReconnectCustom(false);
							}
							// 更新界面的操作
							if (isFirstTransCustom == true) {
								updateUiMessage(messageAdapter, base);
								// 更具ui 操作的界面
								customServiceToRobotModel();
							}
						}
						if (6 == type) {
							customeCreateSession(base, pushMessage);
						}
					}
				} else if (MainActivity.sendMyMessageBrocast.equals(intent
						.getAction())) {
					ZhiChiMessageBase base = (ZhiChiMessageBase) GsonUtil
							.jsonToBean(msg, ZhiChiMessageBase.class);

					// 消息的转换
					ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
					answer.setMsgType(ZhiChiConstant.message_type_text + "");
					answer.setMsg(base.getContext());
					base.setAnswer(answer);
					base.setSenderType(ZhiChiConstant.message_type_wo_text + "");
					if (base.getId() == null || TextUtils.isEmpty(base.getId())) {
						updateUiMessage(messageAdapter, base);
					}
					try {
						sendMessage(base.getId(), base.getContext(), initModel,
								handler, current_client_model);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (MainActivity.ResendTextBrocast.equals(intent
						.getAction())) {// resendTextMessage;
					ZhiChiMessageBase base = (ZhiChiMessageBase) GsonUtil
							.jsonToBean(msg, ZhiChiMessageBase.class);

					// 消息的转换
					ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
					answer.setMsgType(ZhiChiConstant.message_type_text + "");
					answer.setMsg(base.getContext());
					base.setAnswer(answer);
					base.setSenderType(ZhiChiConstant.message_type_wo_text + "");
					try {
						sendMessage(base.getId(), base.getContext(), initModel,
								handler, current_client_model);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else if (MainActivity.picReUploadBrocast.equals(intent
						.getAction())) {// 图片的重新上传

					LogUtils.i(" 图片重新上传的接口广播的接收：  " + msg.toString());
					ZhiChiMessageBase base = (ZhiChiMessageBase) GsonUtil
							.jsonToBean(msg, ZhiChiMessageBase.class);
					// 根据图片的url 上传图片 更新上传图片的进度
					Map<String, String> map = new HashMap<String, String>();
					map.put("cid", initModel.getCid());
					map.put("uid", initModel.getUid());
					sendPicture(map, base.getContext(), handler, base.getId());
					// 重新连接人工服务器
				} else if (MainActivity.reConnectCustom.equals(intent
						.getAction())) {
					ZhiChiMessageBase base = (ZhiChiMessageBase) GsonUtil
							.jsonToBean(msg, ZhiChiMessageBase.class);
					if (base.getReconnectCustom()) {
						connectRongYun();
					}
				} else if (MainActivity.voiceReUploadBrocast.equals(intent
						.getAction())) {
					LogUtils.i("--- voice  brocast received-- :" + msg);
					// 语音的重新上传
					ZhiChiMessageBase base = (ZhiChiMessageBase) GsonUtil
							.jsonToBean(msg, ZhiChiMessageBase.class);
					Map<String, String> map = new HashMap<String, String>();
					map.put("cid", initModel.getCid());
					map.put("uid", initModel.getUid());
					map.put("duration", base.getDuration());
					sendVoice(base.getId(), base.getDuration(), map,
							base.getContext(), handler);
					// 检测网络的连接
				} else if (ConnectivityManager.CONNECTIVITY_ACTION
						.equals(intent.getAction())) {

					ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
					NetworkInfo mobNetInfo = connectMgr
							.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
					NetworkInfo wifiNetInfo = connectMgr
							.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

					if (wifiNetInfo != null && mobNetInfo != null
							&& !mobNetInfo.isConnected()
							&& !wifiNetInfo.isConnected()) {
						setShowNetRemind(true);
					} else {
						setShowNetRemind(false);
					}
				}else if(MainActivity.quitSobotConnection.equals(intent.getAction())){//退出的效果
					// 融云被剔除的效果
					ZhiChiPushMessage pushMessage = new ZhiChiPushMessage();
					pushMessage = (ZhiChiPushMessage) GsonUtil.jsonToBean(msg,
							ZhiChiPushMessage.class);
					ZhiChiMessageBase base = new ZhiChiMessageBase();
					base.setSenderName(pushMessage.getAname());
					base.setSenderType(ZhiChiConstant.message_type_remide_info
							+ "");
					int type = Integer.parseInt(pushMessage.getStatus());
					ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
					reply.setMsg("你打开了新窗口，此窗口会话结束");
					base.setAnswer(reply);
					if(pushMessage.isQuitRongConnect()){
						//转人工的方式
						if (is_startTask) {
							stopCustomTimeTask();
							startUserInfoTimeTask(handler);
						}
						updateUiMessage(messageAdapter, base);
						customServiceToRobotModel();	
					}
				}
			}
		}
	}

	public void customeCreateSession(ZhiChiMessageBase base,
			ZhiChiPushMessage pushMessage) {

		if (isFirstTransCustom) {
			currentUserName = pushMessage.getAname();
			// 设置转认人工可以点击
			btn_set_mode_rengong.setEnabled(true);
			btn_set_mode_rengong.setAlpha(1f);
			// 转换成为自己的定义的格式类型
			base.setSenderType(ZhiChiConstant.message_type_remide_info + "");
			ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
			reply.setMsgType(null);
			reply.setMsg("客服  " + pushMessage.getAname() + "   接受了您的请求");
			base.setAnswer(reply);
			// 更新界面的操作
			updateUiMessage(messageAdapter, base);
			ZhiChiMessageBase base2 = new ZhiChiMessageBase();
			base2.setSenderName(pushMessage.getAname());
			ZhiChiReplyAnswer reply2 = new ZhiChiReplyAnswer();
			reply2.setMsgType(ZhiChiConstant.message_type_text + "");
			base2.setSenderType(ZhiChiConstant.message_type_robot_text + "");
			reply2.setMsg(initModel.getAdminHelloWord());
			base2.setAnswer(reply2);
			// 更新界面的操作
			setTitle(pushMessage.getAname()); // 设置后台推送消息的对象
			updateUiMessage(messageAdapter, base2);
			robotToCustomServiceModel();
			isAboveZero = false;
			isOneMinute = false;
			isPastOneMinute.start();
		}
	}

	private String getMessageContentByOutLineType(String currentUserName,
			int type) {
		if (1 == type) {// 管理员下线
			return "客服"
					+ currentUserName
					+ " "
					+ getResources().getString(
							ResourceUtils.getIdByName(this, "string",
									"outline_managerOutLine"));
			// + getReconnectString();
		} else if (2 == type) { // 被管理员移除
			return "客服   "
					+ currentUserName
					+ " "
					+ getResources().getString(
							ResourceUtils.getIdByName(this, "string",
									"outline_leverByManager"));
			// + getReconnectString();
		} else if (3 == type) { // 被加入黑名单
			return "客服 "
					+ currentUserName
					+ " "
					+ getResources().getString(
							ResourceUtils.getIdByName(this, "string",
									"outline_JoinBlacklistByManager"));
			// + getReconnectString();
		} else if (4 == type) { //
			return getResources().getString(
					ResourceUtils.getIdByName(this, "string",
							"outline_LongTimeNoSay"));
			// + getReconnectString();
		} else if (6 == type) {
			return getResources().getString(
					ResourceUtils.getIdByName(this, "string",
							"outline_openNewWindows"));
		}
		return null;
	}

	private void initView() {
		if (type == 1) {/* 只有机器人 */
			LogUtils.i("type--->" + type + "只有机器人");
			btn_set_mode_rengong.setVisibility(View.GONE);
		} else if (type == 3) {/* 智能客服-机器人优先 */
			btn_set_mode_rengong.setVisibility(View.VISIBLE);
		} else if (type == 4) {/* 4智能客服-人工客服优先', */
			isFirstTransCustom = true;
			/*
			 * if (is_startTask) { stopUserInfoTimeTask();
			 * startUserInfoTimeTask(handler); }
			 */
			btn_set_mode_rengong.setVisibility(View.GONE);
			connectRongYun();
			// robotToCustomServiceModel();
		}
	}

	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			isOneMinute = true;
		}

		@Override
		public void onTick(long arg0) {

		}
	}

	/**
	 * 关闭融云会话连接
	 */
	@Override
	protected void onDestroy() {
		// 取消广播接受者
		unregisterReceiver(receiver);
		// SharedPreferencesUtil.saveStringData(SobotChatActivity.this,
		// "sobotCloudChatAppKey", null);
		// 停止用户的定时任务
		stopUserInfoTimeTask();
		// 停止客服的定时任务
		stopCustomTimeTask();
		AudioTools.getInstance().stop();
		loginOut();
		// 关闭融云会话连接
		RongIMClient.getInstance().logout();
		super.onDestroy();
	}

	/**
	 * 得到设备屏幕的宽度
	 */
	private int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/* 返回按钮 */
	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		if (isAboveZero && isOneMinute) {
			showDialog();
		} else {
			showOtherDialog();
		}
	}

	// 使用String的split 方法把字符串截取为字符串数组
	private String[] convertStrToArray(String str) {
		String[] strArray = null;
		strArray = str.split(","); // 拆分字符为"," ,然后把结果交给数组strArray
		return strArray;
	}

	private void conntRongYun(String token) {

		RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
			@Override
			public void onTokenIncorrect() {
				LogUtils.i("---融云--连接失败-");
				rongyun_is_connect = false;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast toast = Toast.makeText(SobotChatActivity.this,
								"暂时无法转接人工服务，请稍后再试", Toast.LENGTH_SHORT);
						// 可以控制toast显示的位置
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 10);
						toast.show();
					}
				});
			}

			@Override
			public void onError(ErrorCode arg0) {
				LogUtils.i("---融云--连接失败-" + arg0.toString());
				rongyun_is_connect = false;
				SharedPreferencesUtil.saveStringData(
						SobotChatActivity.this,
						CommonUtils
								.getSobotCloudChatAppKey(getApplicationContext()),
						null);
				Toast toast = Toast.makeText(SobotChatActivity.this,
						"暂时无法转接人工服务，请稍后再试", Toast.LENGTH_SHORT);
				// 可以控制toast显示的位置
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 10);
				toast.show();
			}

			@Override
			public void onSuccess(String arg0) {
				com.sobot.chat.application.RongCloudEvent.getInstance()
						.setOtherListener();
				LogUtils.i("融云--连接成功" + arg0);
				rongyun_is_connect = true;
				if (type == 4) {

					connectRongYun();
				}

			}
		});
	}

	public int getScrollPosString() {
		return scrollPos;
	}

	public int getScrollTopString() {
		return scrollTop;
	}

	public void updateListItem(final int scrollPos, final int scrollTop) {
		lv_message.post(new Runnable() {

			@Override
			public void run() {
				LogUtils.i("scrollPos:我 updateListItem 位置：" + scrollPos
						+ "---scrollTop:" + scrollTop);
				lv_message.setSelectionFromTop(scrollPos, scrollTop);
				// messageAdapter.notifyDataSetChanged();

			}
		});
	}

	public void zhichi_TransferCustomer_Service(
			final Button btn_set_mode_rengong, final String uid,
			final String cid, final Handler handler,
			final MessageAdapter messageAdapter) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", uid);
		map.put("cid", cid);
		getData(HttpMethod.POST, ZhiChiApi.api_transfer_people, map,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						btn_set_mode_rengong.setEnabled(true);
						btn_set_mode_rengong.setAlpha(1f);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						LogUtils.i("转人工接口：" + arg0.result);
						// 更新ui客户的状态信息
						if (arg0.result != null) {
							ZhiChiMessage result = (ZhiChiMessage) GsonUtil
									.jsonToBean(arg0.result,
											ZhiChiMessage.class);
							ZhiChiMessageBase zhichiMessage = result.getData();
							ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();

							int status = Integer.parseInt(zhichiMessage
									.getStatus());

							zhichiMessage
									.setSenderType(ZhiChiConstant.message_type_remide_info
											+ "");
							// 发送消息格式内容
							reply.setMsgType(null);
							// setMsgType
							Message message = handler.obtainMessage();
							if (ZhiChiConstant.transfer_customeServeive_success == status) {
								reply.setMsg("成功");
								// 启动计时任务
								message.arg1 = ZhiChiConstant.transfer_customeServeive_success;
							} else if (ZhiChiConstant.transfer_customeServeive_fail == status) {
								reply.setMsg("暂无客服在线");
								// 发送消息更新ui

							} else if (ZhiChiConstant.transfer_customeServeive_isBalk == status) {
								reply.setMsg("暂时无法转接人工客服");
								// 发送消息更新ui
							} else if (ZhiChiConstant.transfer_customeServeive_alreay == status) {
								reply.setMsg("您已经转人工");
							}
							zhichiMessage.setAnswer(reply);
							message.what = ZhiChiConstant.message_type_remide_info;
							message.obj = zhichiMessage;
							message.arg1 = status;
							handler.sendMessage(message);
							if (ZhiChiConstant.transfer_customeServeive_success != status
									&& type == 4) {
								remindRobotMessage(messageAdapter, handler);
								// 转到机械人的模式
								customServiceToRobotModel();
							}
							if (ZhiChiConstant.transfer_customeServeive_fail == status) {
								btn_set_mode_rengong.setEnabled(true);
								btn_set_mode_rengong.setAlpha(1f);
							}
						}
					}
				});

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		 //模式的转化
		 f_proximiny = event.values[0];
		 LogUtils.i("监听模式的转换："+f_proximiny  +  " 听筒的模式："+  mProximiny.getMaximumRange());
      //   if (f_proximiny == mProximiny.getMaximumRange() || f_proximiny==8.0 ) {
        if ( f_proximiny!=0.0 ) {
	         audioManager.setSpeakerphoneOn(true);//打开扬声器
	         audioManager.setMode(AudioManager.MODE_NORMAL); 
             LogUtils.i("监听模式的转换：" + "正常模式");
         } else {  
            audioManager.setSpeakerphoneOn(false);//关闭扬声器
           //audioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            //把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            LogUtils.i("监听模式的转换：" + "听筒模式");
         }  
	}
}