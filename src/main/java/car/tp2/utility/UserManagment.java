package car.tp2.utility;

import java.util.HashMap;
import java.util.UUID;

public class UserManagment {

	public static UserManagment instance = new UserManagment();
	public HashMap<String, User> maptokenuser; 
	
	public UserManagment() {
		maptokenuser = new HashMap<>();
	}
	
	public static UserManagment getInstance() {
		return instance;
	}
	
	public User getUser(String token) {
		return maptokenuser.get(token);
	}
	
	public String addUser(String name,String mdp) {
		String token = generateToken();
		maptokenuser.put(token, new User(name, mdp));
		return token;
	}

	private String generateToken() {
		return UUID.randomUUID().toString();
	}
	
}
