package car.tp2.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import car.tp2.factory.FtpFactory;
import car.tp2.ftp.socket.FtpCommandSocket;
import car.tp2.ftp.socket.FtpDataSocket;
import car.tp2.user.User;
import car.tp2.utility.FtpConfig;

/**
 * Classe qui d�finit les m�thodes de communication avec le serveur FTP
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
	 * Initialise le client FTP. R�cup�re l'adresse du serveur FTP dans le
	 * fichier de configuration et tente de se connecter sur le port de
	 * commande.
	 * 
	 * @throws IOException
	 * @throws FtpException
	 *             en cas d'erreur de communication avec le serveur FTP
	 */
	public FtpClient(User user) throws IOException, FtpException {
		this.ftpFactory = new FtpFactory();
		this.commandSocket = new FtpCommandSocket(ftpFactory);
		this.ftpConfig = new FtpConfig();
		this.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getCommandPort());
		this.connect(user.getName(), user.getPassword());
		this.setPassive();
	}

	/**
	 * Connecte le client au serveur FTP
	 * 
	 * @param address
	 *            l'adresse du serveur FTP
	 * @param port
	 *            le num�ro de port de la socket de commande du serveur FTP
	 * @throws FtpException
	 *             si la tentative de connexion �choue
	 */
	public void openSocket(String address, int port) throws FtpException {
		if (this.commandSocket.openSocket(address, port)) {
			// Lecture de la premi�re ligne (message de bienvenue)
			try {
				String reply = this.commandSocket.readLine();
				if (reply != null && reply.startsWith("220")) {
					this.open = true;
				}
			} catch (IOException e) {
				throw new FtpException("Erreur lors de la lecture du message de bienvenue");
			}
		}
	}

	/**
	 * @return vrai si la connexion au serveur FTP sur la socket de commande est
	 *         active.
	 */
	public boolean isSocketOpen() {
		return this.open;
	}

	/**
	 * @return vrai si l'utilisateur est connect� et authentifi� sur le serveur
	 *         FTP
	 */
	public boolean isConnected() {
		return this.open && this.connected;
	}

	/**
	 * Connecte l'utilisateur au serveur FTP. Si la connexion r�ussie, l'�tat du
	 * client passe � 'connect�'. Si la connexion �choue, l'�tat du client reste
	 * � 'd�connect�'
	 * 
	 * @param user
	 *            le login de l'utilisateur, sous forme de chaine de caract�res
	 * @param password
	 *            le mot de passe non crypt� de l'utilisateur, sous forme de
	 *            chaine de caract�res
	 * 
	 * @throws FTPException
	 *             si l'envoi de la requ�te ou la r�ception de la r�ponse �choue
	 */
	public void connect(String user, String password) throws FtpException {
		// Si la socket n'est pas ouverte, on ne peut pas se connecter -> erreur
		if (!this.isSocketOpen()) {
			throw new FtpException("Pas de connexion active au serveur FTP");
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
				throw new FtpException("Connexion refus�e : mot de passe incorrect");
			}
		} else {
			throw new FtpException("Connexion refus�e : erreur lors de l'envoi du nom d'utilisateur");
		}
	}

	/**
	 * T�l�charge un fichier depuis le serveur FTP
	 * 
	 * @param path
	 *            le chemin d'acc�s au fichier sur le serveur
	 * 
	 * @return le fichier
	 * @throws FtpException
	 *             si la r�cup�ration du fichier �choue pour une quelconque
	 *             raison
	 */
	public File download(String path) throws FtpException {
		if (!this.isConnected()) {
			throw new FtpException("Commande refus�e : vous n'�tes pas connect�");
		}
		if (this.getDataPort() == 0) {
			throw new FtpException("Le mode passif n'est pas actif");
		}
		// Cr�ation de la socket de donn�es
		File file = null;
		try {
			// Envoi de la commande RETR
			this.commandSocket.send(this.ftpFactory.buildRetrCommand(path));
			// Connexion de la socket
			FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
			dataSocket.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getDataPort());
			// Reception des donn�es sur la socket de donn�es
			file = dataSocket.readFileFromReader(path.split("/")[path.split("/").length - 1]);
			dataSocket.closeReaders();
		} catch (IOException e) {
			throw new FtpException("Erreur interne");
		}

		return file;
	}

	/**
	 * Upload un fichier sur le serveur FTP
	 * 
	 * @param inputStream
	 *            le flux de donn�es repr�sentant le fichier
	 * @param fileName
	 *            le nom du fichier sur le serveur FTP
	 * @param path
	 *            le chemin d'acc�s au fichier sur le serveur FTP
	 * @throws FtpException
	 *             si l'upload de fichier �choue pour une quelconque raison
	 */
	public void upload(InputStream inputStream, String fileName, String path) throws FtpException {
		if (!this.isConnected()) {
			throw new FtpException("Commande refus�e : vous n'�tes pas connect�");
		}
		if (this.getDataPort() == 0) {
			throw new FtpException("Le mode passif n'est pas actif");
		}
		path = path.isEmpty() ? "" : path + "/";
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildStorRequest(path + fileName));
		if (reply.isOk("150")) {
			try {
				FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
				dataSocket.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getDataPort());
				dataSocket.writeDataInWriter(inputStream);
				dataSocket.closeReaders();
			} catch (IOException e) {
				throw new FtpException("Erreur interne");
			}
		} else {
			throw new FtpException("Le fichier existe d�j�");
		}
	}

	/**
	 * Liste les fichiers d'un r�pertoire sur le serveur FTP
	 * 
	 * @param path
	 *            le chemin d'acc�s au r�pertoire
	 * 
	 * @return une liste de String contenant diverses informations (nom, date de
	 *         modif, type, ...) sur les fichiers contenus dans le dossier
	 * 
	 * @throws FtpException
	 *             si la r�cup�ration des fichiers �choue pour une quelconque
	 *             raison
	 */
	public List<String> list(String path) throws FtpException {
		if (!this.isConnected()) {
			throw new FtpException("Commande refus�e : vous n'�tes pas connect�");
		}
		if (this.getDataPort() == 0) {
			throw new FtpException("Le mode passif n'est pas actif");
		}
		try {
			FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildCwdRequest(path));
			if (reply.isOk("250")) {
				this.commandSocket.send(this.ftpFactory.buildListRequest(path));
				System.out.println(this.commandSocket.readLine());
				List<String> files = new ArrayList<String>();
				FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
				dataSocket.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getDataPort());
				String line = null;
				while ((line = dataSocket.readLine()) != null) {
					files.add(line);
				}
				System.out.println(this.commandSocket.readLine());
				return files;
			} else {
				throw new FtpException("Erreur : acc�s au dossier impossible");
			}
		} catch (IOException e) {
			throw new FtpException("Erreur lors de la lecture de la socket de donn�es");
		}
	}

	/**
	 * Envoie une commande USER au serveur FTP
	 * 
	 * @param user
	 *            le login de l'utilisateur
	 * 
	 * @return une r�ponse FTP
	 * 
	 * @throws FtpException
	 *             si l'envoi de la commande ou la r�cup�ration de la r�ponse
	 *             �choue
	 */
	public FtpReply sendUserCommand(String user) throws FtpException {
		return this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildUserRequest(user));
	}

	/**
	 * Envoie une commande PASS au serveur FTP
	 * 
	 * @param password
	 *            le mot de passe de l'utilisateur
	 * 
	 * @return une r�ponse FTP
	 * 
	 * @throws FtpException
	 *             si l'envoi de la commande ou la r�cup�ration de la r�ponse
	 *             �choue
	 */
	public FtpReply sendPasswordCommand(String password) throws FtpException {
		return this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildPasswordRequest(password));
	}

	/**
	 * Demande au serveur de passer la connexion en mode passif
	 * 
	 * @throws FtpException
	 *             si le passage en mode passif �choue pour une quelconque
	 *             raison
	 */
	public void setPassive() throws FtpException {
		if (!this.isConnected()) {
			throw new FtpException("Commande refus�e : vous n'�tes pas connect�");
		}
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildSetPassiveRequest());
		if (reply.isOk("229")) {
			String[] split = reply.getMessage().split("\\|");
			try {
				this.setDataPort(Integer.valueOf(split[3]));
			} catch (NumberFormatException e) {
				throw new FtpException("Erreur de format du port lors du passage en mode passif");
			}
		} else {
			throw new FtpException("Echec du passage en mode passif");
		}
	}

	/**
	 * Met � jour le num�ro de port pour la connexion au serveur FTP sur la
	 * socket de donn�es
	 * 
	 * @param dataPort
	 *            le nouveau num�ro de port
	 */
	public void setDataPort(int dataPort) {
		this.getConfig().setConfiguredDataPort(dataPort);
	}

	/**
	 * @return le num�ro de port pour la connexion au serveur FTP sur la socket
	 *         de donn�es
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
	 *            le chemin d'acc�s au fichier
	 * 
	 * @throws FtpException
	 *             si la suppression �choue pour une quelconque raison
	 */
	public void delete(String path) throws FtpException {
		if (!this.isConnected()) {
			throw new FtpException("Commande refus�e : vous n'�tes pas connect�");
		}
		// Envoi de la commande RMD
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildRmdCommand(path));
		if (!reply.isOk("200")) {
			throw new FtpException("Echec de la suppression");
		}
	}

	/**
	 * Renomme un fichier sur le serveur FTP
	 * 
	 * @param from
	 *            le nom d'origine du fichier
	 * @param to
	 *            le nouveau nom du fichier
	 * 
	 * @throws FtpException
	 *             si le renommage �choue pour une quelconque raison
	 */
	public void rename(String from, String to) throws FtpException {
		if (!this.isConnected())
			throw new FtpException("Commande refus�e : vous n'�tes pas connect�");
		// Envoi de la commande RNFR
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildRnfrCommand(from));
		if (!reply.isOk("350"))
			throw new FtpException("Echec du renommage");
		reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildRntoCommand(to));
		if (!reply.isOk("200"))
			throw new FtpException("Echec du renommage");
	}

	/**
	 * Cr�e un dossier sur le serveur FTP
	 * 
	 * @param path
	 *            le chemin d'acc�s au fichier
	 * 
	 * @throws FtpException
	 *             si la cr�ation de dossier �choue pour une quelconque raison
	 */
	public void mkdir(String path) throws FtpException {
		if (!this.isConnected())
			throw new FtpException("Commande refus�e : vous n'�tes pas connect�");
		// Envoi de la commande RNFR
		FtpReply reply = this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildMkdCommand(path));
		if (!reply.isOk("200"))
			throw new FtpException("Echec de la creation");
	}

	/**
	 * Ferme la connexion au serveur FTP
	 * 
	 * @throws IOException
	 */
	public void close() {
		this.commandSocket.send(this.ftpFactory.buildQuitCommand());
		try {
			this.commandSocket.close();
		} catch (IOException e) {
		}
	}

	public File downloadFolder(String path) throws FtpException, IOException {
		if (!this.isConnected()) {
			throw new FtpException("Commande refus�e : vous n'�tes pas connect�");
		}
		if (this.getDataPort() == 0) {
			throw new FtpException("Le mode passif n'est pas actif");
		}
		List<String> listget = generateFileListGet(path);
		for(String get:listget){
			System.out.println("get : " + get);
			this.commandSocket.send(this.ftpFactory.buildRetrCommand(get));
			FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
			dataSocket.openSocket(this.ftpConfig.getCommandAddress(), this.ftpConfig.getDataPort());
			dataSocket.readFileFromReader(get);
			dataSocket.closeReaders();
		}
		return zip("tmp/" + path.split("/")[path.split("/").length - 1],listget);
	}

	public File zip(String path,List<String> listfiles) {
		return zipIt(path + ".zip", listfiles);
	}

	public File zipIt(String zipFile, List<String> listfiles) {
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
				in = new FileInputStream(new File("tmp/"+file));
				int len;
				while ((len = in.read(buffer)) > 0){
					System.out.println("write :"+len );
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
	
	public List<String> generateFileListGet(String node) throws FtpException {
		if(node.length() > 0 && node.endsWith("/")){
			node = node.substring(0, node.length()-1);
		}
		List<String> fileList = new ArrayList<>();
		String filename;
		int index;
		String type;
		List<String> lignes = list(node);
		this.commandSocket.sendAndWaitForReply(this.ftpFactory.buildCwdRequest());
		for (String ligne : lignes) {
			index = ligne.indexOf("type=");
			filename = ligne;
			type = "file";
			if(index != -1){
				type = ligne.substring(index+5,ligne.indexOf(";",index));
				filename = ligne.split("; ")[ligne.split("; ").length-1];
			}
			if(type.equals("file")){
				System.out.println("generateFileListGet:" + node + "/" + filename);
				fileList.add(node + "/" + filename);
			}else{
				fileList.addAll(generateFileListGet(node + "/" + filename));
			}
		}
		
		return fileList;
	}

	public List<String> generateFileList(File node) {
		List<String> fileList = new ArrayList<>();
		if (node.isFile())
			try {
				System.out.println("generateFileList:" +node.getCanonicalPath());
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
