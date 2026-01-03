package jp.co.comona.dmarcviewer.record;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.co.comona.dmarcviewer.record.where.SearchOption;
import jp.co.comona.dmarcviewer.record.where.SearchOptions;
import jp.co.comona.dmarcviewer.xml.DmarcDkim;
import jp.co.comona.dmarcviewer.xml.DmarcFeedback;
import jp.co.comona.dmarcviewer.xml.DmarcRecord;
import jp.co.comona.dmarcviewer.xml.DmarcSpf;

/**
 * record.
 * @author kageyama
 * date: 2025/05/10
 */
public class Record implements RecordColumns {

	// date columns.
	protected static final String COLUMN_CREATED_AT = "created_at";
	protected static final String COLUMN_UPDATED_AT = "updated_at";

	private static final String DIVIDER = "__@@__";
	public static final String FEEDBACKS_TABLE = "dmarc_feedbacks";
	public static final String RECORDS_TABLE = "dmarc_records";
	public static final String AUTH_RESULTS_TABLE = "dmarc_auth_results";
	public static final String SUBQ_TABLE = "subq_table";
	private static final String[] RECORD_KEY_COLUMNS = { COLUMN_ORG_NAME, COLUMN_REPORT_ID, COLUMN_SOURCE_IP, };
	private static final String[] FEEDBACK_COLUMNS = { COLUMN_ORG_NAME, COLUMN_REPORT_ID, COLUMN_EMAIL, COLUMN_EXTRA_CONTACT_INFO,
		COLUMN_BEGIN, COLUMN_END, COLUMN_DOMAIN, COLUMN_ADKIM, COLUMN_ASPF, COLUMN_P, COLUMN_SP, COLUMN_PCT, COLUMN_NP, };
	private static final String[] RECORD_COLUMNS = { COLUMN_SOURCE_IP, COLUMN_COUNT, COLUMN_DISPOSITION,
		COLUMN_DKIM, COLUMN_SPF, COLUMN_HEADER_FROM, };

	// MARK: - Property
	private String org_name = null;
	private String report_id = null;
	private String email = null;
	private String extra_contact_info = null;
	private int begin = 0;
	private int end = 0;
	private String domain = null;
	private String adkim = null;
	private String aspf = null;
	private String p = null;
	private String sp = null;
	private int pct = 0;
	private String np = null;
	private String source_ip = null;
	private int count = 0;
	private String disposition = null;
	private String dkim = null;
	private String spf = null;
	private String header_from = null;
	private List<AuthResultRecord> dkims = null;
	private List<AuthResultRecord> spfs = null;
	private int pseudo_dkim_count = -1;
	private int pseudo_spf_count = -1;

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	public Record() {
		super();
	}

	// MARK: - SQL
	/**
	 * set data from SQL result.
	 * @param rs result set.
	 * @throws SQLException
	 */
	public void setData(ResultSet rs) throws SQLException {
		org_name = rs.getString(COLUMN_ORG_NAME);
		report_id = rs.getString(COLUMN_REPORT_ID);
		email = rs.getString(COLUMN_EMAIL);
		extra_contact_info = rs.getString(COLUMN_EXTRA_CONTACT_INFO);
		begin = rs.getInt(COLUMN_BEGIN);
		end = rs.getInt(COLUMN_END);
		domain = rs.getString(COLUMN_DOMAIN);
		adkim = rs.getString(COLUMN_ADKIM);
		aspf = rs.getString(COLUMN_ASPF);
		p = rs.getString(COLUMN_P);
		sp = rs.getString(COLUMN_SP);
		pct = rs.getInt(COLUMN_PCT);
		np = rs.getString(COLUMN_NP);
		source_ip = rs.getString(COLUMN_SOURCE_IP);
		count = rs.getInt(COLUMN_COUNT);
		disposition = rs.getString(COLUMN_DISPOSITION);
		dkim = rs.getString(COLUMN_DKIM);
		spf = rs.getString(COLUMN_SPF);
		header_from = rs.getString(COLUMN_HEADER_FROM);
		pseudo_dkim_count = rs.getInt(COLUMN_DKIM_COUNT);
		pseudo_spf_count = rs.getInt(COLUMN_SPF_COUNT);
	}

