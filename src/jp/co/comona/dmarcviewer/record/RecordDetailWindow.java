package jp.co.comona.dmarcviewer.record;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import jp.co.comona.dmarcviewer.DmarcViewerTool;
import jp.co.comona.dmarcviewer.MainWindow;
import jp.co.comona.dmarcviewer.SubWindow;

/**
 * record detail window.
 * @author kageyama
 * date: 2025/05/11
 */
public class RecordDetailWindow extends SubWindow implements RecordListTableContainer {

	// MARK: - Static Properties
	private static final String PROP_RECORD_DETAIL_DKIM_PREFIX = "RECORD_DETAIL_DKIM_";
	private static final String PROP_RECORD_DETAIL_SPF_PREFIX = "RECORD_DETAIL_SPF_";
	private static final String[] HEADER_TITLE_DKIM = {
			AuthResultRecord.COLUMN_DOMAIN,
			AuthResultRecord.COLUMN_SELECTOR,
			AuthResultRecord.COLUMN_RESULT,
	};
	private static final String[] HEADER_TITLE_SPF = {
			AuthResultRecord.COLUMN_DOMAIN,
			AuthResultRecord.COLUMN_RESULT,
	};

	// MARK: - Properties
	private RecordListWindow owner = null;
	private Record record = null;
	private RecordListTable table = null;
	private Label dkimLabel = null;
	private Table dkimTable = null;
	private Label spfLabel = null;
	private Table spfTable = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param tool DMARC viewer tool.
	 * @param mainWindow main window.
	 * @param owner owner of this window.
	 */
	public RecordDetailWindow(DmarcViewerTool tool, MainWindow mainWindow, RecordListWindow owner, Record record) {
		super(tool, mainWindow);
		this.owner = owner;
		this.record = record;

		widthPropKey = PROP_RECORD_DETAIL_SCREEN_WIDTH;
		heightPropKey = PROP_RECORD_DETAIL_SCREEN_HEIGHT;
		title = "DMARC Record Detail";
		defaultWidth = DEFAULT_RECORD_DETAIL_SCREEN_WIDTH;
		defaultHeight = DEFAULT_RECORD_DETAIL_SCREEN_HEIGHT;
		minimumWidth = MINIMUM_RECORD_DETAIL_SCREEN_WIDTH;
		minimumHeight = MINIMUM_RECORD_DETAIL_SCREEN_HEIGHT;
	}

	/**
	 * open window.
	 */
	@Override
	public void open() throws Exception {
		createShell();
		createComponents();
	}

	// MARK: - GUI
	/**
	 * create components.
	 */
	protected void createComponents() {
		table = new RecordListTable(this, tool, shell, PROP_RECORD_DETAIL_PREFIX);
		table.createTable();
		table.addRecordToTable(record);

		dkimLabel = new Label(shell, SWT.NULL);
		dkimLabel.setText(Record.COLUMN_DKIM.toUpperCase());

		spfLabel = new Label(shell, SWT.NULL);
		spfLabel.setText(Record.COLUMN_SPF.toUpperCase());

		dkimTable = createTable(HEADER_TITLE_DKIM, PROP_RECORD_DETAIL_DKIM_PREFIX);
		spfTable = createTable(HEADER_TITLE_SPF, PROP_RECORD_DETAIL_SPF_PREFIX);

		addDkimRecords();
		addSpfRecords();

		setComponetPosition(null, shell.getClientArea(), shell.getBounds());
	}

