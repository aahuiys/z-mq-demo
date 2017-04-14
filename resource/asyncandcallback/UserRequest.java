package asyncandcallback;

import java.io.File;

public class UserRequest implements CallBack {

	private final static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	private CalculateDigest calculateDigest;
	
	private File file;
	
	private byte[] digest;
	
	private long time;
	
	public UserRequest(CalculateDigest calculateDigest, File file) {
		this.calculateDigest = calculateDigest;
		this.file = file;
	}

	public Thread execute() {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				time = System.currentTimeMillis();
				calculateDigest.calculate(file, UserRequest.this);
			}
		}, getMethod() + file.getName());
		thread.start();
		return thread;
	}

	@Override
	public void process(Object result) {
		this.digest = (byte[]) result;
		time = System.currentTimeMillis() - time;
		System.out.println(this);
	}
	
	@Override
	public String toString() {
		return new StringBuffer().append(time()).append(getMethod()).append(file.getName())
				.append("\n").append(bufferToHex(digest)).append("\n").toString();
	}
	
	private static String bufferToHex(byte[] digest) {
		if (digest != null) return bufferToHex(digest, 0, digest.length);
		return "no digest!";
	}
	
	private static String bufferToHex(byte[] digest, int begin, int end) {
		char[] str = new char[end * 2];
        int k = 0;
        for (int i = begin; i < begin + end; i++) {
            byte byte0 = digest[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
	}
	
	private String getMethod() {
		return getClassName(calculateDigest.getClass()).substring(getClassName(calculateDigest.getClass().getSuperclass()).length()) + "-";
	}
	
	private String getClassName(Class<?> clazz) {
		return clazz.getSimpleName();
	}
	
	private String time() {
		long min = time / (60 * 1000);
		long sec = time / 1000 - min * 60;
		StringBuffer sb = new StringBuffer();
		sb.append(time % 1000).append("]-")
			.insert(0, sec + ".")
			.insert(0, min + ":")
			.insert(0, "[");
		return sb.toString();
	}
}
