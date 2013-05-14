package sf.bank.utilities.beans;

import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DataBaseStatus {

	private String status;
	
	private int port;
	
	private URI host;
	
	private List<String> databaseNames;

	
	public DataBaseStatus() {
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String value) {
		this.status = value;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public URI getHost() {
		return host;
	}

	public void setHost(URI host) {
		this.host = host;
	}

	public List<String> getDatabaseNames() {
		return databaseNames;
	}

	public void setDatabaseNames(List<String> databaseNames) {
		this.databaseNames = databaseNames;
	}

}
