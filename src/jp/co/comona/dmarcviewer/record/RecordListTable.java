package jp.co.comona.dmarcviewer.record;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import jp.co.comona.dmarcviewer.Constants;
import jp.co.comona.dmarcviewer.DmarcViewerTool;

/**
 * record list table.
 * @author kageyama
 * date: 2025/05/11
 */
public class RecordListTable implements Constants, RecordColumns {

	// MARK: - Static Properties
	private static final String[] HEADER_TITLES = { COLUMN_ORG_NAME, COLUMN_REPORT_ID, COLUMN_EMAIL, COLUMN_EXTRA_CONTACT_INFO,
			COLUMN_BEGIN, COLUMN_END, COLUMN_DOMAIN, COLUMN_ADKIM, COLUMN_ASPF, COLUMN_P, COLUMN_SP, COLUMN_PCT, COLUMN_NP,
			COLUMN_SOURCE_IP, COLUMN_COUNT, COLUMN_DISPOSITION, COLUMN_DKIM, COLUMN_SPF, COLUMN_HEADER_FROM, COLUMN_DKIM_COUNT, COLUMN_SPF_COUNT, };

	private static final char ASCENDING_MARK = 0x2193;
	private static final char DESCENDING_MARK = 0x2191;

	// MARK: - Properties
	private RecordListTableContainer container = null;
	private DmarcViewerTool tool = null;
	private Shell shell = null;
	private Table table = null;
	private String propertiesPrefix = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param container container of this class.
	 * @param tool DMARC viewer tool.
	 * @param shell shell object.
	 * @param headerTitles header titles.
	 * @param propertiesPrefix prefix of properties keys.
	 */
	public RecordListTable(RecordListTableContainer container, DmarcViewerTool tool, Shell shell, String propertiesPrefix) {
		super();
		this.container = container;
		this.tool = tool;
		this.shell = shell;
		this.propertiesPrefix = propertiesPrefix;
	}

	/**
	 * create table.
	 */
	public void createTable() {
		table = new Table(shell, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				container.tableSelectionChanged(e);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				container.tableDoubleClicked(e);
			}
		});
		for (int i = 0; i < HEADER_TITLES.length; i++) {
			TableColumn col = new TableColumn(table, SWT.LEFT);
			col.setText(HEADER_TITLES[i]);
			int colWidth = tool.getPropertyInt(propertiesPrefix + HEADER_TITLES[i].toUpperCase() + PROP_WIDTH_SUFFIX, DEFAULT_COLUMN_WIDTH);
			col.setWidth(colWidth);
			if ((i == 11) || (i == 14) || (i == 19) || (i == 20)) {
				col.setAlignment(SWT.RIGHT);
			}
		}
		TableColumn[] columns = table.getColumns();
		for (int i = 0; i < HEADER_TITLES.length; i++) {
			TableColumn column = columns[i];
			column.setData(Integer.valueOf(i));
			column.addListener(SWT.Resize, new Listener() {
				@Override
				public void handleEvent(Event arg0) {
					TableColumn column = (TableColumn) arg0.widget;
					int index = ((Integer) column.getData()).intValue();
					int width = column.getWidth();
					tool.setPropertyInt(propertiesPrefix + HEADER_TITLES[index].toUpperCase() + PROP_WIDTH_SUFFIX, width);
				}
			});
			column.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					TableColumn column = (TableColumn) event.widget;
					int index = ((Integer) column.getData()).intValue();
					container.tableColumnSelected(HEADER_TITLES[index], event);
				}
			});
		}
	}

	// MARK: - Record
	/**
	 * add record to table.
	 * @param rec record.
	 */
	public void addRecordToTable(Record rec) {
		String[] texts = new String[HEADER_TITLES.length];
		TableItem item = new TableItem(table, SWT.NULL);
		String dateTimeFormat = tool.getPropertyString(PROP_DATE_TIME_FORMAT, "yyyy/MM/dd hh:mm:ss");
		texts[0] = rec.getOrgName();
		texts[1] = rec.getReportId();
		texts[2] = rec.getEmail();
		texts[3] = rec.getExtraContactInfo();
		texts[4] = dateTimeFormat(rec.getBegin(), dateTimeFormat);
		texts[5] = dateTimeFormat(rec.getEnd(), dateTimeFormat);
		texts[6] = rec.getDomain();
		texts[7] = rec.getAdkim();
		texts[8] = rec.getAspf();
		texts[9] = rec.getP();
		texts[10] = rec.getSp();
		texts[11] = "" + rec.getPct();
		texts[12] = rec.getNp();
		texts[13] = rec.getSourceIp();
		texts[14] = "" + rec.getCount();
		texts[15] = rec.getDisposition();
		texts[16] = rec.getDkim();
		texts[17] = rec.getSpf();
		texts[18] = rec.getHeaderFrom();
		texts[19] = "" + rec.getAuthResultDkimCount();
		texts[20] = "" + rec.getAuthResultSpfCount();
		item.setText(texts);
	}

	/**
	 * update authentication results count.
	 * @param rec record.
	 * @param index index of record.
	 */
	public void updateAuthResultsCount(Record rec, int index) {
		TableItem item = table.getItem(index);
		item.setText(19, "" + rec.getAuthResultDkimCount());
		item.setText(20, "" + rec.getAuthResultSpfCount());
	}

	/**
	 * remove all contents of table.
	 */
	public void removeAll() {
		table.clearAll();
		table.removeAll();
	}

	/**
	 * get selection index.
	 * @return selection index.
	 */
	public int getSelectionIndex() {
		return table.getSelectionIndex();
	}

	// MARK: - GUI
	/**
	 * set bounds to table.
	 * @param bounds table bounds.
	 */
	public void setBounds(Rectangle bounds) {
		table.setBounds(bounds);
	}

	/**
	 * remove sort mark.
	 * @param orderBy order by object.
	 */
	public void removeSortMark(OrderBy orderBy) {
		for (int i = 0; i < HEADER_TITLES.length; i++) {
			String title = HEADER_TITLES[i];
			if (title.equals(orderBy.getColumn())) {
				TableColumn[] columns = table.getColumns();
				columns[i].setText(title);
				break;
			}
		}
	}

	/**
	 * add sort mark.
	 * @param orderBy order by object.
	 */
	public void addSortMark(OrderBy orderBy) {
		for (int i = 0; i < HEADER_TITLES.length; i++) {
			String title = HEADER_TITLES[i];
			if (title.equals(orderBy.getColumn())) {
				TableColumn[] columns = table.getColumns();
				columns[i].setText("" + (orderBy.isAscending() ? ASCENDING_MARK : DESCENDING_MARK) + title);
				break;
			}
		}
	}

	// MARK: - Utility
	/**
	 * date time format.
	 * @param unixTime UNIX time in GMT.
	 * @param dateTimeFormat date time format.
	 * @return formatted date time string.
	 */
	public static String dateTimeFormat(int unixTime, String dateTimeFormat) {
		 DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(dateTimeFormat);
		 return Instant.ofEpochSecond(Long.valueOf(unixTime)).atZone(ZoneId.systemDefault()).format(dateFormat);
	}
}
