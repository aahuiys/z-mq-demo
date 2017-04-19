package priv.z.jms.mq.servlet;

import java.io.IOException;

import javax.jms.JMSException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import priv.z.jms.mq.pojo.Message;
import priv.z.jms.mq.pojo.ReceiveMessages;
import priv.z.jms.mq.thread.runnableimpl.PushMessage;
import priv.z.jms.mq.util.UUIDGenerator;

@SuppressWarnings("serial")
public class RequestServlet extends HttpServlet {

	private ReceiveMessages responseMessages;
	
	private PushMessage pushMessage;
	
	@Override
	public void init() throws ServletException {
		responseMessages = (ReceiveMessages) getServletContext().getAttribute("responseMessages");
		try {
			pushMessage = new PushMessage(getServletContext().getInitParameter("broker_url"), getServletContext().getInitParameter("target"), responseMessages);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
//	@Override
//	protected void service(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		String requestString = request.getParameterValues("id")[0];
		String responseString = "Request Faild: [" + requestString + "]-Timeout.\n";
		String color = "blue";
		Message message = pushMessage.request(requestString);
		if (message != null && ("[QUEUE]Reply No." + requestString + " message.").equals(message.getMessage())) {
			message.setHandle(true);
			responseString = message.getMessage();
			color = "green";
			System.out.println("[receive]\n" + message.printInfo());
		} else {
			if (message != null) {
				responseString = "Request Faild: [" + requestString + "]-ID Error!\n";
				color = "red";
			}
			System.err.println(responseString);
		}
		response.getWriter().print(colorTag(responseString, color));
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final CountInfo ok = new CountInfo();
		final CountInfo err = new CountInfo();
		final CountInfo timeout = new CountInfo();
		response.setCharacterEncoding("UTF-8");
		int count = Integer.valueOf(request.getParameterValues("count")[0]);
		Thread[] threads = new Thread[count];
		for (int i = 0; i < count; i++) {
			final String requestString = UUIDGenerator.getUUID();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					String responseString = "Request Faild: [" + requestString + "]-Timeout.\n";
					Message message = pushMessage.request(requestString);
					if (message != null && ("[QUEUE]Reply No." + requestString + " message.").equals(message.getMessage())) {
						message.setHandle(true);
//						responseString = message.getMessage();
						ok.add();
//						System.out.println("[receive]\n" + message.printInfo());
					} else {
						if (message != null) {
							responseString = "Request Faild: [" + requestString + "]-ID Error!\n";
							err.add();
							System.err.println(responseString);
						} else {
							timeout.add();
						}
					}
				}
			});
			thread.start();
			threads[i] = thread;
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		response.getWriter().print(colorTag(ok.toString(), "green") + " | " + colorTag(err.toString(), "red") + " | " + colorTag(timeout.toString(), "blue"));
	}
	
	private String colorTag(String s, String color) {
		return "<span style=\"color: " + color + ";\">" + s + "</span>";
	}
	
	private class CountInfo {
		
		private int n = 0;
		
		private synchronized void add() {
			n++;
		}
		
		@Override
		public String toString() {
			return String.valueOf(n);
		}
	}
}
