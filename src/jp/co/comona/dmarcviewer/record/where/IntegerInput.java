package jp.co.comona.dmarcviewer.record.where;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * integer input for WHERE.
 * @author kageyama
 * date: 2025/05/13
 */
public class IntegerInput extends TextInput {

	// MARK: - Static Property
	private static final String[] OPERATORS = { "equal", "not equal", "less", "less equal", "greater", "greater equal", };

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param shell shell object.
	 * @param column column name.
	 */
	public IntegerInput(Shell shell, String column) {
		super(shell, column);
		skipCase = true;	// integer has no case.
	}

	// MARK: - GUI
	/**
	 * create components.
	 */
	@Override
	public void createComponents() {
		super.createComponents();
		text.addListener(SWT.KeyDown, event ->
			event.doit = event.keyCode == SWT.BS || event.keyCode == SWT.DEL ||
			event.keyCode == SWT.ARROW_LEFT || event.keyCode == SWT.ARROW_RIGHT ||
			Character.isDigit(event.character));
	}

	/**
	 * get text attributes.
	 * @return text attributes.
	 */
	@Override
	protected int getTextAttributes() {
		return SWT.SINGLE | SWT.BORDER | SWT.RIGHT;
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
		IntegerSearchOption searchOption = null;
		String text = this.text.getText();
		if ((text != null) && (text.length() > 0)) {
			searchOption = new IntegerSearchOption(column, Integer.parseInt(text), operators.getSelectionIndex());
			searchOption.setDkim(isDkim());
		}
		return searchOption;
	}

	/**
	 * set search option.
	 */
	@Override
	public void setSearchOption(SearchOption searchOption) {
		IntegerSearchOption intOption = (IntegerSearchOption) searchOption;
		text.setText("" + intOption.getValue());
		operators.select(intOption.getType());
	}
}