	/**
	 * prepare add authentication result.
	 */
	public void preapreAddAuthResult() {
		if (dkims == null) {
			dkims = new ArrayList<>();
		}
		if (spfs == null) {
			spfs = new ArrayList<>();
		}
	}

	/**
	 * add authentication result record.
	 * @param rs result set.
	 * @throws SQLException
	 */
	public void addAuthResult(ResultSet rs) throws SQLException {
		AuthResultRecord resultRec = new AuthResultRecord();
		resultRec.setData(rs);
		String type = resultRec.getType();

		if (AuthResultRecord.TYPE_DKIM.equals(type)) {
			dkims.add(resultRec);
		}
		else if (AuthResultRecord.TYPE_SPF.equals(type)) {
			spfs.add(resultRec);
		}
	}

	// MARK: - From XML
	/**
	 * add a
	 * @param feedback
	 * @param record
	 */
	public void setData(DmarcFeedback feedback, DmarcRecord record) {
		org_name = feedback.getOrgName();
		report_id = feedback.getReportId();
		email = feedback.getEmail();
		extra_contact_info = feedback.getExtraContactInfo();
		begin = feedback.getBegin();
		end = feedback.getEnd();
		domain = feedback.getDomain();
		adkim = feedback.getAdkim();
		aspf = feedback.getAspf();
		p = feedback.getP();
		sp = feedback.getSp();
		pct = feedback.getPct();
		np = feedback.getNp();
		source_ip = record.getSourceIp();
		count = record.getCount();
		disposition = record.getDisposition();
		dkim = record.getDkim();
		spf = record.getSpf();
		header_from = record.getHeaderFrom();

		preapreAddAuthResult();
		for (int i = 0; i < record.getAuthResultDkimCount(); i++) {
			DmarcDkim dkimObj = record.getAuthResultDkim(i);
			AuthResultRecord resultRec = new AuthResultRecord(AuthResultRecord.TYPE_DKIM, i);
			resultRec.setData(feedback, record, dkimObj);
			dkims.add(resultRec);
		}
		for (int i = 0; i < record.getAuthResultSpfCount(); i++) {
			DmarcSpf dkimObj = record.getAuthResultSpf(i);
			AuthResultRecord resultRec = new AuthResultRecord(AuthResultRecord.TYPE_SPF, i);
			resultRec.setData(feedback, record, dkimObj);
			spfs.add(resultRec);
		}
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
	 * get email.
	 * @return email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * get extra_contact_info.
	 * @return extra_contact_info.
	 */
	public String getExtraContactInfo() {
		return extra_contact_info;
	}

	/**
	 * get begin.
	 * @return begin.
	 */
	public int getBegin() {
		return begin;
	}

	/**
	 * get end.
	 * @return end.
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * get domain.
	 * @return domain.
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * get adkim.
	 * @return adkim.
	 */
	public String getAdkim() {
		return adkim;
	}

	/**
	 * get aspf.
	 * @return aspf.
	 */
	public String getAspf() {
		return aspf;
	}

	/**
	 * get p.
	 * @return p.
	 */
	public String getP() {
		return p;
	}

	/**
	 * get sp.
	 * @return sp.
	 */
	public String getSp() {
		return sp;
	}

	/**
	 * get pct.
	 * @return pct.
	 */
	public int getPct() {
		return pct;
	}

	/**
	 * get np.
	 * @return np.
	 */
	public String getNp() {
		return np;
	}

	/**
	 * get source_ip.
	 * @return source_ip.
	 */
	public String getSourceIp() {
		return source_ip;
	}

	/**
	 * get count.
	 * @return count.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * get disposition.
	 * @return disposition.
	 */
	public String getDisposition() {
		return disposition;
	}

	/**
	 * get dkim.
	 * @return dkim.
	 */
	public String getDkim() {
		return dkim;
	}

	/**
	 * get spf.
	 * @return spf.
	 */
	public String getSpf() {
		return spf;
	}

	/**
	 * get header_from.
	 * @return header_from.
	 */
	public String getHeaderFrom() {
		return header_from;
	}

	/**
	 * get dkim count.
	 * @return dkim count.
	 */
	public int getAuthResultDkimCount() {
		if (dkims == null) {
			return pseudo_dkim_count;
		}
		else {
			return dkims.size();
		}
	}

	/**
	 * get dkim.
	 * @param index dkim index.
	 * @return dkim.
	 */
	public AuthResultRecord getAuthResultDkim(int index) {
		return dkims.get(index);
	}

	/**
	 * get spf count.
	 * @return spf count.
	 */
	public int getAuthResultSpfCount() {
		if (spfs == null) {
			return pseudo_spf_count;
		}
		else {
			return spfs.size();
		}
	}

	/**
	 * get spf.
	 * @param index spf index.
	 * @return spf.
	 */
	public AuthResultRecord getAuthResultSpf(int index) {
		return spfs.get(index);
	}

	// MARK: - SQL
	/**
	 * get feedback record key.
	 * @return feedback record key.
	 */
	public String getFeedbackKey() {
		return org_name + DIVIDER + report_id;
	}

	/**
	 * get record record key.
	 * @return record record key.
	 */
	public String getRecordKey() {
		return getFeedbackKey() + DIVIDER + source_ip;
	}

	/**
	 * insert records.
	 * @param records record list.
	 * @param con database connection.
	 * @throws SQLException 
	 */
	public static void insert(List<Record> records, Connection con) throws SQLException {
		String feedbackKey = "";
		for (Record record : records) {
			if (feedbackKey.equals(record.getFeedbackKey()) == false) {
				insertFeedBack(record, con);
			}

			insertRecord(record, con);

			feedbackKey = record.getFeedbackKey();
		}
	}

	/**
	 * insert feed back.
	 * @param record record.
	 * @param con database connection.
	 * @throws SQLException
	 */
	private static void insertFeedBack(Record record, Connection con) throws SQLException {
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(FEEDBACKS_TABLE).append(" (");
		sql.append(COLUMN_ORG_NAME).append(", ").append(COLUMN_REPORT_ID).append(", ");
		sql.append(COLUMN_EMAIL).append(", ").append(COLUMN_EXTRA_CONTACT_INFO).append(", ");
		sql.append(COLUMN_BEGIN).append(", ").append(COLUMN_END).append(", ");
		sql.append(COLUMN_DOMAIN).append(", ").append(COLUMN_ADKIM).append(", ").append(COLUMN_ASPF).append(", ");
		sql.append(COLUMN_P).append(", ").append(COLUMN_SP).append(", ");
		sql.append(COLUMN_PCT).append(", ").append(COLUMN_NP).append(", ");
		sql.append(COLUMN_CREATED_AT).append(", ").append(COLUMN_UPDATED_AT).append(") ");
		sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, DEFAULT, DEFAULT)");
		PreparedStatement stmt = con.prepareStatement(sql.toString());
		stmt.setString(1, record.getOrgName());
		stmt.setString(2, record.getReportId());
		stmt.setString(3, record.getEmail());
		stmt.setString(4, record.getExtraContactInfo());
		stmt.setInt(5, record.getBegin());
		stmt.setInt(6, record.getEnd());
		stmt.setString(7, record.getDomain());
		stmt.setString(8, record.getAdkim());
		stmt.setString(9, record.getAspf());
		stmt.setString(10, record.getP());
		stmt.setString(11, record.getSp());
		stmt.setInt(12, record.getPct());
		stmt.setString(13, record.getNp());
		stmt.execute();
		stmt.close();
	}

