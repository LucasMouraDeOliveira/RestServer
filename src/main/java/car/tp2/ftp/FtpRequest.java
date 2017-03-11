package car.tp2.ftp;

public class FtpRequest {
	
	protected String command;
	
	public FtpRequest(String command) {
		this.command = command;
	}
	
	public String getText() {
		return command;
	}

}
