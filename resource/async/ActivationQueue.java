package async;

public class ActivationQueue {

	private final int maxRequest;
	
	private final Request[] requestQueue;
	
	private int count = 0;
	
	private int head = 0;
	
	private int tail = 0;

	public ActivationQueue(int maxRequest) {
		this.maxRequest = maxRequest;
		requestQueue = new Request[this.maxRequest];
	}
	
	public synchronized void putRequest(Request request) throws InterruptedException {
		while (count == maxRequest) {
			wait();
		}
		if (count == 0) {
			notifyAll();
		}
		count++;
		requestQueue[tail++] = request;
		tail  %= maxRequest;
	}
	
	public synchronized Request takeRequest() throws InterruptedException {
		while (count == 0) {
			wait();
		}
		if (count == maxRequest) {
			notifyAll();
		}
		try {
			count--;
			return requestQueue[head++];
		} finally {
			head %= maxRequest;
		}
	}
}
