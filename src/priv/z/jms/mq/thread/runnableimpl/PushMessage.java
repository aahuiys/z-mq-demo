package priv.z.jms.mq.thread.runnableimpl;

import java.util.Date;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import priv.z.jms.mq.pojo.Message;
import priv.z.jms.mq.pojo.ReceiveMessages;
import priv.z.jms.mq.util.UUIDGenerator;

public class PushMessage {

	private final String broker_url;
	
	private final String target;
	
	private final ReceiveMessages responseMessages;
	
	private final QueueConnection connection;
	
	public PushMessage(String broker_url, String target, ReceiveMessages responseMessages) throws JMSException {
		this.broker_url = broker_url;
		this.target = target;
		this.responseMessages = responseMessages;
		QueueConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, this.broker_url);
		connection = factory.createQueueConnection();
		connection.start();
	}
	
	public Message request(String requestString) {
		Message message = null;
		QueueSession session = null;
		try {
			session = connection.createQueueSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue(target);
			QueueSender sender = session.createSender(queue);
			sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			String messageId = UUIDGenerator.getUUID();
			Message msg = new Message(messageId);
			requestString = "[QUEUE]Send No." + requestString + " message.";
			msg.setMessage(requestString);
			msg.setSendTime(new Date());
			responseMessages.setSemaphore(messageId);
			sender.send(session.createObjectMessage(msg));
			session.close();
//			System.out.println("[send]\n" + msg.printInfo());
			message = responseMessages.getMessage(messageId);
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return message;
	}
}
