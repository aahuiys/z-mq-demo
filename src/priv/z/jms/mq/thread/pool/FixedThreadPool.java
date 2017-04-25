package priv.z.jms.mq.thread.pool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import priv.z.jms.mq.thread.queue.BlockingLinkedQueue;

public class FixedThreadPool {

	private final static Log logger = LogFactory.getLog(FixedThreadPool.class);
	
	private final int nThreads;
	
	private final int length;
	
	private final PoolWorker[] theads;
	
	private final BlockingLinkedQueue<Runnable> queue;
	
	public FixedThreadPool(int nThreads, int length) {
		this.nThreads = nThreads;
		this.length = length;
		theads = new PoolWorker[this.nThreads];
		queue = new BlockingLinkedQueue<Runnable>(this.length);
		for (int i = 0; i < this.nThreads; i++) {
			theads[i] = new PoolWorker("TestThread-[" + (i + 1) + "]");
			theads[i].start();
		}
		logger.info("The " + this.getClass().getSimpleName() + " has been created.");
	}
	
	public void execute(Runnable r) {
		try {
			queue.enqueue(r);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public class PoolWorker extends Thread {
		
		public PoolWorker(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			Runnable r;
			while (true) {
				try {
					r = queue.dequeue();
					r.run();
				} catch (InterruptedException e) {
					e.printStackTrace();
				// If we don't catch RuntimeException, the pool could leak threads
				} catch (RuntimeException e) {
					// You might want to log something here
					logger.error("Catch RuntimeException: " + e.getClass().getSimpleName(), e);
				}
			}
		}
	}
}
