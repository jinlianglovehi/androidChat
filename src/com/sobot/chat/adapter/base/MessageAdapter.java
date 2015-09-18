package com.sobot.chat.adapter.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.sobot.chat.activity.PhotoActivity;
import com.sobot.chat.activity.SobotChatActivity;
import com.sobot.chat.model.ZhiChiMessageBase;
import com.sobot.chat.utils.AudioTools;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ImageUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;

/**
 * 消息适配器
 * 
 * @author jinliang
 * 
 */
public class MessageAdapter extends MyBaseAdapter<ZhiChiMessageBase> {

	public int viewType = 15;
	private ZhiChiMessageBase message;
	private Timer timer = null;
	private int currentVoiceItem = -1;
	protected static final String Voice_path = ZhiChiConstant.voicePositionPath;
	
	//protected static final String Voice_path = CommonUtils.getSDPath() + "sobot/voice/";
	
	// 获取img标签
	private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
	// 提取image url地址的正则表达式
	private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";

	public MessageAdapter(Context context, List<ZhiChiMessageBase> list) {
		super(context, list);
	}

	public void addData(List<ZhiChiMessageBase> moreList) {
		list.addAll(0, moreList);
	}

	public void addData(ZhiChiMessageBase message) {
		list.add(message);
	}

	public void addDataBefore(ZhiChiMessageBase message) {
		list.add(0,message);
	}

	public void updateMsgInfoById(String id, int senderState, int progressBar) {
		ZhiChiMessageBase info = getMsgInfo(id);
		if (info != null) {
			info.setMysendMessageState(senderState);
			info.setSendMessageTime(System.currentTimeMillis());
			info.setTextFailTimes(info.getTextFailTimes() + 1);
			info.setProgressBar(progressBar);
		}

	}

	public void updateVoiceStatusById(String id, int sendStatus) {
		ZhiChiMessageBase info = getMsgInfo(id);
		if (info != null) {
			info.setSendSuccessState(sendStatus);
			info.setTextFailTimes(info.getTextFailTimes() + 1);
			info.setSendMessageTime(System.currentTimeMillis());
		}
	}

	private ZhiChiMessageBase getMsgInfo(String id) {

		for (Object obj : list) {
			if (!(obj instanceof ZhiChiMessageBase)) {
				continue;
			}
			ZhiChiMessageBase msgInfo = (ZhiChiMessageBase) obj;
			if (msgInfo.getId() != null && msgInfo.getId().equals(id)) {
				return msgInfo;
			}
		}
		return null;
	}

	/**
	 * 加载布局后的性能优化
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// LogUtils.i("getView method: " + position);
		int type = getItemViewType(position);
		// LogUtils.i("----该条信息的布局类型---" + type);
		MessageHolderBase baseHolder = null;
		if (convertView == null) {
			switch (type) {
			/**
			 * 文本布局
			 */
			case ZhiChiConstant.message_type_robot_text: // 机械人的文本消息 布局
				baseHolder = new TextMessageHolder();
				convertView = View.inflate(context, ResourceUtils.getIdByName(
						context, "layout", "list_item_robot_text"), null);
				convertView.setTag(baseHolder);
				handerTextMessage((TextMessageHolder) baseHolder, position,
						convertView, 0);
				break;
			case ZhiChiConstant.message_type_wo_text: // 我的文本消息 布局
				baseHolder = new TextMessageHolder();
				convertView = View.inflate(context, ResourceUtils.getIdByName(
						context, "layout", "list_item_wo_text"), null);
				convertView.setTag(baseHolder);
				handerTextMessage((TextMessageHolder) baseHolder, position,
						convertView, 1);
				break;
			case ZhiChiConstant.message_type_robot_reply: // 机械人的回答语

				baseHolder = new TextMessageHolder();
				convertView = View.inflate(context, ResourceUtils.getIdByName(
						context, "layout", "list_item_robot_text"), null);
				convertView.setTag(baseHolder);
				handerTextMessage((TextMessageHolder) baseHolder, position,
						convertView, 2);
				// handerRobotReplyMessage((TextMessageHolder)baseHolder,position
				// ,convertView);
				break;

			/**
			 * 图片布局
			 */
			case ZhiChiConstant.message_type_custom_pic: // 客服图片布局
				break;
			case ZhiChiConstant.message_type_wo_pic: // 我图片布局
				break;
			/**
			 * 图文布局
			 */
			case ZhiChiConstant.message_type_robot_image:
				baseHolder = new ImageMessageHolder();
				convertView = View.inflate(context, ResourceUtils.getIdByName(
						context, "layout", "list_item_robot_image"), null);
				convertView.setTag(baseHolder);
				handerImageMessage((ImageMessageHolder) baseHolder, position,
						convertView, 0);
				break;
			case ZhiChiConstant.message_type_wo_sendImage:
				baseHolder = new ImageMessageHolder();
				convertView = View.inflate(context, ResourceUtils.getIdByName(
						context, "layout", "list_item_wo_image"), null);
				convertView.setTag(baseHolder);
				handerImageMessage((ImageMessageHolder) baseHolder, position,
						convertView, 2);
				break;
			case ZhiChiConstant.message_type_myhistory_image:
				baseHolder = new ImageMessageHolder();
				convertView = View.inflate(context, ResourceUtils.getIdByName(
						context, "layout", "list_item_wo_image"), null);
				convertView.setTag(baseHolder);
				handerImageMessage((ImageMessageHolder) baseHolder, position,
						convertView, 1);
				break;
			case ZhiChiConstant.message_type_remide_info:// 消息提醒
				baseHolder = new TextMessageHolder();
				convertView = View.inflate(context, ResourceUtils.getIdByName(
						context, "layout", "list_item_wo_text"), null);
				convertView.setTag(baseHolder);
				handerRemindMessage((TextMessageHolder) baseHolder, position,
						convertView);
				break;
			case ZhiChiConstant.message_type_send_voice:// 发送语音消息
				baseHolder = new VoiceMessageHolder();
				convertView = View.inflate(context, ResourceUtils.getIdByName(
						context, "layout", "list_item_wo_voice"), null);
				convertView.setTag(baseHolder);
				handerVoiceMessage((VoiceMessageHolder) baseHolder, position,
						convertView, 1);
				break;
			case ZhiChiConstant.message_type_receive_robot_voice:// 接受机械人的语音
				baseHolder = new VoiceMessageHolder();
				convertView = View.inflate(context, ResourceUtils.getIdByName(
						context, "layout", "list_item_robot_voice"), null);
				convertView.setTag(baseHolder);
				handerVoiceMessage((VoiceMessageHolder) baseHolder, position,
						convertView, 0);

