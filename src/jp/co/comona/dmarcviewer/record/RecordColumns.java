package jp.co.comona.dmarcviewer.record;

/**
 * record columns.
 * @author kageyama
 * date: 2025/05/13
 */
public interface RecordColumns {

	// MARK: - Static Property
	// dmarc_feedbacks.
	public static final String COLUMN_ORG_NAME = "org_name";
	public static final String COLUMN_REPORT_ID = "report_id";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_EXTRA_CONTACT_INFO = "extra_contact_info";
	public static final String COLUMN_BEGIN = "begin";
	public static final String COLUMN_END = "end";
	public static final String COLUMN_DOMAIN = "domain";
	public static final String COLUMN_ADKIM = "adkim";
	public static final String COLUMN_ASPF = "aspf";
	public static final String COLUMN_P = "p";
	public static final String COLUMN_SP = "sp";
	public static final String COLUMN_PCT = "pct";
	public static final String COLUMN_NP = "np";
	// dmarc_records.
	public static final String COLUMN_SOURCE_IP = "source_ip";
	public static final String COLUMN_COUNT = "count";
	public static final String COLUMN_DISPOSITION = "disposition";
	public static final String COLUMN_DKIM = "dkim";
	public static final String COLUMN_SPF = "spf";
	public static final String COLUMN_HEADER_FROM = "header_from";
	// dmarc_auth_results columns.
	public static final String AUTH_RESULTS_PREFIX = "auth_results_";
	public static final String COLUMN_AUTH_RESULTS_DOMAIN = AUTH_RESULTS_PREFIX + COLUMN_DOMAIN;
	public static final String COLUMN_AUTH_RESULTS_RESULT = AUTH_RESULTS_PREFIX + "result";
	public static final String COLUMN_AUTH_RESULTS_SELECTOR = AUTH_RESULTS_PREFIX + "selector";

	public static final String COLUMN_DKIM_COUNT = "dkim_count";
	public static final String COLUMN_SPF_COUNT = "spf_count";
}
