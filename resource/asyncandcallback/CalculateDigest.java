package asyncandcallback;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;

public abstract class CalculateDigest {

	protected int buffSize = 256 * 1024 * 1024;
	
	public CalculateDigest() {
	}

	public CalculateDigest(int buffSize) {
		if (buffSize != 0 && buffSize < this.buffSize) this.buffSize = buffSize;
	}

	public void calculate(File file, CallBack callBack) {
		callBack.process(getDigest(file));
	}
	
	private byte[] getDigest(File file) {
		MessageDigest messageDigest = getMessageDigest(file);
		if (messageDigest != null) return messageDigest.digest();
		return null;
	}
	
	private MessageDigest getMessageDigest(File file) {
		try {
			return readOperation(getFileIn(file), MessageDigest.getInstance("MD5"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected abstract MessageDigest readOperation(FileInputStream fin, MessageDigest messageDigest) throws Exception;
	
	private FileInputStream getFileIn(File file) throws FileNotFoundException {
		return new FileInputStream(file);
	}
	
	protected void close(Closeable closeable) throws IOException {
		if (closeable != null) closeable.close();
	}
}
