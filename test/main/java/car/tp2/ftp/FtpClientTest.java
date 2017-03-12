package car.tp2.ftp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import car.tp2.ftp.FtpClient;
import car.tp2.ftp.FtpException;
import car.tp2.ftp.FtpFactory;
import car.tp2.ftp.FtpReply;
import car.tp2.ftp.FtpRequest;
import car.tp2.ftp.socket.FtpCommandSocket;
import car.tp2.utility.FtpConfig;

public class FtpClientTest {

	protected FtpClient ftpClient;
	
	protected FtpCommandSocket ftpSocket;
	
	protected FtpFactory ftpFactory;
	
	protected FtpConfig ftpConfig;
	
	protected int commandPort, dataPort;
	
	protected String commandAddress, user, password;
	
	@Before
	public void setUp() {
		this.ftpSocket = mock(FtpCommandSocket.class);
		this.ftpFactory = mock(FtpFactory.class);
		this.ftpConfig = mock(FtpConfig.class);
		this.ftpClient = new FtpClient(this.ftpSocket, this.ftpFactory, this.ftpConfig);
		this.commandAddress = "localhost";
		this.commandPort = 2020;
		this.dataPort = 2021;
		this.user = "lucas";
		this.password = "password";
	}

	@Test
	public void testOpenSocketOnCorrectPort() throws FtpException, IOException {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		when(this.ftpConfig.getCommandPort()).thenReturn(2020);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		Assert.assertTrue(this.ftpClient.isSocketOpen());
		Assert.assertEquals(this.commandPort, this.ftpClient.getConfig().getCommandPort());
	}
	
	@Test
	public void testOpenSocketFails() throws FtpException {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(false);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		Assert.assertFalse(this.ftpClient.isSocketOpen());
		Assert.assertEquals(0, this.ftpClient.getConfig().getCommandPort());
	}
	
	@Test
	public void testOpenSocketFailsMessageIsNull() throws IOException, FtpException {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn(null);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		Assert.assertFalse(this.ftpClient.isSocketOpen());
		Assert.assertEquals(0, this.ftpClient.getConfig().getCommandPort());

	}
	
	@Test
	public void testOpenSocketFailsMessageIsNot220() throws FtpException, IOException {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("500 error");
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		Assert.assertFalse(this.ftpClient.isSocketOpen());
		Assert.assertEquals(0, this.ftpClient.getConfig().getCommandPort());
	}
	
	@Test(expected=FtpException.class)
	public void testOpenSocketFailsIOException() throws FtpException, IOException {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenThrow(new IOException());
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
	}
	
