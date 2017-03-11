package car.tp2.ftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import car.tp2.ftp.socket.FtpCommandSocket;
import car.tp2.ftp.socket.FtpDataSocket;

public class FtpClient {
	
	protected FtpCommandSocket commandSocket;
	
	protected boolean open, connected;
	
	protected int commandPort, dataPort;

	protected FtpFactory ftpFactory;

	public FtpClient(FtpCommandSocket commandSocket, FtpFactory ftpFactory) {
		this.commandSocket = commandSocket;
		this.ftpFactory = ftpFactory;
		this.commandPort = 0;
	}

	public void openSocket(String commandAddress, int commandPort) throws FtpException {
		if(this.commandSocket.openSocket(commandAddress, commandPort)){
			//Lecture de la première ligne (message de bienvenue)
			try {
				String reply = this.commandSocket.readLine();
				if(reply != null && reply.startsWith("220")){
					this.open = true;
					this.commandPort = commandPort;
				}
			} catch (IOException e) {
				throw new FtpException("Erreur lors de la lecture du message de bienvenue");
			}
		}
	}

	public boolean isSocketOpen() {
		return this.open;
	}
	
	public boolean isConnected() {
		return this.open && this.connected;
	}

	public int getCommandPort() {
		return this.commandPort;
	}
	
	/**
	 * Connecte l'utilisateur au serveur FTP. Si la connexion réussie, l'état du client passe à 'connecté'.
	 * Si la connexion échoue, l'état du client reste à 'déconnecté'
	 * 
	 * @param user le login de l'utilisateur, sous forme de chaine de caractères
	 * @param password le mot de passe non crypté de l'utilisateur, sous forme de chaine de caractères
	 * 
	 * @throws IOException si l'envoi de la requête ou la réception de la réponse échoue
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
			dataSocket.openSocket("localhost", this.dataPort);
			//Reception des données sur la socket de données
			file = dataSocket.readDataInReader(path.replace("/", "_"));
			dataSocket.closeReaders();
		} catch (IOException e) {
			throw new FtpException("Erreur interne");
		}
		
		return file;
	}
	
	public void upload(InputStream inputStream, String fileName) throws FtpException{
		if(!this.isConnected()) {
			throw new FtpException("Commande refusée : vous n'êtes pas connecté");
		}
		if(this.getDataPort() == 0){
			throw new FtpException("Le mode passif n'est pas actif");
		}
		this.commandSocket.send(this.ftpFactory.buildStorRequest(fileName));
		try {
			FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
			dataSocket.openSocket("localhost", this.dataPort);
			dataSocket.writeDataInWriter(inputStream);
			dataSocket.closeReaders();
		} catch (IOException e) {
			throw new FtpException("Erreur interne");
		}
	}
	
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
				dataSocket.openSocket("localhost", this.dataPort);
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
	
	public FtpReply sendUserCommand(String user) throws FtpException {
		return this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildUserRequest(user));
	}
	
	public FtpReply sendPasswordCommand(String password) throws FtpException {
		return this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildPasswordRequest(password));
	}
	
	public void setPassive() throws FtpException {
		if(!this.isConnected()){
			throw new FtpException("Commande refusée : vous n'êtes pas connecté");
		}
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildSetPassiveRequest());
		if(!reply.isOk("200")){
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
	
	public void setDataPort(int dataPort) {
		this.dataPort = dataPort;
	}

	public int getDataPort() {
		return this.dataPort;
	}

}
