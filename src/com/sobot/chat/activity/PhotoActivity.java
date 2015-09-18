package com.sobot.chat.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.util.LogUtils;
import com.sobot.chat.adapter.base.MessageAdapter.ImageClickLisenter;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.uk.co.senab.photoview.PhotoView;
import com.uk.co.senab.photoview.PhotoViewAttacher;
import com.uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

/**
 * 大图片的内容显示
 * 
 * @author jinliang
 * 
 */
public class PhotoActivity extends Activity {

	private PhotoView big_photo;
	private BitmapUtils bitmapUtils;
	private PhotoViewAttacher mAttacher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResourceUtils.getIdByName(this, "layout",
				"activity_photo"));
		big_photo = (PhotoView) findViewById(ResourceUtils.getIdByName(this,
				"id", "big_photo"));
		String imageUrL = getIntent().getStringExtra("imageUrL");
		bitmapUtils = new BitmapUtils(PhotoActivity.this);
		bitmapUtils.configDefaultAutoRotation(true);
	 /*	int degree = CommonUtils.getBitmapDegree(imageUrL);
		if (degree > 0) {
			BitmapFactory.Options opts = new BitmapFactory.Options();// 获取缩略图显示到屏幕上
			opts.inSampleSize = 1;
			Bitmap cbitmap = BitmapFactory.decodeFile(imageUrL, opts);
			*//**
			 * 把图片旋转为正的方向
			 *//*
			Bitmap newbitmap = CommonUtils.rotaingImageView(degree, cbitmap);
			big_photo.setImageBitmap(newbitmap);
		} else {
			bitmapUtils.display(big_photo, imageUrL);
		}*/
		
		bitmapUtils.display(big_photo, imageUrL);
		LogUtils.i("imageUrl:" + imageUrL);
		big_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		mAttacher = new PhotoViewAttacher(big_photo);
		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float x, float y) {
				LogUtils.i("点击图片的时间：" + view + " x:" + x + "  y:" + y);
				finish();
			}
		});

	}

}