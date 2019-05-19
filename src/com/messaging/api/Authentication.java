package com.messaging.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class Authentication implements ContainerRequestFilter {
	public static final String AUTH_HEADER = "Authorization";
	public UsersDao dao = UsersDao.getInstance();
	private static Logger LOG = Logger.getLogger("com.messaging.api.Authentication");

	@Override
	public void filter(ContainerRequestContext request) throws WebApplicationException {
		LOG.info("Logger created successfully!");
		String credentials = request.getHeaderString(AUTH_HEADER);
		if (credentials == null) {
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}
		final String encodedUserPassword = credentials.replaceFirst("Basic" + " ", "");
		String usernameAndPassword = null;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
			usernameAndPassword = new String(decodedBytes, "UTF-8");

			final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
			final String username = tokenizer.nextToken();
			final String password = tokenizer.nextToken();

			int id = dao.authenticate(username, password);
			if (id == -1) {
				LOG.info("authentication failed for " + username);
				throw new WebApplicationException(Response.Status.FORBIDDEN);
			}
			LOG.info("authentication succeded for " + username + ", id is: " + id);
		} catch (IOException | SQLException e) {
			LOG.warning("exception occurred during authentication " + e);
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}
	}

}
