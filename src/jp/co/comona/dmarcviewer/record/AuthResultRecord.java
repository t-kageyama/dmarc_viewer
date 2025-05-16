package jp.co.comona.dmarcviewer.record;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jp.co.comona.dmarcviewer.xml.DmarcFeedback;
import jp.co.comona.dmarcviewer.xml.DmarcRecord;
import jp.co.comona.dmarcviewer.xml.DmarcSpf;

/**
 * authentication result record.
 * @author kageyama
 * date: 2025/05/10
 */
public class AuthResultRecord {

	// MARK: - Static Property
	public static final String COLUMN_ORG_NAME = Record.COLUMN_ORG_NAME;
	public static final String COLUMN_REPORT_ID = Record.COLUMN_REPORT_ID;
	public static final String COLUMN_SOURCE_IP = Record.COLUMN_SOURCE_IP;
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_ROW = "row";
	public static final String COLUMN_DOMAIN = Record.COLUMN_DOMAIN;
	public static final String COLUMN_SELECTOR = "selector";
	public static final String COLUMN_RESULT = "result";
	// date columns.
	protected static final String COLUMN_CREATED_AT = Record.COLUMN_CREATED_AT;
	protected static final String COLUMN_UPDATED_AT = Record.COLUMN_UPDATED_AT;

	public static final String TYPE_DKIM = "dkim";
	public static final String TYPE_SPF = "spf";

	private static final String AUTH_RESULTS_TABLE = Record.AUTH_RESULTS_TABLE;

	// MARK: -  Property
	private String org_name = null;
	private String report_id = null;
	private String source_ip = null;
	private String type = null;
	private int row = -1;
	private String domain = null;
	private String selector = null;
	private String result = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	public AuthResultRecord() {
		super();
	}

	/**
	 * constructor.
	 * @param type type. "dkim" or "spf".
	 * @param row row.
	 */
	public AuthResultRecord(String type, int row) {
		super();
		this.type = type;
		this.row = row;
	}

	// MARK: - SQL
	/**
	 * set data from SQL result.
	 * @param rs result set.
	 * @throws SQLException
	 */
	public void setData(ResultSet rs) throws SQLException {
		//org_name = rs.getString(COLUMN_ORG_NAME);
		//report_id = rs.getString(COLUMN_REPORT_ID);
		//source_ip = rs.getString(COLUMN_SOURCE_IP);
		type = rs.getString(COLUMN_TYPE);
		row = rs.getInt(COLUMN_ROW);
		domain = rs.getString(COLUMN_DOMAIN);
		selector = rs.getString(COLUMN_SELECTOR);
		result = rs.getString(COLUMN_RESULT);
	}

	// MARK: - From XML
	/**
	 * add a
	 * @param feedback
	 * @param record
	 */
	public void setData(DmarcFeedback feedback, DmarcRecord record, DmarcSpf spfDkim) {
		org_name = feedback.getOrgName();
		report_id = feedback.getReportId();
		source_ip = record.getSourceIp();
		domain = spfDkim.getDomain();
		selector = spfDkim.getSelector();
		result = spfDkim.getResult();
	}

	// MARK: - Getters
	/**
	 * get org_name.
	 * @return org_name.
	 */
	public String getOrgName() {
		return org_name;
	}

	/**
	 * get report_id.
	 * @return report_id.
	 */
	public String getReportId() {
		return report_id;
	}

	/**
	 * get source_ip.
	 * @return source_ip.
	 */
	public String getSourceIp() {
		return source_ip;
	}

	/**
	 * get type.
	 * @return type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * get row.
	 * @return row.
	 */
	public int getRow() {
		return row;
	}

	/**
	 * get domain.
	 * @return domain.
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * get selector.
	 * @return selector.
	 */
	public String getSelector() {
		return selector;
	}

	/**
	 * get result.
	 * @return result.
	 */
	public String getResult() {
		return result;
	}

	// MARK: - SQL
	/**
	 * store record.
	 * @param rec authenticate result record.
	 * @param con database connection.
	 * @throws SQLException
	 */
	protected static void insert(AuthResultRecord rec, Connection con) throws SQLException {
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(AUTH_RESULTS_TABLE).append(" (");
		sql.append(COLUMN_ORG_NAME).append(", ").append(COLUMN_REPORT_ID).append(", ");
		sql.append(COLUMN_SOURCE_IP).append(", ").append(COLUMN_TYPE).append(", ");
		sql.append(COLUMN_ROW).append(", ").append(COLUMN_DOMAIN).append(", ");
		sql.append(COLUMN_SELECTOR).append(", ").append(COLUMN_RESULT).append(", ");
		sql.append(COLUMN_CREATED_AT).append(", ").append(COLUMN_UPDATED_AT).append(") ");
		sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, DEFAULT, DEFAULT)");
		PreparedStatement stmt = con.prepareStatement(sql.toString());
		stmt.setString(1, rec.getOrgName());
		stmt.setString(2, rec.getReportId());
		stmt.setString(3, rec.getSourceIp());
		stmt.setString(4, rec.getType());
		stmt.setInt(5, rec.getRow());
		stmt.setString(6, rec.getDomain());
		stmt.setString(7, rec.getSelector());
		stmt.setString(8, rec.getResult());
		stmt.execute();
		stmt.close();
	}

	/**
	 * select records.
	 * @param rec parent record.
	 * @param con database connection.
	 * @throws SQLException 
	 */
	protected static void select(Record rec, Connection con) throws SQLException {
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(COLUMN_TYPE).append(", ").append(COLUMN_ROW).append(", ");
		sql.append(COLUMN_DOMAIN).append(", ").append(COLUMN_SELECTOR).append(", ").append(COLUMN_RESULT);
		sql.append(" FROM ").append(AUTH_RESULTS_TABLE);
		sql.append(" WHERE ").append(COLUMN_ORG_NAME).append(" = ?");
		sql.append(" AND ").append(COLUMN_REPORT_ID).append(" = ?");
		sql.append(" AND ").append(COLUMN_SOURCE_IP).append(" = ?");
		PreparedStatement stmt = con.prepareStatement(sql.toString());
		stmt.setString(1, rec.getOrgName());
		stmt.setString(2, rec.getReportId());
		stmt.setString(3, rec.getSourceIp());

		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			rec.addAuthResult(rs);
		}

		rs.close();
		stmt.close();
	}
}
