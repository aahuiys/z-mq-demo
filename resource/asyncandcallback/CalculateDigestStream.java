package asyncandcallback;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class CalculateDigestStream extends CalculateDigest {

	public CalculateDigestStream() {
	}

	public CalculateDigestStream(int buffSize) {
		super(buffSize);
	}

	@Override
	protected MessageDigest readOperation(FileInputStream fin, MessageDigest messageDigest) throws IOException {
		DigestInputStream din = null;
		try {
			din = new DigestInputStream(fin, messageDigest);
			byte[] buff = new byte[buffSize];
			while (din.read(buff) != -1);
		} finally {
			close(din);
			close(fin);
		}
		return messageDigest;
	}
}
