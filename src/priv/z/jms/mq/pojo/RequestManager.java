package priv.z.jms.mq.pojo;

public class RequestManager {

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
			isStop = true;
			isReady = false;
			return true;
		}
		return false;
	}
	
	public synchronized void end() throws InterruptedException {
		while (semaphore != 0) wait();
	}
	
	public synchronized void release() throws InterruptedException {
		if (--semaphore == 0) notify();
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
