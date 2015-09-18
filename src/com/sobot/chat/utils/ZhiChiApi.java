package com.sobot.chat.utils;

public interface ZhiChiApi {

	public String baseIP = "http://open.sobot.com/chat/sdk/user/v1/";
	// public String baseIP = "http://172.16.8.8/";

	public String baseFormalIP = "http://www.sobot.com/";
	// ################ 人机交互 ##########################
	// 聊天窗口初始化
	public String api_robot_chat_init = baseIP + "init.action";

	// 获取历史记录
	public String api_robot_chat_historyMessage = baseIP + "chatdetail.action";

	// 发送消息接口
	public String api_robot_chat_sendMessage = baseIP + "chat.action";

	// 转人工的接口
	public String api_transfer_people = baseIP + "connect.action";

	// 用户给客服发消息;
	public String api_sendmessage_to_customService = baseIP + "send.action";

	// 上传文件（图片）
	public String api_sendFile_to_customeService = baseIP + "sendFile.action";

	/*
	 * public String api_sendFile_to_customeService =baseIP+
	 * "chat/webchat/fileupload.action";
	 */
	/* 满意度 */
	public String api_chat_comment = baseIP + "comment.action";
	/* 获取配置 */
	public String api_chat_config = baseIP + "chatconfig.action";
	/**/
	public String api_login_out = baseIP + "out.action";
	
	//http://test.sobot.com/chat/sdk/user/v1/token/refresh.action?sysNum=0a88b1e206574af09ecd608b8e927a81&partnerId=863360026238703&way=3
	public String api_token_reflesh= baseIP + "token/refresh.action";
}