package car.tp2.ftp.socket;

import java.io.IOException;
import java.net.Socket;

import car.tp2.ftp.FtpException;
import car.tp2.ftp.FtpFactory;

public abstract class FtpSocket {
	
	protected FtpFactory ftpFactory;
	
	protected Socket socket;
		
	public FtpSocket(FtpFactory ftpFactory) throws IOException {
		this.ftpFactory = ftpFactory;
		this.socket = this.ftpFactory.buildEmptySocket();
	}
	
	public boolean openSocket(String commandAddress, int commandPort) throws FtpException{
		try {
			this.socket.connect(this.ftpFactory.buildInetAddress(commandAddress, commandPort));
			this.openReaders(); 
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public abstract void openReaders() throws IOException;
	
	public abstract void closeReaders() throws IOException;
	
	public abstract String readLine() throws IOException;
	
//	public FtpReply sendAndWaitForReply(FtpRequest request) throws FtpException {
//		this.writeLineInWriter(request.getText());
//		return this.ftpFactory.buildResponse(readLineInReader());
//	}
//	
//	public void send(FtpRequest request) throws FtpException {
//		this.writeLineInWriter(request.getText());
//	}
//	
//	public abstract String readLineInReader() throws FtpException;
////	{
////		try {
////			return this.reader.readLine();
////		} catch(IOException e) {
////			throw new FtpException("Erreur de lecture dans le reader de la socket");
////		}
////	}
//	
//	public abstract void writeLineInWriter(String text) throws FtpException;

}