	/**
	 * insert record.
	 * @param record record.
	 * @param con database connection.
	 * @throws SQLException
	 */
	private static void insertRecord(Record record, Connection con) throws SQLException {
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(RECORDS_TABLE).append(" (");
		sql.append(COLUMN_ORG_NAME).append(", ").append(COLUMN_REPORT_ID).append(", ");
		sql.append(COLUMN_SOURCE_IP).append(", ").append(COLUMN_COUNT).append(", ");
		sql.append(COLUMN_DISPOSITION).append(", ").append(COLUMN_DKIM).append(", ");
		sql.append(COLUMN_SPF).append(", ").append(COLUMN_HEADER_FROM).append(", ");
		sql.append(COLUMN_CREATED_AT).append(", ").append(COLUMN_UPDATED_AT).append(") ");
		sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, DEFAULT, DEFAULT)");
		PreparedStatement stmt = con.prepareStatement(sql.toString());
		stmt.setString(1, record.getOrgName());
		stmt.setString(2, record.getReportId());
		stmt.setString(3, record.getSourceIp());
		stmt.setInt(4, record.getCount());
		stmt.setString(5, record.getDisposition());
		stmt.setString(6, record.getDkim());
		stmt.setString(7, record.getSpf());
		stmt.setString(8, record.getHeaderFrom());
		stmt.execute();
		stmt.close();

