package ftp.socket;

import java.io.IOException;
import java.net.Socket;

import factory.FtpFactory;

/**
 * Classe abstraites d�finissant les m�thodes de communication avec le serveur FTP.
 * Cette classe est impl�ment�e par les classes FtpCommandSocket et FtpDataSocket
 * qui se sp�cialisent chacune dans la communication avec un des deux sockets du protocole FTP
 * 
 * @author Lucas Moura de Oliveira
 *
 */
public abstract class FtpSocket {
	
	protected FtpFactory ftpFactory;
	
	protected Socket socket;
		
	/**
	 * Cr�e une FTPSocket
	 * 
	 * @param ftpFactory une factory g�rant la cr�ation de requetes, sockets et readers/writers
	 */
	public FtpSocket(FtpFactory ftpFactory){
		this.ftpFactory = ftpFactory;
		this.socket = this.ftpFactory.buildEmptySocket();
	}
	
	/**
	 * Tente d'ouvrir la connexion avec le serveur FTP
	 * 
	 * @param address l'adresse du serveur FTP
	 * @param port le port de connexio � la socket de commande du serveur FTP
	 * 
	 * @return vrai si la connexion a pu �tre �tablie, faux s'il y a eu une erreur
	 */
	public boolean openSocket(String address, int port) {
		try {
			this.socket.connect(this.ftpFactory.buildInetAddress(address, port));
			this.openReaders(); 
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Cr�e et ouvre les readers et writers de la socket
	 * 
	 * @throws IOException s'il y a eu une erreur lors de l'ouverture d'un des flux
	 */
	public abstract void openReaders() throws IOException;
	
	/**
	 * Ferme les readers et writers de la socket
	 * 
	 * @throws IOException s'il y a eu une erreur lors de la fermeture d'un des flux
	 */
	public abstract void closeReaders() throws IOException;
	
	/**
	 * Lit une ligne dans la socket et la retourne
	 * 
	 * @return une ligne de texte depuis le reader de la socket
	 * 
	 * @throws IOException si une erreur de lecture a eu lieu
	 */
	public abstract String readLine() throws IOException;
	
	/**
	 * Ferme la socket
	 * 
	 * @throws IOException si une erreur a lieu lors de la fermeture de la socket
	 */
	public void close() throws IOException{
		this.socket.close();
	}
}
