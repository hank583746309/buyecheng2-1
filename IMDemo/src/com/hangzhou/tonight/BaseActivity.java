package com.hangzhou.tonight;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hangzhou.tonight.im.RoomChatActivity;
import com.hangzhou.tonight.manager.XmppConnectionManager;

public abstract class BaseActivity extends Activity{
	public abstract void sendMessage(Message message);
	public void join(final Context context,String user,String rpwd,String rname)
	{
		final MultiUserChat muc = XmppConnectionManager.getInstance().joinRoom(user, rpwd, rname);
		runOnUiThread(new Runnable(){
			@Override
			public void run() { 
				if(null != muc){
					XmppConnectionManager.getInstance().setMuc(muc);
					Toast.makeText(context, "进入成功", Toast.LENGTH_SHORT).show();
					((Activity) context).finish();
					Intent intent = new Intent(context, RoomChatActivity.class);
					context.startActivity(intent);
				}
				else{
					Toast.makeText(context, "进入失败", Toast.LENGTH_SHORT).show();
				}
			}});
	}

}
