package car.tp2.ftp.socket;

import java.io.IOException;
import java.net.Socket;

import car.tp2.factory.FtpFactory;

/**
 * Classe abstraites définissant les méthodes de communication avec le serveur FTP.
 * Cette classe est implémentée par les classes FtpCommandSocket et FtpDataSocket
 * qui se spécialisent chacune dans la communication avec un des deux sockets du protocole FTP
 * 
 * @author Lucas Moura de Oliveira
 *
 */
public abstract class FtpSocket {
	
	protected FtpFactory ftpFactory;
	
	protected Socket socket;
		
	/**
	 * Crée une FTPSocket
	 * 
	 * @param ftpFactory une factory gérant la création de requetes, sockets et readers/writers
	 */
	public FtpSocket(FtpFactory ftpFactory){
		this.ftpFactory = ftpFactory;
		this.socket = this.ftpFactory.buildEmptySocket();
	}
	
	/**
	 * Tente d'ouvrir la connexion avec le serveur FTP
	 * 
	 * @param address l'adresse du serveur FTP
	 * @param port le port de connexio à la socket de commande du serveur FTP
	 * 
	 * @return vrai si la connexion a pu être établie, faux s'il y a eu une erreur
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
	 * Crée et ouvre les readers et writers de la socket
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
