package factory;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import ftp.FtpReply;
import ftp.FtpRequest;

/**
 * Classe utilitaire de cr�ation d'objets (commandes, readers, writers, ...)
 * 
 * @author Lucas Moura de Oliveira
 *
 */
public class FtpFactory {

	/**
	 * Cr�e une commande USER
	 * 
	 * @param user le login de l'utilisateur
	 * 
	 * @return la commande USER
	 */
	public FtpRequest buildUserRequest(String user) {
		return new FtpRequest("USER " + user);
	}

	/**
	 * Cr�e une commande PASS
	 * 
	 * @param password le mot de passe de l'utilisateur
	 * 
	 * @return la commande PASS
	 */
	public FtpRequest buildPasswordRequest(String password) {
		return new FtpRequest("PASS " + password);
	}
	
	/**
	 * Cr�e une commande EPSV (passage en mode passif)
	 * 
	 * @return la commande EPSV
	 */
	public FtpRequest buildSetPassiveRequest() {
		return new FtpRequest("EPSV");
	}

	/**
	 * Cr�e une commande RETR (r�cup�ration de fichier)
	 * 
	 * @param path le chemin d'acc�s au fichier
	 * 
	 * @return la commande RETR
	 */
	public FtpRequest buildRetrCommand(String path) {
		return new FtpRequest("RETR " + path);
	}

	/**
	 * Cr�e une commande MLST (listage du contenu d'un dossier)
	 * 
	 * @param path le chemin d'acc�s au dossier
	 * 
	 * @return la commande MLST
	 */
	public FtpRequest buildListRequest(String path) {
		return new FtpRequest("MLST " + path);
	}

	/**
	 * Cr�e une commande CWD (change le dossier courant)
	 * 
	 * @param path le chemin d'acc�s � un dossier
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
	 * Cr�e une commande RMD (supprime un dossier)
	 * 
	 * @param path le chemin d'acc�s au dossier
	 * 
	 * @return la commande RMD
	 */
	public FtpRequest buildRmdCommand(String path) {
		return new FtpRequest("RMD " + path);
	}

	/**
	 * Cr�e une commande RMFR (renommage : partie 1) 
	 * 
	 * @param from le chemin d'acc�s au fichier
	 * 
	 * @return la commande RNFR
	 */
	public FtpRequest buildRnfrCommand(String from) {
		return new FtpRequest("RNFR " + from);
	}

	/**
	 * Cr�e une commande RNTO (renommage : partie 2)
	 * 
	 * @param to le nouveau chemin d'acc�s au fichier
	 * 
	 * @return la commande RNTO
	 */
	public FtpRequest buildRntoCommand(String to) {
		return new FtpRequest("RNTO " + to);
	}
		
	/**
	 * Cr�e une commande STOR (upload un fichier)
	 * 
	 * @param fileName le nom du fichier
	 * 
	 * @return la commande STOR
	 */
	public FtpRequest buildStorRequest(String fileName) {
		return new FtpRequest("STOR " + fileName);
	}

	/**
	 * Cr�e une commande MKD (cr�e un dossier)
	 * 
	 * @param path le chemin d'acc�s au dossier
	 * 
	 * @return la commande MKD
	 */
	public FtpRequest buildMkdCommand(String path) {
		return new FtpRequest("MKD " + path);
	}

	/**
	 * Cr�e une commande QUIT (coupe la connexion au serveur)
	 * 
	 * @return la commande QUIT
	 */
	public FtpRequest buildQuitCommand() {
		return new FtpRequest("QUIT");
	}
	
	/**
	 * Cr�e une SocketAddress
	 * @param commandAddress l'adresse
	 * @param commandPort le port
	 * @return la SocketAddress
	 */
	public SocketAddress buildInetAddress(String commandAddress, int commandPort) {
		return new InetSocketAddress(commandAddress, commandPort);
	}

	/**
	 * @return une Socket non connect�e
	 */
	public Socket buildEmptySocket() {
		return new Socket();
	}

	/**
	 * R�cup�re et analyse une r�ponse renvoy�e par le serveur FTP
	 * 
	 * @param response la r�ponse du serveur FTP
	 * 
	 * @return une r�ponse FTP format�e si le format du message est correcte, null sinon
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
