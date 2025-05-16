package jp.co.comona.dmarcviewer.record.where;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import jp.co.comona.dmarcviewer.Constants;
import jp.co.comona.dmarcviewer.record.RecordColumns;

/**
 * value input.
 * @author kageyama
 * date: 2025/05/13
 */
public abstract class ValueInput implements Constants, RecordColumns {

	// MARK: - Static Property
	protected static final int LABEL_WIDTH_EXTRA = 50;

	// MARK: - Property
	protected Shell shell = null;
	protected Label label = null;
	protected String column = null;
	private boolean dkim = false;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param shell shell object.
	 * @param column column name.
	 */
	protected ValueInput(Shell shell, String column) {
		super();
		this.shell = shell;
		this.column = column;
	}

	// MARK: - GUI
	/**
	 * create components.
	 */
	public abstract void createComponents();

	/**
	 * set components size.
	 * @param event resize event.
	 * @param clientArea new client area rectangle.
	 * @param bounds new window rectangle.
	 * @param y y coordinate.
	 * @return bottom coordinate.
	 */
	public abstract int setComponentsSize(Event event, Rectangle clientArea, Rectangle bounds, int y);

	/**
	 * create label.
	 */
	protected void createLabel() {
		label = new Label(shell, SWT.NULL);
		label.setText(getDisplayColumn());
	}

	// MARK: - Search Option
	/**
	 * create search option.
	 * @return search option.
	 */
	public abstract SearchOption createSearchOption();

	/**
	 * set search option.
	 * @param searchOption search option.
	 */
	public abstract void setSearchOption(SearchOption searchOption);

	/**
	 * reset.
	 */
	public abstract void reset();

	// MARK: - Getters
	/**
	 * get display column name.
	 * @return display column name.
	 */
	public String getDisplayColumn() {
		if (column.startsWith(AUTH_RESULTS_PREFIX) == false) {
			return column;
		}
		else {
			return (isDkim() ? "dkim_" : "spf_") + column.substring(AUTH_RESULTS_PREFIX.length());
		}
	}

	// MARK: - Getters & Setters
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
	 * is dmarc_auth_results column?
	 * @return true if dmarc_auth_results.
	 */
	public boolean isAuthResultColumn() {
		return column.startsWith(AUTH_RESULTS_PREFIX);
	}
}
