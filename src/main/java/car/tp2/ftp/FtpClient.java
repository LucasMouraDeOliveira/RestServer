package car.tp2.ftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import car.tp2.ftp.socket.FtpCommandSocket;
import car.tp2.ftp.socket.FtpDataSocket;
import car.tp2.utility.FtpConfig;
import car.tp2.utility.User;
import car.tp2.utility.UserManagment;

/**
 * Classe qui définit les méthodes de communication avec le serveur FTP
 * 
 * 
 * @author Lucas Moura de Oliveira
 */
public class FtpClient {
	
	protected FtpCommandSocket commandSocket;
	
	protected boolean open, connected;

	protected FtpFactory ftpFactory;
	
	protected FtpConfig ftpConfig;

	public FtpClient(FtpCommandSocket commandSocket, FtpFactory ftpFactory, FtpConfig ftpConfig) {
		this.commandSocket = commandSocket;
		this.ftpFactory = ftpFactory;
		this.ftpConfig = ftpConfig;
	}
	
	/**
	 * Initialise le client FTP. Récupère l'adresse du serveur FTP dans le fichier de configuration
	 * et tente de se connecter sur le port de commande.
	 * 
	 * @throws IOException
	 * @throws FtpException en cas d'erreur de communication avec le serveur FTP
	 */
	public FtpClient(String token) throws IOException, FtpException{
		User user = UserManagment.getInstance().getUser(token);
		this.ftpFactory = new FtpFactory();
		this.commandSocket = new FtpCommandSocket(ftpFactory);
		this.ftpConfig = new FtpConfig();
		this.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getCommandPort());
		this.connect(user.getNom(), user.getMdp());
		this.setPassive();
	}

	/**
	 * Connecte le client au serveur FTP
	 * 
	 * @param address l'adresse du serveur FTP
	 * @param port le numéro de port de la socket de commande du serveur FTP
	 * @throws FtpException si la tentative de connexion échoue
	 */
	public void openSocket(String address, int port) throws FtpException {
		if(this.commandSocket.openSocket(address, port)){
			//Lecture de la première ligne (message de bienvenue)
			try {
				String reply = this.commandSocket.readLine();
				if(reply != null && reply.startsWith("220")){
					this.open = true;
				}
			} catch (IOException e) {
				throw new FtpException("Erreur lors de la lecture du message de bienvenue");
			}
		}
	}

	/**
	 * @return vrai si la connexion au serveur FTP sur la socket de commande est active.
	 */
	public boolean isSocketOpen() {
		return this.open;
	}
	
	/**
	 * @return vrai si l'utilisateur est connecté et authentifié sur le serveur FTP
	 */
	public boolean isConnected() {
		return this.open && this.connected;
	}
	
	/**
	 * Connecte l'utilisateur au serveur FTP. Si la connexion réussie, l'état du client passe à 'connecté'.
	 * Si la connexion échoue, l'état du client reste à 'déconnecté'
	 * 
	 * @param user le login de l'utilisateur, sous forme de chaine de caractères
	 * @param password le mot de passe non crypté de l'utilisateur, sous forme de chaine de caractères
	 * 
	 * @throws FTPException si l'envoi de la requête ou la réception de la réponse échoue
	 */
	public void connect(String user, String password) throws FtpException {
		//Si la socket n'est pas ouverte, on ne peut pas se connecter -> erreur
		if(!this.isSocketOpen()){
			throw new FtpException("Pas de connexion active au serveur FTP");
		}
		// Sinon on envoie la commande USER
		FtpReply userCommandReply = this.sendUserCommand(user);
		if(userCommandReply.isOk("331")){
			// Si la commande réussie, on envoie la commande PASS
			FtpReply passwordCommandReply = this.sendPasswordCommand(password);
			if(passwordCommandReply.isOk("230")){
				//Si la commande réussie, on passe en état connecté
				this.connected = true;
			} else {
				throw new FtpException("Connexion refusée : mot de passe incorrect");
			}
		} else {
			throw new FtpException("Connexion refusée : erreur lors de l'envoi du nom d'utilisateur");
		}
	}

	/**
	 * Télécharge un fichier depuis le serveur FTP
	 * 
	 * @param path le chemin d'accès au fichier sur le serveur
	 * 
	 * @return le fichier
	 * @throws FtpException si la récupération du fichier échoue pour une quelconque raison
	 */
	public File download(String path) throws FtpException{
		if(!this.isConnected()){
			throw new FtpException("Commande refusée : vous n'êtes pas connecté");
		}
		if(this.getDataPort() == 0){
			throw new FtpException("Le mode passif n'est pas actif");
		}
		//Création de la socket de données
		File file = null;
		try {
			//Envoi de la commande RETR
			this.commandSocket.send(this.ftpFactory.buildRetrCommand(path));
			//Connexion de la socket
			FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
			dataSocket.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getDataPort());
			//Reception des données sur la socket de données
			file = dataSocket.readFileFromReader(path.split("/")[path.length()-1]);
			dataSocket.closeReaders();
		} catch (IOException e) {
			throw new FtpException("Erreur interne");
		}
		
		return file;
	}
	
	/**
	 * Upload un fichier sur le serveur FTP
	 * 
	 * @param inputStream le flux de données représentant le fichier
	 * @param fileName le nom du fichier sur le serveur FTP
	 * @param path le chemin d'accès au fichier sur le serveur FTP
	 * @throws FtpException si l'upload de fichier échoue pour une quelconque raison
	 */
	public void upload(InputStream inputStream, String fileName, String path) throws FtpException{
		if(!this.isConnected()) {
			throw new FtpException("Commande refusée : vous n'êtes pas connecté");
		}
		if(this.getDataPort() == 0){
			throw new FtpException("Le mode passif n'est pas actif");
		}
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildCwdRequest(path));
		if(reply.isOk("250")){
			reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildStorRequest(fileName));
			if(reply.isOk("150")){
				try {
					FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
					dataSocket.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getDataPort());
					dataSocket.writeDataInWriter(inputStream);
					dataSocket.closeReaders();
				} catch (IOException e) {
					throw new FtpException("Erreur interne");
				}
			} else {
				throw new FtpException("Le fichier existe déjà");
			}
		} else {
			throw new FtpException("Chemin d'accès invalide");
		}
	}
	
	/**
	 * Liste les fichiers d'un répertoire sur le serveur FTP
	 * 
	 * @param path le chemin d'accès au répertoire
	 * 
	 * @return une liste de String contenant diverses informations (nom, date de modif, type, ...)
	 * sur les fichiers contenus dans le dossier
	 * 
	 * @throws FtpException si la récupération des fichiers échoue pour une quelconque raison
	 */
	public List<String> list(String path) throws FtpException {
		if(!this.isConnected()){
			throw new FtpException("Commande refusée : vous n'êtes pas connecté");
		}
		if(this.getDataPort() == 0){
			throw new FtpException("Le mode passif n'est pas actif");
		}
		try {
			FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildCwdRequest(path));
			if(reply.isOk("250")){
				this.commandSocket.send(this.ftpFactory.buildListRequest(path));
				List<String> files = new ArrayList<String>();
				FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
				dataSocket.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getDataPort());
				String line = null;
				while((line = dataSocket.readLine()) != null){
					files.add(line);
				}
				return files;
			} else {
				throw new FtpException("Erreur : accès au dossier impossible");
			}
		} catch (IOException e) {
			throw new FtpException("Erreur lors de la lecture de la socket de données");
		}
	}
	
	/**
	 * Envoie une commande USER au serveur FTP
	 * 
	 * @param user le login de l'utilisateur
	 * 
	 * @return une réponse FTP
	 * 
	 * @throws FtpException si l'envoi de la commande ou la récupération de la réponse échoue
	 */
	public FtpReply sendUserCommand(String user) throws FtpException {
		return this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildUserRequest(user));
	}
	
	/**
	 * Envoie une commande PASS au serveur FTP
	 * 
	 * @param password le mot de passe de l'utilisateur
	 * 
	 * @return une réponse FTP
	 * 
	 * @throws FtpException si l'envoi de la commande ou la récupération de la réponse échoue
	 */
	public FtpReply sendPasswordCommand(String password) throws FtpException {
		return this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildPasswordRequest(password));
	}
	
	/**
	 * Demande au serveur de passer la connexion en mode passif
	 * 
	 * @throws FtpException si le passage en mode passif échoue pour une quelconque raison
	 */
	public void setPassive() throws FtpException {
		if(!this.isConnected()){
			throw new FtpException("Commande refusée : vous n'êtes pas connecté");
		}
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildSetPassiveRequest());
		if(reply.isOk("229")){
			String[] split = reply.getMessage().split("\\|");
			try{
				this.setDataPort(Integer.valueOf(split[3]));
			} catch(NumberFormatException e){
				throw new FtpException("Erreur de format du port lors du passage en mode passif");
			}
		} else {
			throw new FtpException("Echec du passage en mode passif");
		}
	}
	
	/**
	 * Met à jour le numéro de port pour la connexion au serveur FTP sur la socket de données
	 * 
	 * @param dataPort le nouveau numéro de port
	 */
	public void setDataPort(int dataPort) {
		this.getConfig().setConfiguredDataPort(dataPort);
	}

	/**
	 * @return le numéro de port pour la connexion au serveur FTP sur la socket de données
	 */
	public int getDataPort() {
		return this.getConfig().getDataPort();
	}
	
	/**
	 * @return la configuration FTP
	 */
	public FtpConfig getConfig() {
		return this.ftpConfig;
	}

	/**
	 * Supprime un fichier sur le serveur FTP
	 * 
	 * @param path le chemin d'accès au fichier
	 * 
	 * @throws FtpException si la suppression échoue pour une quelconque raison
	 */
	public void delete(String path) throws FtpException {
		if(!this.isConnected()){
			throw new FtpException("Commande refusée : vous n'êtes pas connecté");
		}
		//Envoi de la commande RMD
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildRmdCommand(path));
		if(!reply.isOk("200")){
			throw new FtpException("Echec de la suppression");
		}
	}

	/**
	 * Renomme un fichier sur le serveur FTP
	 * 
	 * @param from le nom d'origine du fichier
	 * @param to le nouveau nom du fichier
	 * 
	 * @throws FtpException si le renommage échoue pour une quelconque raison
	 */
	public void rename(String from, String to) throws FtpException {
		System.out.println("Rename "+from+" to "+to);
		if(!this.isConnected())
			throw new FtpException("Commande refusée : vous n'êtes pas connecté");
		//Envoi de la commande RNFR
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildRnfrCommand(from));
		if(!reply.isOk("350"))
			throw new FtpException("Echec du renommage");
		reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildRntoCommand(to));
		if(!reply.isOk("200"))
			throw new FtpException("Echec du renommage");
	}

	/**
	 * Crée un dossier sur le serveur FTP
	 * 
	 * @param path le chemin d'accès au fichier
	 * 
	 * @throws FtpException si la création de dossier échoue pour une quelconque raison
	 */
	public void mkdir(String path) throws FtpException {
		if(!this.isConnected())
			throw new FtpException("Commande refusée : vous n'êtes pas connecté");
		//Envoi de la commande RNFR
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildMkdCommand(path));
		if(!reply.isOk("200"))
			throw new FtpException("Echec de la creation");
	}

	/**
	 * Ferme la connexion au serveur FTP
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		this.commandSocket.send(this.ftpFactory.buildQuitCommand());
		this.commandSocket.close();
	}

}
