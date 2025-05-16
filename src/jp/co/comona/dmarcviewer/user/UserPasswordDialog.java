package jp.co.comona.dmarcviewer.user;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import jp.co.comona.dmarcviewer.DialogBox;
import jp.co.comona.dmarcviewer.DmarcViewerTool;

/**
 * user password dialog box class.
 * @author kageyama
 * date: 2025/05/09
 */
public class UserPasswordDialog extends DialogBox {

	// MARK: - Properties
	private UserPasswordInput input = null;

	// GUI components.
	private Text userIdText = null;
	private Text passwordText = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param parent parent object.
	 * @param tool the DMARC viewer tool.
	 */
	public UserPasswordDialog(Shell parent, DmarcViewerTool tool) {
		super(parent, tool);
	}

	/**
	 * constructor.
	 * @param parent parent object.
	 * @param style dialog box style.
	 * @param tool the DMARC viewer tool.
	 */
	public UserPasswordDialog(Shell parent, int style, DmarcViewerTool tool) {
		super(parent, style, tool);
	}

	// MARK: - GUI
	/**
	 * open dialog box.
	 * @return result object.
	 */
	public UserPasswordInput open() {
		String userId = tool.getPropertyString(PROP_DATABASE_USER, null);
		String password = tool.getPropertyPassword(PROP_DATABASE_PASSWORD);
		boolean store = tool.getPropertyBool(PROP_STORE_DATABASE_AUTH, false);

		input = new UserPasswordInput(userId, password, store);

		createDialog();

		dialog.setSize(USER_PASSWORD_BOX_WIDTH, USER_PASSWORD_BOX_HEIGHT);
		dialog.setText("Connect");

		openDialogBox();	// call addComponents() method.

		if (didOK == false) {	// Cancel button pushed.
			input = null;
		}

		return input;
	}

	/**
	 * add components.
	 */
	@Override
	protected void addComponents() {
		Label userIdLabel = new Label(dialog, SWT.SINGLE);
		userIdLabel.setText("User");
		Label passwrdLabel = new Label(dialog, SWT.SINGLE);
		passwrdLabel.setText("Password");
		userIdText = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		String userId = input.getUserId();
		if (userId == null) {
			userId = "";
		}
		userIdText.setText(userId);

		passwordText = new Text(dialog, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		String password = input.getPassword();
		if (password == null) {
			password = "";
		}
		passwordText.setText(password);

		Button storePasswordBox = new Button(dialog, SWT.CHECK);
		storePasswordBox.setText("Store Password");
		storePasswordBox.setSelection(input.isStorePassword());

		userIdPasswordModified();

		// listeners.
		userIdText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				userIdPasswordModified();
			}
		});
		passwordText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				userIdPasswordModified();
			}
		});
		storePasswordBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.getSource();
				input.setStorePassword(button.getSelection());
			}			
		});

		Rectangle bounds = dialog.getClientArea();
		Rectangle rect = new Rectangle(DEFAULT_X_MARGIN, DEFAULT_Y_MARGIN, DEFAULT_LABEL_WIDTH, DEFAULT_LABEL_HEIGHT);
		userIdLabel.setBounds(rect);
		rect.y += DEFAULT_LABEL_HEIGHT;
		passwrdLabel.setBounds(rect);

		rect = new Rectangle(DEFAULT_X_MARGIN + DEFAULT_LABEL_WIDTH, DEFAULT_Y_MARGIN, bounds.width - DEFAULT_X_MARGIN - DEFAULT_LABEL_WIDTH - DEFAULT_X_MARGIN, DEFAULT_TEXT_HEIGHT);
		userIdText.setBounds(rect);
		rect.y += DEFAULT_TEXT_HEIGHT;
		passwordText.setBounds(rect);
		rect = new Rectangle(DEFAULT_X_MARGIN, DEFAULT_Y_MARGIN + DEFAULT_TEXT_HEIGHT * 2, DEFAULT_STORE_PASSWORD_WIDTH, DEFAULT_BUTTON_HEIGHT);
		storePasswordBox.setBounds(rect);
	}

	// MARK: - Events
	/**
	 * user id/password modified.
	 */
	private void userIdPasswordModified() {
		String str = userIdText.getText();
		if (str == null) {
			str = "";
		}
		input.setUserId(str);
		str = passwordText.getText();
		if (str == null) {
			str = "";
		}
		input.setPassword(str);
		if ((input.getUserId().length() < 1) || (input.getPassword().length() < 1)) {
			okButton.setEnabled(false);
		}
		else {
			okButton.setEnabled(true);
		}
	}
}
