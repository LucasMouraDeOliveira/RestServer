package car.tp2.user;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import user.User;
import user.UserManagment;

public class UserManagmentTest {

	@Test
	public void testAddAndGetUser(){
		String name = "name";
		String mdp = "mdp";
		UserManagment um = UserManagment.getInstance();
		String token = um.addUser(name, mdp);
		User user = um.getUser(token);
		Assert.assertEquals(user.getName(), name);
		Assert.assertEquals(user.getPassword(), mdp);
	}
	
	@Test
	public void testGetUserWithTokenbidon(){
		UserManagment um = UserManagment.getInstance();
		Assert.assertNull(um.getUser("token_bidon"));
	}
}
