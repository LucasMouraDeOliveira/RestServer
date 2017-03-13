# RestServer

## Auteurs :

Lucas Moura de Oliveira
Eliott Bricout

Fait le 13/03/2017

## Description

 Ce programme est une passerelle REST permettant d'acc�der � un serveur FTP via une vue HTML.

## Exécution

 Le jar du programme est exécutable avec la commande :
 
	java -jar Rest-0.1.0.jar
	
 Le serveur est lancé sur le port 8080 et l'adresse de connexion est localhost:8080/user.

 Pour que la passerelle REST fonctionne correctement, il est nécessaire de lancer également le jar du serveur FTP :
 
	java -jar FtpServer.jar
	
 Un fichier de configuration (configuration.properties) permet de configurer les ports et adresses du serveur FTP.
 Par défaut l'adresse de connexion est localhost, le port de commande le 2021 et le port de données le 2020.
 
## Architecture

### Classes abstraites : 

 - ftp.socket.FtpSocket.java : Définit les attributs et méthodes communs des deux types de sockets utilisées (socket de commande et socket de données).

### Interfaces :

 - ftp.socket.ICommandSocket.java : Signatures des méthodes d'envoi de données de la classe ftp.socket.FtpCommandSocket.java

### Classes polymorphiques :

 - ftp.socket.FtpCommandSocket.java : Méthodes de communication avec le serveur FTP via la socket de commande. Etends la classe abstraite FtpSocket.java
 - ftp.socket.FtpDataSocket.java : Méthodes de communication avec le serveur FTP via la socket de données. Etends la classe abstraite FtpSocket.java
 
## Codes samples :

### Système de tokens : 

Pour gérer la connexion des utilisateurs au serveur FTP, on a mis en place un système de token que l'utilisateur identifié envoie en même temps que ses requêtes. Un formulaire de connexion sur la page d'accueil de l'application permet de vérifier que l'utilisateur est connu par le serveur FTP. Une fois cette vérification faite, la passerelle REST lui transmet un token généré aléatoirement.
 
Les tokens sont stockés dans une HashMap<String, User> dans user.UserManagement.java : 

	public HashMap<String, User> maptokenuser; 
	
et l'ajout d'utilisateur se fait par la méthode suivante : 

	public String addUser(String name,String mdp) {
			String token = generateToken();
			maptokenuser.put(token, new User(name, mdp));
			return token;
	}
	
### sendAndWaitForReply :

 Méthode qui envoie une requête au serveur FTP et bloque l'application tant qu'une réponse n'a pas été reçue :
 
	@Override
	public FtpReply sendAndWaitForReply(FtpRequest request) {
		this.send(request);
		try {
			return this.ftpFactory.buildResponse(this.readLine());
		} catch (IOException e) {
			return null;
		}
	}