package async;

public interface Worker {

	public abstract Result request(int count, String str);
	
	public abstract void request(String str);
	
	public abstract void callback(Object obj);
}
