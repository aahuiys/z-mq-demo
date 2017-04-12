package util.connection;

public class Environments {

	private final String LOG_HOME;
	
	private final String SM_SERVER_IP;
	
	private final String SM_SERVER_PORT;
	
	private final String SM_SERVER_PATH;
	
	private final String SM_SERVER_USER;
	
	private final String SM_SERVER_PWD;
	
	private final String MF_SERVER_IP;
	
	private final String MF_DB_NAME;
	
	private final String MF_DB_USER;
	
	private final String MF_DB_PWD;
	
	private final String LOG_MAXECOUNT;

	public Environments(String lOG_HOME, String sM_SERVER_IP, String sM_SERVER_PORT, String sM_SERVER_PATH,
			String sM_SERVER_USER, String sM_SERVER_PWD, String mF_SERVER_IP, String mF_DB_NAME, String mF_DB_USER,
			String mF_DB_PWD, String lOG_MAXECOUNT) {
		LOG_HOME = lOG_HOME;
		SM_SERVER_IP = sM_SERVER_IP;
		SM_SERVER_PORT = sM_SERVER_PORT;
		SM_SERVER_PATH = sM_SERVER_PATH;
		SM_SERVER_USER = sM_SERVER_USER;
		SM_SERVER_PWD = sM_SERVER_PWD;
		MF_SERVER_IP = mF_SERVER_IP;
		MF_DB_NAME = mF_DB_NAME;
		MF_DB_USER = mF_DB_USER;
		MF_DB_PWD = mF_DB_PWD;
		LOG_MAXECOUNT = lOG_MAXECOUNT;
	}

	public String getLOG_HOME() {
		return LOG_HOME;
	}

	public String getSM_SERVER_IP() {
		return SM_SERVER_IP;
	}

	public String getSM_SERVER_PORT() {
		return SM_SERVER_PORT;
	}

	public String getSM_SERVER_PATH() {
		return SM_SERVER_PATH;
	}

	public String getSM_SERVER_USER() {
		return SM_SERVER_USER;
	}

	public String getSM_SERVER_PWD() {
		return SM_SERVER_PWD;
	}

	public String getMF_SERVER_IP() {
		return MF_SERVER_IP;
	}

	public String getMF_DB_NAME() {
		return MF_DB_NAME;
	}

	public String getMF_DB_USER() {
		return MF_DB_USER;
	}

	public String getMF_DB_PWD() {
		return MF_DB_PWD;
	}

	public String getLOG_MAXECOUNT() {
		return LOG_MAXECOUNT;
	}
}
