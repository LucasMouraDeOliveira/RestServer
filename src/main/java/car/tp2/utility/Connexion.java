package car.tp2.utility;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Classe utilitaire gérant l'envoi et la réception de messages par sockets
 * 
 * @author Lucas Moura de Oliveira
 */
public class Connexion {
	
	/**
	 * Envoie un message au format texte à une socket client.
	 * Si le message est null, rien ne se passe
	 * 
	 * @param writer le writer de la socket
	 * @param message le message
	 */
	public static void write(PrintWriter writer, String message){
		if(message == null)
			return;
		writer.write(message+"\r\n");
		writer.flush();
		System.out.println("Message envoyé : " + message);
	}
	
	/**
	 * Reçoit un message au format texte depuis une socket client
	 * 
	 * @param reader le reader de la socket
	 * 
	 * @return le message reçu par la socket, null s'il y a eu une erreur lors de la transmission
	 */
	public static String read(BufferedReader reader){
		try {
			String message = reader.readLine();
			System.out.println("Message reçu : " + message);
			return message;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}

	/**
	 * Ecrit un message au format binaire dans une socket client.
	 * Si le message est null, rien n'est envoyé
	 * 
	 * @param writer le writer de la socket
	 * @param binary un tableau de byte constituant le message
	 */
	public static void writeBinary(DataOutputStream writer, byte[] binary) {
		if(binary == null)
			return;
		try {
			writer.write(binary);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		System.out.println("Message envoyé : " + binary.length +" octet");
	}
	
	/**
	 * Lit un message au format binaire depuis une socket client
	 * 
	 * @param reader le reader de la socket
	 * 
	 * @return un tableau de byte correspondant au message envoyé par la socket, null s'il y a eu une erreur de transmission
	 */
	public static byte[] readBinary(DataInputStream reader) {
		byte[] data = new byte[4096];
		int numread = 0;
		try {
			numread = reader.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(numread <= 0)
			return null;
		else if (numread == data.length) 
			return data;
	    else
	        return Arrays.copyOf(data, numread);
	}

}
