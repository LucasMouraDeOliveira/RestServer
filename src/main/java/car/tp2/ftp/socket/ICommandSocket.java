package car.tp2.ftp.socket;

import car.tp2.ftp.FtpException;
import car.tp2.ftp.FtpReply;
import car.tp2.ftp.FtpRequest;

public interface ICommandSocket {
	
	/**
	 * Envoie une requ�te au serveur FTP
	 * 
	 * @param request la requ�te FTP
	 */
	public void send(FtpRequest request);
	
	/**
	 * Envoie une requ�te au serveur FTP et attends de recevoir une r�ponse.
	 * 
	 * @param request la requ�te FTP
	 * @return une r�ponse FTP
	 * @throws FtpException si une erreur a lieu lors de la construction de la r�ponse FTP
	 */
	public FtpReply sendAndWaitForReply(FtpRequest request) throws FtpException;

}
