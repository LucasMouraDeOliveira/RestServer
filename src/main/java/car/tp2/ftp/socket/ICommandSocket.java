package car.tp2.ftp.socket;

import car.tp2.ftp.FtpException;
import car.tp2.ftp.FtpReply;
import car.tp2.ftp.FtpRequest;

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
	 * @return une réponse FTP
	 * @throws FtpException si une erreur a lieu lors de la construction de la réponse FTP
	 */
	public FtpReply sendAndWaitForReply(FtpRequest request) throws FtpException;

}
