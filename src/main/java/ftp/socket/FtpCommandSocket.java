package ftp.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import factory.FtpFactory;
import ftp.FtpReply;
import ftp.FtpRequest;

/**
 * Classe gérant la communication avec le serveur FTP via sa socket de commande.
 * La classe définit des flux d'entrées et de sorties et implémente les méthodes de lecture et écriture dans ces flux.
 * 
 * @author Lucas Moura de Oliveira
 *
 */
public class FtpCommandSocket extends FtpSocket implements ICommandSocket{
	
	protected BufferedReader reader;
	
	protected PrintWriter writer;

	public FtpCommandSocket(FtpFactory ftpFactory) {
		super(ftpFactory);
	}

	@Override
	public void openReaders() throws IOException {
		this.reader = this.ftpFactory.buildCommandSocketReader(socket);
		this.writer = this.ftpFactory.buildCommandSocketWriter(socket);
	}

	@Override
	public void closeReaders() throws IOException {
		this.reader.close();
		this.writer.close();
	}

	@Override
	public String readLine() throws IOException {
		return this.reader.readLine();
	}

	@Override
	public void send(FtpRequest request) {
		this.writeLine(request.getText());
	}

	@Override
	public FtpReply sendAndWaitForReply(FtpRequest request) {
		this.send(request);
		try {
			return this.ftpFactory.buildResponse(this.readLine());
		} catch (IOException e) {
			return null;
		}
	}
	
	public void writeLine(String text) {
		this.writer.write(text+"\n");
		this.writer.flush();
	}

}
