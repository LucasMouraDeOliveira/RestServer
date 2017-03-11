package car.tp2.ftp;

public class FtpReply {
	
	protected String code;
	
	protected String message;

	public FtpReply(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public boolean isOk(String expectedCode) {
		return expectedCode.equals(code);
	}

	public String getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return this.message;
	}

}
