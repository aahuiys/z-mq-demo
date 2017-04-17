package test;
import java.util.Random;

import async.AsyncWorker;


public class TestAsyncWorker {

	public static void main(String[] args) {
		AsyncWorker asyncWorker = new AsyncWorker("construct") {
			
			@Override
			public Object construct() {
				try {
					int wait = new Random().nextInt(3000);
					System.out.println(wait);
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return "Hellow World!";
			}
			
			@Override
			public void finished() {
				super.finished();
				Object object = this.get();
				System.out.println("Return is " + object);
			}
		};
		long startTime = System.currentTimeMillis();
		asyncWorker.start();
		String result = asyncWorker.get().toString();
		System.out.println("Return is " + result + ", time=" + (System.currentTimeMillis() - startTime));
	}
}
