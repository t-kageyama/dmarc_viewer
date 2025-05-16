package jp.co.comona.dmarcviewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * dialog box base class.
 * @author kageyama
 * date: 2025/05/09
 */
public abstract class DialogBox extends Dialog implements Constants {

	// MARK: - Properties
	protected DmarcViewerTool tool = null;
	protected boolean didOK = false;

	// GUI components.
	protected Shell dialog = null;
	protected Button okButton = null;
	protected Button cancelButton = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param parent parent object.
	 * @param tool the DMARC viewer tool to set
	 */
	public DialogBox(Shell parent, DmarcViewerTool tool) {
		super(parent);
		this.tool = tool;
	}

	/**
	 * constructor.
	 * @param parent parent object.
	 * @param style dialog box style.
	 * @param tool the DMARC viewer tool to set
	 */
	public DialogBox(Shell parent, int style, DmarcViewerTool tool) {
		super(parent, style);
		this.tool = tool;
	}

	// MARK: - GUI
	/**
	 * create dialog.
	 */
	protected void createDialog() {
		dialog = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * open dialog box.
	 */
	protected void openDialogBox() {
		okButton = new Button(dialog, SWT.NULL);
		okButton.setText(getOkButtonTitle());
		cancelButton = new Button(dialog, SWT.NULL);
		cancelButton.setText("Cancel");

		addComponents();

		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (onOkButton(e)) {
					didOK = true;
					dialog.dispose();
				}
			}			
		});
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialog.dispose();
			}			
		});

		Rectangle bounds = dialog.getClientArea();
		Rectangle rect = new Rectangle(bounds.width - DEFAULT_X_MARGIN - DEFAULT_BUTTON_WIDTH, bounds.height - DEFAULT_Y_MARGIN - DEFAULT_BUTTON_HEIGHT, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		okButton.setBounds(rect);
		rect.x -= DEFAULT_BUTTON_WIDTH - DEFAULT_Y_MARGIN;
		cancelButton.setBounds(rect);

		dialog.open();
		Display display = getParent().getDisplay();
		while (dialog.isDisposed() == false) {
			if (display.readAndDispatch() == false) {
				display.sleep();
			}
		}
	}

	/**
	 * add components.
	 * @param dialog dialog box.
	 */
	protected abstract void addComponents();

	/**
	 * get OK button title.
	 * @return OK button title.
	 */
	protected String getOkButtonTitle() {
		return "OK";
	}

	// MARK: - Event
	/**
	 * on OK button.
	 * @param e event.
	 * @return true.
	 */
	protected boolean onOkButton(SelectionEvent e) {
		return true;
	}
}
