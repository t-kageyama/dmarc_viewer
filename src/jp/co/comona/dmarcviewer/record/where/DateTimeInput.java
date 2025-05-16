package jp.co.comona.dmarcviewer.record.where;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

/**
 * date time input.
 * @author kageyama
 * date: 2025/05/13
 */
public class DateTimeInput extends ValueInput {

	// MARK: - Property
	private boolean start = false;
	private DateTime date = null;
	private DateTime time = null;
	private Button useBox = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param shell shell object.
	 * @param column column name.
	 */
	public DateTimeInput(Shell shell, String column, boolean start) {
		super(shell, column);
		this.start = start;
	}

	// MARK: - GUI
	/**
	 * create components.
	 */
	@Override
	public void createComponents() {
		createLabel();

		date = new DateTime(shell, SWT.DATE);
		time = new DateTime(shell, SWT.TIME);
		useBox = new Button(shell, SWT.CHECK);
		useBox.setText("Use");
		useBox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.widget;
				date.setEnabled(btn.getSelection());
				time.setEnabled(btn.getSelection());
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		reset();
	}

	/**
	 * set components size.
	 */
	@Override
	public int setComponentsSize(Event event, Rectangle clientArea, Rectangle bounds, int y) {
		int x = DEFAULT_X_MARGIN;
		Rectangle rect = new Rectangle(x, y, DEFAULT_LABEL_WIDTH + LABEL_WIDTH_EXTRA, DEFAULT_LABEL_HEIGHT);
		label.setBounds(rect);

		x += DEFAULT_LABEL_WIDTH + LABEL_WIDTH_EXTRA + DEFAULT_X_MARGIN;
		rect = new Rectangle(x, y, DEFAULT_DATETIME_WIDTH, DEFAULT_DATETIME_HEIGHT);
		date.setBounds(rect);

		x += DEFAULT_DATETIME_WIDTH + DEFAULT_X_MARGIN;
		rect = new Rectangle(x, y, DEFAULT_DATETIME_WIDTH, DEFAULT_DATETIME_HEIGHT);
		time.setBounds(rect);

		x += DEFAULT_DATETIME_WIDTH + DEFAULT_X_MARGIN;
		rect = new Rectangle(x, y, 120, DEFAULT_TEXT_HEIGHT);
		useBox.setBounds(rect);

		return rect.y + rect.height;
	}

	// MARK: - Search Option
	/**
	 * create search option.
	 */
	@Override
	public SearchOption createSearchOption() {
		IntegerSearchOption searchOption = null;
		if (useBox.getSelection()) {
			int epochTime = getEpochTime();
			searchOption = new IntegerSearchOption(column, epochTime, start ? IntegerSearchOption.TYPE_GREATER_EQUAL : IntegerSearchOption.TYPE_LESS_EQUAL);
			searchOption.setDkim(isDkim());
		}
		return searchOption;
	}

	/**
	 * set search option.
	 */
	@Override
	public void setSearchOption(SearchOption searchOption) {
		date.setEnabled(true);
		time.setEnabled(true);
		useBox.setSelection(true);

		IntegerSearchOption intSearch = (IntegerSearchOption) searchOption;
		int epochTime = intSearch.getValue();
		LocalDateTime dateTime = LocalDateTime.ofEpochSecond(epochTime, 0, ZoneOffset.UTC);
		Calendar cal = GregorianCalendar.from(dateTime.atZone(ZoneId.of("GMT")));
		// set time zone.
		cal.setTimeZone(TimeZone.getDefault());
		date.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		time.setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
	}

	/**
	 * reset.
	 */
	@Override
	public void reset() {
		Calendar cal = GregorianCalendar.getInstance();
		// create GMT date time.
		ZonedDateTime now = ZonedDateTime.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0, 0, ZoneId.of("GMT"));
		cal = GregorianCalendar.from(now);
		// set time zone.
		cal.setTimeZone(TimeZone.getDefault());
		if (start == false) {	// advance 23:59:59.
			cal.add(Calendar.SECOND, 24 * 60 * 60 - 1);
		}

		date.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		date.setEnabled(false);
		time.setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		time.setEnabled(false);
		useBox.setSelection(false);
	}

	// MARK: - Date Time
	/**
	 * get epoch time.
	 * @return epoch time.
	 */
	public int getEpochTime() {
		if (useBox.getSelection() == false) {
			return -1;
		}

		int year = date.getYear();
		int month = date.getMonth();
		int day = date.getDay();
		int hour = time.getHours();
		int minute = time.getMinutes();
		int second = time.getSeconds();
		LocalDateTime localDateTime = LocalDateTime.of(year, month + 1, day, hour, minute, second);
		return (int) (ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000);
	}
}
