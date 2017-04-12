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
		String responseString = "Request Faild: [" + requestString + "]\n";
		Message message = pushMessage.request(requestString);
		if (message != null) {
			message.setHandle(true);
			responseString = message.getMessage();
			
			System.out.println("[receive]\n" + message.printInfo());
		} else {
			System.out.println(responseString);
		}
		response.getWriter().print(responseString);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
