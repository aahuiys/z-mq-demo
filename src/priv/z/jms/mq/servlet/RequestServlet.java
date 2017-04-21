package priv.z.jms.mq.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import priv.z.jms.mq.pojo.Message;
import priv.z.jms.mq.pojo.ReceiveMessages;
import priv.z.jms.mq.pojo.RequestManager;
import priv.z.jms.mq.thread.runnableimpl.PushMessage;
import priv.z.jms.mq.util.UUIDGenerator;

@SuppressWarnings("serial")
public class RequestServlet extends HttpServlet {

	private ReceiveMessages responseMessages;
	
	private PushMessage pushMessage;
	
	private RequestManager manager;
	
	@Override
	public void init() throws ServletException {
		responseMessages = (ReceiveMessages) getServletContext().getAttribute("responseMessages");
		try {
			pushMessage = new PushMessage(getServletContext().getInitParameter("broker_url"), getServletContext().getInitParameter("target"), responseMessages);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		manager = new RequestManager();
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
		response.setCharacterEncoding("UTF-8");
		String action = request.getParameterValues("action")[0];
		final List<Thread> threads = new ArrayList<Thread>();
		final RequestManager.Request req = manager.new Request() {
			
			@Override
			public void run() {
				String requestString = UUIDGenerator.getUUID();
				String responseString = "Request Faild: [" + requestString + "]-Timeout.\n";
				Message message = pushMessage.request(requestString);
				if (message != null && ("[QUEUE]Reply No." + requestString + " message.").equals(message.getMessage())) {
//					message.setHandle(true);
//					responseString = message.getMessage();
					ok.add();
//					System.out.println("[receive]\n" + message.printInfo());
				} else {
					if (message != null) {
						responseString = "Request Faild: [" + requestString + "]-ID Error!\n";
						err.add();
						System.err.println(responseString);
					} else {
						timeout.add();
					}
				}
				synchronized (threads) {
					threads.remove(Thread.currentThread());
				}
			}
		};
		if (action.equals("GET")) {
			if (!manager.request(req)) response.getWriter().print(colorTag("Over the maximum numbers of requests.", "blue"));
			else {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						while (!manager.isStop()) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							Thread thread = new Thread(req);
							threads.add(thread);
							thread.start();
						}
						List<Thread> t = new ArrayList<Thread>();
						synchronized (threads) {
							for (Thread thread : threads) t.add(thread);
						}
						try {
							for (Thread thread : t) thread.join();
							manager.release();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
				response.getWriter().print(colorTag("Start a new request.", "green"));
			}
		} else if (action.equals("STATUS")) {
			if (!manager.isStop()) {
				StringBuffer sb = new StringBuffer();
				for (RequestManager.Request r : manager.getRequests()) {
					sb.append(colorTag(r.ok.toString(), "green")).append(" | ")
						.append(colorTag(r.err.toString(), "red")).append(" | ")
						.append(colorTag(r.timeout.toString(), "blue")).append("<br />");
				}
				response.getWriter().print(sb.toString());
			} else response.getWriter().print(colorTag("No request or In preparation.", "blue"));
		} else if (action.equals("STOP")) {
			if (!manager.isStop() && manager.stop()) {
				try {
					manager.end();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				StringBuffer sb = new StringBuffer();
				sb.append(colorTag("All requests has been stopped.<br />", "green"));
				for (RequestManager.Request r : manager.getRequests()) {
					sb.append(colorTag(r.ok.toString(), "green")).append(" | ")
					.append(colorTag(r.err.toString(), "red")).append(" | ")
					.append(colorTag(r.timeout.toString(), "blue")).append("<br />");
				}
				response.getWriter().print(sb.toString());
				manager.ready();
			} else response.getWriter().print(colorTag("No request or In preparation.", "blue"));
		}
	}
	
	private String colorTag(String s, String color) {
		return "<span style=\"color: " + color + ";\">" + s + "</span>";
	}
}
