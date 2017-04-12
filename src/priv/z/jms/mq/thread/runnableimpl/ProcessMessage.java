package priv.z.jms.mq.thread.runnableimpl;

import java.util.Date;
import java.util.Random;

import priv.z.jms.mq.pojo.Message;
import priv.z.jms.mq.pojo.ReceiveMessages;
import priv.z.jms.mq.thread.queue.BlockingLinkedQueue;

public class ProcessMessage<T> implements Runnable {

	private final BlockingLinkedQueue<T>  requestMessages;
	
	private final ReceiveMessages responseMessages;
	
	public ProcessMessage(BlockingLinkedQueue<T> requestMessages, ReceiveMessages responseMessages) {
		this.requestMessages = requestMessages;
		this.responseMessages = responseMessages;
	}

	@Override
	public void run() {
		while (true) {
			try {
				T t = requestMessages.dequeue();
				Message msg = (Message) t;
				
				Thread.sleep(new Random().nextInt(100));
				
				msg.setMessage(msg.getMessage().replaceFirst("Send", "Reply"));
				msg.setReplyTime(new Date());
				responseMessages.putMessage(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
