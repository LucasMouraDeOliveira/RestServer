package car.tp2.ftp.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import car.tp2.factory.FtpFactory;
import car.tp2.ftp.FtpException;
import car.tp2.ftp.FtpReply;
import car.tp2.ftp.FtpRequest;

public class FtpCommandSocket extends FtpSocket implements ICommandSocket{
	
	protected BufferedReader reader;
	
	protected PrintWriter writer;

	public FtpCommandSocket(FtpFactory ftpFactory) throws IOException {
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
	public FtpReply sendAndWaitForReply(FtpRequest request) throws FtpException{
		this.send(request);
		try {
			return this.ftpFactory.buildResponse(this.readLine());
		} catch (IOException e) {
			throw new FtpException("Erreur lors de la construction de la réponse FTP");
		}
	}
	
	public void writeLine(String text) {
		this.writer.write(text+"\n");
		this.writer.flush();
	}

}
