package ftp.socket;

import ftp.FtpReply;
import ftp.FtpRequest;

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
	 * 
	 * @return une r�ponse FTP
	 */
	public FtpReply sendAndWaitForReply(FtpRequest request) ;

}
