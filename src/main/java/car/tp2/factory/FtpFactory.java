package car.tp2.factory;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import car.tp2.ftp.FtpReply;
import car.tp2.ftp.FtpRequest;

/**
 * Classe utilitaire de création d'objets (commandes, readers, writers, ...)
 * 
 * @author Lucas Moura de Oliveira
 *
 */
public class FtpFactory {

	/**
	 * Crée une commande USER
	 * 
	 * @param user le login de l'utilisateur
	 * 
	 * @return la commande USER
	 */
	public FtpRequest buildUserRequest(String user) {
		return new FtpRequest("USER " + user);
	}

	/**
	 * Crée une commande PASS
	 * 
	 * @param password le mot de passe de l'utilisateur
	 * 
	 * @return la commande PASS
	 */
	public FtpRequest buildPasswordRequest(String password) {
		return new FtpRequest("PASS " + password);
	}
	
	/**
	 * Crée une commande EPSV (passage en mode passif)
	 * 
	 * @return la commande EPSV
	 */
	public FtpRequest buildSetPassiveRequest() {
		return new FtpRequest("EPSV");
	}

	/**
	 * Crée une commande RETR (récupération de fichier)
	 * 
	 * @param path le chemin d'accès au fichier
	 * 
	 * @return la commande RETR
	 */
	public FtpRequest buildRetrCommand(String path) {
		return new FtpRequest("RETR " + path);
	}

	/**
	 * Crée une commande MLST (listage du contenu d'un dossier)
	 * 
	 * @param path le chemin d'accès au dossier
	 * 
	 * @return la commande MLST
	 */
	public FtpRequest buildListRequest(String path) {
		return new FtpRequest("MLST " + path);
	}

	/**
	 * Crée une commande CWD (change le dossier courant)
	 * 
	 * @param path le chemin d'accès à un dossier
	 * 
	 * @return la commande CWD
	 */
	public FtpRequest buildCwdRequest(String path) {
		return new FtpRequest("CWD " + path);
	}
	public FtpRequest buildCwdRequest() {
		return new FtpRequest("CWD");
	}


	/**
	 * Crée une commande RMD (supprime un dossier)
	 * 
	 * @param path le chemin d'accès au dossier
	 * 
	 * @return la commande RMD
	 */
	public FtpRequest buildRmdCommand(String path) {
		return new FtpRequest("RMD " + path);
	}

	/**
	 * Crée une commande RMFR (renommage : partie 1) 
	 * 
	 * @param path le chemin d'accès au fichier
	 * 
	 * @return la commande RNFR
	 */
	public FtpRequest buildRnfrCommand(String from) {
		return new FtpRequest("RNFR " + from);
	}

	/**
	 * Crée une commande RNTO (renommage : partie 2)
	 * 
	 * @param path le nouveau chemin d'accès au fichier
	 * 
	 * @return la commande RNTO
	 */
	public FtpRequest buildRntoCommand(String to) {
		return new FtpRequest("RNTO " + to);
	}
		
	/**
	 * Crée une commande STOR (upload un fichier)
	 * 
	 * @param path le nom du fichier
	 * 
	 * @return la commande STOR
	 */
	public FtpRequest buildStorRequest(String fileName) {
		return new FtpRequest("STOR " + fileName);
	}

	/**
	 * Crée une commande MKD (crée un dossier)
	 * 
	 * @param path le chemin d'accès au dossier
	 * 
	 * @return la commande MKD
	 */
	public FtpRequest buildMkdCommand(String path) {
		return new FtpRequest("MKD " + path);
	}

	/**
	 * Crée une commande QUIT (coupe la connexion au serveur)
	 * 
	 * @return la commande QUIT
	 */
	public FtpRequest buildQuitCommand() {
		return new FtpRequest("QUIT");
	}
	
	/**
	 * Crée une SocketAddress
	 * @param commandAddress l'adresse
	 * @param commandPort le port
	 * @return la SocketAddress
	 */
	public SocketAddress buildInetAddress(String commandAddress, int commandPort) {
		return new InetSocketAddress(commandAddress, commandPort);
	}

	/**
	 * @return une Socket non connectée
	 */
	public Socket buildEmptySocket() {
		return new Socket();
	}

	/**
	 * Récupère et analyse une réponse renvoyée par le serveur FTP
	 * 
	 * @param response la réponse du serveur FTP
	 * 
	 * @return une réponse FTP formatée si le format du message est correcte, null sinon
	 */
	public FtpReply buildResponse(String response) {
		if(response == null)
			return null;
		String[] split = response.split(" ");
		String code = split[0];
		String text = null;
		if(split.length > 1){
			text = "";
			for(int i = 1; i < split.length; i++){
				text+=split[i]+" ";
			}
			text.trim();
		}
		return new FtpReply(code, text);
	}

	public BufferedReader buildCommandSocketReader(Socket socket) throws IOException {
		return new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public PrintWriter buildCommandSocketWriter(Socket socket) throws IOException {
		return new PrintWriter(socket.getOutputStream());
	}
	
	public DataInputStream buildDataSocketReader(Socket socket) throws IOException {
		return new DataInputStream(socket.getInputStream());
	}
	
	public DataOutputStream buildDataSocketWriter(Socket socket) throws IOException {
		return new DataOutputStream(socket.getOutputStream());
	}


}
