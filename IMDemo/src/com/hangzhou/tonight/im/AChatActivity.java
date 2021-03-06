package com.hangzhou.tonight.im;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.hangzhou.tonight.ActivitySupport;
import com.hangzhou.tonight.comm.Constant;
import com.hangzhou.tonight.manager.MessageManager;
import com.hangzhou.tonight.manager.NoticeManager;
import com.hangzhou.tonight.manager.XmppConnectionManager;
import com.hangzhou.tonight.model.IMMessage;
import com.hangzhou.tonight.model.Notice;
import com.hangzhou.tonight.util.DateUtil;

/**
 * 
 * 聊天对话.
 * 
 * @author shimiso
 */
public abstract class AChatActivity extends ActivitySupport {

	private Chat chat = null;
	private List<IMMessage> message_pool = null;
	protected String to;// 聊天人
	private static int pageSize = 10;
	private List<Notice> noticeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		to = getIntent().getStringExtra("to");
		if (to == null)
			return;
		chat = XmppConnectionManager.getInstance().getConnection()
				.getChatManager().createChat(to, null);
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// 第一次查询
		message_pool = MessageManager.getInstance(context)
				.getMessageListByFrom(to, 1, pageSize);
		if (null != message_pool && message_pool.size() > 0)
			Collections.sort(message_pool);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.NEW_MESSAGE_ACTION);
		registerReceiver(receiver, filter);

		// 更新某人所有通知
		NoticeManager.getInstance(context).updateStatusByFrom(to, Notice.READ);
		super.onResume();

	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
				IMMessage message = intent
						.getParcelableExtra(IMMessage.IMMESSAGE_KEY);
				message_pool.add(message);
				receiveNewMessage(message);
				refreshMessage(message_pool);
			}
		}

	};

	protected abstract void receiveNewMessage(IMMessage message);

	protected abstract void refreshMessage(List<IMMessage> messages);

	protected List<IMMessage> getMessages() {
		return message_pool;
	}

	protected void sendMessage(String messageContent) throws Exception {
		String time = DateUtil.date2Str(Calendar.getInstance(),
				Constant.MS_FORMART);
		Message message = new Message();
		message.setMt("text");
		message.setTp("add friend");
		message.setSt("1900:08:08");
		//message.setFrom("1000000@115.29.246.139");
		message.setProperty(IMMessage.KEY_TIME, time);
		message.setBody(messageContent);
		chat.sendMessage(message);
		Log.d("发送信息", message.toXML());
		IMMessage newMessage = new IMMessage();
		newMessage.setMsgType(1);
		newMessage.setFromSubJid(chat.getParticipant());
		newMessage.setContent(messageContent);
		newMessage.setTime(time);
		//newMessage.setPath(path);
		message_pool.add(newMessage);
		MessageManager.getInstance(context).saveIMMessage(newMessage);
		// MChatManager.message_pool.add(newMessage);

		// 刷新视图
		refreshMessage(message_pool);

	}

	/**
	 * 下滑加载信息,true 返回成功，false 数据已经全部加载，全部查完了，
	 * 
	 * @param message
	 */
	protected Boolean addNewMessage() {
		List<IMMessage> newMsgList = MessageManager.getInstance(context)
				.getMessageListByFrom(to, message_pool.size(), pageSize);
		if (newMsgList != null && newMsgList.size() > 0) {
			message_pool.addAll(newMsgList);
			Collections.sort(message_pool);
			return true;
		}
		return false;
	}

	protected void resh() {
		// 刷新视图
		refreshMessage(message_pool);
	}

}
