package activemq.queue;

import java.util.Date;
import java.util.Random;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
//import javax.jms.MapMessage;
//import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import priv.z.jms.mq.pojo.Message;

public class QueueSender {

	public final static int SEND_NUM = 5;
	
	public final static String BROKER_URL = "tcp://localhost:61616";
	
	public final static String DESTINATION = "z.reply";
	
	public static void sendMessage(QueueSession session, javax.jms.QueueSender sender) throws JMSException {
//		int count = 100;
		int count = new Random().nextInt(10000);
		for (int i = 0; i < count; i++) {
			int id = new Random().nextInt(count);
			String message = "[QUEUE]Send No." + id + " message.";
//			MapMessage mapMessage = session.createMapMessage();
//			mapMessage.setString("text", message);
//			mapMessage.setLong("time", System.currentTimeMillis());
//			System.out.println(mapMessage);
//			sender.send(mapMessage);
			Message msg = new Message(String.valueOf(id));
			msg.setMessage(message);
			msg.setSendTime(new Date());
			System.out.println("[send]\n" + msg);
			sender.send(session.createObjectMessage(msg));
		}
		System.out.println("*************************************************************\n" + count);
	}
	
	public static void run() {
		QueueConnection connection = null;
		QueueSession session = null;
		try {
			QueueConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URL);
			connection = factory.createQueueConnection();
			connection.start();
			session = connection.createQueueSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue(DESTINATION);
			javax.jms.QueueSender sender = session.createSender(queue);
			sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			sendMessage(session, sender);
			session.commit();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (session != null) {
					session.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		QueueSender.run();
	}
}
