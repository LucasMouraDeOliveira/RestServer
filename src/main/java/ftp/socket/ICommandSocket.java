package ftp.socket;

import ftp.FtpReply;
import ftp.FtpRequest;

/**
 * Interface qui définit les méthodes d'envoi de requête de la classe {@link FtpCommandSocket}
 * 
 * @author Lucas Moura de Oliveira
 *
 */
public interface ICommandSocket {
	
	/**
	 * Envoie une requête au serveur FTP
	 * 
	 * @param request la requête FTP
	 */
	public void send(FtpRequest request);
	
	/**
	 * Envoie une requête au serveur FTP et attends de recevoir une réponse.
	 * 
	 * @param request la requête FTP
	 * 
	 * @return une réponse FTP
	 */
	public FtpReply sendAndWaitForReply(FtpRequest request) ;

}
