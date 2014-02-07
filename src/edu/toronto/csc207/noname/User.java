package edu.toronto.csc207.noname;

/**
 * User template
 */
class User {

	private static String name; // reserved
	private static String email;
	private static String password;
	private static boolean loggedin = false;

	/**
	 * Set name of the user
	 * @param name user's name
	 */
	public static void setName(String name) {
		User.name = name;
	}
	
	/**
	 * Get user's name
	 * @return user's name
	 */
	public static String getName() {
		return name;
	}

	/**
	 * Set the user's log in status
	 * 
	 * @param loggedin
	 *            true if user count as logged in, false otherwise
	 */
	static void setLoggedin(boolean loggedin) {
		User.loggedin = loggedin;
	}

	/**
	 * Check whether the user is logged in
	 * 
	 * @return whether the user is logged in
	 */
	static boolean isLoggedin() {
		return loggedin;
	}

	/**
	 * Set email for JTunes cloud access
	 * 
	 * @param email
	 *            the email used to access JTunes cloud
	 */
	static void setEmail(String email) {
		User.email = email;
	}

	/**
	 * Get email to access JTunes cloud
	 * 
	 * @return the email to access JTunes cloud
	 */
	static String getEmail() {
		return email;
	}

	/**
	 * Set password for JTunes cloud access
	 * 
	 * @param password
	 *            the password used to access JTunes cloud
	 */
	static void setPassword(String password) {
		User.password = password;
	}

	/**
	 * Get the password to access JTunes cloud
	 * 
	 * @return the password to access JTunes cloud
	 */
	static String getPassword() {
		return password;
	}
}
