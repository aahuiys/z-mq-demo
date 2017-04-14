package test;

import java.io.File;

import asyncandcallback.CalculateDigest;
import asyncandcallback.CalculateDigestChannel;
import asyncandcallback.CalculateDigestStream;
import asyncandcallback.UserRequest;

public class TestCallBack {

	public static void main(String[] args) throws InterruptedException {
		CalculateDigest calculateDigest = new CalculateDigestStream();
		File path = new File("D:\\");
		File[] files = path.isDirectory() ? path.listFiles() : new File[] {path};
		for (File file : files) if (file.isFile()) new UserRequest(calculateDigest, file).execute().join();
		calculateDigest = new CalculateDigestChannel();
		for (File file : files) if (file.isFile()) new UserRequest(calculateDigest, file).execute().join();
	}
}