		for (int i = 0; i < record.getAuthResultDkimCount(); i++) {
			AuthResultRecord dkimRec = record.getAuthResultDkim(i);
			AuthResultRecord.insert(dkimRec, con);
		}

		for (int i = 0; i < record.getAuthResultSpfCount(); i++) {
			AuthResultRecord spfRec = record.getAuthResultSpf(i);
			AuthResultRecord.insert(spfRec, con);
		}
	}

	/**
	 * delete records.
	 * @param records record list.
	 * @param connection database connection.
	 * @throws SQLException 
	 */
	public static void delete(List<Record> records, Connection connection) throws SQLException {
		for (Record record : records) {
			deleteFromTable(record, AUTH_RESULTS_TABLE, connection);
			deleteFromTable(record, RECORDS_TABLE, connection);
			deleteFromTable(record, FEEDBACKS_TABLE, connection);
		}
	}

	/**
	 * delete from dmarc_auth_results.
	 * @param record record to delete.
	 * @param con database connection.
	 * @throws SQLException
	 */
	private static void deleteFromTable(Record record, String tableName, Connection con) throws SQLException {
		String sql = "DELETE FROM " + tableName + " WHERE org_name = ? AND report_id = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, record.getOrgName());
		stmt.setString(2, record.getReportId());
		stmt.execute();
		stmt.close();
	}

	/**
	 * select records.
	 * @param connection database connection.
	 * @param searchOptions search options.
	 * @param orderBy order by object.
	 * @return record list.
	 * @throws SQLException 
	 */
	public static List<Record> select(Connection con, SearchOptions searchOptions, OrderBy orderBy) throws SQLException {
		List<Record> records = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT ");
		boolean hasAuthResultColumns = false;
		if ((searchOptions != null) && searchOptions.hasAuthResultColumn()) {
			sql.append("DISTINCT ");
			hasAuthResultColumns = true;
		}
		String comma = "";
		for (String column : FEEDBACK_COLUMNS) {
			sql.append(comma).append(selectColName(FEEDBACKS_TABLE, column));
			comma = ", ";
		}
		for (String column : RECORD_COLUMNS) {
			sql.append(comma).append(selectColName(RECORDS_TABLE, column));
		}

		sql.append(comma).append(selectColName(SUBQ_TABLE, COLUMN_DKIM_COUNT));
		sql.append(comma).append(selectColName(SUBQ_TABLE, COLUMN_SPF_COUNT));

		sql.append(" FROM ").append(FEEDBACKS_TABLE).append(", ").append(RECORDS_TABLE);

		addSubQuery(sql, searchOptions);	// add sub query.

		if (hasAuthResultColumns) {
			sql.append(", ").append(AUTH_RESULTS_TABLE);
		}

		sql.append(createWhere(" WHERE ", FEEDBACKS_TABLE, RECORDS_TABLE, COLUMN_ORG_NAME));	// FEEDBACKS_TABLE = RECORDS_TABLE.
		sql.append(createWhere(" AND ", FEEDBACKS_TABLE, RECORDS_TABLE, COLUMN_REPORT_ID));

		sql.append(createWhere(" AND ", FEEDBACKS_TABLE, SUBQ_TABLE, COLUMN_ORG_NAME));	// FEEDBACKS_TABLE = SUBQ_TABLE.
		sql.append(createWhere(" AND ", FEEDBACKS_TABLE, SUBQ_TABLE, COLUMN_REPORT_ID));
		sql.append(createWhere(" AND ", RECORDS_TABLE, SUBQ_TABLE, COLUMN_ORG_NAME));	// RECORDS_TABLE = SUBQ_TABLE.
		sql.append(createWhere(" AND ", RECORDS_TABLE, SUBQ_TABLE, COLUMN_REPORT_ID));
		sql.append(createWhere(" AND ", RECORDS_TABLE, SUBQ_TABLE, COLUMN_SOURCE_IP));

		if (hasAuthResultColumns) {
			sql.append(createWhere(" AND ", FEEDBACKS_TABLE, AUTH_RESULTS_TABLE, COLUMN_ORG_NAME));	// FEEDBACKS_TABLE = AUTH_RESULTS_TABLE.
			sql.append(createWhere(" AND ", FEEDBACKS_TABLE, AUTH_RESULTS_TABLE, COLUMN_REPORT_ID));

			sql.append(createWhere(" AND ", RECORDS_TABLE, AUTH_RESULTS_TABLE, COLUMN_ORG_NAME));	// RECORDS_TABLE = AUTH_RESULTS_TABLE.
			sql.append(createWhere(" AND ", RECORDS_TABLE, AUTH_RESULTS_TABLE, COLUMN_REPORT_ID));
			sql.append(createWhere(" AND ", RECORDS_TABLE, AUTH_RESULTS_TABLE, COLUMN_SOURCE_IP));

			sql.append(createWhere(" AND ", SUBQ_TABLE, AUTH_RESULTS_TABLE, COLUMN_ORG_NAME));	// SUBQ_TABLE = AUTH_RESULTS_TABLE.
			sql.append(createWhere(" AND ", SUBQ_TABLE, AUTH_RESULTS_TABLE, COLUMN_REPORT_ID));
			sql.append(createWhere(" AND ", SUBQ_TABLE, AUTH_RESULTS_TABLE, COLUMN_SOURCE_IP));
		}

		createAuthResultsWhere(sql, searchOptions);	// WHERE for  authentication result records.

		final String[] otherOrders = { COLUMN_BEGIN, COLUMN_END, COLUMN_ORG_NAME, COLUMN_REPORT_ID, COLUMN_HEADER_FROM, COLUMN_SOURCE_IP, };
		sql.append(OrderBy.buildOrderBy(orderBy, otherOrders));

		System.out.println("SQL: " + sql.toString());
		PreparedStatement stmt = con.prepareStatement(sql.toString());

		int indexBase = 1;
		if (searchOptions != null) {	// set search values for sub query.
			for (String key : RECORD_KEY_COLUMNS) {
				SearchOption searchOption = searchOptions.getSearchOption(key, false);
				if (searchOption != null) {
					searchOption.setSearchValue(stmt, indexBase);
					indexBase++;
				}
			}
		}

		if (searchOptions != null) {	// set search values.
			for (int i = 0; i < searchOptions.getSearchOptionCount(); i++) {
				SearchOption searchOption = searchOptions.getSearchOption(i);
				searchOption.setSearchValue(stmt, i + indexBase);
			}
		}
	
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Record record = new Record();
			record.setData(rs);
			records.add(record);
		}
		rs.close();
		stmt.close();

		return records;
	}

	/**
	 * add sub query chunk.
	 * @param sql string buffer.
	 * @param searchOptions search options.
	 */
	private static void addSubQuery(StringBuilder sql, SearchOptions searchOptions) {
		sql.append(", (SELECT ");	// sub query start.
		sql.append(COLUMN_ORG_NAME);
		sql.append(", ").append(COLUMN_REPORT_ID);
		sql.append(", ").append(COLUMN_SOURCE_IP);
		sql.append(", ").append("SUM(").append(AuthResultRecord.COLUMN_TYPE).append(" = '").append(AuthResultRecord.TYPE_DKIM).append("') AS ").append(COLUMN_DKIM_COUNT);
		sql.append(", ").append("SUM(").append(AuthResultRecord.COLUMN_TYPE).append(" = '").append(AuthResultRecord.TYPE_SPF).append("') AS ").append(COLUMN_SPF_COUNT);
		sql.append(" FROM ").append(AUTH_RESULTS_TABLE);

		if (searchOptions != null) {
			String whareClause = " WHERE ";
			for (String key : RECORD_KEY_COLUMNS) {
				SearchOption searchOption = searchOptions.getSearchOption(key, false);
				if (searchOption != null) {
					sql.append(whareClause).append(searchOption.createWhereOmitTableName());	// do not use table name for sub query.
					whareClause = " AND ";
				}
			}
		}

		sql.append(" GROUP BY ").append(COLUMN_ORG_NAME);
		sql.append(", ").append(COLUMN_REPORT_ID);
		sql.append(", ").append(COLUMN_SOURCE_IP);
		sql.append(") AS ").append(SUBQ_TABLE);	// sub query end.
	}

	/**
	 * create authentication results WHERE query chunk.
	 * @param sql string buffer.
	 * @param searchOptions search options.
	 */
	private static void createAuthResultsWhere(StringBuilder sql, SearchOptions searchOptions) {
		int authCount = 0;
		int dkimAuth = 0;
		int spfAuth = 0;
		if (searchOptions != null) {
			for (int i = 0; i < searchOptions.getSearchOptionCount(); i++) {
				SearchOption searchOption = searchOptions.getSearchOption(i);
				if (searchOption.isAuthResultColumn() == false) {
					sql.append(" AND ").append(searchOption.createWhere());
				}
				else {
					authCount++;
					if (searchOption.isDkim()) {
						dkimAuth++;
					}
					else {
						spfAuth++;
					}
				}
			}
			if (authCount > 0) {
				sql.append(" AND ");
				if (authCount > 1) {
					sql.append("(");
					if ((dkimAuth > 0) || (spfAuth > 0)) {
						sql.append("(");
					}
				}
				int authSetCount = 0;
				int dkimSetAuth = (dkimAuth > 0) ? 0 : -1;
				for (int i = 0; i < searchOptions.getSearchOptionCount(); i++) {
					if (authSetCount > 0) {
						if (dkimSetAuth == dkimAuth) {
							sql.append(") OR (");	// DKIM -> SPF.
							dkimSetAuth = -1;	// do not hit again.
						}
						else {
							sql.append(" AND ");
						}
					}
					SearchOption searchOption = searchOptions.getSearchOption(i);
					if (searchOption.isAuthResultColumn()) {
						sql.append(searchOption.createWhere());
						authSetCount++;
						if (searchOption.isDkim()) {
							dkimSetAuth++;
						}
					}
				}
				if (authCount > 1) {
					if ((dkimAuth > 0) || (spfAuth > 0)) {
						sql.append(")");
					}
					sql.append(")");
				}
			}
		}
	}

	/**
	 * create where clause.
	 * @param whereClause " WHERE " or " AND ".
	 * @param table1 table name 1.
	 * @param table2 table name 2.
	 * @param column column name.
	 * @return a chunk of where clause.
	 */
	private static String createWhere(String whereClause, String table1, String table2, String column) {
		StringBuilder sql = new StringBuilder(whereClause);
		sql.append(whereColName(table1, column));
		sql.append(" = ").append(whereColName(table2, column));
		return sql.toString();
	}

	/**
	 * column name for SELECT.
	 * @param table table name.
	 * @param column column name.
	 * @return table.column AS column.
	 */
	private static String selectColName(String table, String column) {
		return whereColName(table, column) + " AS " + column;
	}

	/**
	 * column name for WHERE.
	 * @param table table name.
	 * @param column column name.
	 * @return table.column.
	 */
	private static String whereColName(String table, String column) {
		return table + "." + column;
	}

	// MARK: - Merge Record.
	/**
	 * check key is duplicate or not.
	 * @param compareRec comparing record.
	 * @return true if key is duplicate.
	 */
	protected boolean isDuplicateKey(Record compareRec) {
		return org_name.equals(compareRec.org_name) && report_id.equals(compareRec.report_id) && source_ip.equals(compareRec.source_ip);
	}

	/**
	 * merge record.
	 * @param mergeRec record to merge.
	 */
	protected void mergeRecord(Record mergeRec) {
		count += mergeRec.count;
		if (disposition.contains(mergeRec.disposition) == false) {
			disposition += "," + mergeRec.disposition;
		}
		if (dkim.contains(mergeRec.dkim) == false) {
			dkim += "," + mergeRec.dkim;
		}
		if (spf.contains(mergeRec.spf) == false) {
			spf += "," + mergeRec.spf;
		}

		dkims.addAll(mergeRec.dkims);
		int row = dkims.get(0).getRow();
		for (int i = 1; i < dkims.size(); i++) {
			dkims.get(i).setRow(row + i);
		}

		spfs.addAll(mergeRec.spfs);
		row = spfs.get(0).getRow();
		for (int i = 1; i < spfs.size(); i++) {
			spfs.get(i).setRow(row + i);
		}
	}
}
