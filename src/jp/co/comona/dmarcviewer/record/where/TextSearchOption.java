package jp.co.comona.dmarcviewer.record.where;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.co.comona.dmarcviewer.DmarcViewerTool;
import jp.co.comona.dmarcviewer.record.AuthResultRecord;

/**
 * text search option.
 * @author kageyama
 * date: 2025/05/13
 */
public class TextSearchOption extends SearchOption {

	// MARK: - Static Property
	public static final int TYPE_UNKNOWN = -1;
	public static final int TYPE_EQUAL = 0;
	public static final int TYPE_NOT_EQUAL = 1;
	public static final int TYPE_CONTAINS = 2;
	public static final int TYPE_NOT_CONTAINS = 3;
	public static final int TYPE_PREFIX = 4;
	public static final int TYPE_NOT_PREFIX = 5;
	public static final int TYPE_SUFFIX = 6;
	public static final int TYPE_NOT_SUFFIX = 7;

	// MARK: - Property
	private String value = null;
	private boolean caseSensitive = false;
	private int type = TYPE_UNKNOWN;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param column column name.
	 * @param value search value.
	 * @param caseSensitive true if case sensitive.
	 * @param type search type. (=, <>, LIKE...)
	 */
	public TextSearchOption(String column, String value, boolean caseSensitive, int type) {
		super(column);
		this.value = value;
		this.caseSensitive = caseSensitive;
		this.type = type;
	}

	// MARK: - SQL
	/**
	 * create where clause.
	 */
	@Override
	public String createWhere() {
		StringBuilder sql = new StringBuilder();

		if (isAuthResultColumn()) {
			sql.append('(').append(getTablePrefix()).append("type = '");
			sql.append(isDkim() ? AuthResultRecord.TYPE_DKIM : AuthResultRecord.TYPE_SPF).append("' AND ");
		}

		if (caseSensitive == false) {
			sql.append("LOWER(");
		}
		sql.append(getTablePrefix()).append(getWhereColumn());
		return appendWhereTrailer(sql);
	}

	/**
	 * create where clause omit table name.
	 */
	@Override
	public String createWhereOmitTableName() {
		StringBuilder sql = new StringBuilder();
		if (caseSensitive == false) {
			sql.append("LOWER(");
		}
		sql.append(getWhereColumn());
		return appendWhereTrailer(sql);
	}

	/**
	 * append WHERE trailer.
	 * @param sql string buffer.
	 * @return WHERE clause.
	 */
	private String appendWhereTrailer(StringBuilder sql) {
		if (caseSensitive == false) {
			sql.append(")");
		}

		switch(type) {
			case TYPE_EQUAL:
				sql.append(EQUAL);
				setQuestion(sql);
				break;
			case TYPE_NOT_EQUAL:
				sql.append(NOT_EQUAL);
				setQuestion(sql);
				break;
			case TYPE_CONTAINS:
			case TYPE_PREFIX:
			case TYPE_SUFFIX:
				sql.append(LIKE);
				setQuestion(sql);
				break;
			case TYPE_NOT_CONTAINS:
			case TYPE_NOT_PREFIX:
			case TYPE_NOT_SUFFIX:
				sql.append(NOT_LIKE);
				setQuestion(sql);
				break;
			default:
				assert(false);
				break;
		}

		if (isAuthResultColumn()) {
			sql.append(')');
		}

		return sql.toString();
	}

	/**
	 * set question flag.
	 * @param sql string builder.
	 */
	private void setQuestion(StringBuilder sql) {
		if (caseSensitive == false) {
			sql.append("LOWER(");
		}
		sql.append("?");
		if (caseSensitive == false) {
			sql.append(")");
		}
	}

	/**
	 * set search value.
	 */
	@Override
	public void setSearchValue(PreparedStatement stmt, int index) throws SQLException {
		String value = null;
		switch (type) {
			case TYPE_EQUAL:
			case TYPE_NOT_EQUAL:
				value = this.value;
				break;
			case TYPE_CONTAINS:
			case TYPE_NOT_CONTAINS:
				value = "%" + this.value + "%";
				break;
			case TYPE_PREFIX:
			case TYPE_NOT_PREFIX:
				value = this.value + "%";
				break;
			case TYPE_SUFFIX:
			case TYPE_NOT_SUFFIX:
				value = "%" + this.value;
				break;
			default:
				assert(false);
				break;
		}

		stmt.setString(index, value);
	}

	// MARK: - Getters
	/**
	 * get search value.
	 * @return search value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * case sensitive.
	 * @return true if case sensitive.
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * get search type.
	 * @return search type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * get search option sign.
	 */
	@Override
	public String getSearchOptionSign(DmarcViewerTool tool) {
		return getDisplayColumn() + getSplitter() + value + getTrailer();
	}

	/**
	 * get splitter.
	 * @return splitter.
	 */
	private String getSplitter() {
		switch(type) {
			case TYPE_EQUAL:
				return isCaseSensitive() ? "=" : ":";
			case TYPE_NOT_EQUAL:
				return isCaseSensitive() ? "!=" : "!:";
			case TYPE_CONTAINS:
			case TYPE_SUFFIX:
				return isCaseSensitive() ? "=like(%" : ":like(%";
			case TYPE_PREFIX:
				return isCaseSensitive() ? "=like(" : ":like(";
			case TYPE_NOT_CONTAINS:
			case TYPE_NOT_SUFFIX:
				return isCaseSensitive() ? "!=like(%" : "!:like(%";
			case TYPE_NOT_PREFIX:
				return isCaseSensitive() ? "!=like(" : "!:like(";
			default:
				assert(false);
				return null;
		}
	}

	/**
	 * get trailer.
	 * @return trailer.
	 */
	private String getTrailer() {
		switch(type) {
			case TYPE_CONTAINS:
			case TYPE_NOT_CONTAINS:
			case TYPE_SUFFIX:
			case TYPE_NOT_SUFFIX:
				return ")";
			case TYPE_PREFIX:
			case TYPE_NOT_PREFIX:
				return "%)";
			default:
				return "";
		}
	}
}
