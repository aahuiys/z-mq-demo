package async;

public class RealResult extends Result {

	private final Object result;
	
	public RealResult(Object result) {
		this.result = result;
	}

	@Override
	public Object getResult() {
		return result;
	}
}
