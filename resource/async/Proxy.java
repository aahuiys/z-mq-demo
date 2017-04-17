package async;

public class Proxy implements Worker {

	private final Scheduler scheduler;
	
	private final Worker worker;

	public Proxy(Scheduler scheduler, Worker worker) {
		this.scheduler = scheduler;
		this.worker = worker;
	}

	@Override
	public Result request(int count, String str) {
		FutureResult futureResult = new FutureResult();
		scheduler.invoke(new DataRequest(worker, futureResult, count, str));
		return futureResult;
	}

	@Override
	public void request(String str) {
		scheduler.invoke(new VoidRequest(worker, str));
	}

	@Override
	public void callback(Object obj) {
		worker.callback(obj);
	}
}
