/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.user;

import org.jivesoftware.openfire.user.DefaultUserProvider;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;

/**
 * Implementation of {@link DefaultUserProvider} to confirm the 
 * authorization of the sender
 * 
 * @author alex
 *
 */
public class UserProvider extends DefaultUserProvider{

	@Override
	public User createUser(String username, String password, String name, String email)
			throws UserAlreadyExistsException {
		// TODO Confirmar la autenticacion
		return super.createUser(username, password, name, email);
	}

}
