package jp.co.comona.dmarcviewer.record;

/**
 * order by.
 * @author kageyama
 * date: 2025/05/13
 */
public class OrderBy {

	// MARK: - Static Property
	private static final String ASCENDING = " ASC";
	private static final String DECCENDING = " DESC";

	// MARK: - Property
	private String column = null;
	private boolean ascending = false;

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	public OrderBy(String column, boolean ascending) {
		super();
		this.column = column;
		this.ascending = ascending;
	}

	// MARK: - Getters
	/**
	 * get column name.
	 * @return column name.
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * get ascending.
	 * @return true if ascending.
	 */
	public boolean isAscending() {
		return ascending;
	}

	// MARK: - SQL
	/**
	 * get ORDER BY column.
	 * @return ORDER BY column.
	 */
	private String orderByColumn() {
		return orderByColumn(column, ascending);
	}

	/**
	 * get ORDER BY column.
	 * @param column column name.
	 * @param ascending ascending when true.
	 * @return ORDER BY column.
	 */
	private static String orderByColumn(String column, boolean ascending) {
		return column + (ascending ? ASCENDING : DECCENDING);
	}

	/**
	 * check column name is equal or not.
	 * @param column column name to check.
	 * @return true if equal.
	 */
	private boolean isEqualColumnName(String column) {
		return column.equals(this.column);
	}

	/**
	 * build ORDER BY string.
	 * @param orderBy order by object.
	 * @param otherColumns other columns to order by.
	 * @return ORDER BY string.
	 */
	public static String buildOrderBy(OrderBy orderBy, String[] otherColumns) {
		StringBuilder sql = new StringBuilder(" ORDER BY ");
		String comma = "";
		if (orderBy != null) {
			sql.append(comma).append(orderBy.orderByColumn());
			comma = ", ";
		}
		for (String other : otherColumns) {
			if ((orderBy == null) || (orderBy.isEqualColumnName(other) == false)) {
				sql.append(comma).append(OrderBy.orderByColumn(other, true));
				comma = ", ";
			}
		}

		return sql.toString();
	}
}
