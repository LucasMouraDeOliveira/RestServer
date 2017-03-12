package car.tp2.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import car.tp2.ftp.FtpException;

/**
 * Classe récupérant le paramétrage du fichier de configuration (configuration.properties) 
 * situé à la racine du projet.
 * 
 * Le fichier de configuration permet de connnaitre l'adrese et les numéros de port pour se connecter au serveur FTP
 *
 * @author Lucas Moura de Oliveira
 *
 */
public class FtpConfig {
	
	protected String address;
	
	protected int commandPort;
	
	protected int dataPort;
	
	/**
	 * Récupère le fichier de configuration.
	 * 
	 * @throws FtpException si la récupération du fichier de configuration échoue (fichier introuvable ou mal formé)
	 */
	public FtpConfig() throws FtpException{
		this.loadProperties();
	}
	
	/**
	 * Charge les propriétés du fichier de configuration (Adresse, port de commande et port de données)
	 * 
	 * @throws FtpException si la récupération d'un ou plusieurs paramètres échoue
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
			throw new FtpException("Erreur lors de la récupération du fichier de configuration");
		}
	}

	/**
	 * @return l'adresse du serveur FTP
	 */
	public String getCommandAddress() {
		return this.address;
	}
	
	/**
	 * @return le numéro du port de commande
	 */
	public int getCommandPort() {
		return this.commandPort;
	}
	
	/**
	 * @return le numéro du port de données
	 */
	public int getDataPort() {
		return this.dataPort;
	}
	
	/**
	 * Met à jour le numéro du port de données
	 * 
	 * @param port le nouveau numéro de port
	 */
	public void setConfiguredDataPort(int port) {
		this.dataPort = port;
	}

}
