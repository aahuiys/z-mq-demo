package priv.z.jms.mq.thread;

import priv.z.jms.mq.thread.lock.ReentrantFairLock;
import priv.z.jms.mq.thread.pool.FixedThreadPool;

public class TestLock implements Runnable {

	private ReentrantFairLock lock = new ReentrantFairLock();

	@Override
	public void run() {
		// TODO Auto-generated method stub
		doLockAndUnlock(3, "Run: " + Thread.currentThread().getName());
	}
	
	public static void main(String[] args) {
		int n = 1000;
		FixedThreadPool fixedThreadPool = new FixedThreadPool(4, 30);
		TestLock testLock = new TestLock();
		for (int i = 0; i < n; i++) {
			fixedThreadPool.execute(testLock);
		}
	}
	
	private void doLockAndUnlock(final int num, String print) {
		try {
			lock.lock();
			print += "I";
			System.out.println(print);
			if(num != 1) {
				doLockAndUnlock(num - 1, print);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
