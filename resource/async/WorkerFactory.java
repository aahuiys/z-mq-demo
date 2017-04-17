package async;

public class WorkerFactory {

	private static final Worker WORKER = new MyWorker();
	
	private static final ActivationQueue QUEUE = new ActivationQueue(9);
	
	private static final Scheduler SCHEDULER = new Scheduler(QUEUE, 7);
	
	private static final Worker PROXY_WORKER = new Proxy(SCHEDULER, WORKER);

	public static Worker createWorker() {
		return PROXY_WORKER;
	}
}
