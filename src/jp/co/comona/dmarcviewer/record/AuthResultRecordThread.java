package jp.co.comona.dmarcviewer.record;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.co.comona.dmarcviewer.MainWindow;

/**
 * authentication result record fetching thread.
 * @author kageyama
 * date: 2025/05/12
 */
public class AuthResultRecordThread extends Thread {

	// MARK: - Properties
	private MainWindow mainWindow = null;
	private List<Record> records = null;

	// MARK: - Properties
	/**
	 * constructor.
	 */
	public AuthResultRecordThread() {
		super();
	}

	/**
	 * constructor.
	 * @param target target.
	 */
	public AuthResultRecordThread(Runnable target) {
		super(target);
	}

	/**
	 * constructor.
	 * @param name thread name.
	 */
	public AuthResultRecordThread(String name) {
		super(name);
	}

	/**
	 * constructor.
	 * @param group thread group.
	 * @param target target.
	 */
	public AuthResultRecordThread(ThreadGroup group, Runnable target) {
		super(group, target);
	}

	/**
	 * constructor.
	 * @param group thread group.
	 * @param name thread name.
	 */
	public AuthResultRecordThread(ThreadGroup group, String name) {
		super(group, name);
	}

	/**
	 * constructor.
	 * @param target target.
	 * @param name thread name.
	 */
	public AuthResultRecordThread(Runnable target, String name) {
		super(target, name);
	}

	/**
	 * constructor.
	 * @param group thread group.
	 * @param target target.
	 * @param name thread name.
	 */
	public AuthResultRecordThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
	}

	/**
	 * constructor.
	 * @param group thread group.
	 * @param target target.
	 * @param name thread name.
	 * @param stackSize stack size.
	 */
	public AuthResultRecordThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
	}

	// MARK: - Run
	/**
	 * run.
	 */
	@Override
	public void run() {
		boolean tellToMainWindow = true;
		for (Record record : records) {
			//System.out.println("record: " + record);
			try {
				record.preapreAddAuthResult();
				AuthResultRecord.select(record, mainWindow.getTool().getConnection());
				mainWindow.authResultRecordSelected(record);
			}
			catch (SQLException e) {
				e.printStackTrace(System.err);
			}

			if (mainWindow.isExitFlag()) {
				tellToMainWindow = false;
				break;
			}
		}

		if (tellToMainWindow) {
			mainWindow.setThreadEnd(this);
		}
	}

	// MARK: - Getters & Setters
	/**
	 * set record.
	 * @param records record list.
	 */
	public void setRecords(List<Record> records) {
		this.records = new ArrayList<>();
		this.records.addAll(records);
	}

	/**
	 * get main window.
	 * @return main window.
	 */
	public MainWindow getMainWindow() {
		return mainWindow;
	}

	/**
	 * set main window.
	 * @param mainWindow set main window.
	 */
	public void setMainWindow(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}
}
