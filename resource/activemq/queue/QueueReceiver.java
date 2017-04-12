package activemq.queue;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class QueueReceiver {

	public final static String BROKER_URL = "tcp://localhost:61616";
	
	public final static String TARGET = "z.mq";
	
	public static void run() {
		QueueConnection connection = null;
		QueueSession session = null;
		try {
			QueueConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URL);
			connection = factory.createQueueConnection();
			connection.start();
			session = connection.createQueueSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue(TARGET);
			javax.jms.QueueReceiver receiver = session.createReceiver(queue);
			receiver.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message message) {
					// TODO Auto-generated method stub
					if(message != null) {
						MapMessage mapMessage = (MapMessage) message;
						try {
							System.out.println(mapMessage.getLong("time") + "Received:" + mapMessage.getString("text"));
						} catch (JMSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			Thread.sleep(1000 * 10);
			session.commit();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
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
		QueueReceiver.run();
	}
}
