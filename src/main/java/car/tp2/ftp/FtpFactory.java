package car.tp2.ftp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class FtpFactory {

	public FtpRequest buildUserRequest(String user) {
		return new FtpRequest("USER " + user);
	}

	public FtpRequest buildPasswordRequest(String password) {
		return new FtpRequest("PASS " + password);
	}
	
	public FtpRequest buildSetPassiveRequest() {
		return new FtpRequest("EPSV");
	}

	public SocketAddress buildInetAddress(String commandAddress, int commandPort) {
		return new InetSocketAddress(commandAddress, commandPort);
	}

	public Socket buildEmptySocket() {
		return new Socket();
	}

	public FtpReply buildResponse(String response) {
		if(response == null)
			return null;
		String[] split = response.split(" ");
		String code = split[0];
		String text = null;
		if(split.length > 1){
			text = "";
			for(int i = 1; i < split.length; i++){
				text+=split[i]+" ";
			}
			text.trim();
		}
		return new FtpReply(code, text);
	}

	public BufferedReader buildCommandSocketReader(Socket socket) throws IOException {
		return new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public PrintWriter buildCommandSocketWriter(Socket socket) throws IOException {
		return new PrintWriter(socket.getOutputStream());
	}
	
	public DataInputStream buildDataSocketReader(Socket socket) throws IOException {
		return new DataInputStream(socket.getInputStream());
	}
	
	public DataOutputStream buildDataSocketWriter(Socket socket) throws IOException {
		return new DataOutputStream(socket.getOutputStream());
	}

	public FtpRequest buildRetrCommand(String path) {
		return new FtpRequest("RETR " + path);
	}

	public FtpRequest buildListRequest(String path) {
		return new FtpRequest("LIST " + path);
	}

	public FtpRequest buildCwdRequest(String path) {
		return new FtpRequest("CWD " + path);
	}

	public FtpRequest buildRmdCommand(String path) {
		return new FtpRequest("RMD " + path);
	}

}
