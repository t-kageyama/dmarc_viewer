package jp.co.comona.dmarcviewer.record;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;

/**
 * record list table.
 * @author kageyama
 * date: 2025/05/11
 */
public interface RecordListTableContainer {

	/**
	 * table selection changed.
	 * @param e event.
	 */
	public void tableSelectionChanged(SelectionEvent e);

	/**
	 * table double clicked.
	 * @param e event.
	 */
	public void tableDoubleClicked(SelectionEvent e);

	/**
	 * table column selected.
	 * @param columnName column name.
	 * @param e event.
	 */
	public void tableColumnSelected(String columnName, Event e);
}
