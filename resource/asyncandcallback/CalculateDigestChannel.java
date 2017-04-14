package asyncandcallback;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.PrivilegedAction;

import sun.misc.Cleaner;

public class CalculateDigestChannel extends CalculateDigest {

	public CalculateDigestChannel() {
	}

	public CalculateDigestChannel(int buffSize) {
		super(buffSize);
	}

	@Override
	protected MessageDigest readOperation(FileInputStream fin, MessageDigest messageDigest) throws Exception {
		FileChannel ch = fin.getChannel();
		try {
			mapFile(messageDigest, ch);
		} finally {
			close(ch);
			close(fin);
		}
		return messageDigest;
	}
	
	private void mapFile(final MessageDigest messageDigest, FileChannel ch) throws Exception {
		long pos = 0;
		while (pos < ch.size()) {
			final MappedByteBuffer byteBuffer = getByteBuffer(ch, pos);
			new CleanOperation<MessageDigest>(byteBuffer) {

				@Override
				protected MessageDigest run() {
					messageDigest.update(byteBuffer);
					return messageDigest;
				}
			}.execute();
			pos += buffSize;
		}
	}
	
	private MappedByteBuffer getByteBuffer(FileChannel ch, long pos) throws IOException {
		if(pos + buffSize < ch.size()) return ch.map(FileChannel.MapMode.READ_ONLY, pos, buffSize);
		else return ch.map(FileChannel.MapMode.READ_ONLY, pos, ch.size() - pos);
	}
	
	private static abstract class Operation<T> {
		
		protected abstract T run() throws Exception;
		
		protected abstract void done() throws Exception ;
		
		protected  T execute() throws Exception {
			boolean thrown = false;
			try {
				return run();
			} catch (Exception e) {
				thrown = true;
				throw e;
			} finally {
				try {
					done();
				} catch (Exception e) {
					if (!thrown) throw e;
				}
			}
		}
	}
	
	private static abstract class CleanOperation<T> extends Operation<T> {
		
		private MappedByteBuffer byteBuffer;

		private CleanOperation(MappedByteBuffer byteBuffer) {
			this.byteBuffer = byteBuffer;
		}
		
		@Override
		protected void done() {
			AccessController.doPrivileged(new PrivilegedAction<Object>() {

				@Override
				public Object run() {
					try {
						Method getCleanerMethod = byteBuffer.getClass().getMethod("cleaner", new Class[0]);
						getCleanerMethod.setAccessible(true);
						Cleaner cleaner = (Cleaner) getCleanerMethod.invoke(byteBuffer, new Object[0]);
						cleaner.clean();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			});
		}
	}
}
