package util.connection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SetEnv {

	private final static SetEnv SET_ENV = new SetEnv();
	
	private Environments environments = null;
	
	private SetEnv() {
		Properties env = new Properties();
		String path = JarTool.getJarDir();
		InputStream is = null;
		try {
			is = new FileInputStream(path + "\\env.properties");
			env.load(is);
			environments = new Environments(env.getProperty("LOG_HOME"), env.getProperty("SM_SERVER_IP"), 
					env.getProperty("SM_SERVER_PORT"), env.getProperty("SM_SERVER_PATH"), 
					env.getProperty("SM_SERVER_USER"), env.getProperty("SM_SERVER_PWD"), 
					env.getProperty("MF_SERVER_IP"), env.getProperty("MF_DB_NAME"), 
					env.getProperty("MF_DB_USER"), env.getProperty("MF_DB_PWD"), env.getProperty("LOG_MAXECOUNT"));
		} catch (FileNotFoundException e) {
			throw new ProgramRunException("Configuration file not found.", e);
		} catch (IOException e) {
			throw new ProgramRunException("Failed to load configuration file.", e);
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					System.err.println("InputStream close failed!");
				}
			}
		}
	}
	
	public static SetEnv getInstance() {
		return SET_ENV;
	}

	public Environments getEnvironments() {
		return environments;
	}
}
