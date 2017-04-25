package priv.z.jms.mq.pojo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RequestManager {

	private final static Log logger = LogFactory.getLog(RequestManager.class);
	
	private int size = 0;
	
	private int maximum = 3;
	
	private boolean isReady = true;
	
	private volatile boolean isStop = true;
	
	private Request[] requests;
	
	private int semaphore;

	public RequestManager() {
		requests = new Request[maximum];
	}

	public RequestManager(int maximum) {
		this.maximum = maximum;
		requests = new Request[this.maximum];
	}
	
	public synchronized boolean request(Request request) {
		if (isReady && size < maximum) {
			requests[semaphore = size++] = request;
			isStop = false;
			return true;
		}
		return false;
	}
	
	public synchronized Request[] getRequests() {
		Request[] requests = new Request[size];
		for (int i = 0; i < size; i++) requests[i] = this.requests[i];
		return requests;
	}
	
	public synchronized boolean stop() {
		if (!isStop) {
			logger.info("Set stop, wait for all requests end.");
			isStop = true;
			isReady = false;
			return true;
		}
		return false;
	}
	
	public synchronized void end() throws InterruptedException {
		int count = 0;
		while (semaphore != -1 && count++ < 3) wait(5000);
		if (semaphore == -1) logger.info("All requests ended successfully.");
		else logger.error((semaphore + 1) + " request(s) can not be released.");
	}
	
	public synchronized void release() throws InterruptedException {
		if (semaphore-- == 0) notify();
		logger.info("A request ended successfully.");
	}
	
	public synchronized  void ready() {
		for (int i = 0; i < size; i++) requests[i] = null;
		size = 0;
		isReady = true;
	}

	public boolean isStop() {
		return isStop;
	}
	
	public abstract class Request implements Runnable {
		
		public CountInfo ok = new CountInfo();
		
		public CountInfo err = new CountInfo();
		
		public CountInfo timeout = new CountInfo();
	}
	
	public class CountInfo {
		
		private volatile long n = 0;
		
		public synchronized void add() {
			n++;
		}
		
		@Override
		public String toString() {
			return String.valueOf(n);
		}
	}
}
