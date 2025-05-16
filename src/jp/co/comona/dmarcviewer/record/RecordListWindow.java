package jp.co.comona.dmarcviewer.record;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;

import jp.co.comona.dmarcviewer.DmarcViewerTool;
import jp.co.comona.dmarcviewer.MainWindow;
import jp.co.comona.dmarcviewer.SubWindow;
import jp.co.comona.dmarcviewer.util.MessageBox;
import jp.co.comona.dmarcviewer.xml.DmarcFeedback;
import jp.co.comona.dmarcviewer.xml.DmarcRecord;

/**
 * record list window.
 * @author kageyama
 * date: 2025/05/10
 */
public class RecordListWindow extends SubWindow implements RecordListTableContainer {

	// MARK: - Static Properties
	private static final String DETAIL_BUTTON_TITLE = STR_DETAIL;
	private static final String STORE_BUTTON_TITLE = "Store";
	private static final String CLOSE_BUTTON_TITLE = "Close";

	// MARK: - Properties
	private DmarcFeedback feedback = null;
	private RecordListTable table = null;
	private Button detailButton = null;
	private Button storeButton = null;
	private Button closeButton = null;
	private List<Record> records = null;
	private RecordDetailWindow detailWindow = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param tool DMARC viewer tool.
	 * @param mainWindow main window.
	 * @param feedback feedback XML result.
	 */
	public RecordListWindow(DmarcViewerTool tool, MainWindow mainWindow, DmarcFeedback feedback) {
		super(tool, mainWindow);
		this.feedback = feedback;
		records = new ArrayList<>();

		widthPropKey = PROP_RECORD_LIST_SCREEN_WIDTH;
		heightPropKey = PROP_RECORD_LIST_SCREEN_HEIGHT;
		title = "DMARC XML Result";
		defaultWidth = DEFAULT_RECORD_LIST_SCREEN_WIDTH;
		defaultHeight = DEFAULT_RECORD_LIST_SCREEN_HEIGHT;
		minimumWidth = MINIMUM_RECORD_LIST_SCREEN_WIDTH;
		minimumHeight = MINIMUM_RECORD_LIST_SCREEN_HEIGHT;
	}

	/**
	 * open window.
	 */
	@Override
	public void open() throws Exception {
		createShell();
		createComponents();
		expandXMLResult();
	}

