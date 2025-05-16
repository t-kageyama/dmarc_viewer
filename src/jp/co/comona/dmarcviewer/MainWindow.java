package jp.co.comona.dmarcviewer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;

import jp.co.comona.dmarcviewer.record.AuthResultRecordThread;
import jp.co.comona.dmarcviewer.record.OrderBy;
import jp.co.comona.dmarcviewer.record.Record;
import jp.co.comona.dmarcviewer.record.RecordDetailWindow;
import jp.co.comona.dmarcviewer.record.RecordListTable;
import jp.co.comona.dmarcviewer.record.RecordListTableContainer;
import jp.co.comona.dmarcviewer.record.RecordListWindow;
import jp.co.comona.dmarcviewer.record.where.SearchOptionDialog;
import jp.co.comona.dmarcviewer.record.where.SearchOptions;
import jp.co.comona.dmarcviewer.user.UserPasswordDialog;
import jp.co.comona.dmarcviewer.user.UserPasswordInput;
import jp.co.comona.dmarcviewer.util.MessageBox;
import jp.co.comona.dmarcviewer.xml.DmarcFeedback;

/**
 * main window.
 * @author kageyama
 * date: 2025/05/09
 */
public class MainWindow extends SubWindow implements RecordListTableContainer {

	// MARK: - Static Properties
	public static final String SEARCH_BUTTON_TITLE = "Search";
	public static final String SEARCH_OPTION_BUTTON_TITLE = "Search Option";
	private static final String OPEN_BUTTON_TITLE = "Open File";
	private static final String CONNECT_BUTTON_TITLE = "Connect";
	private static final String DISCONNECT_BUTTON_TITLE = "Disconnect";
	private static final String DETAIL_BUTTON_TITLE = STR_DETAIL;
	private static final int SEARCH_OPTION_TEXT_HEIGHT = 58;

	// MARK: - Properties
	private Display display = null;
	private Composite optionsComposite = null;
	private StyledText searchOptionText = null;
	//private Label searchOptionText = null;
	private Listener readonlyListener = null;
	private RecordListTable table = null;
	private Button searchButton = null;
	private Button optionButton = null;
	private Button openFileButton = null;
	private Button detailButton = null;
	private Button connectDbButton = null;
	private RecordListWindow recordListWindow = null;
	private RecordDetailWindow detailWindow = null;

	private List<Record> records = null;
	private boolean exitFlag = false;
	private boolean threadRunning = false;
	private AuthResultRecordThread authThread = null;
	private OrderBy orderBy = null;
	private SearchOptions searchOptions = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param tool DMARC viewer tool.
	 */
	protected MainWindow(DmarcViewerTool tool) {
		super(tool, null);
		records = new ArrayList<>();

		widthPropKey = PROP_MAIN_SCREEN_WIDTH;
		heightPropKey = PROP_MAIN_SCREEN_HEIGHT;
		title = "DMARC Viewer";
		defaultWidth = DEFAULT_MAIN_SCREEN_WIDTH;
		defaultHeight = DEFAULT_MAIN_SCREEN_HEIGHT;
		minimumWidth = MINIMUM_MAIN_SCREEN_WIDTH;
		minimumHeight = MINIMUM_MAIN_SCREEN_HEIGHT;
		mainWindow = this;
	}

	/**
	 * open window.
	 */
	@Override
	public void open() {
		Display.setAppName("DMARC Viewer Tool");
		display = Display.getDefault();
		createShell();

		addComponents();

		shell.open();
		shell.layout();

		if (tool.getConnection() != null) {
			searchRecords();
		}

		while (shell.isDisposed() == false) {
			if (display.readAndDispatch() == false) {
				display.sleep();
			}
		}

		exitFlag = true;
		if (authThread != null) {
			try {
				authThread.join();
			}
			catch (InterruptedException e) {
				e.printStackTrace(System.err);
			}
		}

		display.dispose();
	}

	// MARK: - Getters & Setters
	/**
	 * get display.
	 * @return display.
	 */
	public Display getDisplay() {
		return display;
	}

	/**
	 * is all sub windows closed?
	 * @return true if all sub windows closed.
	 */
	public boolean isAllSubWindowsClosed() {
		if (recordListWindow != null) {
			return false;
		}
		if (detailWindow != null) {
			return false;
		}
		return true;
	}

