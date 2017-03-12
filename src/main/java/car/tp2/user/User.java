package car.tp2.user;

/**
 * Classe contenant les informations d'un utilisateur
 * 
 * @author Lucas Moura de Oliveira
 *
 */
public class User {

	public String name;
	
	public String password;
	
	/**
	 * Crée un utilisateur et lui assigne ses logins/mot de passe
	 * 
	 * @param name le login de l'utilisateur
	 * @param password le mot de passe de l'utilisateur
	 */
	public User(String name, String password){
		this.name = name;
		this.password = password;
	}

	/**
	 * @return le nom de l'utilisateur
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return le mot de passe de l'utilisateur
	 */
	public String getPassword() {
		return password;
	}
	
}