	// MARK: - GUI
	/**
	 * create components.
	 */
	protected void createComponents() {
		table = new RecordListTable(this, tool, shell, PROP_RECORD_LIST_PREFIX);
		table.createTable();

		detailButton = new Button(shell, SWT.PUSH);
		detailButton.setText(DETAIL_BUTTON_TITLE);
		detailButton.setEnabled(false);
		detailButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * widget selected.
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				onDetailButton(e);
			}
		});

		storeButton = new Button(shell, SWT.PUSH);
		storeButton.setText(STORE_BUTTON_TITLE);
		storeButton.setEnabled(tool.getConnection() != null);
		storeButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * widget selected.
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				onStoreButton(e);
			}
		});

		closeButton = new Button(shell, SWT.PUSH);
		closeButton.setText(CLOSE_BUTTON_TITLE);
		closeButton.setEnabled(true);
		closeButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * widget selected.
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				onCloseButton(e);
			}
		});

		setComponetPosition(null, shell.getClientArea(), shell.getBounds());
	}

	/**
	 * set components position.
	 * @param event resize event.
	 * @param clientArea new client area rectangle.
	 * @param bounds new window rectangle.
	 */
	private void setComponetPosition(Event event, Rectangle clientArea, Rectangle bounds) {
		int x = DEFAULT_X_MARGIN;
		int y = DEFAULT_Y_MARGIN;
		int width = clientArea.width - DEFAULT_X_MARGIN * 3 - DEFAULT_BUTTON_WIDTH;
		int height = clientArea.height - DEFAULT_Y_MARGIN * 2;
		Rectangle rect = new Rectangle(x, y, width, height);
		table.setBounds(rect);

		x = clientArea.width - DEFAULT_X_MARGIN - DEFAULT_BUTTON_WIDTH;
		rect = new Rectangle(x, y, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		detailButton.setBounds(rect);

		y = clientArea.height - DEFAULT_Y_MARGIN - DEFAULT_BUTTON_HEIGHT;
		rect = new Rectangle(x, y, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		closeButton.setBounds(rect);

		y -= DEFAULT_Y_MARGIN + DEFAULT_BUTTON_HEIGHT;
		rect = new Rectangle(x, y, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		storeButton.setBounds(rect);
	}

	/**
	 * window close selected.
	 */
	@Override
	protected void windowCloseSelected(Event event) {
		mainWindow.subWindowClose(this);
	}

	/**
	 * window size changed.
	 */
	@Override
	protected void windowSizeChanged(Event event, Rectangle clientArea, Rectangle bounds) {
		super.windowSizeChanged(event, clientArea, bounds);
		setComponetPosition(event, clientArea, bounds);
	}

	// MARK: - RecordListTableContainer
	/**
	 * table selection changed.
	 */
	@Override
	public void tableSelectionChanged(SelectionEvent e) {
		int index = table.getSelectionIndex();
		detailButton.setEnabled(index > -1);
	}

	/**
	 * table double clicked.
	 */
	@Override
	public void tableDoubleClicked(SelectionEvent e) {
		int index = table.getSelectionIndex();
		detailButton.setEnabled(index > -1);
		if (index > -1) {
			onDetailButton(e);
		}
	}

	/**
	 * table column selected.
	 */
	@Override
	public void tableColumnSelected(String columnName, Event e) {
		// nothing to do.
	}

	// MARK: - Sub Window
	/**
	 * sub window closed.
	 * @param subWindow sub window instance.
	 */
	public void subWindowClose(SubWindow subWindow) {
		if (subWindow == detailWindow) {
			detailWindow.closeFromOutside();
			detailWindow = null;
			storeButton.setEnabled(tool.getConnection() != null);
		}
	}

	// MARK: - XML
	/**
	 * expand XML result.
	 */
	private void expandXMLResult() {
		records.clear();
		for (int i = 0; i < feedback.getRecordCount(); i++) {
			DmarcRecord dmarcRec = feedback.getRecord(i);
			Record rec = new Record();
			rec.setData(feedback, dmarcRec);
			records.add(rec);
		}

		table.removeAll();
		for (Record rec : records) {
			table.addRecordToTable(rec);
		}
	}

	// MARK: - Event
	/**
	 * detail button pushed.
	 * @param e event.
	 */
	private void onDetailButton(SelectionEvent e) {
		String error = null;
		if (detailWindow != null) {
			detailWindow.bringToTop();
		}
		else {
			try {
				detailWindow = new RecordDetailWindow(tool, mainWindow, this, getSelectedRecord());
				detailWindow.open();
				storeButton.setEnabled(false);
			}
			catch (Exception ex) {
				ex.printStackTrace(System.err);
				error = ex.getLocalizedMessage();
			}
		}

		if ((error != null) && (error.length() > 0)) {
			MessageBox.showError(shell, null, error, SWT.OK);
		}
	}

	/**
	 * store button pushed.
	 * @param e event.
	 */
	private void onStoreButton(SelectionEvent e) {
		String error = null;
		try {
			Record.delete(records, tool.getConnection());	// delete org_name, report_id first.
			Record.insert(records, tool.getConnection());	// then insert.
			mainWindow.searchAfterStore();
			onCloseButton(e);
		}
		catch (SQLException ex) {
			ex.printStackTrace(System.err);
			error = ex.getLocalizedMessage();
		}

		if ((error != null) && (error.length() > 0)) {
			MessageBox.showError(shell, null, error, SWT.OK);
		}
	}

	/**
	 * store button pushed.
	 * @param e event.
	 */
	private void onCloseButton(SelectionEvent e) {
		mainWindow.subWindowClose(this);
	}

	/**
	 * connection status changed.
	 */
	public void connectionStatusChanged() {
		storeButton.setEnabled(tool.getConnection() != null);
	}

	// MARK: - Getters
	/**
	 * get selected record.
	 * @return selected record.
	 */
	private Record getSelectedRecord() {
		int index = table.getSelectionIndex();
		return records.get(index);
	}
}
