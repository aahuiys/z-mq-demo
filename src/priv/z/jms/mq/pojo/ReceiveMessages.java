package priv.z.jms.mq.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import priv.z.jms.mq.thread.lock.ReentrantReadWriteLock;

public class ReceiveMessages {

	private final ReentrantReadWriteLock lock;
	
	private final Map<Integer, Message> messages;
	
	private final List<Message> timeOutMessages;
	
	private final long timeMillis;

	public ReceiveMessages(long timeMillis) {
		lock = new ReentrantReadWriteLock();
		messages = new HashMap<Integer, Message>();
		timeOutMessages = new ArrayList<Message>();
		this.timeMillis = timeMillis;
	}
	
	public void putMessage(Message message) throws InterruptedException {
		lock.lockWrite();
		try {
			messages.put(message.getMessageId(), message);
		} finally {
			lock.unlockWrite();
		}
	}
	
	public Message getMessage(int messageId) throws InterruptedException {
		Message message = null;
		lock.lockRead();
		try {
			message = messages.get(messageId);
			if (message != null && isTimeOut(message)) message = null;
		} finally {
			lock.unlockRead();
		}
		return message;
	}
	
	public int msgSize() throws InterruptedException {
		lock.lockRead();
		try {
			return messages.size();
		} finally {
			lock.unlockRead();
		}
	}
	
	public int outSize() throws InterruptedException {
		lock.lockRead();
		try {
			return timeOutMessages.size();
		} finally {
			lock.unlockRead();
		}
	}
	
	public void checkMessages() throws InterruptedException {
		lock.lockRead();
		try {
			Iterator<Message> it = messages.values().iterator();
			while (it.hasNext()) {
				Message message = it.next();
				if (message.isHandle()) {
					it.remove();
				} else if (isTimeOut(message)) {
					it.remove();
					timeOutMessages.add(message);
				}
			}
		} finally {
			lock.unlockRead();
		}
	}
	
	private boolean isTimeOut(Message message) {
		if(message.getReplyTime().getTime() - message.getSendTime().getTime() > timeMillis) return true;
		return false;
	}
}
