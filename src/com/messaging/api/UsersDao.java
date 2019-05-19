package com.messaging.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import redis.clients.jedis.Jedis;

public class UsersDao {
	private static Connection c;
	private static Jedis redis;
	private static UsersDao dao = new UsersDao();
	private int accountID;
	private static Logger LOG = Logger.getLogger("com.messaging.api.UsersDao");

	private UsersDao() {
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://database-1.cqddluxkthda.us-east-2.rds.amazonaws.com:5432/postgres", "postgres", "postgres");
			c.setAutoCommit(false);
			redis = new Jedis("redis1.oeyhez.0001.use2.cache.amazonaws.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static UsersDao getInstance() {
		return dao;
	}

	public int authenticate(String user, String pwd) throws SQLException {
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery("select id from account where username='" + user + "' and auth_id='" + pwd + "'");
		if (res.next() == false) {
			return -1;
		}
		accountID = res.getInt("id");
		return accountID;
	}

	public String validateAndProcessIncomingSms(String from, String to, String text) {
		if (from == null) {
			return "from is missing";
		} else if (to == null) {
			return "to is missing";
		} else if (text == null) {
			return "text is missing";
		}
		String numberRegex = "\\d+";
		if (!from.matches(numberRegex)) {
			return "from is invalid";
		} else if (!to.matches(numberRegex)) {
			return "to is invalid";
		}
		try {
			Statement st = c.createStatement();
			ResultSet res;
			res = st.executeQuery("select id from phone_number where account_id=" + accountID + " and number='" + to + "'");
			if (res.next() == false) {
				return "to parameter not found";
			} else {
				// write to redis if text is "STOP"
				text = text.trim();
				if (text.equals("STOP")) {
					LOG.info("STOP request is received for " + from + " " + to + " communication not allowed for 4 hours");
					redis.setex(from.concat(to), 4 * 60 * 60, "true");
				}
			}
		} catch (SQLException e) {
			LOG.warning("exception occurred during validating input parameters" + e);
			e.printStackTrace();
			return "unknown failure";
		}
		return "";
	}

	public String validataAndProcessOutgoingSms(String from, String to, String text) {
		if (from == null) {
			return "from is missing";
		} else if (to == null) {
			return "to is missing";
		} else if (text == null) {
			return "text is missing";
		}
		String numberRegex = "\\d+";
		if (!from.matches(numberRegex)) {
			return "from is invalid";
		} else if (!to.matches(numberRegex)) {
			return "to is invalid";
		}
		try {
			Statement st = c.createStatement();
			ResultSet res = st.executeQuery("select id from phone_number where account_id=" + accountID + " and number='" + from + "'");
			if (res.next() == false) {
				return "from parameter not found";
			} else {
				// read redis to see if STOP is pushed for this conversation
				if (redis.exists(from.concat(to))) {
					return "sms from " + from + " to " + to + " blocked by STOP request";
				}
				// read redis and limit number of outgoing sms to 50
				if (redis.exists(from)) {
					if (Integer.parseInt(redis.get(from)) >= 50) {
						LOG.info("more than 50 requests are sent from " + from + " in last 24 hours");
						return "limit reached for from " + from;
					}
					redis.incr(from);
					LOG.info("messages sent so far from " + from + " is: " + redis.get(from));
				} else {
					redis.setex(from, 24 * 60 * 60, "1");
				}
			}
		} catch (SQLException e) {
			LOG.warning("exception occurred during validating output parameters" + e);
			e.printStackTrace();
			return "unknown failure";
		}
		return "";
	}
}
