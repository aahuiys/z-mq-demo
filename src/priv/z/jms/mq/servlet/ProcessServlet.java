package priv.z.jms.mq.servlet;

import javax.jms.JMSException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import priv.z.jms.mq.pojo.Message;
import priv.z.jms.mq.pojo.ReceiveMessages;
import priv.z.jms.mq.thread.pool.FixedThreadPool;
import priv.z.jms.mq.thread.queue.BlockingLinkedQueue;
import priv.z.jms.mq.thread.runnableimpl.ProcessMessage;
import priv.z.jms.mq.thread.runnableimpl.PullMessage;

@SuppressWarnings("serial")
public class ProcessServlet extends HttpServlet {

	private FixedThreadPool threadPool;
	
	private BlockingLinkedQueue<Message>  requestMessages;
	
	private ReceiveMessages responseMessages;
	
	private ProcessMessage<Message> processMessage;
	
	@Override
	public void init() throws ServletException {
		threadPool = new FixedThreadPool(Integer.valueOf(getInitParameter("nThreads")), Integer.valueOf(getInitParameter("length")));
		requestMessages = new BlockingLinkedQueue<Message>(Integer.valueOf(getInitParameter("length")));
		responseMessages = new ReceiveMessages(Integer.valueOf(getInitParameter("timeMillis")));
		getServletContext().setAttribute("responseMessages", responseMessages);
		processMessage = new ProcessMessage<Message>(requestMessages, responseMessages);
		try {
			threadPool.execute(new PullMessage<Message>(getServletContext().getInitParameter("broker_url"), getServletContext().getInitParameter("target"), requestMessages));
			for (int i = 0; i < Integer.valueOf(getInitParameter("nThreads")); i++) {
				threadPool.execute(processMessage);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
