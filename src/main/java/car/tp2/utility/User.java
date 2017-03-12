package car.tp2.utility;

public class User {

	public String nom;
	public String mdp;

	
	
	public User(String nom, String mdp){
		this.nom = nom;
		this.mdp = mdp;
	}

	public String getMdp() {
		return mdp;
	}
	
	public String getNom() {
		return nom;
	}
}
