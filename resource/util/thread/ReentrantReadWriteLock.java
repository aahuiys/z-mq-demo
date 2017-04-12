package util.thread;

import java.util.HashMap;
import java.util.Map;

public class ReentrantReadWriteLock {
	
	private Map<Thread, Integer> readingThreads = new HashMap<Thread, Integer>();
	
	private int writeAccesses = 0;
	
	private int writeRequests = 0;
	
	private Thread writingThread = null;
	
	public synchronized void lockRead() throws InterruptedException {
		Thread callingThread = Thread.currentThread();
		while (!canGrantReadAccess(callingThread)) {
			wait();
		}
		readingThreads.put(callingThread, getReadAccessCount(callingThread) + 1);
	}
	
	public synchronized void unlockRead() {
		Thread callingThread = Thread.currentThread();
		if (!isReader(callingThread)) {
			throw new IllegalMonitorStateException("Calling thread does not hold a read lock on this ReentrantReadWriteLock.");
		}
		int accessCount = getReadAccessCount(callingThread);
		if (--accessCount == 0) {
			readingThreads.remove(callingThread);
			notifyAll();
		} else {
			readingThreads.put(callingThread, accessCount);
		}
	}
	
	public synchronized void lockWrite() throws InterruptedException {
		writeRequests++;
		Thread callingThread = Thread.currentThread();
		while (!canGrantWriteAccess(callingThread)) {
			wait();
		}
		writeRequests--;
		writeAccesses++;
		writingThread = callingThread;
	}
	
	public synchronized void unlockWrite() {
		if (!isWriter(Thread.currentThread())) {
			throw new IllegalMonitorStateException("Calling thread does not hold the write lock on this ReentrantReadWriteLock.");
		}
		if (--writeAccesses == 0) {
			writingThread = null;
		}
		notifyAll();
	}
	
	private int getReadAccessCount(Thread callingThread) {
		Integer accessCount = readingThreads.get(callingThread);
		if (accessCount == null) return 0;
		return accessCount.intValue();
	}
	
	private boolean canGrantReadAccess(Thread callingThread) {
		if (isWriter(callingThread)) return true;
		if (hasWriter()) return false;
		if (isReader(callingThread)) return true;
		if (hasWriteRequests()) return false;
		return true;
	}
	
	private boolean canGrantWriteAccess(Thread callingThread) {
		if (isOnlyReader(callingThread)) return true;
		if (hasReaders()) return false;
		if (!hasWriter()) return true;
		if (!isWriter(callingThread)) return false;
		return true;
	}
	
	private boolean hasReaders() {
		return readingThreads.size() > 0;
	}
	
	private boolean isReader(Thread callingThread) {
		return readingThreads.get(callingThread) != null;
	}
	
	private boolean isOnlyReader(Thread callingThread) {
		return readingThreads.size() == 1 && isReader(callingThread);
	}
	
	private boolean hasWriter() {
		return writingThread != null;
	}
	
	private boolean isWriter(Thread callingThread) {
		return writingThread == callingThread;
	}
	
	private boolean hasWriteRequests() {
		return writeRequests > 0;
	}
}
