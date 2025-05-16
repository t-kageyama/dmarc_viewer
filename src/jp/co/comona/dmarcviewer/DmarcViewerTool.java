package jp.co.comona.dmarcviewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jp.co.comona.dmarcviewer.cipher.SSHKeyTool;
import jp.co.comona.dmarcviewer.util.CommandLineParser;

/**
 * DMARC XML viewer tool.
 * @author kageyama
 * date: 2025/05/09
 */
public class DmarcViewerTool implements Constants {

	// MARK: - Properties
	private String propertiesPath = null;
	private Properties properties = null;
	private Connection connection = null;
	private String dbURL = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param propertiesPath properties file path.
	 */
	public DmarcViewerTool(String propertiesPath) {
		super();
		this.propertiesPath = propertiesPath;
	}

	// MARK: - Process
	/**
	 * main process.
	 * @return 0 if success.
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	private int process() throws IOException, ClassNotFoundException, SQLException {
		prepareProperties();
		prepareConnection();


		// show main window.
		MainWindow mainWnd = new MainWindow(this);
		mainWnd.open();

		if (connection != null) {
			disconnectConnection();
		}
		writeProperties();

		return 0;
	}

	// MARK: - Properties
	/**
	 * get property string.
	 * @param key property key.
	 * @param defaultValue default value.
	 * @return property string.
	 */
	public String getPropertyString(String key, String defaultValue) {
		String ret = properties.getProperty(key);
		if (ret == null) {
			ret = defaultValue;
		}
		return ret;
	}

	/**
	 * get property string.
	 * @param key property key.
	 * @param value property value.
	 */
	public void setPropertyString(String key, String value) {
		properties.setProperty(key, value);
	}

	/**
	 * get property integer.
	 * @param key property key.
	 * @param defaultValue default value.
	 * @return property value.
	 */
	public int getPropertyInt(String key, int defaultValue) {
		try {
			String value = getPropertyString(key, "" + defaultValue);
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * get property integer.
	 * @param key property key.
	 * @param value property value.
	 */
	public void setPropertyInt(String key, int value) {
		properties.setProperty(key, "" + value);
	}

	/**
	 * get property boolean.
	 * @param key property key.
	 * @param defaultValue default value.
	 * @return property value.
	 */
	public boolean getPropertyBool(String key, boolean defaultValue) {
		return STR_TRUE.equals(getPropertyString(key, defaultValue ? STR_TRUE : STR_FALSE));
	}

	/**
	 * set property boolean.
	 * @param key property key.
	 * @param value property value.
	 */
	public void setPropertyBool(String key, boolean value) {
		if (value) {
			setPropertyString(key, STR_TRUE);
		}
		else {
			removeProperty(key);
		}
	}

	/**
	 * get property password.
	 * @param key property key.
	 * @return property value.
	 */
	public String getPropertyPassword(String key) {
		String prop = getPropertyString(key, "");
		if (prop.length() > 0) {
			try {
				byte[] encrypted = SSHKeyTool.base64Bytes(prop);
				SecretKeySpec sksSpec = new SecretKeySpec(STR_PASSWORD_KEY.getBytes(), STR_ALGORITHM);
				Cipher cipher = Cipher.getInstance(STR_ALGORITHM);
				cipher.init(Cipher.DECRYPT_MODE, sksSpec);
				byte[] decrypted = cipher.doFinal(encrypted);
				prop = new String(decrypted);
			}
			catch (Exception e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}
		}
		return prop;
	}

	/**
	 * set property password.
	 * @param key property key.
	 */
	public void setPropertyPassword(String key, String value) {
		assert(value != null);
		assert(value.length() > 0);
		try {
			SecretKeySpec sksSpec = new SecretKeySpec(STR_PASSWORD_KEY.getBytes(), STR_ALGORITHM);
			Cipher cipher = Cipher.getInstance(STR_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
			byte[] encrypted = cipher.doFinal(value.getBytes());
			String str = SSHKeyTool.base64String(encrypted);
			setPropertyString(key, str);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}
	}

	/**
	 * set property.
	 * @param key property key.
	 * @param value property value.
	 */
	public void removeProperty(String key) {
		properties.remove(key);
	}

	// Setup
	/**
	 * prepare properties file.
	 * @throws IOException
	 */
	private void prepareProperties() throws IOException {
		File file = new File(propertiesPath);
		properties = new Properties();
		FileReader reader = new FileReader(file);
		properties.load(reader);
		reader.close();
	}

	/**
	 * wrote properties.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void writeProperties() throws FileNotFoundException, IOException {
		properties.store(new FileOutputStream(propertiesPath), "");
	}

	/**
	 * prepare database connection.
	 * @throws ClassNotFoundException
	 * @throws SQLException 
	 */
	private void prepareConnection() throws ClassNotFoundException, SQLException {
		String className = getPropertyString(PROP_DATABASE_CLASS, null);
		dbURL = getPropertyString(PROP_DATABASE_URL, null);
		String dbUser = getPropertyString(PROP_DATABASE_USER, null);
		String dbPassword = getPropertyPassword(PROP_DATABASE_PASSWORD);
		boolean authRequired = getPropertyBool(PROP_DATABASE_AUTH_REQUIRED, false);
		boolean storeDbAuth = getPropertyBool(PROP_STORE_DATABASE_AUTH, false);

		Class.forName(className);

		if (authRequired == false) {
			connection = DriverManager.getConnection(dbURL);	// SQLite3 must be here.
		}
		if (connection == null) {
			if ((dbUser != null) && (dbUser.length() > 0) && (dbPassword != null) && (dbPassword.length() > 0)) {
				createAuthConnection(dbUser, dbPassword, storeDbAuth);
			}
		}
	}

	/**
	 * create authenticated database connection.
	 * @throws SQLException
	 */
	public void createAuthConnection(String dbUser, String dbPassword, boolean storeDbAuth) throws SQLException {
		connection = DriverManager.getConnection(dbURL, dbUser, dbPassword);

		setPropertyBool(PROP_STORE_DATABASE_AUTH, storeDbAuth);
		if (storeDbAuth) {
			setPropertyString(PROP_DATABASE_USER, dbUser);	// update database user.
			setPropertyPassword(PROP_DATABASE_PASSWORD, dbPassword);	// update database password.
		}
		else {
			removeProperty(PROP_DATABASE_USER);
			removeProperty(PROP_DATABASE_PASSWORD);
		}
	}

	/**
	 * disconnect database connection.
	 * @throws SQLException
	 */
	public void disconnectConnection() throws SQLException {
		assert(connection != null);
		connection.close();
		connection = null;
	}

	// MARK: - Getters & Setters
	/**
	 * get database connection.
	 * @return database connection.
	 */
	public Connection getConnection() {
		return connection;
	}

	// MARK: - Entry Point
	/**
	 * entry point.
	 * @param args arguments.
	 */
	public static void main(String[] args) {
		String help = CommandLineParser.parseArgument(args, "-?");
		if (help != null) {
			doUsage();
			return;
		}

		String properties = CommandLineParser.parseArgument(args, "-p");
		if ((properties == null) || (properties.length() < 1)) {
			doUsage();
			System.exit(-1);
		}

		int result = -1;
		DmarcViewerTool tool = new DmarcViewerTool(properties);
		try {
			result = tool.process();
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			result = -1;
		}

		System.exit(result);
	}

	/**
	 * do usage.
	 */
	private static void doUsage() {
		System.out.println("DmarcViewerTool -p proerties-file-path");
	}
}
