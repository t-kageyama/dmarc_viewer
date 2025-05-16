package jp.co.comona.dmarcviewer.record.where;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

/**
 * combo input.
 * @author kageyama
 *
 */
public class ComboInput extends ValueInput {

	// MARK: - Property
	private Combo values = null;
	private Button notBox = null;
	private String[] candidates = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param shell shell object.
	 * @param column column name.
	 * @param candidates candidates of combo.
	 */
	public ComboInput(Shell shell, String column, String[] candidates) {
		super(shell, column);
		this.candidates = candidates;
	}

	// MARK: - GUI
	/**
	 * create components.
	 */
	@Override
	public void createComponents() {
		createLabel();

		values = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		values.setItems(candidates);
		values.select(0);

		notBox = new Button(shell, SWT.CHECK);
		notBox.setSelection(false);
		notBox.setText(STR_NOT);
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
		rect = new Rectangle(x, y, DEFAULT_COMBO_WIDTH, DEFAULT_COMBO_HEIGHT);
		values.setBounds(rect);

		x += DEFAULT_COMBO_WIDTH + DEFAULT_X_MARGIN;
		rect = new Rectangle(x, y, 120, DEFAULT_TEXT_HEIGHT);
		notBox.setBounds(rect);

		return rect.y + rect.height;
	}

	// MARK: - Search Option
	/**
	 * create search option.
	 */
	@Override
	public SearchOption createSearchOption() {
		TextSearchOption option = null;
		String text = values.getItem(values.getSelectionIndex());
		if ((text != null) && (text.length() > 0)) {
			option = new TextSearchOption(column, text, true, notBox.getSelection() ? TextSearchOption.TYPE_NOT_EQUAL : TextSearchOption.TYPE_EQUAL);
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
		String value = textOption.getValue();
		for (int i = 0; i < candidates.length; i++) {
			String candidate = candidates[i];
			if (candidate.equals(value) && textOption.isDkim() == isDkim()) {
				values.select(i);
				break;
			}
		}

		notBox.setSelection(textOption.getType() == TextSearchOption.TYPE_NOT_EQUAL);
	}

	/**
	 * reset.
	 */
	@Override
	public void reset() {
		values.select(0);
		notBox.setSelection(false);
	}
}
