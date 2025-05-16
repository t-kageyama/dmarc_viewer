package jp.co.comona.dmarcviewer.record.where;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import jp.co.comona.dmarcviewer.DialogBox;
import jp.co.comona.dmarcviewer.DmarcViewerTool;
import jp.co.comona.dmarcviewer.MainWindow;
import jp.co.comona.dmarcviewer.record.RecordColumns;
import jp.co.comona.dmarcviewer.util.MessageBox;

/**
 * search option dialog box.
 * @author kageyama
 * date: 2025/05/13
 */
public class SearchOptionDialog extends DialogBox implements RecordColumns {

	// MARK: - Static Properties
	private static final String SEARCH_BUTTON_TITLE = MainWindow.SEARCH_BUTTON_TITLE;
	private static final String TITLE = MainWindow.SEARCH_OPTION_BUTTON_TITLE;
	private static final String CLEAR_BUTTON_TITLE = "Clear";

	private static final String[] CANDIDATES_ADKIM = { "", "r", "s", };
	private static final String[] CANDIDATES_ASPF = CANDIDATES_ADKIM;
	private static final String[] CANDIDATES_P = { "", "none", "quarantine", "reject", };
	private static final String[] CANDIDATES_SP = CANDIDATES_P;
	private static final String[] CANDIDATES_NP = CANDIDATES_SP;
	private static final String[] CANDIDATES_DISPOSITION = CANDIDATES_NP;
	private static final String[] CANDIDATES_DKIM = { "", "pass", "fail", "none", };
	private static final String[] CANDIDATES_SPF = CANDIDATES_DKIM;
	private static final String[] CANDIDATES_AUTH_DKIM = CANDIDATES_DKIM;
	private static final String[] CANDIDATES_AUTH_SPF = { "", "pass", "fail", "softfail", "none", };

	// MARK: - Properties
	private SearchOptions searchOptions = null;
	private TextInput orgName = null;
	private TextInput reportId = null;
	private TextInput email = null;
	private TextInput extraContactInfo = null;
	private DateTimeInput begin = null;
	private DateTimeInput end = null;
	private TextInput domain = null;
	private ComboInput adkim = null;
	private ComboInput aspf = null;
	private ComboInput p = null;
	private ComboInput sp = null;
	private IntegerInput pct = null;
	private ComboInput np = null;
	private TextInput sourceIp = null;
	private IntegerInput count = null;
	private ComboInput disposition = null;
	private ComboInput dkim = null;
	private ComboInput spf = null;
	private TextInput headerFrom = null;
	private TextInput dkimDomain = null;
	private ComboInput dkimResult = null;
	private TextInput dkimSelector = null;
	private TextInput spfDomain = null;
	private ComboInput spfResult = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param parent parent object.
	 * @param tool DMARC viewer tool.
	 * @param searchOptions search options.
	 */
	public SearchOptionDialog(Shell parent, DmarcViewerTool tool, SearchOptions searchOptions) {
		super(parent, tool);
		this.searchOptions = searchOptions;
	}

	/**
	 * constructor.
	 * @param parent parent object.
	 * @param style dialog box style.
	 * @param tool the DMARC viewer tool to set
	 */
	public SearchOptionDialog(Shell parent, int style, DmarcViewerTool tool) {
		super(parent, style, tool);
	}

	// MARK: - GUI
	/**
	 * open dialog.
	 * @return
	 */
	public SearchOptions open() {

		SearchOptions options = null;

		createDialog();

		dialog.setSize(SEARCH_OPTION_BOX_WIDTH, SEARCH_OPTION_BOX_HEIGHT);
		dialog.setText(TITLE);

		openDialogBox();	// call addComponents() method.

		if (didOK == false) {	// Cancel button pushed.
			options = null;
		}
		else {
			options = searchOptions;
		}

		return options;
	}

