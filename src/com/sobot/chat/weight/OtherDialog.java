package com.sobot.chat.weight;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.sobot.chat.activity.SobotChatActivity;
import com.sobot.chat.utils.ResourceUtils;

/**
 * 
 * Create custom Dialog windows for your application Custom dialogs rely on
 * custom layouts wich allow you to create and use your own look & feel.
 * 
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * <a href="http://my.oschina.net/arthor" target="_blank"
 * rel="nofollow">@author</a> antoine vianey
 * 
 */
public class OtherDialog extends Dialog {

	public OtherDialog(Context context, int theme) {
		super(context, theme);
	}

	public OtherDialog(Context context) {
		super(context);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private Context context;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private Button sureBtn,cancleBtn;
		private SobotChatActivity m;
		private String color = null;

		public Builder(Context context,SobotChatActivity mm, String color) {
			this.context = context;
			this.m = mm;
			this.color = color;
		}

		/**
		 * Set the Dialog message from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(String message) {
			this.message = message;
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
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		@SuppressWarnings("deprecation")
		public OtherDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final OtherDialog dialog = new OtherDialog(context,
					ResourceUtils.getIdByName(context, "style", "Dialog"));
			View layout = inflater.inflate(ResourceUtils.getIdByName(context,
					"layout", "other_dialog_layout"), null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the confirm button
			if (positiveButtonText != null) {
				sureBtn = (Button) layout.findViewById(ResourceUtils.getIdByName(context, "id", "positiveButton"));
				sureBtn.setText(positiveButtonText);
				sureBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
						m.finish();
					}
				});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(
						ResourceUtils.getIdByName(context, "id",
								"positiveButton")).setVisibility(View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				cancleBtn = (Button) layout.findViewById(ResourceUtils.getIdByName(context, "id", "negativeButton"));
				GradientDrawable myGrad = (GradientDrawable) cancleBtn.getBackground();
				myGrad.setColor(Color.parseColor(color));
				cancleBtn.setBackgroundResource(ResourceUtils.getIdByName(context, "drawable", "subbutton_shap"));
				cancleBtn.setText(negativeButtonText);
				cancleBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(
						ResourceUtils.getIdByName(context, "id",
								"negativeButton")).setVisibility(View.GONE);
			}
			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(ResourceUtils.getIdByName(
						context, "id", "message"))).setText(message);
			}
			dialog.setContentView(layout);
			return dialog;
		}
	}
}