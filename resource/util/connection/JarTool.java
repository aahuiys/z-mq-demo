package util.connection;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class JarTool {

	private static File getFile() {
		String path = JarTool.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		try {
			path = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		return new File(path);
	}
	
	public static String getJarPath() {
		File file = getFile();
		if(file == null) return null;
		return file.getAbsolutePath();
	}
	
	public static String getJarDir() {
		String dir = getJarPath();
		if(dir == null) return null;
		if(dir.endsWith(".jar")) {
			return new File(dir).getParent();
		}
		return dir;
	}
	
	public static String getJarName() {
		File file = getFile();
		if(file == null) return null;
		return file.getName();
	}
}
