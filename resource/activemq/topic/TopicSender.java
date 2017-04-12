package activemq.topic;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class TopicSender {

	public final static int SEND_NUM = 5;
	
	public final static String BROKER_URL = "tcp://localhost:61616";
	
	public final static String DESTINATION = "z.mq";
	
	public static void sendMessage(TopicSession session, TopicPublisher publisher) throws JMSException {
		for (int i = 0; i < SEND_NUM; i++) {
			String message = "[TOPIC]Send No." + (i + 1) + " message.";
			MapMessage mapMessage = session.createMapMessage();
			mapMessage.setString("text", message);
			mapMessage.setLong("time", System.currentTimeMillis());
			System.out.println(mapMessage);
			publisher.send(mapMessage);
		}
	}
	
	public static void run() {
		TopicConnection connection = null;
		TopicSession session = null;
		try {
			TopicConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URL);
			connection = factory.createTopicConnection();
			connection.start();
			session = connection.createTopicSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic(DESTINATION);
			TopicPublisher publisher = session.createPublisher(topic);
			publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			sendMessage(session, publisher);
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
		TopicSender.run();
	}
}
