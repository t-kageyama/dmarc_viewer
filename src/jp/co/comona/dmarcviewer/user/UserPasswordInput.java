package jp.co.comona.dmarcviewer.user;

/**
 * user id/password input.
 * @author kageyama
 * date: 2025/05/09
 */
public class UserPasswordInput {

	// MARK: - Properties
	private String userId = null;
	private String password = null;
	private boolean storePassword = false;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param userId user id.
	 * @param password password.
	 * @param storePassword store password.
	 */
	protected UserPasswordInput(String userId, String password, boolean storePassword) {
		super();
		this.userId = userId;
		this.password = password;
		this.storePassword = storePassword;
	}

	// MARK: - Getters & Setters
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the storePassword
	 */
	public boolean isStorePassword() {
		return storePassword;
	}

	/**
	 * @param storePassword the storePassword to set
	 */
	public void setStorePassword(boolean storePassword) {
		this.storePassword = storePassword;
	}
}
