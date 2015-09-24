package com.hangzhou.tonight.module.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.hangzhou.tonight.R;

public abstract class CustomActionActivity extends Activity {
	
	//protected Context $context = this;
	
	private View actionbarView;
	private TextView tvBack;
	private TextView tvTitle;
	private View     handler;
	
	private String title;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		doView();
		doActionbar();
		
		doListeners();
		doHandler();
		
		doDefault();
	}

	protected abstract void doView();
	protected abstract void doListeners();
	protected abstract void doHandler();
	
	protected void doDefault(){
		if(null == title && null != getTitle()){ setTitle(getTitle().toString());}
	}
	
	protected void doActionbar() {
		actionbarView = findViewById(R.id.custom_actionbar_container);
		if(null != actionbarView){
			tvBack = (TextView) actionbarView.findViewById(R.id.custom_actionbar_back);
			tvTitle= (TextView) actionbarView.findViewById(R.id.custom_actionbar_title);
			handler= actionbarView.findViewById(R.id.custom_actionbar_hanlder);
			
			tvBack.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) { onBackPressed(); }
			});
			
			doHandlerView(handler);
			handler.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if(null != handlerClick){ handlerClick.onClick(handler); }
				}
			});
		}
	}
	
	public void setTitle(String title) {
		this.title = title;
		if(tvTitle != null){ tvTitle.setText(title);}
	}
	
	public void setBackViewVisibility(int visibility){
		if(null != tvBack){ tvBack.setVisibility(visibility);}
	}
	
	public void doHandlerView(View handler){}
	
	OnHandlerClickListener handlerClick = null;
	public void setOnClickListener(OnHandlerClickListener handlerClick){
		this.handlerClick = handlerClick;
	}
	
	public interface OnHandlerClickListener{
		public void onClick(View handlerView);
	}

	public Context getActivity(){
		return this;
	}
	
	/**
	 * 查找组件[简洁封装]
	 * @param resId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T findView(int resId){
		return (T) findViewById(resId);
	}
}
