package async;

public class DataRequest extends Request {

	private final int count;
	
	private final String str;
	
	public DataRequest(Worker worker, FutureResult futureResult, int count, String str) {
		super(worker, futureResult);
		this.count = count;
		this.str = str;
	}

	@Override
	public void execute() {
		Result result = worker.request(count, str);
		futureResult.setResult(result);
		try {
			worker.callback(futureResult.getResult());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
