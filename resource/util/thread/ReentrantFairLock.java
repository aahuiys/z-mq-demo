package util.thread;

import java.util.ArrayList;
import java.util.List;

public class ReentrantFairLock {

	private boolean isLocked = false;
	
	private Thread lockingThread = null;
	
	private List<QueueSemaphore> waitingThreads = new ArrayList<QueueSemaphore>();
	
	private int lockedCount = 0;
	
	public void lock() throws InterruptedException {
		QueueSemaphore queueSemaphore = new QueueSemaphore(Thread.currentThread().getName());
		boolean isLockedForThisThread  = true;
		synchronized (this) {
			if (lockingThread != Thread.currentThread()) {
				waitingThreads.add(queueSemaphore);
				System.out.println(queueSemaphore.getThreadName() + " Enqueue.");
			}
		}
		while (isLockedForThisThread) {
			synchronized (this) {
				isLockedForThisThread = isLocked && lockingThread != Thread.currentThread() || waitingThreads.size() > 0 && waitingThreads.get(0) != queueSemaphore && lockedCount == 0;
				if (!isLockedForThisThread) {
					isLocked = true;
					if (lockedCount++ == 0) {
						waitingThreads.remove(queueSemaphore);
						System.out.println(queueSemaphore.getThreadName() + " Locked.");
					}
					lockingThread = Thread.currentThread();
					return;
				}
			}
			try {
				queueSemaphore.doWait();
			} catch (InterruptedException e) {
				// TODO: handle exception
				synchronized (this) {
					waitingThreads.remove(queueSemaphore);
				}
				throw e;
			}
		}
	}
	
	public synchronized void unlock() {
		if (lockingThread != Thread.currentThread()) {
			throw new IllegalMonitorStateException("Calling thread has not locked this lock.");
		}
		if (--lockedCount == 0) {
			isLocked = false;
			lockingThread = null;
			if (waitingThreads.size() > 0) {
				waitingThreads.get(0).doNotify();
			}
		}
	}
}
