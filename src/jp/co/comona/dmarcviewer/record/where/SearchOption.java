package jp.co.comona.dmarcviewer.record.where;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.co.comona.dmarcviewer.Constants;
import jp.co.comona.dmarcviewer.DmarcViewerTool;
import jp.co.comona.dmarcviewer.record.Record;
import jp.co.comona.dmarcviewer.record.RecordColumns;

/**
 * search option.
 * @author kageyama
 * date: 2025/05/13
 */
public abstract class SearchOption implements Constants, RecordColumns {

	// MARK: - Static Property
	protected static final String EQUAL = " = ";
	protected static final String NOT_EQUAL = " <> ";
	protected static final String LESS = " < ";
	protected static final String LESS_EQUAL = " <= ";
	protected static final String GREATER = " > ";
	protected static final String GREATER_EQUAL = " >= ";
	protected static final String LIKE = " LIKE ";
	protected static final String NOT_LIKE = " NOT LIKE ";

	// MARK: - Property
	private String column = null;
	private boolean dkim = false;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param column
	 */
	protected SearchOption(String column) {
		super();
		this.column = column;
	}

	// MARK: - Getters & Setters
	/**
	 * get column name.
	 * @return column name.
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * get column name for WHERE.
	 * @return column name for WHERE.
	 */
	public String getWhereColumn() {
		if (isAuthResultColumn() == false) {
			return getColumn();
		}
		else {
			return getColumn().substring(AUTH_RESULTS_PREFIX.length());
		}
	}

	/**
	 * is dmarc_auth_results column?
	 * @return true if dmarc_auth_results.
	 */
	public boolean isAuthResultColumn() {
		return getColumn().startsWith(AUTH_RESULTS_PREFIX);
	}

	/**
	 * is DKIM?
	 * @return true if DKIM.
	 */
	public boolean isDkim() {
		return dkim;
	}

	/**
	 * set DKIM flag.
	 * @param dkim true if DKIM.
	 */
	public void setDkim(boolean dkim) {
		this.dkim = dkim;
	}

	/**
	 * get search option sign.
	 * @param tool the DMARC viewer tool
	 * @return search option sign.
	 */
	public abstract String getSearchOptionSign(DmarcViewerTool tool);

	/**
	 * get display column name.
	 * @return display column name.
	 */
	protected String getDisplayColumn() {
		if (column.startsWith(AUTH_RESULTS_PREFIX) == false) {
			return column;
		}
		else {
			return (isDkim() ? "dkim_" : "spf_") + column.substring(AUTH_RESULTS_PREFIX.length());
		}
	}

	// MARK: - SQL
	/**
	 * create where clause.
	 * @return WHERE clause.
	 */
	public abstract String createWhere();

	/**
	 * create where clause omit table name.
	 * @return WHERE clause omit table name.
	 */
	public abstract String createWhereOmitTableName();

	/**
	 * set search value.
	 * @param index index of prepared statement.
	 */
	public abstract void setSearchValue(PreparedStatement stmt, int index) throws SQLException;

	/**
	 * get table name prefix.
	 * @return table name prefix.
	 */
	protected String getTablePrefix() {
		switch (column) {
			case COLUMN_ORG_NAME:
			case COLUMN_REPORT_ID:
			case COLUMN_EMAIL:
			case COLUMN_EXTRA_CONTACT_INFO:
			case COLUMN_BEGIN:
			case COLUMN_END:
			case COLUMN_DOMAIN:
			case COLUMN_ADKIM:
			case COLUMN_ASPF:
			case COLUMN_P:
			case COLUMN_SP:
			case COLUMN_PCT:
			case COLUMN_NP:
				return Record.FEEDBACKS_TABLE + ".";
			case  COLUMN_SOURCE_IP:
			case  COLUMN_COUNT:
			case  COLUMN_DISPOSITION:
			case  COLUMN_DKIM:
			case  COLUMN_SPF:
			case  COLUMN_HEADER_FROM:
				return Record.RECORDS_TABLE + ".";

			default:
				return Record.AUTH_RESULTS_TABLE + ".";
		}
	}
}
