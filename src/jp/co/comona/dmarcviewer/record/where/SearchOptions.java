package jp.co.comona.dmarcviewer.record.where;

import java.util.ArrayList;
import java.util.List;

import jp.co.comona.dmarcviewer.DmarcViewerTool;

/**
 * search options.
 * @author kageyama
 * date: 2025/05/13
 */
public class SearchOptions {

	// MARK: - Property
	private List<SearchOption> options = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	public SearchOptions() {
		super();
		options = new ArrayList<>();
	}

	// MARK: - Getters & Setters
	/**
	 * get search option count.
	 * @return search option count.
	 */
	public int getSearchOptionCount() {
		return options.size();
	}

	/**
	 * get search option.
	 * @param index index of search option.
	 * @return search option.
	 */
	public SearchOption getSearchOption(int index) {
		return options.get(index);
	}

	/**
	 * get search option.
	 * @param column column name.
	 * @param dkim true if DKIM.
	 * @return search option.
	 */
	public SearchOption getSearchOption(String column, boolean dkim) {
		for (SearchOption option : options) {
			if (column.equals(option.getColumn()) && (option.isDkim() == dkim)) {
				return option;
			}
		}
		return null;
	}

	/**
	 * get search option signs.
	 * @param tool the DMARCviewer tool.
	 * @return search option signs.
	 */
	public String getSearchOptionSigns(DmarcViewerTool tool) {
		StringBuilder signs = new StringBuilder();
		for (SearchOption option : options) {
			if (signs.length() > 0) {
				signs.append(',');
			}
			signs.append(option.getSearchOptionSign(tool));
		}
		return signs.toString();
	}

	/**
	 * add search option.
	 * @param searchOption search option.
	 */
	public void addSearchOption(SearchOption searchOption) {
		options.add(searchOption);
	}

	/**
	 * has dmarc_auth_results columns.
	 * @return true if has.
	 */
	public boolean hasAuthResultColumn() {
		for (SearchOption option : options) {
			if (option.isAuthResultColumn()) {
				return true;
			}
		}
		return false;
	}
}
