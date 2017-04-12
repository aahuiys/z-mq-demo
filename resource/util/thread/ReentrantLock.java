package util.thread;

public class ReentrantLock {

	private boolean isLocked = false;
	
	private Thread lockingThread = null;
	
	private int lockedCount = 0;
	
	public synchronized void lock() throws InterruptedException {
		while (isLocked && lockingThread != Thread.currentThread()) {
			wait();
		}
		isLocked = true;
		if (lockedCount++ == 0) {
			System.out.println(Thread.currentThread().getName() + " Locked.");
		}
		lockingThread = Thread.currentThread();
	}
	
	public synchronized void unlock() {
		if (lockingThread != Thread.currentThread()) {
			throw new IllegalMonitorStateException("Calling thread has not locked this lock.");
		}
		if (--lockedCount == 0) {
			isLocked = false;
			lockingThread = null;
			notify();
		}
	}
}
