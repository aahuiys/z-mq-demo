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
	
	private final ReentrantReadWriteLock semaphoreLock;
	
	private final ReentrantReadWriteLock messageLock;
	
	private final Map<String, HandleSemaphore> semaphores;
	
	private final Map<String, Message> messages;
	
	private final List<Message> timeOutMessages;
	
	private final long timeMillis;

	public ReceiveMessages(long timeMillis) {
		semaphoreLock = new ReentrantReadWriteLock();
		messageLock = new ReentrantReadWriteLock();
		semaphores = new HashMap<String, HandleSemaphore>();
		messages = new HashMap<String, Message>();
		timeOutMessages = new ArrayList<Message>();
		this.timeMillis = timeMillis;
		logger.info(this.getClass().getSimpleName() + " Init Complete.");
	}
	
	public boolean putMessage(Message message) throws InterruptedException {
		boolean isTimeout = true;
		try {
			HandleSemaphore semaphore = getSemaphore(message);
			synchronized (semaphore) {
				isTimeout = semaphore.release();
				if (!isTimeout) {
					messageLock.lockWrite();
					try {
						messages.put(message.getMessageId(), message);
					} finally {
						messageLock.unlockWrite();
					}
				}
			}
			removeSemaphore(message);
		} catch (RuntimeException e) {
			logger.error("PutMessage not find semaphore: " + message.getMessageId());
		}
		return isTimeout;
	}
	
	public Message getMessage(String messageId) throws InterruptedException {
		Message message = null;
		try {
			getSemaphore(messageId).take();
			messageLock.lockRead();
			try {
				message = messages.get(messageId);
			} finally {
				messageLock.unlockRead();
			}
			if (message != null) {
				messageLock.lockWrite();
				try {
					messages.remove(messageId);
				} finally {
					messageLock.unlockWrite();
				}
			}
		} catch (RuntimeException e) {
			logger.error("GetMessage not find semaphore: " + messageId);
		}
		return message;
	}
	
	public int msgSize() throws InterruptedException {
		messageLock.lockRead();
		try {
			return messages.size();
		} finally {
			messageLock.unlockRead();
		}
	}
	
	public int outSize() throws InterruptedException {
		messageLock.lockRead();
		try {
			return timeOutMessages.size();
		} finally {
			messageLock.unlockRead();
		}
	}
	
	public void checkMessages() throws InterruptedException {
		messageLock.lockRead();
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
			messageLock.unlockRead();
		}
	}
	
	private boolean isTimeOut(Message message) {
		if(message.getReplyTime().getTime() - message.getSendTime().getTime() > timeMillis) return true;
		return false;
	}
	
	public void setSemaphore(String messageId) throws InterruptedException {
		semaphoreLock.lockWrite();
		try {
			semaphores.put(messageId, new HandleSemaphore(timeMillis));
			logger.debug("Set the send semaphore: " + messageId);
		} finally {
			semaphoreLock.unlockWrite();
		}
	}
	
	private HandleSemaphore getSemaphore(Message message) throws InterruptedException {
		return getSemaphore(message.getMessageId());
	}
	
	private HandleSemaphore getSemaphore(String messageId) throws InterruptedException {
		semaphoreLock.lockRead();
		try {
			return semaphores.get(messageId);
		} finally {
			semaphoreLock.unlockRead();
		}
	}
	
	private void removeSemaphore(Message message) throws InterruptedException {
		removeSemaphore(message.getMessageId());
	}
	
	private void removeSemaphore(String messageId) throws InterruptedException {
		semaphoreLock.lockWrite();
		try {
			semaphores.remove(messageId);
		} finally {
			semaphoreLock.unlockWrite();
		}
	}
}