				break;
			case ZhiChiConstant.message_type_receive_my_voice:// 接受我的语音
				baseHolder = new VoiceMessageHolder();
				convertView = View.inflate(context, ResourceUtils.getIdByName(
						context, "layout", "list_item_wo_voice"), null);
				convertView.setTag(baseHolder);
				handerVoiceMessage((VoiceMessageHolder) baseHolder, position,
						convertView, 2);
				break;
			default:
				break;
			}
			return convertView;
		} else {
			switch (type) {
			/**
			 * 文本布局
			 */
			case ZhiChiConstant.message_type_robot_text: // 机械人的文本消息 布局

				baseHolder = (MessageHolderBase) convertView.getTag();
				handerTextMessage((TextMessageHolder) baseHolder, position,
						convertView, 0);
				break;
			case ZhiChiConstant.message_type_wo_text: // 我的文本消息 布局
				baseHolder = (MessageHolderBase) convertView.getTag();
				handerTextMessage((TextMessageHolder) baseHolder, position,
						convertView, 1);
				break;
			case ZhiChiConstant.message_type_robot_reply: // 我的文本消息 布局
				baseHolder = (MessageHolderBase) convertView.getTag();
				handerTextMessage((TextMessageHolder) baseHolder, position,
						convertView, 2);
				// handerRobotReplyMessage((TextMessageHolder)baseHolder,position
				// ,convertView);
				break;
			/**
			 * 图片布局
			 */
			case ZhiChiConstant.message_type_custom_pic: // 客服图片布局
				break;
			case ZhiChiConstant.message_type_wo_pic: // 我图片布局
				break;
			/**
			 * 图文布局
			 */
			case ZhiChiConstant.message_type_robot_image:
				baseHolder = (MessageHolderBase) convertView.getTag();
				handerImageMessage((ImageMessageHolder) baseHolder, position,
						convertView, 0);
				break;
			case ZhiChiConstant.message_type_wo_sendImage:
				baseHolder = (MessageHolderBase) convertView.getTag();
				handerImageMessage((ImageMessageHolder) baseHolder, position,
						convertView, 2);
				break;
			case ZhiChiConstant.message_type_myhistory_image:
				baseHolder = (MessageHolderBase) convertView.getTag();
				handerImageMessage((ImageMessageHolder) baseHolder, position,
						convertView, 1);
				break;
			case ZhiChiConstant.message_type_remide_info:// 消息提醒
				baseHolder = (MessageHolderBase) convertView.getTag();
				handerRemindMessage((TextMessageHolder) baseHolder, position,
						convertView);
				break;
			case ZhiChiConstant.message_type_send_voice:
				baseHolder = (MessageHolderBase) convertView.getTag();
				handerVoiceMessage((VoiceMessageHolder) baseHolder, position,
						convertView, 1);
				break;

			case ZhiChiConstant.message_type_receive_robot_voice:// 接受机械人的语音
				baseHolder = (MessageHolderBase) convertView.getTag();
				handerVoiceMessage((VoiceMessageHolder) baseHolder, position,
						convertView, 0);
				break;
			case ZhiChiConstant.message_type_receive_my_voice:// 接受我的语音
				baseHolder = (MessageHolderBase) convertView.getTag();
				handerVoiceMessage((VoiceMessageHolder) baseHolder, position,
						convertView, 2);
				break;
			default:
				break;
			}
			return convertView;

		}
	}

	// 处理语音消息
	private void handerVoiceMessage(final VoiceMessageHolder voiceHolder,
			final int position, View convertView, int type) {
		message = list.get(position);
		voiceHolder.name = (TextView) convertView.findViewById(ResourceUtils
				.getIdByName(context, "id", "name"));
		voiceHolder.voice = (ImageView) convertView.findViewById(ResourceUtils
				.getIdByName(context, "id", "iv_voice"));
		voiceHolder.voiceTimeLong = (TextView) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"voiceTimeLong"));
		voiceHolder.ll_voice_layout = (LinearLayout) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"ll_voice_layout"));

		voiceHolder.msgProgressBar = (ProgressBar) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"msgProgressBar"));

		// 设置语音的状态
		voiceHolder.voiceTimeLong.setText(message.getAnswer().getDuration()+"''");
		final String voidePath = message.getAnswer().getMsg();

		if (currentVoiceItem > 0 && currentVoiceItem != position) {
			if (type == 1 || type == 2) {
				// 我放的语音图片
				voiceHolder.voice.setImageResource(ResourceUtils.getIdByName(
						context, "drawable", "pop_voice_send_anime_3"));
			} else if (type == 0) {
				// 对方发送语音图片
				voiceHolder.voice.setImageResource(ResourceUtils.getIdByName(
						context, "drawable", "pop_voice_receive_anime_3"));
			}
		}

		if (0 == type) { // 接受机械人的语音 或者是人工的语音 机械人无语音
			// 点击播放语音
			voiceHolder.name.setVisibility(View.VISIBLE);
			voiceHolder.name.setText(message.getSenderName());
			voiceHolder.ll_voice_layout
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// 直接播放本地的音乐
							// o//b
							voiceHolder.voice.setImageResource(ResourceUtils
									.getIdByName(context, "drawable",
											"voice_from_icon"));
							AnimationDrawable animationDrawable = (AnimationDrawable) voiceHolder.voice
									.getDrawable();
//							LogUtils.i("播放 我发送的语音文件的路径：" + voidePath);
							list.get(position).setVoideIsPlaying(true);
							animationDrawable.start();
							playVoice(position, voidePath, animationDrawable,
									voiceHolder.voice, 1,0,0);
						}
					});
		} else if (1 == type) {// 我发送的语音
			// 点击播放语音
			voiceHolder.voiceStatus = (ImageView) convertView
					.findViewById(ResourceUtils.getIdByName(context, "id",
							"voiceStatus"));
			voiceHolder.voiceStatus.setClickable(true);
			if (message.getSendSuccessState() == 1) {
				voiceHolder.voiceStatus.setVisibility(View.GONE);
				voiceHolder.msgProgressBar.setVisibility(View.GONE);
			} else if (message.getSendSuccessState() == 0) {
				voiceHolder.voiceStatus.setVisibility(View.VISIBLE);
				voiceHolder.msgProgressBar.setVisibility(View.GONE);
				// 语音的重新发送
				voiceHolder.voiceStatus.setClickable(true);
				voiceHolder.voiceStatus
						.setOnClickListener(new RetrySendVoiceLisenter(message
								.getId(), message.getAnswer().getMsg(), message
								.getDuration(), voiceHolder.voiceStatus,
								position));
				//voiceHolder.voiceStatus.setClickable(false);
			} else if (message.getSendSuccessState() == 2) {// 发送中
				voiceHolder.msgProgressBar.setVisibility(View.VISIBLE);
				voiceHolder.voiceStatus.setVisibility(View.GONE);
			}

			voiceHolder.name.setText(null);
			
			voiceHolder.ll_voice_layout
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// 直接播放本地的音乐
							// o//b
							int   scrollPos=  ((SobotChatActivity) context).getScrollPosString();
						    int  scrollTop=((SobotChatActivity) context).getScrollTopString();
							voiceHolder.voice.setImageResource(ResourceUtils
									.getIdByName(context, "drawable",
											"voice_to_icon"));
							list.get(position).setVoideIsPlaying(true);
							AnimationDrawable animationDrawable = (AnimationDrawable) voiceHolder.voice
									.getDrawable();
							animationDrawable.start();
							playVoice(position, voidePath, animationDrawable,
									voiceHolder.voice, 0,scrollPos,scrollTop);
						}
					});

		} else if (2 == type) { // 接受我的语音
			voiceHolder.voiceStatus = (ImageView) convertView
					.findViewById(ResourceUtils.getIdByName(context, "id",
							"voiceStatus"));
			voiceHolder.voiceStatus.setVisibility(View.GONE);
			voiceHolder.name.setText(null);
			final String path = message.getAnswer().getMsg();
			//msg/20150910/6d272e3402764e389dfe958f2ae2284b.wav
		    final	String contentPath = path.substring(path.indexOf("msg")+4, path.length());
			LogUtils.i("获取我的语音消息：msgContentContent:" + path);
			if (path != null && path.length() > 0) {
				LogUtils.i("---------------------------播放语音----------------------------------");
				File directory = new File(Voice_path + contentPath).getParentFile();
				if (!directory.exists() && !directory.mkdirs()) {
					try {
						directory.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				// 点击播放语音
				voiceHolder.ll_voice_layout
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								LogUtils.i("下载语音动画的path:" + path);
						        final int   scrollPos=  ((SobotChatActivity) context).getScrollPosString();
							    final  int  scrollTop=((SobotChatActivity) context).getScrollTopString();
								// 直接播放本地的音乐
								httpUtils.download(path, Voice_path + contentPath,
										true, new RequestCallBack<File>() {
											@Override
											public void onFailure(
													HttpException arg0,
													String arg1) {
												LogUtils.i(" 收到的语音的操作： fail    "
														+ arg0
														+ "---arg1:"
														+ arg1);
											}

											@Override
											public void onSuccess(
													ResponseInfo<File> arg0) {
												LogUtils.i("下载语音的位置："+ arg0.result);
												voiceHolder.voice
														.setImageResource(ResourceUtils
																.getIdByName(
																		context,
																		"drawable",
																		"voice_to_icon"));
												AnimationDrawable animationDrawable = (AnimationDrawable) voiceHolder.voice
														.getDrawable();
												LogUtils.i("播放 我接受的语音文件的路径："
														+ arg0.result
																.toString());
												animationDrawable.start();
												LogUtils.i("scrollPos:我点击的位置"+scrollPos+"---scrollTop:"+scrollTop);
												playVoice(position,
														arg0.result.toString(),
														animationDrawable,
														voiceHolder.voice, 0,scrollPos,scrollTop);
												// voiceHolder.voice.setImageResource(R.drawable.chatto_voice_playing);
											}
										});
							}
						});
			}
		}
		//

	}

	/**
	 * 
	 * @param voidePath
	 * @param animationDrawable
	 * @param image
	 * @param sendType
	 *            发送类型 0 代表是我发 送语音, 1 代表 对方发送语音
	 */

	public interface CallBack {
		void execute();
	}
   private ImageView lasgImageView;
   private int  lastSendType =-1;
   
	public void playVoice(final int position, String voidePath,
			final AnimationDrawable animationDrawable, final ImageView image,
			final int sendType ,final int scrollPos,final int scrollTop) {
		
		//当点击的不是当前的对象的时候
		if(currentVoiceItem != position ){
		     if(currentVoiceItem!=-1){
				if(AudioTools.getInstance().isPlaying()){
					     AudioTools.stop();// 停止语音的播放
						if (lastSendType == 0) {
							// 我放的语音图片
							lasgImageView.setImageResource(ResourceUtils
									.getIdByName(context, "drawable",
											"pop_voice_send_anime_3"));
						} else if (lastSendType == 1) {
							// 对方发送语音图片
							lasgImageView.setImageResource(ResourceUtils
									.getIdByName(context, "drawable",
											"pop_voice_receive_anime_3"));
						}
				   }
				
				 playVoiceByPath(position, sendType, voidePath, animationDrawable, image); 
				   
				} else if(currentVoiceItem==-1){
		    	  // 第一次播放的时候
		    	  playVoiceByPath(position, sendType, voidePath, animationDrawable, image); 
		      }
			currentVoiceItem = position;
			lasgImageView  =image;
		}else if(currentVoiceItem==position ){
			
			//点击同一个的元素
			if(currentVoiceItem==position  && AudioTools.getInstance().isPlaying()){
				AudioTools.stop();// 停止语音的播放
				if (sendType == 0) {
					// 我放的语音图片
					image.setImageResource(ResourceUtils
							.getIdByName(context, "drawable",
									"pop_voice_send_anime_3"));
				} else if (sendType == 1) {
					// 对方发送语音图片
					image.setImageResource(ResourceUtils
							.getIdByName(context, "drawable",
									"pop_voice_receive_anime_3"));
			   }
			}else{
				playVoiceByPath(position, sendType, voidePath, animationDrawable, image);
			}
			
			// 当第一次点击的时候是播放的动画
			currentVoiceItem = position;
		}
	};
	
	public void playVoiceByPath(final int position ,final int sendType,String voidePath,
			final AnimationDrawable animationDrawable, final ImageView image){
		list.get(position).setVoideIsPlaying(true);
		lastSendType = sendType;
		try {
			AudioTools.getInstance();
			if(AudioTools.getIsPlaying()){
				AudioTools.stop();
			}
			AudioTools.getInstance().setAudioStreamType(
					AudioManager.STREAM_MUSIC);
			
			AudioTools.getInstance().reset();
			// 设置要播放的文件的路径
			AudioTools.getInstance().setDataSource(voidePath);
			// 准备播放
			AudioTools.getInstance().prepare();
			// 开始播放
			// mMediaPlayer.start();
			AudioTools.getInstance().setOnPreparedListener(
					new MediaPlayer.OnPreparedListener() {
						@Override
						public void onPrepared(MediaPlayer mediaPlayer) {
							mediaPlayer.start();
						}
					});

			// 这在播放的动画
			AudioTools.getInstance().setOnCompletionListener(
					new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer arg0) {
							// 停止播放
							list.get(position).setVoideIsPlaying(false);
							AudioTools.getInstance().stop();
							LogUtils.i("----语音播放完毕----");
							animationDrawable.stop();
							if (sendType == 0) {
								// 我放的语音图片
								image.setImageResource(ResourceUtils
										.getIdByName(context, "drawable",
												"pop_voice_send_anime_3"));
							} else if (sendType == 1) {
								// 对方发送语音图片
								image.setImageResource(ResourceUtils
										.getIdByName(context, "drawable",
												"pop_voice_receive_anime_3"));
							}
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.i("音频播放失败");
			if (sendType == 0) {
				// 我放的语音图片
				image.setImageResource(ResourceUtils
						.getIdByName(context, "drawable",
								"pop_voice_send_anime_3"));
			} else if (sendType == 1) {
				// 对方发送语音图片
				image.setImageResource(ResourceUtils
						.getIdByName(context, "drawable",
								"pop_voice_receive_anime_3"));
			}
		} finally {
			
			if (AudioTools.getInstance() != null) {
				if (AudioTools.getInstance().isPlaying()) {
					AudioTools.getInstance().stop();
				}
			}
			list.get(position).setVoideIsPlaying(false);
		}
	}
	
	// 处理图片的消息
	@SuppressLint("NewApi")
	private void handerImageMessage(final ImageMessageHolder imageHolder,
			int position, View convertView, int type) {
		message = list.get(position);
		imageHolder.name = (TextView) convertView.findViewById(ResourceUtils
				.getIdByName(context, "id", "name"));
		imageHolder.msg = (TextView) convertView.findViewById(ResourceUtils
				.getIdByName(context, "id", "msg"));
		imageHolder.msg.setVisibility(View.GONE);
		imageHolder.image = (ImageView) convertView.findViewById(ResourceUtils
				.getIdByName(context, "id", "iv_picture"));
		imageHolder.name.setVisibility(View.GONE);
		imageHolder.image.setVisibility(View.VISIBLE);
		imageHolder.pic_send_status = (ImageView) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"pic_send_status"));
		imageHolder.pic_progress = (ProgressBar) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"pic_progress"));
		// List<String> urlList =getImageSrc(getImageUrl(message.getMsg()));
		// imageHolder.pic_progress = (ProgressBar)
		// convertView.findViewById(R.id.pic_progress);
		if (ZhiChiConstant.result_fail_code == message.getMysendMessageState()) {
			imageHolder.pic_send_status.setVisibility(View.VISIBLE);
			imageHolder.pic_progress.setVisibility(View.GONE);
			imageHolder.image.setAlpha(0.5f);
			// 点击重新发送按钮
			// new RetrySendImageLisenter(message.getId(),message.getMsg())
			imageHolder.pic_send_status
					.setOnClickListener(new RetrySendImageLisenter(message
							.getId(), message.getAnswer().getMsg(),
							imageHolder.pic_send_status, position));
		} else if (ZhiChiConstant.result_success_code == message
				.getMysendMessageState()) {
			imageHolder.pic_send_status.setVisibility(View.GONE);
			imageHolder.pic_progress.setVisibility(View.GONE);
			imageHolder.image.setAlpha(1f);

			// imageHolder.pic_send_status.setText(null);
		} else if (ZhiChiConstant.hander_sendPicIsLoading == message
				.getMysendMessageState()) {
			// imageHolder.pic_send_status.setVisibility(View.VISIBLE);
			imageHolder.pic_progress.setVisibility(View.VISIBLE);
			// imageHolder.pic_progress.setProgress(message.getProgressBar());
			LogUtils.i("进度的百分比的：" + message.getProgressBar());
			// imageHolder.pic_send_status.setText(message.getProgressBar()+"%");
		} else {
			imageHolder.pic_send_status.setVisibility(View.GONE);
		}

		// 加载机械人文本
		if (0 == type) { // 机械人图片消息 显示名字
			try {
				imageHolder.name.setText(message.getSender());
				imageHolder.name.setVisibility(View.VISIBLE);
				bitmapUtils.display(imageHolder.image, message.getAnswer()
						.getMsg());
				imageHolder.image
						.setOnClickListener(new ImageClickLisenter(message
								.getAnswer().getMsg()));
				imageHolder.pic_progress.setVisibility(View.GONE);
				imageHolder.image.setAlpha(1f);
				// 显示图片大小
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (1 == type) { // 我的图片消息 不显示名字
			
			 bitmapUtils
				.display(imageHolder.image,message.getAnswer().getMsg());
				imageHolder.image.setOnClickListener(new ImageClickLisenter(
						message.getAnswer().getMsg()));
				imageHolder.pic_progress.setVisibility(View.GONE);
				imageHolder.image.setAlpha(1f);
				
				/*	try {
				final String path = message.getAnswer().getMsg();
				LogUtils.i("----图片Path:----"+path);
				String contentPath = null;
				if (path != null && path.length() > 0) {
					  contentPath	 = path.substring(path.indexOf("msg")+4, path.length());
					File directory = new File(Voice_path + contentPath).getParentFile();
					if (!directory.exists() && !directory.mkdirs()) {
						try {
							directory.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}*/
				
				
		/*		httpUtils.download(path, Voice_path + contentPath,
						true, new RequestCallBack<File>() {
							@Override
							public void onFailure(
									HttpException arg0,
									String arg1) {
							}
							@Override
							public void onSuccess(
									ResponseInfo<File> arg0) {
								LogUtils.i("下载图片的位置："+ arg0.result);
								int degree = CommonUtils.getBitmapDegree(arg0.result.toString());  
					            if(degree >60 ){
						              BitmapFactory.Options opts=new BitmapFactory.Options();//获取缩略图显示到屏幕上
						              opts.inSampleSize=10;
						              Bitmap cbitmap=BitmapFactory.decodeFile(arg0.result.toString(),opts);
						              *//** 
						               * 把图片旋转为正的方向 
						               *//* 
						              Bitmap newbitmap =CommonUtils.rotaingImageView(degree, cbitmap);  
						              imageHolder.image.setImageBitmap(newbitmap);
						              
						              
						              BitmapFactory.Options opts = new BitmapFactory.Options();  
						              opts.inJustDecodeBounds = true;  
						              BitmapFactory.decodeFile(arg0.result.toString(), opts);  
						                
						              opts.inSampleSize = ImageUtils.computeSampleSize(opts, -1, 128*128);  
						              opts.inJustDecodeBounds = false;  
						              try {  
						               Bitmap bmp =CommonUtils.rotaingImageView(degree, BitmapFactory.decodeFile(arg0.result.toString(), opts)) ;  
						               imageHolder.image.setImageBitmap(bmp);  
						                  } catch (OutOfMemoryError err) {  
						              }  
							    }else {
								    bitmapUtils
									.display(imageHolder.image,arg0.result.toString());
							    }
					            bitmapUtils
								.display(imageHolder.image,arg0.result.toString());
								imageHolder.image.setOnClickListener(new ImageClickLisenter(
										arg0.result.toString()));
								imageHolder.pic_progress.setVisibility(View.GONE);
								imageHolder.image.setAlpha(1f);
							}
						});
				
			} catch (Exception e) {
				e.printStackTrace();
			}*/
		} else if (2 == type) { // 处理我发送的图片
			
		 /*  int degree = CommonUtils.getBitmapDegree(message.getAnswer().getMsg());  
            if(degree>0){
	              BitmapFactory.Options opts=new BitmapFactory.Options();//获取缩略图显示到屏幕上
	              opts.inSampleSize=10;
	              Bitmap cbitmap=BitmapFactory.decodeFile(message.getAnswer().getMsg(),opts);
	              *//** 
	               * 把图片旋转为正的方向 
	               *//* 
	              Bitmap newbitmap =CommonUtils.rotaingImageView(degree, cbitmap);  
	              imageHolder.image.setImageBitmap(newbitmap);
		    }else {
			    bitmapUtils
				.display(imageHolder.image, message.getAnswer().getMsg());
		    }*/
			
            bitmapUtils
			.display(imageHolder.image, message.getAnswer().getMsg());
			imageHolder.image.setOnClickListener(new ImageClickLisenter(message
					.getAnswer().getMsg()));
			
		}
	}

	
	
	// 处理提醒的消息
	private void handerRemindMessage(final TextMessageHolder textHolder,
			final int position, View convertView) {
		message = list.get(position);
		textHolder.center_Remind_Info = (TextView) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"center_Remind_note"));
		textHolder.msg = (TextView) convertView.findViewById(ResourceUtils
				.getIdByName(context, "id", "msg"));
		textHolder.name = (TextView) convertView.findViewById(ResourceUtils
				.getIdByName(context, "id", "name"));
		textHolder.ll_content = (LinearLayout) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"ll_content"));
		textHolder.my_msg = (LinearLayout) convertView
				.findViewById(ResourceUtils
						.getIdByName(context, "id", "my_msg"));

		textHolder.ll_content.setVisibility(View.GONE);
		textHolder.msg.setVisibility(View.GONE);
		textHolder.name.setVisibility(View.GONE);
		textHolder.my_msg.setVisibility(View.GONE);
		textHolder.center_Remind_Info.setVisibility(View.VISIBLE);
		// textHolder.center_Remind_Info.setText(Html.fromHtml(message.getMsg()));
		// 可以重新连接的 是属于最后的一个才可以进行连接

		if (message.getReconnectCustom() && position == (list.size() - 1)) {// 可以点击重连的状态

			textHolder.center_Remind_Info.setText(Html.fromHtml(message
					.getAnswer().getMsg() + getRedReconnectString(true)));
			textHolder.center_Remind_Info.setEnabled(true);
			final  String msg1 = message.getAnswer().getMsg();
			textHolder.center_Remind_Info
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// 重新点击连接人工
							
							textHolder.center_Remind_Info.setText(Html.fromHtml(msg1 +"<font>重新接入</font>"));
							textHolder.center_Remind_Info.setClickable(false);
							list.get(position).setReconnectCustom(false);
							Intent intent = new Intent();
							intent.setAction(SobotChatActivity.reConnectCustom);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("msgContent",
									"{\"reconnectCustom\":" + true + "}");
							context.sendBroadcast(intent);
						}
					});

		} else if (message.getReconnectCustom() && position < (list.size() - 1)) {
			// 不可以进行连接
			textHolder.center_Remind_Info.setText(""
					+ Html.fromHtml(message.getAnswer().getMsg()
							+ getRedReconnectString(false)));
			// 设置不可以进行点击
			textHolder.center_Remind_Info.setEnabled(false);
		} else {
			textHolder.center_Remind_Info.setText(Html.fromHtml(message
					.getAnswer().getMsg()));
			textHolder.center_Remind_Info.setEnabled(false);
		}
	}

	// 重新发送的字段的拼接
	private String getRedReconnectString(boolean isRedFont) {
		if (isRedFont) { // 显示的是红色可以进行点击
			return "<font  color=\"red\">重新接入</font>";
		} else {
			return "";
		}
	}

	/**
	 * 富文本的消息内容
	 * 
	 * @param picansTextMessage
	 * @param position
	 * @param convertView
	 * @param type
	 */
	public void handerPicAndTextMessage(
			PicAndTextMessageHolder picAndTextHolder, int position,
			View convertView, int type) {
		message = list.get(position);

		// 标题内容
		picAndTextHolder.text_answer = (TextView) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"picandtext_answer"));

		// 中间大图片
		picAndTextHolder.picImage = (ImageView) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"picandtext_image"));
		// 回复的内容
		picAndTextHolder.stripe = (TextView) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"picandtext_stricp"));

		// 问答语的问题list列表
		picAndTextHolder.ll_answerList = (LinearLayout) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"picandtext_answerlist"));
		// 进行不同的
	}

	/**
	 * 机械人 文本
	 * 
	 * @param textHolder
	 * @param position
	 * @param convertView
	 * @param type
	 *            ： 0 :robot 1: 我 2 ： 机械人的回复
	 */
	public void handerTextMessage(final TextMessageHolder textHolder,
			final int position, View convertView, int type) {
		// message.getMsgType(
		message = new ZhiChiMessageBase();
		message = list.get(position);

		textHolder.name = (TextView) convertView.findViewById(ResourceUtils
				.getIdByName(context, "id", "name"));
		textHolder.msg = (TextView) convertView.findViewById(ResourceUtils
				.getIdByName(context, "id", "msg"));

		// 纯图片的信息
		textHolder.simple_picture = (ImageView) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"simple_picture"));

		// 富文本的大图片
		textHolder.bigPicImage = (ImageView) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"bigPicImage"));
		// 阅读全文
		textHolder.rendAllText = (TextView) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"rendAllText"));

		textHolder.ll_voice_layout = (LinearLayout) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"ll_voice_layout"));
		textHolder.audio_picture = (ImageView) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"audio_picture"));

		textHolder.voiceTimeLong = (TextView) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"voiceTimeLong"));

		textHolder.stripe = (TextView) convertView.findViewById(ResourceUtils
				.getIdByName(context, "id", "stripe"));
		textHolder.answersList = (LinearLayout) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"answersList"));

		textHolder.msgProgressBar = (ProgressBar) convertView
				.findViewById(ResourceUtils.getIdByName(context, "id",
						"msgProgressBar")); // 重新发送的进度条信息

		// {"answer":"<p>我不好啊，你好吗</p>","sugguestions":null,"answerType":1,"stripe":""}
		// 加载机械人文本
		if (1 != type && 2 != type) { // 机械人和客服的消息文本
			// 分析是单纯的文本还是问答语句的内容的信息；
			// 单纯文本的消息
			// textHolder.webView_msg = (WebView)
			// convertView.findViewById(R.id.webView_msg);
			textHolder.name.setText(message.getSenderName());
			textHolder.msg.setText(null);
			// if(ZhiChiConstant.message_type_custonmType==Integer.parseInt(message.getSenderType())){
			if (message.getAnswer().getMsg() != null
					&& message.getAnswer().getMsg().toString().length() > 0) {// 纯文本消息
				try {
					// 处理带有图文并茂的文本消息
					textHolder.msg.setVisibility(View.VISIBLE);
					LogUtils.i(" msg ： " + message.getAnswer().getMsg());
//					textHolder.msg.setText(Html.fromHtml(message
//							.getAnswer().getMsg()));
					htmlTools = new HtmlTools();
					htmlTools.handTextWithPic(textHolder.msg, message
							.getAnswer().getMsg());
					textHolder.msg.setMovementMethod(LinkMovementMethod
							.getInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				textHolder.msg.setText(null);
				textHolder.msg.setVisibility(View.GONE);
			}
		}

		if (type == 0 || type == 2) {
			textHolder.stripe.setText(null);
			textHolder.name.setText(message.getSenderName());
			// 更具消息类型进行对布局的优化
			if (message.getAnswer() != null) {
				// 获取的消息类型
				if (0 == Integer.parseInt(message.getAnswer().getMsgType())) {// 文本
					if(message.getAnswer().getMsg()!=null){
						textHolder.msg.setVisibility(View.VISIBLE);
						htmlTools = new HtmlTools();
						htmlTools.handTextWithPic(textHolder.msg, message
								.getAnswer().getMsg());
						textHolder.msg.setMovementMethod(LinkMovementMethod
								.getInstance());
//						textHolder.msg.setText(Html.fromHtml(message.getAnswer().getMsg()));
					} else {
						textHolder.msg.setVisibility(View.GONE);
						message.getAnswer().setMsg(null);
					}
				} else if (1 == Integer.parseInt(message.getAnswer()
						.getMsgType())) {// 图片
					if (message.getAnswer().getMsg() != null) {
						textHolder.simple_picture.setVisibility(View.VISIBLE);
						LogUtils.i("输入的action:filePath:"+ message.getAnswer().getMsg());
						bitmapUtils.display(textHolder.simple_picture, message.getAnswer().getMsg());
						textHolder.simple_picture
								.setOnClickListener(new ImageClickLisenter(
										message.getAnswer().getMsg()));
					} else {
						textHolder.simple_picture.setVisibility(View.GONE);
						message.getAnswer().setMsg(null);
					}
				} else if (2 == Integer.parseInt(message.getAnswer()
						.getMsgType())) {// 音频
					if (message.getAnswer().getMsg() != null) {
						final String audioPath = message.getAnswer().getMsg();
						textHolder.ll_voice_layout.setVisibility(View.VISIBLE);
						textHolder.audio_picture.setVisibility(View.VISIBLE);
						textHolder.voiceTimeLong.setVisibility(View.VISIBLE);
						textHolder.voiceTimeLong.setText(message.getAnswer()
								.getDuration());
						if (currentVoiceItem > 0
								&& currentVoiceItem != position) {
							textHolder.audio_picture
									.setImageResource(ResourceUtils
											.getIdByName(context, "drawable",
													"pop_voice_receive_anime_3"));
						}
						
						// 获取当前的位置
				    
						// 点击播放音频
						textHolder.ll_voice_layout
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View arg0) {
									    int   scrollPos=  ((SobotChatActivity) context).getScrollPosString();
									    int  scrollTop=((SobotChatActivity) context).getScrollTopString();
											
										//获取当前的listView的数据的信息
										textHolder.audio_picture.setImageResource(ResourceUtils
												.getIdByName(context,
														"drawable",
														"voice_from_icon"));
										// R.drawable.voice_to_icon
										AnimationDrawable animationDrawable = (AnimationDrawable) textHolder.audio_picture
												.getDrawable();
										animationDrawable.start();
										playVoice(position, audioPath,
												animationDrawable,
												textHolder.audio_picture, 1,scrollPos,scrollTop);
									}
								});
					} else {
						message.getAnswer().setMsg(null);
						textHolder.ll_voice_layout.setVisibility(View.GONE);
					}

				} else if (3 == Integer.parseInt(message.getAnswer()
						.getMsgType())) {
					if (message.getAnswer().getMsg() != null) {
						textHolder.msg.setVisibility(View.VISIBLE);
						// textHolder.msg.setText(text)；
						htmlTools.handTextWithPic(textHolder.msg, message
								.getAnswer().getMsg());
						textHolder.msg.setMovementMethod(LinkMovementMethod
								.getInstance());
					} else {
						message.getAnswer().setMsg(null);
						textHolder.msg.setVisibility(View.GONE);
					}
				} else if (4 == Integer.parseInt(message.getAnswer()
						.getMsgType())) {// 富文本中有图片
					// 设置富文本的文字的消息
					if (message.getAnswer().getMsg() != null) {
						textHolder.msg.setVisibility(View.VISIBLE);
						htmlTools = new HtmlTools();
						htmlTools.handTextWithPic(textHolder.msg, message
								.getAnswer().getMsg());
						textHolder.msg.setMovementMethod(LinkMovementMethod
								.getInstance());

						if (message.getAnswer().getRichpricurl() != null) {
							textHolder.bigPicImage.setVisibility(View.VISIBLE);
							bitmapUtils.display(textHolder.bigPicImage, message
									.getAnswer().getRichpricurl());
							// 点击大图 查看大图的内容
							textHolder.bigPicImage
									.setOnClickListener(new ImageClickLisenter(
											message.getAnswer()
													.getRichpricurl()));
						} else {
							textHolder.bigPicImage.setVisibility(View.GONE);
						}

					} else {
						message.getAnswer().setMsg(null);
						textHolder.msg.setVisibility(View.GONE);
					}
				} else if (5 == Integer.parseInt(message.getAnswer()
						.getMsgType())) {// 富文本中纯文字
					if (message.getAnswer().getMsg() != null) {
						textHolder.msg.setVisibility(View.VISIBLE);
						textHolder.msg.setText(Html.fromHtml(message.getAnswer().getMsg()));
						textHolder.msg.setMovementMethod(LinkMovementMethod.getInstance());
						/*
						 * htmlTools.handTextWithPic(textHolder.msg,
						 * message.getAnswer().getMsg());
						 * textHolder.msg.setMovementMethod(LinkMovementMethod
						 * .getInstance());
						 */
					} else {
						textHolder.msg.setVisibility(View.GONE);
						message.getAnswer().setMsg(null);
					}
				} else if (6 == Integer.parseInt(message.getAnswer()
						.getMsgType())) {// 富文本中有视频
					// 暂时不解析
				}

				if (message.getAnswer().getRichmoreurl() != null
						&& message.getAnswer().getRichmoreurl().length() > 0) {
					textHolder.rendAllText.setVisibility(View.VISIBLE);
					textHolder.rendAllText.setTextColor(Color
							.parseColor(SharedPreferencesUtil.getStringData(
									context, "robot_current_themeColor",
									"#09aeb0")));
					textHolder.rendAllText
							.setOnClickListener(new ReadAllTextLisenter(
									position));
				} else {
					textHolder.rendAllText.setVisibility(View.GONE);
				}
				// 隐藏不改显示的内容
				hideViewByType(textHolder,
						Integer.parseInt(message.getAnswer().getMsgType()));
			}
			if (message.getRictype() != null
					&& message.getRictype().length() > 0) {
				if (0 == Integer.parseInt(message.getRictype())) {// 代表无图片的格式

					textHolder.bigPicImage.setVisibility(View.GONE);
					textHolder.rendAllText.setVisibility(View.GONE);
				} else if (1 == Integer.parseInt(message.getRictype())) {
					textHolder.bigPicImage.setVisibility(View.VISIBLE);
					textHolder.rendAllText.setVisibility(View.VISIBLE);
					bitmapUtils.display(textHolder.bigPicImage,
							message.getPicurl());
					textHolder.rendAllText.setTextColor(Color
							.parseColor(SharedPreferencesUtil.getStringData(
									context, "robot_current_themeColor",
									"#09aeb0")));
					textHolder.rendAllText.setVisibility(View.VISIBLE);
					textHolder.rendAllText
							.setOnClickListener(new ReadAllTextLisenter(
									position));
				}
			}
			// 回复语的答复
			String stripeContent = null;

			if (message.getStripe() != null) {
				stripeContent = Html.fromHtml(message.getStripe()).toString()
						.trim();
			}
			if (stripeContent != null && stripeContent.length() > 0) {
				LogUtils.i(" stripe ： " + message.getStripe());
				// 设置提醒的内容
				textHolder.stripe.setVisibility(View.VISIBLE);
				textHolder.stripe.setText(stripeContent);
			} else {
				textHolder.stripe.setText(null);
				textHolder.stripe.setVisibility(View.GONE);
			}
			textHolder.answersList.setVisibility(View.GONE);
			if (message.getSugguestions() != null
					&& message.getSugguestions().length > 0) {
				String[] answerStringList = message.getSugguestions();
				LogUtils.i(" answerStringList ： " + answerStringList.toString());
				textHolder.answersList.setVisibility(View.VISIBLE);
				textHolder.answersList.removeAllViews();
				for (int i = 0; i < answerStringList.length; i++) {
					TextView answer = new TextView(context);
					answer.setTextSize(16);
					answer.setLineSpacing(2f, 1f);
					int currentItem = i + 1;
					if(message.getSugguestionsFontColor() == 1){
						answer.setTextColor(Color.parseColor("#000000"));
					}else{
						// 设置字体的颜色的样式
						answer.setTextColor(Color.parseColor(SharedPreferencesUtil
								.getStringData(context, "robot_current_themeColor",
										"#09aeb0")));
						answer.setOnClickListener(new AnsWerClickLisenter(null,
								currentItem + "", null));
					}
					answer.setText(currentItem + "、" + answerStringList[i]);
					textHolder.answersList.addView(answer);
				}
			}

		} else if (1 == type) { // 我的文本消息
			try {
				// 消息的状态
				textHolder.frameLayout = (FrameLayout) convertView
						.findViewById(ResourceUtils.getIdByName(context, "id",
								"frame_layout"));
				textHolder.msgStatus = (ImageView) convertView
						.findViewById(ResourceUtils.getIdByName(context, "id",
								"msgStatus"));
				textHolder.msgStatus.setClickable(true);
				textHolder.pic_send_status = (TextView) convertView
						.findViewById(ResourceUtils.getIdByName(context, "id",
								"pic_send_status"));

				LogUtils.i("textFailTimes:" + message.getTextFailTimes());
				if (message.getSendSuccessState() == 1) {// 成功的状态
					textHolder.msgStatus.setVisibility(View.GONE);
					textHolder.frameLayout.setVisibility(View.GONE);
					textHolder.pic_send_status.setVisibility(View.GONE);
					textHolder.msgProgressBar.setVisibility(View.GONE);
				}

				if (message.getSendSuccessState() == 0
						&& message.getTextFailTimes() == 1) {

					textHolder.frameLayout.setVisibility(View.VISIBLE);
					textHolder.msgStatus.setVisibility(View.VISIBLE);
					textHolder.msgProgressBar.setVisibility(View.GONE);
					textHolder.msgStatus
							.setOnClickListener(new ReSendTextLisenter(message
									.getId(), message.getAnswer().getMsg(),
									position, false,textHolder.msgStatus));

				} else if (message.getSendSuccessState() == 0
						&& message.getTextFailTimes() % 2 == 0) {
					textHolder.frameLayout.setVisibility(View.VISIBLE);
					textHolder.msgProgressBar.setVisibility(View.VISIBLE);
					textHolder.msgStatus.setVisibility(View.GONE);
					if (timer == null) {
						timer = new Timer();
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								sendTextBrocast(context, list.get(position)
										.getId(), list.get(position)
										.getAnswer().getMsg(), true);
							}
						}, 7000);
					}

				} else if (message.getSendSuccessState() == 0
						&& message.getTextFailTimes() % 2 == 1) {
					textHolder.frameLayout.setVisibility(View.VISIBLE);
					textHolder.msgStatus.setVisibility(View.VISIBLE);
					textHolder.msgProgressBar.setVisibility(View.GONE);
					textHolder.msgStatus
							.setOnClickListener(new ReSendTextLisenter(message
									.getId(), message.getAnswer().getMsg(),
									position, false,textHolder.msgStatus));
				} else if (message.getSendSuccessState() == 2) {
					// send processing ;
					textHolder.frameLayout.setVisibility(View.VISIBLE);
					textHolder.msgProgressBar.setVisibility(View.VISIBLE);
					textHolder.msgStatus.setVisibility(View.GONE);
				}

				if (message.getAnswer().getMsg() != null
						&& message.getAnswer().getMsg().length() > 0) {
					textHolder.msg.setVisibility(View.VISIBLE);
					textHolder.msg.setText(message.getAnswer().getMsg());
				}
				textHolder.name.setVisibility(View.GONE); // 我的名字不显示
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void hideViewByType(TextMessageHolder textHolder, int type) {

		/*
		 * Int 消息类型0文本 1图片 2音频 4 富文本中有图片 5 富文本中纯文字 6 富文本中有视频
		 */
		if (0 == type) { // 纯文本
			textHolder.ll_voice_layout.setVisibility(View.GONE);
			textHolder.bigPicImage.setVisibility(View.GONE);
			textHolder.simple_picture.setVisibility(View.GONE);
			textHolder.audio_picture.setVisibility(View.GONE);

			// 阅读全文
			textHolder.rendAllText.setVisibility(View.GONE);
			textHolder.voiceTimeLong.setVisibility(View.GONE);
		} else if (1 == type) {// 图片
			textHolder.msg.setVisibility(View.GONE);
			textHolder.ll_voice_layout.setVisibility(View.GONE);
			textHolder.bigPicImage.setVisibility(View.GONE);
			textHolder.audio_picture.setVisibility(View.GONE);
			textHolder.rendAllText.setVisibility(View.GONE);
			textHolder.voiceTimeLong.setVisibility(View.GONE);
		} else if (2 == type) {// 音频
			textHolder.msg.setVisibility(View.GONE);
			textHolder.bigPicImage.setVisibility(View.GONE);
			textHolder.simple_picture.setVisibility(View.GONE);
		} else if (3 == type) {
			textHolder.ll_voice_layout.setVisibility(View.GONE);
			textHolder.bigPicImage.setVisibility(View.GONE);
			textHolder.simple_picture.setVisibility(View.GONE);
			textHolder.audio_picture.setVisibility(View.GONE);
			textHolder.rendAllText.setVisibility(View.GONE);
			textHolder.voiceTimeLong.setVisibility(View.GONE);

		} else if (4 == type) {// 富文本中有图片
			textHolder.simple_picture.setVisibility(View.GONE);
			textHolder.audio_picture.setVisibility(View.GONE);
			textHolder.voiceTimeLong.setVisibility(View.GONE);

		} else if (5 == type) {// 富文本中纯文字
			textHolder.bigPicImage.setVisibility(View.GONE);
			textHolder.audio_picture.setVisibility(View.GONE);
			textHolder.simple_picture.setVisibility(View.GONE);
			textHolder.voiceTimeLong.setVisibility(View.GONE);
		} else if (6 == type) {// 富文本中有视频

		}

	}

	// 获取不同界面的布局
	@Override
	public int getViewTypeCount() {
		/**
		 * 对方 我 文本 文本 图文 图片 图片
		 */
		return viewType;
	}

	@Override
	public int getItemViewType(int position) {
//		System.out.println("getItemViewByType:" + position);
		// 获取消息的类型
		ZhiChiMessageBase message = list.get(position);

		/**
		 * 根据消息的类型 转回不同的布局的信息
		 */
		if ("0".equalsIgnoreCase(message.getSenderType())
				|| "1".equalsIgnoreCase(message.getSenderType())
				|| "2".equalsIgnoreCase(message.getSenderType())) { // 平台传过来的消息
			/*
			 * String msgContentType = getMsgContentClass(message.getMsg() !=
			 * null ? message .getMsg() : "");
			 */
			if (message.getAnswer() != null) {
				// 1
				if (ZhiChiConstant.message_type_pic == Integer.parseInt(message
						.getAnswer().getMsgType())) {
					// 文本中含有图片信息
					// 机械人消息 布局
					if (ZhiChiConstant.message_type_robot_text == Integer
							.parseInt(message.getSenderType())
							|| ZhiChiConstant.message_type_custonmType == Integer
									.parseInt(message.getSenderType())) {

						// return ZhiChiConstant.message_type_robot_image;
						return ZhiChiConstant.message_type_robot_reply;
					} else if (ZhiChiConstant.message_type_wo_text == Integer
							.parseInt(message.getSenderType())) { // 我的消息布局

						return ZhiChiConstant.message_type_myhistory_image;
					}
				} else if (ZhiChiConstant.message_type_voice == Integer // 2
						.parseInt(message.getAnswer().getMsgType())) {
					// 语接口
					if (ZhiChiConstant.message_type_robot_text == Integer
							.parseInt(message.getSenderType())
							|| ZhiChiConstant.message_type_custonmType == Integer
									.parseInt(message.getSenderType())) {
						// return
						// ZhiChiConstant.message_type_receive_robot_voice;
						return ZhiChiConstant.message_type_robot_reply;
					} else {
						// 我的消息布局
						LogUtils.i(" ------收到我接受的语音的语音------message_type_receive_my_voice----");
						return ZhiChiConstant.message_type_receive_my_voice;
					}
					// 3
				} else if (ZhiChiConstant.message_type_emoji == Integer
						.parseInt(message.getAnswer().getMsgType())) {
					// 语接口
					if (ZhiChiConstant.message_type_robot_text == Integer
							.parseInt(message.getSenderType())
							|| ZhiChiConstant.message_type_custonmType == Integer
									.parseInt(message.getSenderType())) {
						return ZhiChiConstant.message_type_robot_reply;
					}

				} else if (ZhiChiConstant.message_type_textAndPic == Integer
						.parseInt(message.getAnswer().getMsgType())) {

					if (ZhiChiConstant.message_type_robot_text == Integer
							.parseInt(message.getSenderType())
							|| ZhiChiConstant.message_type_custonmType == Integer
									.parseInt(message.getSenderType())) {
						return ZhiChiConstant.message_type_robot_reply;
					}
				} else if (ZhiChiConstant.message_type_textAndText == Integer
						.parseInt(message.getAnswer().getMsgType())) {
					if (ZhiChiConstant.message_type_robot_text == Integer
							.parseInt(message.getSenderType())
							|| ZhiChiConstant.message_type_custonmType == Integer
									.parseInt(message.getSenderType())) {
						return ZhiChiConstant.message_type_robot_reply;
					}
					// 0
				} else if (ZhiChiConstant.message_type_text == Integer
						.parseInt(message.getAnswer().getMsgType())
						|| ZhiChiConstant.message_type_textAndPic == Integer
								.parseInt(message.getAnswer().getMsgType())) {
					if (ZhiChiConstant.message_type_robot_text == Integer
							.parseInt(message.getSenderType())
							|| ZhiChiConstant.message_type_custonmType == Integer
									.parseInt(message.getSenderType())) {
						// return ZhiChiConstant.message_type_robot_text;

						return ZhiChiConstant.message_type_robot_reply;
					} else if (ZhiChiConstant.message_type_wo_text == Integer
							.parseInt(message.getSenderType())) { // 我的消息布局
						return ZhiChiConstant.message_type_wo_text;
					}
				}

			}
		} else if (ZhiChiConstant.message_type_remide_info == Integer
				.parseInt(message.getSenderType())) { // 自己发送的消息
			// 中间提醒的消息
			return ZhiChiConstant.message_type_remide_info;
		} else if (ZhiChiConstant.message_type_wo_sendImage == Integer
				.parseInt(message.getSenderType())) {
			// 与我的图片消息
			return ZhiChiConstant.message_type_wo_sendImage;
		} else if (ZhiChiConstant.message_type_send_voice == Integer
				.parseInt(message.getSenderType())) {
			// 发送语音消息
			return ZhiChiConstant.message_type_send_voice;

			// 机械人的回复
		} else if (ZhiChiConstant.message_type_robot_reply == Integer
				.parseInt(message.getSenderType())) {
			return ZhiChiConstant.message_type_robot_reply;

		}
		return 0;
	}
	

	// 解析音频格式
	public String getVoiceHref(String msgContent) {
		Pattern pattern2 = Pattern.compile("href=\"(.+?)\"");
		Matcher matcher2 = pattern2.matcher(msgContent);
		if (matcher2.find()) {
			return matcher2.group(1);
		}
		return null;
	}

	// 解析消息 获取class 类型
	public String getMsgContentClass(String msgContent) {
		Pattern pattern3 = Pattern.compile("class=\"(.+?)\"");
		Matcher matcher3 = pattern3.matcher(msgContent);
		if (matcher3.find()) {
			return (matcher3.group(1));
		}
		return null;
	}

	// 解析图片 <img src="" >
	public List<String> getImageUrl(String msgContent) {
		Matcher matcher = Pattern.compile(IMGURL_REG).matcher(msgContent);
		List<String> listImgUrl = new ArrayList<String>();
		while (matcher.find()) {
			listImgUrl.add(matcher.group());
		}
		return listImgUrl;
	}

	// 获取图片的URL
	public List<String> getImageSrc(List<String> listImageUrl) {
		List<String> listImgSrc = new ArrayList<String>();
		for (String image : listImageUrl) {
			Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(image);
			while (matcher.find()) {
				listImgSrc.add(matcher.group().substring(0,
						matcher.group().length() - 1));
			}
		}
		return listImgSrc;
	}

	// 消息基类
	private static class MessageHolderBase {

		TextView center_Remind_Info; // 中间提醒消息
		TextView name; // 用户姓名

	}

	private static class TextMessageHolder extends MessageHolderBase {

		TextView pic_send_status;
		TextView msg; // 聊天的消息内容
		ImageView msgStatus;// 消息发送的状态
		LinearLayout ll_content;
		LinearLayout ll_voice_layout;
		LinearLayout answersList;
		TextView voiceTimeLong;
		LinearLayout my_msg;
		TextView stripe;
		// 答案
		ImageView bigPicImage; // 大的图片的展示
		TextView rendAllText; // 阅读全文
		FrameLayout frameLayout;
		ImageView simple_picture;// 单图片
		ImageView audio_picture;

		ProgressBar msgProgressBar; // 重新发送的进度条的信信息；

	}

	// 图片消息
	private static class ImageMessageHolder extends TextMessageHolder {
		ImageView image;
		ImageView pic_send_status;
		ProgressBar pic_progress;
	}

	// 语音消息
	private static class VoiceMessageHolder extends MessageHolderBase {
		TextView voiceTimeLong;
		ImageView voice;
		LinearLayout ll_voice_layout;
		ImageView voiceStatus;
		ProgressBar msgProgressBar;
	}

	// 图文并茂的消息布局实体
	private static class PicAndTextMessageHolder extends MessageHolderBase {
		@SuppressWarnings("unused")
		TextView text_answer;// 标题内容
		@SuppressWarnings("unused")
		ImageView picImage;// 显示的大图片
		@SuppressWarnings("unused")
		TextView stripe; // 回复
		@SuppressWarnings("unused")
		LinearLayout ll_answerList; // 问答语的内容
	}

	// 语音的重新发送
	public class RetrySendVoiceLisenter implements OnClickListener {
		private String voicePath;
		private String id;
		private String duration;
		private ImageView img;
		private int position;

		public RetrySendVoiceLisenter(String id, String voicePath,
				String duration, ImageView image, int position) {
			super();
			this.voicePath = voicePath;
			this.id = id;
			this.duration = duration;
			this.img = image;
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {

			if (img != null) {
				img.setVisibility(View.GONE);
				img.setClickable(false);
			}
			list.get(position).setSendSuccessState(1);
			// list.get(position).setFirstSend(false);
			// list.get(position).setTextFistSend(0);
			notifyDataSetChanged();
			Intent intent = new Intent();
			intent.setAction(SobotChatActivity.voiceReUploadBrocast);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("msgContent", "{\"context\":\"" + voicePath
					+ "\",\"id\":\"" + id + "\"" + " ,\"duration\":\""
					+ duration + "\"}");
			context.sendBroadcast(intent);
		}

	}

	// 图片的重新发送监听
	public class RetrySendImageLisenter implements OnClickListener {
		private String id;
		private String imageUrl;
		private ImageView img;
		private int position;

		public RetrySendImageLisenter(String id, String imageUrl,
				ImageView image, int position) {
			super();
			this.id = id;
			this.imageUrl = imageUrl;
			this.img = image;
			this.position = position;
		}

		@Override
		public void onClick(View view) {

			if (img != null) {
				img.setVisibility(View.GONE);
			}
			list.get(position).setMysendMessageState(
					ZhiChiConstant.hander_sendPicIsLoading);
			notifyDataSetChanged();
			// 获取图片的地址url
			// 上传url
			// 采用广播的机制进行重的图片刚发送；
			LogUtils.i(" 点击重新上传的接口： " + "{\"msg\":\"" + imageUrl
					+ "\",\"id\":\"" + id + "\"}");
			Intent intent = new Intent();
			intent.setAction(SobotChatActivity.picReUploadBrocast);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("msgContent", "{\"context\":\"" + imageUrl
					+ "\",\"id\":\"" + id + "\"}");
			context.sendBroadcast(intent);

		}
	}

	// 查看阅读全文的监听
	public class ReadAllTextLisenter implements OnClickListener {
		private int item;

		public ReadAllTextLisenter(int item) {
			super();
			this.item = item;
		}

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			String urlContent = list.get(item).getAnswer().getRichmoreurl();
			// 打开浏览器
			if (!urlContent.startsWith("http://")
					&& !urlContent.startsWith("https://")) {
				urlContent = "http://" + urlContent;
			}
			Uri content = Uri.parse(urlContent);
			intent.setData(content);

			context.startActivity(intent);
		}
	}

	// 图片的事件监听
	public class ImageClickLisenter implements OnClickListener {
		private String imageUrl;

		public ImageClickLisenter(String imageUrl) {
			super();
			this.imageUrl = imageUrl;
		}

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(context, PhotoActivity.class);
			intent.putExtra("imageUrL", imageUrl);
			context.startActivity(intent);
		}

	}

	// 问题的回答监听
	public class AnsWerClickLisenter implements OnClickListener {

		private String msgContext;
		private String id;
		private ImageView img;

		public AnsWerClickLisenter(String id, String msgContext, ImageView image) {
			super();
			this.msgContext = msgContext;
			this.id = id;
			this.img = image;
		}

		@Override
		public void onClick(View arg0) {
			if (img != null) {
				img.setVisibility(View.GONE);
			}
			// 广播消息
			Intent intent = new Intent();
			intent.setAction(SobotChatActivity.sendMyMessageBrocast);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (id == null) {
				intent.putExtra("msgContent", "{\"context\":\"" + msgContext
						+ "\"}");
			} else {
				intent.putExtra("msgContent", "{\"context\":\"" + msgContext
						+ "\",\"id\":\"" + id + "\"}");
			}
			context.sendBroadcast(intent);
		}
	}

	public class ReSendTextLisenter implements OnClickListener {

		private String id;
		private String msgContext;
        private ImageView msgStatus;
		public ReSendTextLisenter(String id, String msgContext, int position,
				boolean isDeplay,ImageView msgStatus) {
			super();
			this.id = id;
			this.msgContext = msgContext;
			this.msgStatus = msgStatus;
		}

		@Override
		public void onClick(View arg0) {
			if(msgStatus!=null){
				msgStatus.setClickable(false);
			}
			sendTextBrocast(context, id, msgContext, false);
		}

	}

	public void sendTextBrocast(Context context, String id, String msgContext,
			boolean isTimeNull) {
		if (isTimeNull) {
			timer = null;
		}
		Intent intent = new Intent();
		intent.setAction(SobotChatActivity.ResendTextBrocast);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("msgContent", "{\"context\":\"" + msgContext
				+ "\",\"id\":\"" + id + "\"}");
		context.sendBroadcast(intent);

	}
	

}