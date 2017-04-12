package util.thread;

import java.util.LinkedList;

public class BlockingQueue {

	private int length = 10;
	
	private LinkedList<Runnable> queue = new LinkedList<Runnable>();
	
	public BlockingQueue(int length) {
		this.length = length;
	}
	
	public synchronized void enqueue (Runnable r) throws InterruptedException {
		while (queue.size() == length) {
			wait();
		}
		if(queue.size() == 0) {
			notifyAll();
		}
		queue.addLast(r);
	}
	
	public synchronized Runnable dequeue() throws InterruptedException {
		while (queue.size() == 0) {
			wait();
		}
		if(queue.size() == length) {
			notifyAll();
		}
		return queue.removeFirst();
	}
}