	/**
	 * add components.
	 */
	@Override
	protected void addComponents() {
		Button clearButton = new Button(dialog, SWT.PUSH);
		clearButton.setText(CLEAR_BUTTON_TITLE);
		clearButton.setEnabled(tool.getConnection() != null);
		clearButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * widget selected.
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				onClearButton(e);
			}
		});

		Rectangle bounds = dialog.getClientArea();
		int x = bounds.width - DEFAULT_BUTTON_WIDTH - DEFAULT_Y_MARGIN;
		Rectangle rect = new Rectangle(x, DEFAULT_Y_MARGIN, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		clearButton.setBounds(rect);

		orgName = createTextInput(COLUMN_ORG_NAME, 0, false);
		reportId = createTextInput(COLUMN_REPORT_ID, 30, false);
		email = createTextInput(COLUMN_EMAIL, 60, false);
		extraContactInfo = createTextInput(COLUMN_EXTRA_CONTACT_INFO, 90, false);
		begin = createDateTimeInput(COLUMN_BEGIN, true, 120);
		end = createDateTimeInput(COLUMN_END, false, 150);
		domain = createTextInput(COLUMN_DOMAIN, 180, false);
		adkim = createComboInput(COLUMN_ADKIM, CANDIDATES_ADKIM, 210, false);
		aspf = createComboInput(COLUMN_ASPF, CANDIDATES_ASPF, 240, false);
		p = createComboInput(COLUMN_P, CANDIDATES_P, 270, false);
		sp = createComboInput(COLUMN_SP, CANDIDATES_SP, 300, false);
		pct = createIntegerInput(COLUMN_PCT, 330);
		np = createComboInput(COLUMN_NP, CANDIDATES_NP, 360, false);
		sourceIp = createTextInput(COLUMN_SOURCE_IP, 390, false);
		count = createIntegerInput(COLUMN_COUNT, 420);
		disposition = createComboInput(COLUMN_DISPOSITION, CANDIDATES_DISPOSITION, 450, false);
		dkim = createComboInput(COLUMN_DKIM, CANDIDATES_DKIM, 480, false);
		spf = createComboInput(COLUMN_SPF, CANDIDATES_SPF, 510, false);
		headerFrom = createTextInput(COLUMN_HEADER_FROM, 540, false);
		dkimDomain = createTextInput(COLUMN_AUTH_RESULTS_DOMAIN, 570, true);
		dkimResult = createComboInput(COLUMN_AUTH_RESULTS_RESULT, CANDIDATES_AUTH_DKIM, 600, true);
		dkimSelector = createTextInput(COLUMN_AUTH_RESULTS_SELECTOR, 630, true);
		spfDomain = createTextInput(COLUMN_AUTH_RESULTS_DOMAIN, 660, false);
		spfResult = createComboInput(COLUMN_AUTH_RESULTS_RESULT, CANDIDATES_AUTH_SPF, 690, false);
	}

	/**
	 * create text input.
	 * @param columnName column name.
	 * @param y y coordinate.
	 * @param dkim true if DKIM.
	 * @return text input.
	 */
	private TextInput createTextInput(String columnName, int y, boolean dkim) {
		TextInput textInput = new TextInput(dialog, columnName);
		textInput.setDkim(dkim);
		textInput.createComponents();
		textInput.setComponentsSize(null, dialog.getClientArea(), dialog.getBounds(), y + DEFAULT_Y_MARGIN);
		if (searchOptions != null) {
			SearchOption searchOption = searchOptions.getSearchOption(columnName, dkim);
			if (searchOption != null) {
				textInput.setSearchOption(searchOption);
			}
		}
		return textInput;
	}

	/**
	 * create date time input.
	 * @param columnName column name.
	 * @param start true if begin.
	 * @param y y coordinate.
	 * @return text input.
	 */
	private DateTimeInput createDateTimeInput(String columnName, boolean start, int y) {
		DateTimeInput dateTimeInput = new DateTimeInput(dialog, columnName, start);
		dateTimeInput.createComponents();
		dateTimeInput.setComponentsSize(null, dialog.getClientArea(), dialog.getBounds(), y + DEFAULT_Y_MARGIN);
		if (searchOptions != null) {
			SearchOption searchOption = searchOptions.getSearchOption(columnName, false);
			if (searchOption != null) {
				dateTimeInput.setSearchOption(searchOption);
			}
		}
		return dateTimeInput;
	}

	/**
	 * create combo input.
	 * @param columnName column name.
	 * @param y y coordinate.
	 * @param dkim true if DKIM.
	 * @return text input.
	 */
	private ComboInput createComboInput(String columnName, String[] candidates, int y, boolean dkim) {
		ComboInput comboInput = new ComboInput(dialog, columnName, candidates);
		comboInput.setDkim(dkim);
		comboInput.createComponents();
		comboInput.setComponentsSize(null, dialog.getClientArea(), dialog.getBounds(), y + DEFAULT_Y_MARGIN);
		if (searchOptions != null) {
			SearchOption searchOption = searchOptions.getSearchOption(columnName, dkim);
			if (searchOption != null) {
				comboInput.setSearchOption(searchOption);
			}
		}
		return comboInput;
	}

	/**
	 * create integer input.
	 * @param columnName column name.
	 * @param y y coordinate.
	 * @return text input.
	 */
	private IntegerInput createIntegerInput(String columnName, int y) {
		IntegerInput intInput = new IntegerInput(dialog, columnName);
		intInput.createComponents();
		intInput.setComponentsSize(null, dialog.getClientArea(), dialog.getBounds(), y + DEFAULT_Y_MARGIN);
		if (searchOptions != null) {
			SearchOption searchOption = searchOptions.getSearchOption(columnName, false);
			if (searchOption != null) {
				intInput.setSearchOption(searchOption);
			}
		}
		return intInput;
	}

	/**
	 * get OK button title.
	 */
	@Override
	protected String getOkButtonTitle() {
		return SEARCH_BUTTON_TITLE;
	}

	// MARK: - Events
	/**
	 * clear button pushed.
	 * @param e event.
	 */
	private void onClearButton(SelectionEvent e) {
		orgName.reset();
		reportId.reset();
		email.reset();
		extraContactInfo.reset();
		begin.reset();
		end.reset();
		domain.reset();
		adkim.reset();
		aspf.reset();
		p.reset();
		sp.reset();
		pct.reset();
		np.reset();
		sourceIp.reset();
		count.reset();
		disposition.reset();
		dkim.reset();
		spf.reset();
		headerFrom.reset();
		dkimDomain.reset();
		dkimResult.reset();
		dkimSelector.reset();
		spfDomain.reset();
		spfResult.reset();
	}


	// MARK: - Event
	/**
	 * on OK button.
	 */
	@Override
	protected boolean onOkButton(SelectionEvent e) {
		boolean ret = super.onOkButton(e);
		int beginEpoch = begin.getEpochTime();
		int endEpoch = end.getEpochTime();
		if ((beginEpoch > -1) && (endEpoch > -1)) {
			ret = endEpoch >= beginEpoch;	// end must greater or equal to begin.
		}
		if (ret) {
			searchOptions = createSearchOptions();
		}
		else {
			MessageBox.showInformation(dialog, null, MESSAGE_END_MUST_GREATER, SWT.OK);
		}
		return ret;
	}

	// MARK: - Search Options
	/**
	 * create search options.
	 * @return search options.
	 */
	private SearchOptions createSearchOptions() {
		SearchOptions options = new SearchOptions();

		SearchOption option = orgName.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = reportId.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = email.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = extraContactInfo.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = begin.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = end.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = domain.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = adkim.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = aspf.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = p.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = sp.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = pct.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = np.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = sourceIp.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = count.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = disposition.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = dkim.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = spf.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = headerFrom.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = dkimDomain.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = dkimResult.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = dkimSelector.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = spfDomain.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}
		option = spfResult.createSearchOption();
		if (option != null) {
			options.addSearchOption(option);
		}

		return options;
	}
}
