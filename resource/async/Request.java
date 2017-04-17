package async;

public abstract class Request {

	protected final Worker worker;
	
	protected final FutureResult futureResult;

	public Request(Worker worker, FutureResult futureResult) {
		this.worker = worker;
		this.futureResult = futureResult;
	}
	
	public abstract void execute();
}
