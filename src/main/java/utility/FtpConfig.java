package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Classe récupérant le paramétrage du fichier de configuration (configuration.properties) 
 * situé à la racine du projet.
 * 
 * Le fichier de configuration permet de connnaitre l'adresse et les numéros de port pour se connecter au serveur FTP
 *
 * @author Lucas Moura de Oliveira
 *
 */
public class FtpConfig {
	
	protected String address;
	
	protected int commandPort;
	
	protected int dataPort;
	
	protected boolean loadSuccess;
	
	/**
	 * Récupère le fichier de configuration.
	 */
	public FtpConfig() {
		this.loadSuccess = this.loadProperties();
	}
	
	/**
	 * Charge les propriétés du fichier de configuration (Adresse, port de commande et port de données)
	 * 
	 * @return vrai si le chargement du fichier de configuration a réussit, faux sinon
	 */
	private boolean loadProperties() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File("configuration.properties")));
			System.out.println("Fichier de configuration chargé avec succès");
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
			System.out.println("Erreur lors du chargement du fichier de configuration");
			return false;
		}
		return true;
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
	
	/**
	 * @return vrai si le fichier de configuration a été chargé avec succès
	 */
	public boolean loadSucces() {
		return this.loadSucces();
	}

}
