package priv.z.jms.mq.thread.semaphore;

public class HandleSemaphore {

	private long timeMillis = 0;
	
	private boolean isWait = false;
	
	private boolean isTimeout = false;

	public HandleSemaphore(long timeMillis) {
		this.timeMillis = timeMillis;
	}
	
	public synchronized void take() throws InterruptedException {
		isWait = true;
		notify();
		wait(timeMillis);
		isTimeout = true;
	}
	
	public synchronized boolean release() throws InterruptedException {
		while (!isWait) {
			wait();
		}
		notify();
		return isTimeout;
	}
}