	@Test
	public void testConnectClient() throws IOException, FtpException {
		//setUp
		FtpRequest userRequest = mock(FtpRequest.class);
		FtpRequest passwordRequest = mock(FtpRequest.class);
		FtpReply userReply = mock(FtpReply.class);
		FtpReply passwordReply = mock(FtpReply.class);
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
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
	public void testSetPassive() throws FtpException, IOException {
		FtpRequest userRequest = mock(FtpRequest.class);
		FtpRequest passwordRequest = mock(FtpRequest.class);
		FtpRequest setPassiveRequest = mock(FtpRequest.class);
		FtpReply userReply = mock(FtpReply.class);
		FtpReply passwordReply = mock(FtpReply.class);
		FtpReply setPassiveReply = mock(FtpReply.class);
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		when(this.ftpFactory.buildUserRequest(this.user)).thenReturn(userRequest);
		when(this.ftpFactory.buildPasswordRequest(this.password)).thenReturn(passwordRequest);
		when(this.ftpFactory.buildSetPassiveRequest()).thenReturn(setPassiveRequest);
		when(this.ftpSocket.sendAndWaitForReply(userRequest)).thenReturn(userReply);
		when(this.ftpSocket.sendAndWaitForReply(passwordRequest)).thenReturn(passwordReply);
		when(this.ftpSocket.sendAndWaitForReply(setPassiveRequest)).thenReturn(setPassiveReply);
		when(userReply.isOk("331")).thenReturn(true);
		when(passwordReply.isOk("230")).thenReturn(true);
		when(setPassiveReply.isOk("229")).thenReturn(true);
		when(setPassiveReply.getMessage()).thenReturn("Passage en mode passif etendu (|||2021|) ");
		when(this.ftpConfig.getDataPort()).thenReturn(2021);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		// function call
		this.ftpClient.connect(this.user, this.password);
		this.ftpClient.setPassive();
		// verify / oracle
		Assert.assertEquals(this.dataPort, this.ftpClient.getDataPort());
	}
	
	@Test(expected=FtpException.class)
	public void testSetPassiveFailsNotConnected() throws FtpException, IOException {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		this.ftpClient.setPassive();
	}
	
	@Test(expected=FtpException.class)
	public void testSetPassiveFailsRequestThrowsException() throws IOException, FtpException {
		FtpRequest userRequest = mock(FtpRequest.class);
		FtpRequest passwordRequest = mock(FtpRequest.class);
		FtpRequest setPassiveRequest = mock(FtpRequest.class);
		FtpReply userReply = mock(FtpReply.class);
		FtpReply passwordReply = mock(FtpReply.class);
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		when(this.ftpFactory.buildUserRequest(this.user)).thenReturn(userRequest);
		when(this.ftpFactory.buildPasswordRequest(this.password)).thenReturn(passwordRequest);
		when(this.ftpFactory.buildSetPassiveRequest()).thenReturn(setPassiveRequest);
		when(this.ftpSocket.sendAndWaitForReply(userRequest)).thenReturn(userReply);
		when(this.ftpSocket.sendAndWaitForReply(passwordRequest)).thenReturn(passwordReply);
		when(this.ftpSocket.sendAndWaitForReply(setPassiveRequest)).thenThrow(new FtpException(""));
		when(userReply.isOk("331")).thenReturn(true);
		when(passwordReply.isOk("230")).thenReturn(true);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		// function call
		this.ftpClient.connect(this.user, this.password);
		this.ftpClient.setPassive();
	}
	
	@Test(expected=FtpException.class)
	public void testSetPassiveFailsReplyIsNot229() throws IOException, FtpException {
		FtpRequest userRequest = mock(FtpRequest.class);
		FtpRequest passwordRequest = mock(FtpRequest.class);
		FtpRequest setPassiveRequest = mock(FtpRequest.class);
		FtpReply userReply = mock(FtpReply.class);
		FtpReply passwordReply = mock(FtpReply.class);
		FtpReply setPassiveReply = mock(FtpReply.class);
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		when(this.ftpFactory.buildUserRequest(this.user)).thenReturn(userRequest);
		when(this.ftpFactory.buildPasswordRequest(this.password)).thenReturn(passwordRequest);
		when(this.ftpFactory.buildSetPassiveRequest()).thenReturn(setPassiveRequest);
		when(this.ftpSocket.sendAndWaitForReply(userRequest)).thenReturn(userReply);
		when(this.ftpSocket.sendAndWaitForReply(passwordRequest)).thenReturn(passwordReply);
		when(this.ftpSocket.sendAndWaitForReply(setPassiveRequest)).thenReturn(setPassiveReply);
		when(userReply.isOk("331")).thenReturn(true);
		when(passwordReply.isOk("230")).thenReturn(true);
		when(setPassiveReply.isOk("229")).thenReturn(false);
		when(setPassiveReply.getMessage()).thenReturn("Passage en mode passif etendu (|||2021|) ");
		when(this.ftpConfig.getDataPort()).thenReturn(2021);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		// function call
		this.ftpClient.connect(this.user, this.password);
		this.ftpClient.setPassive();
	}
	
	@Test(expected=FtpException.class)
	public void testSetPassiveFailsReplyPortIsNotNumeric() throws IOException, FtpException {
		FtpRequest userRequest = mock(FtpRequest.class);
		FtpRequest passwordRequest = mock(FtpRequest.class);
		FtpRequest setPassiveRequest = mock(FtpRequest.class);
		FtpReply userReply = mock(FtpReply.class);
		FtpReply passwordReply = mock(FtpReply.class);
		FtpReply setPassiveReply = mock(FtpReply.class);
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		when(this.ftpFactory.buildUserRequest(this.user)).thenReturn(userRequest);
		when(this.ftpFactory.buildPasswordRequest(this.password)).thenReturn(passwordRequest);
		when(this.ftpFactory.buildSetPassiveRequest()).thenReturn(setPassiveRequest);
		when(this.ftpSocket.sendAndWaitForReply(userRequest)).thenReturn(userReply);
		when(this.ftpSocket.sendAndWaitForReply(passwordRequest)).thenReturn(passwordReply);
		when(this.ftpSocket.sendAndWaitForReply(setPassiveRequest)).thenReturn(setPassiveReply);
		when(userReply.isOk("331")).thenReturn(true);
		when(passwordReply.isOk("230")).thenReturn(true);
		when(setPassiveReply.isOk("229")).thenReturn(true);
		when(setPassiveReply.getMessage()).thenReturn("Passage en mode passif etendu (|||bad_format|) ");
		when(this.ftpConfig.getDataPort()).thenReturn(2021);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		// function call
		this.ftpClient.connect(this.user, this.password);
		this.ftpClient.setPassive();
		// verify / oracle
		Assert.assertEquals(this.dataPort, this.ftpClient.getDataPort());
	}
	
}
