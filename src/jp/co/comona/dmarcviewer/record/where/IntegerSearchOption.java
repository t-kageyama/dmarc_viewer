package jp.co.comona.dmarcviewer.record.where;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.co.comona.dmarcviewer.DmarcViewerTool;
import jp.co.comona.dmarcviewer.record.RecordListTable;

/**
 * integer search option.
 * @author kageyama
 * date: 2025/05/13
 */
public class IntegerSearchOption extends SearchOption {

	// MARK: - Static Property
	public static final int TYPE_UNKNOWN = -1;
	public static final int TYPE_EQUAL = 0;
	public static final int TYPE_NOT_EQUAL = 1;
	public static final int TYPE_LESS = 2;
	public static final int TYPE_LESS_EQUAL = 3;
	public static final int TYPE_GREATER = 4;
	public static final int TYPE_GREATER_EQUAL = 5;

	// MARK: - Property
	private int value = 0;
	private int type = TYPE_UNKNOWN;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param column column name.
	 * @param value search value.
	 * @param type search type. (=, <>, <, <=, >, >=)
	 */
	public IntegerSearchOption(String column, int value, int type) {
		super(column);
		this.value = value;
		this.type = type;
	}

	// MARK: - SQL
	/**
	 * create where clause.
	 */
	@Override
	public String createWhere() {
		StringBuilder sql = new StringBuilder(getTablePrefix());
		sql.append(getColumn());

		return appendWhereTrailer(sql);
	}

	/**
	 * create where clause omit table name.
	 */
	@Override
	public String createWhereOmitTableName() {
		StringBuilder sql = new StringBuilder(getColumn());

		return appendWhereTrailer(sql);
	}

	/**
	 * append WHERE trailer.
	 * @param sql string buffer.
	 * @return WHERE clause.
	 */
	private String appendWhereTrailer(StringBuilder sql) {
		switch(type) {
			case TYPE_EQUAL:
				sql.append(EQUAL);
				break;
			case TYPE_NOT_EQUAL:
				sql.append(NOT_EQUAL);
				break;
			case TYPE_LESS:
				sql.append(LESS);
				break;
			case TYPE_LESS_EQUAL:
				sql.append(LESS_EQUAL);
				break;
			case TYPE_GREATER:
				sql.append(GREATER);
				break;
			case TYPE_GREATER_EQUAL:
				sql.append(GREATER_EQUAL);
				break;
			default:
				assert(false);
				break;
		}
		sql.append("?");

		return sql.toString();
	}

	/**
	 * set search value.
	 */
	@Override
	public void setSearchValue(PreparedStatement stmt, int index) throws SQLException {
		stmt.setInt(index, value);
	}

	// MARK: - Getter
	/**
	 * get value.
	 * @return value.
	 */
	public int getValue() {
		return value;
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
		return getDisplayColumn() + getSplitter() + getSignValue(tool);
	}

	/**
	 * get splitter.
	 * @return splitter.
	 */
	private String getSplitter() {
		switch(type) {
			case TYPE_EQUAL:
				return "=";
			case TYPE_NOT_EQUAL:
				return "!=";
			case TYPE_LESS:
				return "<";
			case TYPE_LESS_EQUAL:
				return "<=";
			case TYPE_GREATER:
				return ">";
			case TYPE_GREATER_EQUAL:
				return ">=";
			default:
				assert(false);
				return null;
		}
	}

	/**
	 * get sign value.
	 * @param tool the DMARC tool.
	 * @return sign value.
	 */
	private String getSignValue(DmarcViewerTool tool) {
		if (COLUMN_BEGIN.equals(getColumn()) || COLUMN_END.equals(getColumn())) {
			return RecordListTable.dateTimeFormat(value, tool.getPropertyString(PROP_DATE_TIME_FORMAT, "yyyy/MM/dd hh:mm:ss"));
		}
		else {
			return "" + value;
		}
	}
}
