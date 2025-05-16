package jp.co.comona.dmarcviewer.record.where;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * text input for WHERE.
 * @author kageyama
 * date: 2025/05/13
 */
public class TextInput extends ValueInput {

	// MARK: - Static Property
	private static final String[] OPERATORS = { "equal", "not equal", "contains", "not contains", "startsWith", "not startWith", "endsWidth",  "not endsWidth", };

	// MARK: - Property
	protected Text text = null;
	protected Combo operators = null;
	private Button ignoreCaseBox = null;
	protected boolean skipCase = false;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param shell shell object.
	 * @param column column name.
	 */
	public TextInput(Shell shell, String column) {
		super(shell, column);
	}

	// MARK: - GUI
	/**
	 * create components.
	 */
	@Override
	public void createComponents() {
		createLabel();

		text = new Text(shell, getTextAttributes());
		text.setEnabled(true);

		operators = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		operators.setItems(getOperatorCandidates());
		operators.select(TextSearchOption.TYPE_EQUAL);	// 0.

		if (skipCase == false) {
			ignoreCaseBox = new Button(shell, SWT.CHECK);
			ignoreCaseBox.setSelection(false);
			ignoreCaseBox.setText(STR_IGNORE_CASE);
		}
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
		rect = new Rectangle(x, y, 120, DEFAULT_TEXT_HEIGHT);
		text.setBounds(rect);

		x += 120 + DEFAULT_X_MARGIN;
		rect = new Rectangle(x, y, DEFAULT_COMBO_WIDTH, DEFAULT_TEXT_HEIGHT);
		operators.setBounds(rect);

		if (skipCase == false) {
			x += DEFAULT_COMBO_WIDTH + DEFAULT_X_MARGIN;
			rect = new Rectangle(x, y, 120, DEFAULT_TEXT_HEIGHT);
			ignoreCaseBox.setBounds(rect);
		}

		return rect.y + rect.height;
	}

	/**
	 * get text attributes.
	 * @return text attributes.
	 */
	protected int getTextAttributes() {
		return SWT.SINGLE | SWT.BORDER;
	}

	/**
	 * get operator candidates.
	 * @return operator candidates.
	 */
	protected String[] getOperatorCandidates() {
		return OPERATORS;
	}

	// MARK: - Search Option
	/**
	 * create search option.
	 */
	@Override
	public SearchOption createSearchOption() {
		TextSearchOption option = null;
		String text = this.text.getText();
		if ((text != null) && (text.length() > 0)) {
			option = new TextSearchOption(column, text, ignoreCaseBox.getSelection() == false, operators.getSelectionIndex());
			option.setDkim(isDkim());
		}

		return option;
	}

	/**
	 * set search option.
	 */
	@Override
	public void setSearchOption(SearchOption searchOption) {
		TextSearchOption textOption = (TextSearchOption) searchOption;
		text.setText(textOption.getValue());
		operators.select(textOption.getType());
		if (skipCase == false) {
			ignoreCaseBox.setSelection(textOption.isCaseSensitive() == false);
		}
	}

	/**
	 * reset.
	 */
	@Override
	public void reset() {
		text.setText("");
		operators.select(TextSearchOption.TYPE_EQUAL);
		if (skipCase == false) {
			ignoreCaseBox.setSelection(false);
		}
	}
}
