package async;

public class VoidRequest extends Request {

	private final String str;
	
	public VoidRequest(Worker worker, String str) {
		super(worker, null);
		this.str = str;
	}

	@Override
	public void execute() {
		worker.request(str);
	}
}
