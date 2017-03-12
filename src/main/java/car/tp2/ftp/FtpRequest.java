package car.tp2.ftp;

/**
 * Classe encapsulant un message envoyé au serveur FTP
 * 
 * @author Lucas Moura de Oliveira
 *
 */
public class FtpRequest {
	
	protected String command;
	
	/**
	 * Crée un message à partir d'une commande au format texte
	 * 
	 * @param command la commande
	 */
	public FtpRequest(String command) {
		this.command = command;
	}
	
	/**
	 * @return le texte de la commande
	 */
	public String getText() {
		return command;
	}

}
