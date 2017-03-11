package car.tp2.ftp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import car.tp2.ftp.socket.FtpSocket;

public class FtpClientTest {
	
	protected FtpClient ftpClient;
	
	protected FtpSocket ftpSocket;
	
	protected FtpFactory ftpFactory;
	
	protected int commandPort, dataPort;
	
	protected String commandAddress, user, password;

	@Before
	public void setUp() {
		this.ftpSocket = mock(FtpSocket.class);
		this.ftpFactory = mock(FtpFactory.class);
		this.ftpClient = new FtpClient(this.ftpSocket, this.ftpFactory);
		this.commandAddress = "localhost";
		this.commandPort = 2020;
		this.dataPort = 2021;
		this.user = "lucas";
		this.password = "password";
	}

	@Test
	public void testOpenSocketOnCorrectPort() {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		Assert.assertTrue(this.ftpClient.isSocketOpen());
		Assert.assertEquals(this.commandPort, this.ftpClient.getCommandPort());
	}
	
	@Test
	public void testOpenSocketFails() {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(false);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		Assert.assertFalse(this.ftpClient.isSocketOpen());
		Assert.assertEquals(0, this.ftpClient.getCommandPort());
	}
	
	@Test
	public void testConnectClient() throws IOException, FtpException {
		//setUp
		FtpRequest userRequest = mock(FtpRequest.class);
		FtpRequest passwordRequest = mock(FtpRequest.class);
		FtpReply userReply = mock(FtpReply.class);
		FtpReply passwordReply = mock(FtpReply.class);
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpFactory.buildUserRequest(this.user)).thenReturn(userRequest);
		when(this.ftpFactory.buildPasswordRequest(this.password)).thenReturn(passwordRequest);
		when(this.ftpSocket.sendAndWaitForReply(userRequest)).thenReturn(userReply);
		when(this.ftpSocket.sendAndWaitForReply(passwordRequest)).thenReturn(passwordReply);
		when(userReply.isOk("331")).thenReturn(true);
		when(passwordReply.isOk("230")).thenReturn(true);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		// function call
		this.ftpClient.connect(this.user, this.password);
		// verify / oracle
		Assert.assertTrue(this.ftpClient.isConnected());
	}
	
	@Test
	public void testConnectClientFailsUserUnknown() throws IOException, FtpException {
		//setUp
		FtpRequest userRequest = mock(FtpRequest.class);
		FtpReply userReply = mock(FtpReply.class);
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpFactory.buildUserRequest(this.user)).thenReturn(userRequest);
		when(this.ftpSocket.sendAndWaitForReply(userRequest)).thenReturn(userReply);
		when(userReply.isOk("331")).thenReturn(false);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		// function call
		try{
			this.ftpClient.connect(this.user, this.password);
			Assert.fail("Should have thrown an FTPException by now");
		} catch(FtpException ftpe){
			Assert.assertFalse(this.ftpClient.isConnected());
		}
		// verify / oracle
	}
	
	@Test
	public void testConnectClientFailsPasswordInvalid() throws IOException, FtpException{
		//setUp
		FtpRequest userRequest = mock(FtpRequest.class);
		FtpRequest passwordRequest = mock(FtpRequest.class);
		FtpReply userReply = mock(FtpReply.class);
		FtpReply passwordReply = mock(FtpReply.class);
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpFactory.buildUserRequest(this.user)).thenReturn(userRequest);
		when(this.ftpFactory.buildPasswordRequest(this.password)).thenReturn(passwordRequest);
		when(this.ftpSocket.sendAndWaitForReply(userRequest)).thenReturn(userReply);
		when(this.ftpSocket.sendAndWaitForReply(passwordRequest)).thenReturn(passwordReply);
		when(userReply.isOk("331")).thenReturn(true);
		when(passwordReply.isOk("230")).thenReturn(false);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		
		//A defaut de solution plus élégante, celle-ci devrait marcher
		try{
			// function call
			this.ftpClient.connect(this.user, this.password);
			Assert.fail("Should have thrown an FTPException by now");
		} catch(FtpException ftpe){
			Assert.assertFalse(this.ftpClient.isConnected());
		}
	}
	
	@Test
	public void testSetPassive() {
		
		//oracle
		Assert.assertEquals(this.ftpClient.getDataPort(), this.dataPort);
	}

}
