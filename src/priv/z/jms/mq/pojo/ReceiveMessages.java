package priv.z.jms.mq.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import priv.z.jms.mq.thread.lock.ReentrantReadWriteLock;
import priv.z.jms.mq.thread.semaphore.HandleSemaphore;

public class ReceiveMessages {

	private final static Log logger = LogFactory.getLog(ReceiveMessages.class);
	
	private final ReentrantReadWriteLock lock;
	
	private final Map<String, HandleSemaphore> semaphores;
	
	private final Map<String, Message> messages;
	
	private final List<Message> timeOutMessages;
	
	private final long timeMillis;

	public ReceiveMessages(long timeMillis) {
		lock = new ReentrantReadWriteLock();
		semaphores = new HashMap<String, HandleSemaphore>();
		messages = new HashMap<String, Message>();
		timeOutMessages = new ArrayList<Message>();
		this.timeMillis = timeMillis;
		logger.info(this.getClass().getSimpleName() + " Init Complete.");
	}
	
	public boolean putMessage(Message message) throws InterruptedException {
		boolean isTimeout = false;
		HandleSemaphore semaphore = getSemaphore(message);
//		lock.lockWrite();
		try {
			synchronized (semaphore) {
				isTimeout = semaphore.release();
				if (!isTimeout) messages.put(message.getMessageId(), message);
			}
		} catch (RuntimeException e) {
			logger.error("PutMessage not find semaphore: " + message.getMessageId());
		} finally {
//			lock.unlockWrite();
			removeSemaphore(message);
		}
		return isTimeout;
	}
	
	public Message getMessage(String messageId) throws InterruptedException {
		Message message = null;
		HandleSemaphore semaphore = getSemaphore(messageId);
//		lock.lockRead();
		try {
			semaphore.take();
//			synchronized (semaphore) {
				message = messages.get(messageId);
//			}
		} catch (RuntimeException e) {
			logger.error("GetMessage not find semaphore: " + messageId);
		} finally {
//			lock.unlockRead();
			if (message != null) messages.remove(messageId);
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
	
	public void setSemaphore(String messageId) {
		semaphores.put(messageId, new HandleSemaphore(timeMillis));
		logger.debug("Set the send semaphore: " + messageId);
	}
	
	private HandleSemaphore getSemaphore(Message message) {
		return getSemaphore(message.getMessageId());
	}
	
	private HandleSemaphore getSemaphore(String messageId) {
		return semaphores.get(messageId);
	}
	
	private void removeSemaphore(Message message) {
		removeSemaphore(message.getMessageId());
	}
	
	private void removeSemaphore(String messageId) {
		semaphores.remove(messageId);
	}
}
