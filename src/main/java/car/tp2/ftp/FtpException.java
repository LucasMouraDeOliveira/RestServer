package car.tp2.ftp;

/**
 * Classe d'erreur englobant la plupart des cas d'erreurs possibles dans l'applications.
 * La différentation entre les erreurs se fait par le biais du message passé en paramètre au moment ou l'erreur
 * est déclenchée
 * 
 * @author Lucas Moura de Oliveira
 *
 */
public class FtpException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6674229156445119080L;

	/**
	 * Crée une nouvelle FtpException avec un message d'erreur
	 * 
	 * @param message le message expliquant la cause probable de l'erreur
	 */
	public FtpException(String message){
		super(message);
	}

}
