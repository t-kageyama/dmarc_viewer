package jp.co.comona.dmarcviewer;

/**
 * constant values.
 * @author kageyama
 * date: 2025/05/09
 */
public interface Constants {

	// MARK: - Titles
	public static final String APP_TITLE = "DMARC Viewer";

	// MARK: - Message
	public static final String MESSAGE_END_MUST_GREATER = "end must be equal or greater than begin";

	// MARK: - Properties Keys
	public static final String PROP_DATABASE_CLASS = "database_class";
	public static final String PROP_DATABASE_URL = "database_url";
	public static final String PROP_DATABASE_USER = "database_user";
	public static final String PROP_DATABASE_PASSWORD = "database_password";
	public static final String PROP_DATABASE_AUTH_REQUIRED = "database_auth_required";
	public static final String PROP_STORE_DATABASE_AUTH = "store_database_auth";

	public static final String PROP_MAIN_SCREEN_WIDTH = "MAIN_SCREEN_WIDTH";
	public static final String PROP_MAIN_SCREEN_HEIGHT = "MAIN_SCREEN_HEIGHT";

	public static final String PROP_RECORD_LIST_SCREEN_WIDTH = "RECORD_LIST_SCREEN_WIDTH";
	public static final String PROP_RECORD_LIST_SCREEN_HEIGHT = "RECORD_LIST_SCREEN_HEIGHT";

	public static final String PROP_RECORD_DETAIL_SCREEN_WIDTH = "RECORD_DETAIL_SCREEN_WIDTH";
	public static final String PROP_RECORD_DETAIL_SCREEN_HEIGHT = "RECORD_DETAIL_SCREEN_HEIGHT";

	public static final String PROP_WIDTH_SUFFIX = "_WIDTH";
	public static final String PROP_MAIN_WINDOW_PREFIX = "MAIN_WINDOW_";
	public static final String PROP_RECORD_LIST_PREFIX = "RECORD_LIST_";
	public static final String PROP_RECORD_DETAIL_PREFIX = "RECORD_DETAIL_";

	public static final String PROP_DATE_TIME_FORMAT = "date_time_format";

	// MARK: - Strings
	public static final String STR_TRUE = "true";
	public static final String STR_FALSE = "false";
	public static final String STR_PASSWORD_KEY = "DMARC_Viewer_Tool";
	public static final String STR_ALGORITHM = "BLOWFISH";
	public static final String STR_DETAIL = "Detail";
	public static final String STR_IGNORE_CASE = "ignore case";
	public static final String STR_NOT = "not";

	// MARK: - Integer Values.
	public static final int DEFAULT_MAIN_SCREEN_WIDTH = 640;
	public static final int MINIMUM_MAIN_SCREEN_WIDTH = DEFAULT_MAIN_SCREEN_WIDTH / 4;
	public static final int DEFAULT_MAIN_SCREEN_HEIGHT = 480;
	public static final int MINIMUM_MAIN_SCREEN_HEIGHT = DEFAULT_MAIN_SCREEN_HEIGHT / 4;
	public static final int USER_PASSWORD_BOX_WIDTH = 300;
	public static final int USER_PASSWORD_BOX_HEIGHT = 200;
	public static final int DEFAULT_RECORD_LIST_SCREEN_WIDTH = DEFAULT_MAIN_SCREEN_WIDTH;
	public static final int MINIMUM_RECORD_LIST_SCREEN_WIDTH = DEFAULT_RECORD_LIST_SCREEN_WIDTH / 4;
	public static final int DEFAULT_RECORD_LIST_SCREEN_HEIGHT = DEFAULT_MAIN_SCREEN_HEIGHT;
	public static final int MINIMUM_RECORD_LIST_SCREEN_HEIGHT = DEFAULT_RECORD_LIST_SCREEN_HEIGHT - 120;
	public static final int DEFAULT_RECORD_DETAIL_SCREEN_WIDTH = DEFAULT_MAIN_SCREEN_WIDTH;
	public static final int MINIMUM_RECORD_DETAIL_SCREEN_WIDTH = DEFAULT_RECORD_DETAIL_SCREEN_WIDTH / 4;
	public static final int DEFAULT_RECORD_DETAIL_SCREEN_HEIGHT = DEFAULT_MAIN_SCREEN_HEIGHT;
	public static final int MINIMUM_RECORD_DETAIL_SCREEN_HEIGHT = DEFAULT_RECORD_DETAIL_SCREEN_HEIGHT - 80;
	public static final int SEARCH_OPTION_BOX_WIDTH = 620;
	public static final int SEARCH_OPTION_BOX_HEIGHT = 800;

	// MARK: - Default Component Size
	public static final int DEFAULT_X_MARGIN = 8;
	public static final int DEFAULT_Y_MARGIN = 8;
	public static final int DEFAULT_BUTTON_WIDTH = 128;
	public static final int DEFAULT_BUTTON_HEIGHT = 30;
	public static final int DEFAULT_TEXT_HEIGHT = 30;
	public static final int DEFAULT_LABEL_WIDTH = 64;
	public static final int DEFAULT_LABEL_HEIGHT = 30;
	public static final int DEFAULT_STORE_PASSWORD_WIDTH = 120;
	public static final int DEFAULT_COLUMN_WIDTH = 100;
	public static final int DEFAULT_COMBO_WIDTH = 100;
	public static final int DEFAULT_COMBO_HEIGHT = 30;
	public static final int DEFAULT_DATETIME_WIDTH = 128;
	public static final int DEFAULT_DATETIME_HEIGHT = 30;
}
