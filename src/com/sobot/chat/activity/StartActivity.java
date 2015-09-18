package com.sobot.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sobot.chat.model.Information;
import com.sobot.chat.utils.ResourceUtils;

public class StartActivity extends Activity {
	
	private EditText sysNum;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResourceUtils.getIdByName(this, "layout",
				"activity_start"));

		this.sysNum = ((EditText) findViewById(ResourceUtils.getIdByName(this,
				"id", "sysNum")));

		findViewById(ResourceUtils.getIdByName(this, "id", "btnStart"))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View arg0) {
						

						Information info = new Information();
						info.setAppKey(sysNum.getText().toString());/*必填*/
						/* info.setColor("") 设置气泡主题颜色，默认颜色为 #09aeb0
						 修改气泡的颜色，可以替换资源文件，资源文件在drawable-xhdpi文件夹下，
						 机器人信息的背景图片为		 chatfrom_bg_normal.9.png
						 我的信息的背景图片有以下两张
						 chatto_bg_normal.9.png
						 chatto1_bg_normal.9.png
						 */
						info.setColor("");/*选填*/
						/* info.setUid("") 设置用户唯一标识，*/
						info.setUid("");
						info.setNickName("");/*用户昵称，选填*/
						info.setPhone("");/*用户电话，选填*/
						info.setEmail("");/*用户邮箱，选填*/
						
						Intent intent = new Intent(StartActivity.this,
								RobotLoadingActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("info", info);
						intent.putExtra("bundle", bundle);
						StartActivity.this.startActivity(intent);
					}
				});
	}
}