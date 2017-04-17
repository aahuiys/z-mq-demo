package async;

public class Scheduler {

	private final ActivationQueue queue;

	public Scheduler(ActivationQueue queue, int num) {
		this.queue = queue;
		for (int i = 0; i < num;) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						try {
							Request request = Scheduler.this.queue.takeRequest();
							request.execute();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}, "Scheduler-" + ++i + "/" + num).start();
		}
	}
	
	public void invoke(Request request) {
		try {
			queue.putRequest(request);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
