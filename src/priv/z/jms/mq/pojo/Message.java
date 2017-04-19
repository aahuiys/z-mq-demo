package priv.z.jms.mq.pojo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Message implements Serializable {

	private final String messageId;
	
	private String message = null;
	
	private Date sendTime = null;
	
	private Date replyTime = null;
	
	private boolean handle = false;
	
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public String printInfo() {
		StringBuffer sb = new StringBuffer();
		synchronized (sdf) {
			if(!handle) {
				sb.append(sdf.format(sendTime));
			} else {
				sb.append(sdf.format(replyTime));
			}
		}
		sb.append("===").append(messageId);
		sb.append("\n")
			.append(super.toString())
			.append("\n")
			.append(message)
			.append("\n");
		return sb.toString();
	}
	
	public Message(String messageId) {
		this.messageId = messageId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Date getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(Date replyTime) {
		this.replyTime = replyTime;
	}

	public boolean isHandle() {
		return handle;
	}

	public void setHandle(boolean handle) {
		this.handle = handle;
	}

	public String getMessageId() {
		return messageId;
	}
}
