package Dependencies;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class LogData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, ArrayList<HashMap<String, String>>> QUE_eventDB;
	private HashMap<String, ArrayList<HashMap<String, String>>> MTL_eventDB;
	private HashMap<String, ArrayList<HashMap<String, String>>> SHE_eventDB;
	
	HashMap<String, String> custDB;

	public HashMap<String, ArrayList<HashMap<String, String>>> getQUE_eventDB() {
		return QUE_eventDB;
	}

	public void setQUE_eventDB(HashMap<String, ArrayList<HashMap<String, String>>> qUE_eventDB) {
		QUE_eventDB = qUE_eventDB;
	}

	public HashMap<String, ArrayList<HashMap<String, String>>> getMTL_eventDB() {
		return MTL_eventDB;
	}

	public void setMTL_eventDB(HashMap<String, ArrayList<HashMap<String, String>>> mTL_eventDB) {
		MTL_eventDB = mTL_eventDB;
	}

	public HashMap<String, ArrayList<HashMap<String, String>>> getSHE_eventDB() {
		return SHE_eventDB;
	}

	public void setSHE_eventDB(HashMap<String, ArrayList<HashMap<String, String>>> sHE_eventDB) {
		SHE_eventDB = sHE_eventDB;
	}

	public HashMap<String, String> getCustDB() {
		return custDB;
	}

	public void setCustDB(HashMap<String, String> custDB) {
		this.custDB = custDB;
	}

	@Override
	public String toString() {
		return "LogData [QUE_eventDB=" + QUE_eventDB + ", MTL_eventDB=" + MTL_eventDB + ", SHE_eventDB=" + SHE_eventDB
				+ ", custDB=" + custDB + "]";
	}
}
