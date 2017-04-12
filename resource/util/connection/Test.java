package util.connection;

import java.io.File;

public class Test {

	public static void main(String[] args) {
		String path = JarTool.getJarPath();
		System.out.println(path);
		File file = new File(path, "test.txt");
		path = file.getParent();
		System.out.println(path);
	}
}
