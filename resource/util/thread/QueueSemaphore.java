package util.thread;

public class QueueSemaphore {
 
	private boolean isNotified = false;
	
	private String threadName;
	
	public QueueSemaphore(String threadName) {
		this.threadName = threadName;
	}
	
	public synchronized void doWait() throws InterruptedException {
		while (!isNotified) {
			wait();
		}
		isNotified = false;
	}
	
	public synchronized void doNotify() {
		isNotified = true;
		notify();
	}
	
	public String getThreadName() {
		return threadName;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this == obj;
	}
}
