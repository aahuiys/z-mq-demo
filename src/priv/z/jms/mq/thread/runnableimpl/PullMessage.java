package priv.z.jms.mq.thread.runnableimpl;


import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public abstract class PullMessage implements Runnable, MessageListener {

	private final String broker_url;
	
	private final String target;
	
	private final QueueConnection connection;

	public PullMessage(String broker_url, String target) throws JMSException {
		this.broker_url = broker_url;
		this.target = target;
		QueueConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, this.broker_url);
		connection = factory.createQueueConnection();
		connection.start();
	}

	@Override
	public void run() {
		QueueSession session = null;
		try {
			session = connection.createQueueSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue(target);
			QueueReceiver receiver = session.createReceiver(queue);
			receiver.setMessageListener(this);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
