package priv.z.jms.mq.thread.pool;

import priv.z.jms.mq.thread.queue.BlockingLinkedQueue;

public class FixedThreadPool {

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
	}
	
	public void execute(Runnable r) {
		try {
			queue.enqueue(r);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class PoolWorker extends Thread {
		
		public PoolWorker(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Runnable r;
			while (true) {
				try {
					r = queue.dequeue();
					r.run();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				// If we don't catch RuntimeException, the pool could leak threads
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					// You might want to log something here
					System.out.println("Catch RuntimeException: " + e.getClass().getSimpleName());
					e.printStackTrace();
				}
			}
		}
	}
}
