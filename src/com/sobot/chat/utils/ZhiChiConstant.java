package com.sobot.chat.utils;

public interface ZhiChiConstant {

	/**
	 * 图片存放的位置
	 */
	/**
	 * 所有的接口，如果cod=0，就不要解析data了
                                             如果code=1，就解析data里面的数据
	 */
	public  int  result_success_code = 1 ;
	public  int  result_fail_code =0 ;
	public String imagePositionPath="/sdcard/MyVoiceForder/Record/";
	public String voicePositionPath ="/sdcard/MyVoiceForder/Record/";
	public int hander_history = 600;
	public int hander_my_senderMessage = 601;
	public int hander_my_update_senderMessageStatus=1602;
	public int hander_robot_message = 602;
	public int hander_close_voice_view =603;
	
	public int hander_sendPicStatus_fail =401;
	public int hander_sendPicStatus_success=402;
	public int hander_sendPicIsLoading = 403;
	
	public int hander_timeTask_userInfo=800;
	public int hander_timeTask_custom_finish =801;
	public int hander_timeTask_custom_isBusying =802;
	
	/**
	 * 模式类型
	 */
	//人与机械 模式进行聊天 模式
	public int client_model_robot =301;
	//人与客服之间聊天
	public int client_model_customService=302;

	/**
	 * 消息类型
	 */
	// 文本消息
	public int message_type_wo_text = 0;  // 我发送的模式 以及  文本消息  文本消息是基类
	public int message_type_robot_text = 1;//机械人发送的模式的模式 以及  文本消息  文本消息是基类
	
	public int message_type_custonmType=2 ;// 客服客服发送的发送的消息类型

	// 图片消息
	public int message_type_custom_pic = 11;
	public int message_type_wo_pic = 3;

	// 纯图文消息
	public int message_type_robot_image = 4;
	public int message_type_myhistory_image = 5;
	public int message_type_wo_sendImage = 6;
	
	public int message_type_remide_info =7;
	
	public int message_type_send_voice=8; // 语音类型
	public int message_type_update_voice=2000;
	public int message_type_receive_robot_voice=9;
	
	public int message_type_receive_my_voice=10;
	public int message_type_robot_reply =12;
	
	
	public int push_message_createChat = 200 ;// 用户建立会话
	public int push_message_paidui = 201;// 用户排队
	public int push_message_receverNewMessage =202;
	public int push_message_outLine = 204 ;// 用户下线通知
	
	
	
	//############  转人工返回的状态码    ##############
	//1成功2失败3、用户被拉黑
	public int transfer_customeServeive_success =1 ;
	//不好意思，当前无客服在线
	public int transfer_customeServeive_fail =2 ;
	
	//用户被拉黑
	public int transfer_customeServeive_isBalk=3;
	
	//已经转人工
	public int transfer_customeServeive_alreay=4;
	

    public int REQUEST_CODE_picture = 701; // 获取照片列表
    
    public int REQUEST_CODE_makePictureFromCamera = 702;//直接照相获取图片
    
    // 消息类型  文本类型：0文本 1图片 2音频 3富文本
    public int message_type_text = 0;
    
    public int message_type_pic =1;
    
    public int message_type_voice =2;
    public int message_type_emoji =3 ; // 客服发送来的表情
    public int message_type_textAndPic=4;
    
    public int message_type_textAndText =5 ;
    public int message_type_textAndVideo  =6;
    
    public int voiceIsRecoding = 1000;
    
    /**
     * 消息类型0文本 1图片 2音频  4 富文本中有图片
 5 富文本中纯文字 6 富文本中有视频(APP不用解析)
     */
    

}