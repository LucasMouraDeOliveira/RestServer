package user;

import java.util.HashMap;
import java.util.UUID;

/**
 * Class qui permet la creation et recuperation d'utilisateur via des token
 * 
 * @author brico
 */
public class UserManagment {

	public static UserManagment instance = new UserManagment();
	public HashMap<String, User> maptokenuser; 
	
	public UserManagment() {
		maptokenuser = new HashMap<String, User>();
	}
	
	/**
	 * @return instance du singleton 
	 */
	public static UserManagment getInstance() {
		return instance;
	}
	
	/**
	 * recupere l'utilisateur lié aux token
	 * @param token
	 * @return User
	 */
	public User getUser(String token) {
		return maptokenuser.get(token);
	}
	
	/**
	 * Crée un token qui permet de récupere par la suite un objet User avec le name et mdp avec getUser(token)
	 * @param name
	 * @param mdp
	 * @return token
	 */
	public String addUser(String name,String mdp) {
		String token = generateToken();
		maptokenuser.put(token, new User(name, mdp));
		return token;
	}

	/**
	 * @return un token aléatoire
	 */
	private String generateToken() {
		return UUID.randomUUID().toString();
	}
	
}
