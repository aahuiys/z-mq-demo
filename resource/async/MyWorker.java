package async;

public class MyWorker implements Worker {

	@Override
	public Result request(int count, String str) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new RealResult(new String("Return Data: " + str + count));
	}

	@Override
	public void request(String str) {
		System.out.println("PrintString: " + str);	
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void callback(Object obj) {
		System.out.println(obj.toString());
	}
}
