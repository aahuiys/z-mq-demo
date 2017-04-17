package test;

import async.WorkerFactory;

public class TestAsyncRequest {

	public static void main(String[] args) {
		final int count = 100;
		new Thread("Data1") {
			public void run() {
				for (int i = 0; i < count;) {
					WorkerFactory.createWorker().request(++i, Thread.currentThread().getName() + "-");
				}
			}
		}.start();
		new Thread("Void1") {
			public void run() {
				for (int i = 0; i < count;) {
					WorkerFactory.createWorker().request(Thread.currentThread().getName() + "-" + ++i);
				}
			}
		}.start();
		new Thread("Data2") {
			public void run() {
				for (int i = 0; i < count;) {
					WorkerFactory.createWorker().request(++i, Thread.currentThread().getName() + "-");
				}
			}
		}.start();
	}
}
