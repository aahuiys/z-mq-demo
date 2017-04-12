package priv.z.jms.mq.thread.queue;

import java.util.LinkedList;

public class BlockingLinkedQueue<T> {

	private int length = 10;
	
	private LinkedList<T> queue = new LinkedList<T>();
	
	public BlockingLinkedQueue(int length) {
		this.length = length;
	}
	
	public synchronized void enqueue (T obj) throws InterruptedException {
		while (queue.size() == length) {
			wait();
		}
		if(queue.size() == 0) {
			notifyAll();
		}
		queue.addLast(obj);
	}
	
	public synchronized T dequeue() throws InterruptedException {
		while (queue.size() == 0) {
			wait();
		}
		if(queue.size() == length) {
			notifyAll();
		}
		return queue.removeFirst();
	}
}
