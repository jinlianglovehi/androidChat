package com.sobot.chat.application;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener;
import io.rong.imlib.RongIMClient.OnReceiveMessageListener;
import io.rong.imlib.RongIMClient.OnReceivePushMessageListener;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.notification.PushNotificationMessage;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.util.LogUtils;
import com.sobot.chat.activity.SobotChatActivity;
import com.sobot.chat.model.ZhiChiMessage;

public class RongCloudEvent implements OnReceiveMessageListener,
		ConnectionStatusListener,OnReceivePushMessageListener {
	private static final String TAG = RongCloudEvent.class.getSimpleName();

	
	private static RongCloudEvent mRongCloudInstance;

	private Context mContext;

	public static RongCloudEvent getInstance() {
		return mRongCloudInstance;
	}

	public static void init(Context context) {

		if (mRongCloudInstance == null) {

			synchronized (RongCloudEvent.class) {

				if (mRongCloudInstance == null) {
					mRongCloudInstance = new RongCloudEvent(context);
				}
			}
		}
	}

	private RongCloudEvent(Context context) {
		mContext = context;
		initDefaultListener();
	}

	private void initDefaultListener() {
		RongIMClient.setOnReceivePushMessageListener(this);
	}
	public void setOtherListener() {
		RongIMClient.setOnReceiveMessageListener(this);
		RongIMClient.setConnectionStatusListener(this);
	}

	@Override
	public boolean onReceived(Message message, int left) {
		// 接受消息的内容
	  //  mContext.unregisterReceiver(arg0);
		MessageContent messageContent = message.getContent();
		@SuppressWarnings("unused")
		ZhiChiMessage zhichiMessage = null;
		if (messageContent instanceof TextMessage) {// �ı���Ϣ
			TextMessage textMessage = (TextMessage) messageContent;

			LogUtils.i("onReceived-TextMessage-content:"
					+ textMessage.getContent());
			LogUtils.i("onReceived-TextMessage:" + textMessage.getContent());
			// 封装好Message实体对象
			LogUtils.i(" 接受  推送来的文本消息 ：  " + textMessage.getContent());
			Intent intent = new Intent();
			intent.setAction(SobotChatActivity.brocastContantant);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("msgContent", textMessage.getContent());
			mContext.sendBroadcast(intent);

		} else if (messageContent instanceof ImageMessage) {// ͼƬ��Ϣ
			ImageMessage imageMessage = (ImageMessage) messageContent;
			Log.d(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
		} else if (messageContent instanceof VoiceMessage) {// ������Ϣ
			VoiceMessage voiceMessage = (VoiceMessage) messageContent;
			Log.d(TAG, "onReceived-voiceMessage:"
					+ voiceMessage.getUri().toString());
		} else if (messageContent instanceof RichContentMessage) {// ͼ����Ϣ
			RichContentMessage richContentMessage = (RichContentMessage) messageContent;
			Log.d(TAG,
					"onReceived-RichContentMessage:"
							+ richContentMessage.getContent());
		} else if (messageContent instanceof InformationNotificationMessage) {// С������Ϣ
			InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
			Log.d(TAG, "onReceived-informationNotificationMessage:"
					+ informationNotificationMessage.getMessage());
		} else if (messageContent instanceof ContactNotificationMessage) {// ���������Ϣ
			ContactNotificationMessage contactContentMessage = (ContactNotificationMessage) messageContent;
			Log.d(TAG, "onReceived-ContactNotificationMessage:getExtra;"
					+ contactContentMessage.getExtra());
			Log.d(TAG, "onReceived-ContactNotificationMessage:+getmessage:"
					+ contactContentMessage.getMessage().toString());

		} else {
		}
		return true;
	}

	
	@Override
	public void onChanged(ConnectionStatus arg0) {
		Log.d(TAG, "RongIMonChangedStatus:" + arg0.toString());
		if(arg0==ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT){
			Intent intent = new Intent();
			intent.setAction(SobotChatActivity.brocastContantant);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("msgContent", "{\"quitRongConnect\":\"" + true
				     + "\"}");
			mContext.sendBroadcast(intent);
		}
	}

	@Override
	public boolean onReceivePushMessage(PushNotificationMessage arg0) {
		
		return true;
	}
	
	
	
}
