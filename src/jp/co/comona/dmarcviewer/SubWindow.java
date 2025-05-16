package jp.co.comona.dmarcviewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * sub window.
 * @author kageyama
 * date: 2025/05/09
 */
public abstract class SubWindow implements Constants {

	// MARK: - Properties
	protected DmarcViewerTool tool = null;
	protected MainWindow mainWindow = null;
	protected Shell shell = null;
	protected String widthPropKey = null;
	protected String heightPropKey = null;
	protected String title = null;
	protected int defaultWidth = 0;
	protected int defaultHeight = 0;
	protected int minimumWidth = 0;
	protected int minimumHeight = 0;

	// MARK: - Constructor
	/**
	 * constructor.
	 * @param tool DMARC viewer tool.
	 * @param mainWindow main window.
	 */
	public SubWindow(DmarcViewerTool tool, MainWindow mainWindow) {
		super();
		this.tool = tool;
		this.mainWindow = mainWindow;
	}

	/**
	 * open window.
	 * @throws Exception
	 */
	public abstract void open() throws Exception;

	// MARK: - GUI
	/**
	 * bring to top.
	 */
	public void bringToTop() {
		if (shell != null) {
			shell.setActive();
		}
	}

	/**
	 * close from out side.
	 */
	public void closeFromOutside() {
		shell.dispose();
		shell = null;
	}

	/**
	 * create shell.
	 */
	protected void createShell() {
		shell = new Shell();

		assert(widthPropKey != null);
		assert(defaultWidth > 0);
		int width = tool.getPropertyInt(widthPropKey, defaultWidth);
		if (width < minimumWidth) {
			width = minimumWidth;
		}

		assert(heightPropKey != null);
		assert(defaultHeight > 0);
		int height = tool.getPropertyInt(heightPropKey, defaultHeight);
		if (height < minimumHeight) {
			height = minimumHeight;
		}

		shell.setLayout(null);
		shell.setSize(width, height);
		shell.setMinimumSize(minimumWidth, minimumHeight);

		assert(title != null);
		shell.setText(title);
		shell.open();
		shell.addListener(SWT.Resize, new Listener() {
			/**
			 * window size changed.
			 * @param event size change event.
			 */
			@Override
			public void handleEvent(Event event) {
				windowSizeChanged(event, shell.getClientArea(), shell.getBounds());
			}
		});
		shell.addListener(SWT.Close, new Listener() {
			/**
			 * close event.
			 * @param event close event.
			 */
			@Override
			public void handleEvent(Event event) {
				windowCloseSelected(event);
			}
		});
	}

	/**
	 * window size changed.
	 * @param event resize event.
	 * @param clientArea new client area rectangle.
	 * @param bounds new window rectangle.
	 */
	protected void windowSizeChanged(Event event, Rectangle clientArea, Rectangle bounds) {
		assert(widthPropKey != null);
		tool.setPropertyInt(widthPropKey, bounds.width);
		assert(heightPropKey != null);
		tool.setPropertyInt(heightPropKey, bounds.height);
	}

	/**
	 * window close selected.
	 * @param event window close event.
	 */
	protected abstract void windowCloseSelected(Event event);

	// MARK: - Getters
	/**
	 * get tool object.
	 * @return tool object.
	 */
	public DmarcViewerTool getTool() {
		return tool;
	}
}
