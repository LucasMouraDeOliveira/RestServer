package ftp;

/**
 * Classe encapsulant une r�ponse du serveur FTP
 * 
 * @author Lucas Moura de Oliveira
 *
 */
public class FtpReply {
	
	protected String code;
	
	protected String message;

	/**
	 * Cr�e une ftpReply � partir d'un code de retour et un message
	 * 
	 * @param code le code de retour
	 * @param message le message
	 */
	public FtpReply(String code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * Retourne vrai si le code de retour corresponds � celui qui est attendu
	 * 
	 * @param expectedCode le code de retour attendu
	 * 
	 * @return vrai si la chaine du code de retour attendu est la m�me que celle du code de retour effectif
	 */
	public boolean isOk(String expectedCode) {
		return expectedCode.equals(code);
	}
	
	/**
	 * @return le message du serveur FTP
	 */
	public String getMessage() {
		return this.message;
	}

}
