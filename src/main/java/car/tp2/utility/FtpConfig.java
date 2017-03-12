package car.tp2.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import car.tp2.ftp.FtpException;

/**
 * Classe r�cup�rant le param�trage du fichier de configuration (configuration.properties) 
 * situ� � la racine du projet.
 * 
 * Le fichier de configuration permet de connnaitre l'adrese et les num�ros de port pour se connecter au serveur FTP
 *
 * @author Lucas Moura de Oliveira
 *
 */
public class FtpConfig {
	
	protected String address;
	
	protected int commandPort;
	
	protected int dataPort;
	
	/**
	 * R�cup�re le fichier de configuration.
	 * 
	 * @throws FtpException si la r�cup�ration du fichier de configuration �choue (fichier introuvable ou mal form�)
	 */
	public FtpConfig() throws FtpException{
		this.loadProperties();
	}
	
	/**
	 * Charge les propri�t�s du fichier de configuration (Adresse, port de commande et port de donn�es)
	 * 
	 * @throws FtpException si la r�cup�ration d'un ou plusieurs param�tres �choue
	 */
	private void loadProperties() throws FtpException{
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
			throw new FtpException("Erreur lors de la r�cup�ration du fichier de configuration");
		}
	}

	/**
	 * @return l'adresse du serveur FTP
	 */
	public String getCommandAddress() {
		return this.address;
	}
	
	/**
	 * @return le num�ro du port de commande
	 */
	public int getCommandPort() {
		return this.commandPort;
	}
	
	/**
	 * @return le num�ro du port de donn�es
	 */
	public int getDataPort() {
		return this.dataPort;
	}
	
	/**
	 * Met � jour le num�ro du port de donn�es
	 * 
	 * @param port le nouveau num�ro de port
	 */
	public void setConfiguredDataPort(int port) {
		this.dataPort = port;
	}

}
