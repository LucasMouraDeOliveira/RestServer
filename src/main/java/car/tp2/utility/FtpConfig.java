package car.tp2.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FtpConfig {
	
	protected String address;
	
	protected int commandPort;
	
	protected int dataPort;
	
	public FtpConfig() {
		this.loadProperties();
	}
	
	private void loadProperties() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File("configuration.properties")));
			this.address = properties.getProperty("FTP_HOST");
			String propertyCommandPort = properties.getProperty("FTP_COMMAND_PORT");
			if(propertyCommandPort != null){
				this.commandPort = Integer.valueOf(propertyCommandPort);
			}
			String propertyDataPort = properties.getProperty("FTP_DATA_PORT");
			if(propertyDataPort != null){
				this.dataPort = Integer.valueOf(propertyDataPort);
			}
		} catch (IOException e) {
			//TODO gestion d'erreur
		}
	}

	public String getCommandAddress() {
		return this.address;
	}
	
	public int getCommandPort() {
		return this.commandPort;
	}
	
	public int getDataPort() {
		return this.dataPort;
	}
	
	public void setConfiguredDataPort(int port) {
		this.dataPort = port;
	}

}
