package ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import factory.FtpFactory;
import ftp.socket.FtpCommandSocket;
import ftp.socket.FtpDataSocket;
import user.User;
import utility.FtpConfig;

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

	/**
	 * Initialise le client FTP a partir d'une socket, d'une factory et d'une
	 * configuration.
	 * 
	 * @param commandSocket
	 *            un objet permettant la commmunication avec le serveur FTP via
	 *            sa socket de commande
	 * @param ftpFactory
	 *            factory de requetes/réponses
	 * @param ftpConfig
	 *            objet contenant la configuration du serveur FTP (adresse et
	 *            ports)
	 */
	public FtpClient(FtpCommandSocket commandSocket, FtpFactory ftpFactory, FtpConfig ftpConfig) {
		this.commandSocket = commandSocket;
		this.ftpFactory = ftpFactory;
		this.ftpConfig = ftpConfig;
	}

	/**
	 * Initialise le client FTP. Récupère l'adresse du serveur FTP dans le
	 * fichier de configuration et tente de se connecter sur le port de
	 * commande.
	 * 
	 * @param user les informations de l'utilisateur
	 */
	public FtpClient(User user) {
		this.ftpFactory = new FtpFactory();
		this.commandSocket = new FtpCommandSocket(ftpFactory);
		this.ftpConfig = new FtpConfig();
		if (this.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getCommandPort())) {
			if (this.connect(user.getName(), user.getPassword())) {
				this.setPassive();
			}
		}
	}

	/**
	 * Tente d'ouvrir la connexion avec le serveur FTP sur un port et renvoie
	 * vrai si la connexion réussit
	 * 
	 * @param address
	 *            l'adresse du serveur
	 * @param port
	 *            le port de connexion
	 * @return vrai si la connexion a abouti, faux sinon
	 */
	public boolean openSocket(String address, int port) {
		if (this.commandSocket.openSocket(address, port)) {
			// Lecture de la premi�re ligne (message de bienvenue)
			try {
				String reply = this.commandSocket.readLine();
				if (reply != null && reply.startsWith("220")) {
					this.open = true;
					return true;
				}
			} catch (IOException e) {
			}
		}
		return false;
	}

	/**
	 * @return vrai si la connexion au serveur FTP sur la socket de commande est
	 *         active.
	 */
	public boolean isSocketOpen() {
		return this.open;
	}

	/**
	 * @return vrai si l'utilisateur est connecté et authentifié sur le serveur
	 *         FTP
	 */
	public boolean isConnected() {
		return this.open && this.connected;
	}

	/**
	 * Connecte l'utilisateur au serveur FTP. Si la connexion réussie, l'état du
	 * client passe à 'connecté'. Si la connexion échoue, l'état du client reste
	 * à 'déconnecté'
	 * 
	 * @param user
	 *            le login de l'utilisateur, sous forme de chaine de caractères
	 * @param password
	 *            le mot de passe non crypté de l'utilisateur, sous forme de
	 *            chaine de caractères
	 * 
	 * @return vrai si l'authentification a réussi, faux sinon
	 */
	public boolean connect(String user, String password) {
		// Si la socket n'est pas ouverte, on ne peut pas se connecter -> erreur
		if (!this.isSocketOpen()) {
			return false;
		}
		// Sinon on envoie la commande USER
		FtpReply userCommandReply = this.sendUserCommand(user);
		if (userCommandReply.isOk("331")) {
			// Si la commande r�ussie, on envoie la commande PASS
			FtpReply passwordCommandReply = this.sendPasswordCommand(password);
			if (passwordCommandReply.isOk("230")) {
				// Si la commande r�ussie, on passe en �tat connect�
				this.connected = true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Télécharge un fichier depuis le serveur FTP
	 * 
	 * @param path
	 *            le chemin d'accès au fichier sur le serveur
	 * 
	 * @return le fichier si le téléchargement a réussi, faux sinon
	 */
	public File download(String path) {
		if (!this.isConnected()) {
			return null;
		}
		if (this.getDataPort() == 0) {
			return null;
		}
		// Cr�ation de la socket de donn�es
		File file = null;
		// Envoi de la commande RETR
		this.commandSocket.send(this.ftpFactory.buildRetrCommand(path));
		// Connexion de la socket
		FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
		if (dataSocket.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getDataPort())) {
			// Reception des donn�es sur la socket de donn�es
			file = dataSocket.readFileFromReader(path.split("/")[path.split("/").length - 1]);
			dataSocket.closeReaders();
		}
		return file;
	}

	/**
	 * Upload un fichier sur le serveur FTP
	 * 
	 * @param inputStream
	 *            le flux de données représentant le fichier
	 * @param fileName
	 *            le nom du fichier sur le serveur FTP
	 * @param path
	 *            le chemin d'accès au fichier sur le serveur FTP
	 * @return vrai si l'upload du fichier a réussi, faux sinon
	 */
	public boolean upload(InputStream inputStream, String fileName, String path) {
		if (!this.isConnected()) {
			return false;
		}
		if (this.getDataPort() == 0) {
			return false;
		}
		path = path.isEmpty() ? "" : path + "/";
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildStorRequest(path + fileName));
		if (reply.isOk("150")) {
			FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
			dataSocket.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getDataPort());
			boolean success = dataSocket.writeDataInWriter(inputStream);
			dataSocket.closeReaders();
			return success;
		} else {
			return false;
		}
	}

	/**
	 * Liste les fichiers d'un répertoire sur le serveur FTP
	 * 
	 * @param path
	 *            le chemin d'accès au répertoire
	 * 
	 * @return une liste de String contenant diverses informations (nom, date de
	 *         modif, type, ...) sur les fichiers contenus dans le dossier ou null si la commande a échoué
	 * 
	 */
	public List<String> list(String path) {
		if (!this.isConnected()) {
			return null;
		}
		if (this.getDataPort() == 0) {
			return null;
		}
		try {
			FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildCwdRequest(path));
			if (reply.isOk("250")) {
				this.commandSocket.send(this.ftpFactory.buildListRequest(path));
				List<String> files = new ArrayList<String>();
				FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
				dataSocket.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getDataPort());
				String line = null;
				while ((line = dataSocket.readLine()) != null) {
					files.add(line);
				}
				return files;
			} else {
				return null;
			}
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Envoie une commande USER au serveur FTP
	 * 
	 * @param user
	 *            le login de l'utilisateur
	 * 
	 * @return une réponse FTP si la commande a réussi, null sinon
	 */
	public FtpReply sendUserCommand(String user) {
		return this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildUserRequest(user));
	}

	/**
	 * Envoie une commande PASS au serveur FTP
	 * 
	 * @param password
	 *            le mot de passe de l'utilisateur
	 * 
	 * @return une réponse FTP si la commande a réussi, null sinon
	 */
	public FtpReply sendPasswordCommand(String password) {
		return this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildPasswordRequest(password));
	}

	/**
	 * Demande au serveur de passer la connexion en mode passif
	 * 
	 * @return vrai si le passage en mode passif a réussi, faux sinon
	 */
	public boolean setPassive() {
		if (!this.isConnected()) {
			return false;
		}
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildSetPassiveRequest());
		if (reply.isOk("229")) {
			String[] split = reply.getMessage().split("\\|");
			try {
				this.setDataPort(Integer.valueOf(split[3]));
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Met à jour le numéro de port pour la connexion au serveur FTP sur la
	 * socket de données
	 * 
	 * @param dataPort
	 *            le nouveau numéro de port
	 */
	public void setDataPort(int dataPort) {
		this.getConfig().setConfiguredDataPort(dataPort);
	}

	/**
	 * @return le numéro de port pour la connexion au serveur FTP sur la socket
	 *         de données
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
	 * @param path
	 *            le chemin d'accès au fichier
	 * 
	 * @return vrai si la suppression a réussi, faux sinon
	 */
	public boolean delete(String path) {
		if (!this.isConnected()) {
			return false;
		}
		// Envoi de la commande RMD
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildRmdCommand(path));
		return reply.isOk("200");
	}

	/**
	 * Renomme un fichier sur le serveur FTP
	 * 
	 * @param from
	 *            le nom d'origine du fichier
	 * @param to
	 *            le nouveau nom du fichier
	 * 
	 * @return vrai si le renommage a réussi, faux sinon
	 */
	public boolean rename(String from, String to) {
		if (!this.isConnected())
			return false;
		// Envoi de la commande RNFR
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildRnfrCommand(from));
		if (!reply.isOk("350"))
			return false;
		reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildRntoCommand(to));
		return reply.isOk("200");
	}

	/**
	 * Crée un dossier sur le serveur FTP
	 * 
	 * @param path
	 *            le chemin d'accès au fichier
	 * 
	 * @return vrai si la création de dossier a réussi, faux sinon
	 */
	public boolean mkdir(String path) {
		if (!this.isConnected())
			return false;
		// Envoi de la commande RNFR
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildMkdCommand(path));
		return reply.isOk("200");
	}

	/**
	 * Ferme la connexion au serveur FTP
	 */
	public void close() {
		this.commandSocket.send(this.ftpFactory.buildQuitCommand());
		try {
			this.commandSocket.close();
		} catch (IOException e) {
		}
	}

	/**
	 * telecharge un dossier sous forme de zip
	 * @param path du dossier
	 * @return zip
	 */
	public File downloadFolder(String path) {
		if (!this.isConnected()) {
			return null;
		}
		if (this.getDataPort() == 0) {
			return null;
		}
		List<String> listget = generateFileListGet(path);
		for (String get : listget) {
			System.out.println("get : " + get);
			this.commandSocket.send(this.ftpFactory.buildRetrCommand(get));
			FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
			dataSocket.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getDataPort());
			// Si le fichier est null, il y a eu une erreur -> on retourne null
			if (dataSocket.readFileFromReader(get) == null) {
				return null;
			}
			dataSocket.closeReaders();
		}
		return zip("tmp/" + path.split("/")[path.split("/").length - 1] + ".zip", listget);
	}


	/**
	 * zip une liste de fichier (listfiles) dans un zip (path)
	 * @param path nom du zip(.zip)
	 * @param listfiles nom des path vers les fichier a inclure dans le zip
	 * @return un zip
	 */
	public File zip(String zipFile, List<String> listfiles) {
		byte[] buffer = new byte[1024];
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(fos);
			FileInputStream in = null;

			for (String file : listfiles) {
				System.out.println("zipIt :" + file);
				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);
				in = new FileInputStream(new File("tmp/" + file));
				int len;
				while ((len = in.read(buffer)) > 0) {
					System.out.println("write :" + len);
					zos.write(buffer, 0, len);
				}
				in.close();

			}

			zos.closeEntry();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				zos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new File(zipFile);
	}

	/**
	 * Genere tous les chemins vers des fichiers d'un dossier ( en FTP )
	 * @param node path vers un dossier
	 * @return list de path vers les fichiers du dossier (node)
	 */
	public List<String> generateFileListGet(String node) {
		if (node.length() > 0 && node.endsWith("/")) {
			node = node.substring(0, node.length() - 1);
		}
		List<String> fileList = new ArrayList<String>();
		String filename;
		int index;
		String type;
		List<String> lignes = list(node);
		this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildCwdRequest());
		for (String ligne : lignes) {
			index = ligne.indexOf("type=");
			filename = ligne;
			type = "file";
			if (index != -1) {
				type = ligne.substring(index + 5, ligne.indexOf(";", index));
				filename = ligne.split("; ")[ligne.split("; ").length - 1];
			}
			if (type.equals("file")) {
				fileList.add(node + "/" + filename);
			} else {
				fileList.addAll(generateFileListGet(node + "/" + filename));
			}
		}

		return fileList;
	}

	/**
	 * Genere tous les chemins vers des fichiers d'un dossier ( en local )
	 * @param node path vers un dossier
	 * @return list de path vers les fichiers du dossier (node)
	 */
	public List<String> generateFileList(File node) {
		List<String> fileList = new ArrayList<String>();
		if (node.isFile())
			try {
				fileList.add(node.getCanonicalPath());
			} catch (IOException e) {
			}

		if (node.isDirectory()) {
			String[] files = node.list();
			for (String filename : files) {
				fileList.addAll(generateFileList(new File(node, filename)));
			}
		}
		return fileList;
	}
}
