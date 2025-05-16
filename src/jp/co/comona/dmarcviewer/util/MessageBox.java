package jp.co.comona.dmarcviewer.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import jp.co.comona.dmarcviewer.Constants;

/**
 * message box class.
 * @author kageyama
 * date: 2025/05/16
 */
public abstract class MessageBox implements Constants {

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	private MessageBox() {
		super();
	}

	// MARK: - Message Box
	/**
	 * show warning message box.
	 * @param shell shell.
	 * @param title message box title.
	 * @param message message.
	 * @param options options.
	 * @param options like SWT.YES | SWT.NO, SWT.OK | SWT.CANCEL...
	 */
	public static int showWarning(Shell shell, String title, String message, int options) {
		return show(shell, title, message, SWT.ICON_WARNING | options);
	}

	/**
	 * show error message box.
	 * @param shell shell.
	 * @param title message box title.
	 * @param message message.
	 * @param options options.
	 * @param options like SWT.YES | SWT.NO, SWT.OK | SWT.CANCEL...
	 */
	public static int showError(Shell shell, String title, String message, int options) {
		return show(shell, title, message, SWT.ICON_ERROR | options);
	}

	/**
	 * show information message box.
	 * @param shell shell.
	 * @param title message box title.
	 * @param message message.
	 * @param options options.
	 * @param options like SWT.YES | SWT.NO, SWT.OK | SWT.CANCEL...
	 */
	public static int showInformation(Shell shell, String title, String message, int options) {
		return show(shell, title, message, SWT.ICON_INFORMATION | options);
	}

	/**
	 * show message box.
	 * @param shell shell.
	 * @param title message box title.
	 * @param message message.
	 * @param options options.
	 * @return return code of message box.
	 */
	private static int show(Shell shell, String title, String message, int options) {
		org.eclipse.swt.widgets.MessageBox box = new org.eclipse.swt.widgets.MessageBox(shell, options);
		if ((title != null) && (title.length() > 0)) {
			box.setText(title);
		}
		else {
			box.setText(APP_TITLE);
		}
		box.setMessage(message);
		return box.open();
	}
}
