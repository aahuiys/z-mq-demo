package async;

public class FutureResult extends Result {

	private Result result;
	
	private boolean completed = false;
	
	public synchronized void setResult(Result result) {
		this.result = result;
		completed = true;
		notify();
	}

	@Override
	public synchronized Object getResult() throws InterruptedException {
		while (!completed) {
			wait();
		}
		return result.getResult();
	}
}
