package priv.z.jms.mq.servlet;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import priv.z.jms.mq.pojo.ReceiveMessages;
import priv.z.jms.mq.thread.pool.FixedThreadPool;
import priv.z.jms.mq.thread.runnableimpl.ProcessMessage;
import priv.z.jms.mq.thread.runnableimpl.PullMessage;

@SuppressWarnings("serial")
public class ProcessServlet extends HttpServlet {

	private FixedThreadPool threadPool;
	
	private ReceiveMessages responseMessages;
	
	@Override
	public void init() throws ServletException {
		threadPool = new FixedThreadPool(Integer.valueOf(getInitParameter("nThreads")), Integer.valueOf(getInitParameter("length")));
		responseMessages = new ReceiveMessages(Integer.valueOf(getInitParameter("timeMillis")));
		getServletContext().setAttribute("responseMessages", responseMessages);
		try {
			new Thread(new PullMessage(getServletContext().getInitParameter("broker_url"), getServletContext().getInitParameter("target")) {
				
				@Override
				public void onMessage(Message message) {
					threadPool.execute(new ProcessMessage(message, responseMessages));
				}
			}).start();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
