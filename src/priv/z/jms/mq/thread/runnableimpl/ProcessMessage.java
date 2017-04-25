package priv.z.jms.mq.thread.runnableimpl;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import priv.z.jms.mq.pojo.Message;
import priv.z.jms.mq.pojo.ReceiveMessages;

public class ProcessMessage implements Runnable {

	private final javax.jms.Message message;
	
	private final ReceiveMessages responseMessages;

	public ProcessMessage(javax.jms.Message message, ReceiveMessages responseMessages) {
		this.message = message;
		this.responseMessages = responseMessages;
	}

	@Override
	public void run() {
		try {
			Message msg = (Message) ((ObjectMessage) message).getObject();
			msg.setMessage(msg.getMessage().replaceFirst("Send", "Reply"));
			msg.setReplyTime(new Date());
			responseMessages.putMessage(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