	/**
	 * get selected record.
	 * @return selected record.
	 */
	private Record getSelectedRecord() {
		int index = table.getSelectionIndex();
		if (index > -1) {
			return records.get(index);
		}
		else {
			return null;
		}
	}

	/**
	 * get exit flag.
	 * @return the exit flag.
	 */
	public boolean isExitFlag() {
		return exitFlag;
	}

	/**
	 * check thread running.
	 * @return the thread end flag.
	 */
	public boolean isThreadRunning() {
		return threadRunning;
	}

	/**
	 * set thread end.
	 * @param thread thread object.
	 */
	public void setThreadEnd(AuthResultRecordThread thread) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				String error = null;
				try {
					threadRunning = false;
					authThread.join();
				}
				catch (InterruptedException e) {
					e.printStackTrace(System.err);
					error = e.getLocalizedMessage();
				}
				authThread = null;

				if ((error != null) && (error.length() > 0)) {
					MessageBox.showError(shell, null, error, SWT.OK);
				}
			}
		});
	}

	// MARK: - GUI
	/**
	 * window close selected.
	 */
	@Override
	protected void windowCloseSelected(Event event) {
		event.doit = isAllSubWindowsClosed();
	}

	/**
	 * add components.
	 */
	private void addComponents() {
		optionsComposite = new Composite(shell, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		optionsComposite.setLayout(gridLayout);
		searchOptionText = new StyledText(optionsComposite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		readonlyListener = new Listener() {	// read only listener.
			@Override
			public void handleEvent(Event e) {
				e.doit = false;
			}
		};
		searchOptionText.setText("");
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = SEARCH_OPTION_TEXT_HEIGHT;
		searchOptionText.setLayoutData(gd);

		table = new RecordListTable(this, tool, shell, PROP_MAIN_WINDOW_PREFIX);
		table.createTable();

		searchButton = new Button(shell, SWT.PUSH);
		searchButton.setText(SEARCH_BUTTON_TITLE);
		searchButton.setEnabled(tool.getConnection() != null);
		searchButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * widget selected.
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSearchButton(e);
			}
		});

		optionButton = new Button(shell, SWT.PUSH);
		optionButton.setText(SEARCH_OPTION_BUTTON_TITLE);
		optionButton.setEnabled(tool.getConnection() != null);
		optionButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * widget selected.
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				onOptionButton(e);
			}
		});

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

		openFileButton = new Button(shell, SWT.PUSH);
		openFileButton.setText(OPEN_BUTTON_TITLE);
		openFileButton.setEnabled(true);
		openFileButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * widget selected.
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				onOpenButton(e);
			}
		});

		if (tool.getPropertyBool(PROP_DATABASE_AUTH_REQUIRED, false)) {
			connectDbButton = new Button(shell, SWT.PUSH);
			connectDbButton.setText(tool.getConnection() != null ? DISCONNECT_BUTTON_TITLE : CONNECT_BUTTON_TITLE);
			connectDbButton.setEnabled(true);
			connectDbButton.addSelectionListener(new SelectionAdapter() {
				/**
				 * widget selected.
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					onConnectButton(e);
				}
			});
		}

		setComponentsSize(null, shell.getClientArea(), shell.getBounds());
	}

	/**
	 * set components size.
	 * @param event resize event.
	 * @param clientArea new client area rectangle.
	 * @param bounds new window rectangle.
	 */
	private void setComponentsSize(Event event, Rectangle clientArea, Rectangle bounds) {
		int x = DEFAULT_X_MARGIN;
		int y = DEFAULT_Y_MARGIN;
		int width = clientArea.width - DEFAULT_X_MARGIN * 3 - DEFAULT_BUTTON_WIDTH;
		int height = SEARCH_OPTION_TEXT_HEIGHT;
		Rectangle rect = new Rectangle(x, y, width, height);
		searchOptionText.setBounds(rect);
		optionsComposite.setBounds(rect);

		y += SEARCH_OPTION_TEXT_HEIGHT + DEFAULT_X_MARGIN;
		height = clientArea.height - DEFAULT_Y_MARGIN * 2 - SEARCH_OPTION_TEXT_HEIGHT - DEFAULT_X_MARGIN;
		rect = new Rectangle(x, y, width, height);
		table.setBounds(rect);

		x = clientArea.width - DEFAULT_BUTTON_WIDTH - DEFAULT_X_MARGIN;
		y = DEFAULT_Y_MARGIN;
		rect = new Rectangle(x, y, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		searchButton.setBounds(rect);

		y += DEFAULT_BUTTON_HEIGHT + DEFAULT_Y_MARGIN;
		rect = new Rectangle(x, y, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		optionButton.setBounds(rect);

		y += DEFAULT_BUTTON_HEIGHT + DEFAULT_Y_MARGIN;
		rect = new Rectangle(x, y, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		detailButton.setBounds(rect);

		y += DEFAULT_BUTTON_HEIGHT + DEFAULT_Y_MARGIN;
		rect = new Rectangle(x, y, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		openFileButton.setBounds(rect);

		if (connectDbButton != null) {
			y = clientArea.height - DEFAULT_BUTTON_HEIGHT - DEFAULT_Y_MARGIN;
			rect = new Rectangle(x, y, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
			connectDbButton.setBounds(rect);
		}
	}

	/**
	 * window size changed.
	 */
	@Override
	protected void windowSizeChanged(Event event, Rectangle clientArea, Rectangle bounds) {
		super.windowSizeChanged(event, clientArea, bounds);
		setComponentsSize(event, clientArea, bounds);
	}

	/**
	 * clear table.
	 */
	private void clearTable() {
		table.removeAll();
		records.clear();
		detailButton.setEnabled(false);
	}

	// MARK: - Sub Window
	/**
	 * sub window closed.
	 * @param subWindow sub window instance.
	 */
	public void subWindowClose(SubWindow subWindow) {
		if (subWindow == recordListWindow) {
			recordListWindow.closeFromOutside();
			recordListWindow = null;
		}
		else if (subWindow == detailWindow) {
			detailWindow.closeFromOutside();
			detailWindow = null;
		}
	}

	// MARK: - Events
	/**
	 * search button pushed.
	 * @param e event.
	 */
	private void onSearchButton(SelectionEvent e) {
		clearTable();
		resetOrderBy();
		searchRecords();
	}

	/**
	 * search option button pushed.
	 * @param e event.
	 */
	private void onOptionButton(SelectionEvent e) {
		SearchOptionDialog dialog = new SearchOptionDialog(shell, tool, searchOptions);
		SearchOptions options = dialog.open();
		if (options != null) {
			//System.out.println("options: " + options);
			if (options.getSearchOptionCount() < 1) {	// no search options.
				 options = null;	// clear search options.
			}
			searchOptions = options;
			clearTable();
			resetOrderBy();
			searchRecords();
		}
	}

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
			Record record = getSelectedRecord();
			if (record != null) {
				try {
					detailWindow = new RecordDetailWindow(tool, mainWindow, null, record);
					detailWindow.open();
				}
				catch (Exception ex) {
					ex.printStackTrace(System.err);
					error = ex.getLocalizedMessage();
				}
			}
		}

		if ((error != null) && (error.length() > 0)) {
			MessageBox.showError(shell, null, error, SWT.OK);
		}
	}

	/**
	 * open button pushed.
	 * @param e event.
	 */
	private void onOpenButton(SelectionEvent e) {
		String error = null;
		if (recordListWindow != null) {
			recordListWindow.bringToTop();
		}
		else {
			FileDialog dialog = new FileDialog(shell, SWT.OPEN);
			String [] exts = { "*.xml", "*.*" };
			String [] filterNames = {
				"XML Files(*.xml)",
				"All Files(*.*)"
			};
			dialog.setFilterExtensions(exts);
			dialog.setFilterNames(filterNames);
			String path = dialog.open();
			if ((path != null) && (path.length() > 0)) {
				try {
					DmarcFeedback feedback = DmarcFeedback.parse(path);
					//System.out.println("feedback: " + feedback);
					recordListWindow = new RecordListWindow(tool, this, feedback);
					recordListWindow.open();
				}
				catch (Exception ex) {
					ex.printStackTrace(System.err);
					error = ex.getLocalizedMessage();
				}
			}
		}

		if ((error != null) && (error.length() > 0)) {
			MessageBox.showError(shell, null, error, SWT.OK);
		}
	}

	/**
	 * open button pushed.
	 * @param e event.
	 */
	private void onConnectButton(SelectionEvent e) {
		String error = null;
		if (tool.getConnection() != null) {	// disconnect.
			try {
				tool.disconnectConnection();
				connectDbButton.setText(CONNECT_BUTTON_TITLE);
				searchButton.setEnabled(false);
				optionButton.setEnabled(false);
				if (recordListWindow != null) {
					recordListWindow.connectionStatusChanged();
				}
				clearTable();
			}
			catch (SQLException ex) {
				ex.printStackTrace(System.err);
				error = ex.getLocalizedMessage();
			}
		}
		else {	// connect.
			UserPasswordDialog dialog = new UserPasswordDialog(shell, tool);
			UserPasswordInput input = dialog.open();
			if (input != null) {
				try {
					tool.createAuthConnection(input.getUserId(), input.getPassword(), input.isStorePassword());
					connectDbButton.setText(DISCONNECT_BUTTON_TITLE);
					searchButton.setEnabled(true);
					optionButton.setEnabled(true);
					if (recordListWindow != null) {
						recordListWindow.connectionStatusChanged();
					}
					searchRecords();	// select.
				}
				catch (SQLException ex) {
					ex.printStackTrace(System.err);
					error = ex.getLocalizedMessage();
				}
			}
		}

		if ((error != null) && (error.length() > 0)) {
			MessageBox.showError(shell, null, error, SWT.OK);
		}
	}

	// MARK: - SQL
	/**
	 * search records.
	 */
	private void searchRecords() {
		String error = null;
		if (authThread != null) {
			exitFlag = true;
			try {
				authThread.join();
			}
			catch (InterruptedException e) {
				e.printStackTrace(System.err);
				error = e.getLocalizedMessage();
			}
			authThread = null;
			exitFlag = false;
		}

		searchOptionText.removeListener(SWT.Verify, readonlyListener);
		searchOptionText.setText("");
		if (searchOptions != null) {
			String signs = searchOptions.getSearchOptionSigns(tool);
			searchOptionText.setText("search options: " + signs);
		}
		searchOptionText.addListener(SWT.Verify, readonlyListener);

		try {
			List<Record> records = Record.select(tool.getConnection(), searchOptions, orderBy);
			//System.out.println("records: " + records);
			for (Record record : records) {
				table.addRecordToTable(record);
			}
			this.records.addAll(records);

			threadRunning = true;
			authThread = new AuthResultRecordThread();
			authThread.setMainWindow(this);
			authThread.setRecords(records);
			authThread.start();
		}
		catch (SQLException e) {
			e.printStackTrace(System.err);
			error = e.getLocalizedMessage();
		}

		if ((error != null) && (error.length() > 0)) {
			MessageBox.showError(shell, null, error, SWT.OK);
		}
	}

	/**
	 * authentication result record selected.
	 * @param record record.
	 */
	public void authResultRecordSelected(Record record) {
		int index = -1;
		String recordKey = record.getRecordKey();
		for (int i = 0; i < records.size(); i++) {
			if (records.get(i).getRecordKey().equals(recordKey)) {
				index = i;
			}
		}
		if (index > -1) {
			final int dispIndex = index;
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					table.updateAuthResultsCount(record, dispIndex);
				}
			});
		}
	}

	/**
	 * search after store.
	 */
	public void searchAfterStore() {
		clearTable();
		searchRecords();
	}

	/**
	 * reset ORDER BY.
	 */
	private void resetOrderBy() {
		if (orderBy != null) {
			table.removeSortMark(orderBy);
			orderBy = null;	// clear ORDER BY.
		}
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
		if (index > 0) {
			onDetailButton(e);
		}
	}

	/**
	 * table column selected.
	 */
	@Override
	public void tableColumnSelected(String columnName, Event e) {
		//System.out.println("columnName: " + columnName);
		if (orderBy != null) {
			table.removeSortMark(orderBy);
		}
		boolean ascending = true;
		if (orderBy != null) {
			if (columnName.equals(orderBy.getColumn())) {
				ascending = orderBy.isAscending() ? false : true;
			}
		}
		clearTable();
		orderBy = new OrderBy(columnName, ascending);
		table.addSortMark(orderBy);
		searchRecords();
	}
}
