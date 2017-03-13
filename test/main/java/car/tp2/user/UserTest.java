package car.tp2.user;

import org.junit.Assert;
import org.junit.Test;

import user.User;

public class UserTest {
	
	@Test
	public void testCreateUser(){
		String name = "name";
		String mdp = "mdp";
		User user = new User(name,mdp);
		Assert.assertEquals(user.getName(), name);
		Assert.assertEquals(user.getPassword(), mdp);
	}
}
