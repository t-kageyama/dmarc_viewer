package jp.co.comona.dmarcviewer.xml;

import org.xml.sax.Attributes;

/**
 * record element.
 * @author kageyama
 * date: 2025/05/09
 */
public class DmarcRecord extends XmlItemBase {

	// MARK: - Static Properties
	private static final String RECORD = DmarcFeedback.RECORD;
	protected static final String ROW = "row";
	private static final String IDENTIFIERS = "identifiers";
	private static final String HEADER_FROM = "header_from";
	protected static final String AUTH_RESULTS = "auth_results";

	// MARK: - Properties
	private boolean inIdentifiers = false;
	private DmarcRow row = null;
	private String header_from = null;
	private DmarcAuthResults authResults = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	protected DmarcRecord() {
		super();
	}

	// MARK: - XML Parser
	/**
	 * start of element.
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
		boolean rowOpen = (row != null) && row.isOpen();
		if (rowOpen) {
			row.startElement(namespaceURI, localName, qName, atts);
		}
		boolean authResultsOpen = (authResults != null) && authResults.isOpen();
		if (authResultsOpen) {
			authResults.startElement(namespaceURI, localName, qName, atts);
		}

		if ((rowOpen == false) && (authResultsOpen == false)) {
			switch (qName) {
				case IDENTIFIERS:
					inIdentifiers = true;
					break;
				case ROW:
					row = new DmarcRow();
					row.startElement(namespaceURI, localName, qName, atts);
					break;
				case AUTH_RESULTS:
					authResults = new DmarcAuthResults();
					authResults.startElement(namespaceURI, localName, qName, atts);
					break;
				default:
					break;
			}
		}
	}

	/**
	 * end of element.
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName, String content) {
		boolean rowOpen = (row != null) && row.isOpen();
		if (rowOpen) {
			row.endElement(namespaceURI, localName, qName, content);
		}
		boolean authResultsOpen = (authResults != null) && authResults.isOpen();
		if (authResultsOpen) {
			authResults.endElement(namespaceURI, localName, qName, content);
		}

		if ((rowOpen == false) && (authResultsOpen == false)) {
			switch (qName) {
				case IDENTIFIERS:
					inIdentifiers = false;
					break;
				case HEADER_FROM:
					assert(inIdentifiers);
					header_from = content;
					break;

				case RECORD:
					open = false;
					break;
				default:
					break;
			}
		}
	}

	// MARK: - Getters
	/**
	 * get header_from.
	 * @return header_from.
	 */
	public String getHeaderFrom() {
		return header_from;
	}

	/**
	 * get source_ip.
	 * @return source_ip.
	 */
	public String getSourceIp() {
		if (row != null) {
			return row.getSourceIp();
		}
		else {
			return null;
		}
	}

	/**
	 * get count.
	 * @return count.
	 */
	public int getCount() {
		if (row != null) {
			return row.getCount();
		}
		else {
			return 0;
		}
	}

	/**
	 * get disposition.
	 * @return disposition.
	 */
	public String getDisposition() {
		if (row != null) {
			return row.getDisposition();
		}
		else {
			return null;
		}
	}

	/**
	 * get dkim.
	 * @return dkim.
	 */
	public String getDkim() {
		if (row != null) {
			return row.getDkim();
		}
		else {
			return null;
		}
	}

	/**
	 * get spf.
	 * @return spf.
	 */
	public String getSpf() {
		if (row != null) {
			return row.getSpf();
		}
		else {
			return null;
		}
	}

	/**
	 * get dkim count.
	 * @return dkim count.
	 */
	public int getAuthResultDkimCount() {
		if (authResults != null) {
			return authResults.getDkimCount();
		}
		else {
			return 0;
		}
	}

	/**
	 * get dkim.
	 * @param index dkim index.
	 * @return dkim.
	 */
	public DmarcDkim getAuthResultDkim(int index) {
		if (authResults != null) {
			return authResults.getDkim(index);
		}
		else {
			return null;
		}
	}

	/**
	 * get spf count.
	 * @return spf count.
	 */
	public int getAuthResultSpfCount() {
		if (authResults != null) {
			return authResults.getSpfCount();
		}
		else {
			return 0;
		}
	}

	/**
	 * get spf.
	 * @param index spf index.
	 * @return spf.
	 */
	public DmarcSpf getAuthResultSpf(int index) {
		if (authResults != null) {
			return authResults.getSpf(index);
		}
		else {
			return null;
		}
	}
}