	/**
	 * create table.
	 * @param headerTitles header titles.
	 * @param propertiesPrefix properties key prefix.
	 * @return created table.
	 */
	private Table createTable(String[] headerTitles, String propertiesPrefix) {
		Table table = new Table(shell, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableSelectionChanged(e);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				tableDoubleClicked(e);
			}
		});
		for (int i = 0; i < headerTitles.length; i++) {
			TableColumn col = new TableColumn(table, SWT.LEFT);
			col.setText(headerTitles[i]);
			int colWidth = tool.getPropertyInt(propertiesPrefix + headerTitles[i].toUpperCase() + PROP_WIDTH_SUFFIX, DEFAULT_COLUMN_WIDTH);
			col.setWidth(colWidth);
			if ((i == 11) || (i == 14) || (i == 19) || (i == 20)) {
				col.setAlignment(SWT.RIGHT);
			}
		}
		TableColumn[] columns = table.getColumns();
		for (int i = 0; i < headerTitles.length; i++) {
			TableColumn column = columns[i];
			column.setData(Integer.valueOf(i));
			column.addListener(SWT.Resize, new Listener() {
				@Override
				public void handleEvent(Event arg0) {
					TableColumn column = (TableColumn) arg0.widget;
					int index = ((Integer) column.getData()).intValue();
					int width = column.getWidth();
					tool.setPropertyInt(propertiesPrefix + headerTitles[index].toUpperCase() + PROP_WIDTH_SUFFIX, width);
				}
			});
		}
		return table;
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
		int width = clientArea.width - DEFAULT_X_MARGIN * 2;
		int height = 64;
		Rectangle rect = new Rectangle(x, y, width, height);
		table.setBounds(rect);

		y = rect.y + rect.height + DEFAULT_Y_MARGIN;
		int lower = clientArea.height - y;
		int halfLower = lower / 2;

		rect = new Rectangle(x, y, DEFAULT_LABEL_WIDTH, DEFAULT_LABEL_HEIGHT);
		dkimLabel.setBounds(rect);
		y += DEFAULT_LABEL_HEIGHT;
		height = halfLower - DEFAULT_LABEL_HEIGHT;
		rect = new Rectangle(x, y, width, height);
		dkimTable.setBounds(rect);

		y = rect.y + rect.height + DEFAULT_Y_MARGIN;
		rect = new Rectangle(x, y, DEFAULT_LABEL_WIDTH, DEFAULT_LABEL_HEIGHT);
		spfLabel.setBounds(rect);
		y += DEFAULT_LABEL_HEIGHT;
		height = halfLower - DEFAULT_LABEL_HEIGHT;
		rect = new Rectangle(x, y, width, height);
		spfTable.setBounds(rect);
	}

	/**
	 * window size changed.
	 */
	@Override
	protected void windowSizeChanged(Event event, Rectangle clientArea, Rectangle bounds) {
		super.windowSizeChanged(event, clientArea, bounds);
		setComponetPosition(event, clientArea, bounds);
	}

	/**
	 * window close selected.
	 */
	@Override
	protected void windowCloseSelected(Event event) {
		if (owner != null) {
			owner.subWindowClose(this);
		}
		else {
			mainWindow.subWindowClose(this);
		}
	}

	/**
	 * add DKIM records.
	 */
	private void addDkimRecords() {
		for (int i = 0; i < record.getAuthResultDkimCount(); i++) {
			AuthResultRecord authRec = record.getAuthResultDkim(i);
			String[] texts = new String[HEADER_TITLE_DKIM.length];
			TableItem item = new TableItem(dkimTable, SWT.NULL);
			texts[0] = authRec.getDomain();
			texts[1] = authRec.getSelector();
			texts[2] = authRec.getResult();
			item.setText(texts);
		}
	}

	/**
	 * add SPF records.
	 */
	private void addSpfRecords() {
		for (int i = 0; i < record.getAuthResultSpfCount(); i++) {
			AuthResultRecord authRec = record.getAuthResultSpf(i);
			String[] texts = new String[HEADER_TITLE_SPF.length];
			TableItem item = new TableItem(spfTable, SWT.NULL);
			texts[0] = authRec.getDomain();
			texts[1] = authRec.getResult();
			item.setText(texts);
		}
	}

	// MARK: - RecordListTableContainer
	/**
	 * table selection changed.
	 */
	@Override
	public void tableSelectionChanged(SelectionEvent e) {
		// nothing to do.
	}

	/**
	 * table double clicked.
	 */
	@Override
	public void tableDoubleClicked(SelectionEvent e) {
		// nothing to do.
	}

	/**
	 * table column selected.
	 */
	@Override
	public void tableColumnSelected(String columnName, Event e) {
		// nothing to do.
	}
}
