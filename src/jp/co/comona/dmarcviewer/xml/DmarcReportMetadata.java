package jp.co.comona.dmarcviewer.xml;

import org.xml.sax.Attributes;

/**
 * report_metadata element.
 * @author kageyama
 * date: 2025/05/09
 */
public class DmarcReportMetadata extends XmlItemBase {

	// MARK: - Static Properties
	private static final String REPORT_METADATA = DmarcFeedback.REPORT_METADATA;
	private static final String ORG_NAME = "org_name";
	private static final String EMAIL = "email";
	private static final String EXTRA_CONTACT_INFO = "extra_contact_info";
	private static final String REPORT_ID = "report_id";
	private static final String DATE_RANGE = "date_range";
	private static final String BEGIN = "begin";
	private static final String END = "end";

	// MARK: - Properties
	private String org_name = null;
	private String email = null;
	private String extra_contact_info = null;
	private String reportId = null;
	private int begin = 0;
	private int end = 0;

	private boolean inDateRange = false;

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	protected DmarcReportMetadata() {
		super();
	}

	// MARK: - XML Parser
	/**
	 * start of element.
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
		if (DATE_RANGE.equals(qName)) {
			inDateRange = true;
		}
	}

	/**
	 * end of element.
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName, String content) {
		switch (qName) {
			case ORG_NAME:
				org_name = content;
				break;
			case EMAIL:
				email = content;
				break;
			case EXTRA_CONTACT_INFO:
				extra_contact_info = content;
				break;
			case REPORT_ID:
				reportId = content;
				break;
			case DATE_RANGE:
				inDateRange = false;
				break;
			case BEGIN:
				assert(inDateRange);
				begin = Integer.parseInt(content);
				break;
			case END:
				assert(inDateRange);
				end = Integer.parseInt(content);
				break;

			case REPORT_METADATA:
				open = false;
				break;
			default:
				break;
		}
	}

	// MARK: - Getters
	/**
	 * get org_name.
	 * @return the org_name
	 */
	public String getOrgName() {
		return org_name;
	}

	/**
	 * get email
	 * @return email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * get extra_contact_info.
	 * @return extra_contact_info.
	 */
	public String getExtraContactInfo() {
		return extra_contact_info;
	}

	/**
	 * get report_id.
	 * @return report_id.
	 */
	public String getReportId() {
		return reportId;
	}

	/**
	 * get begin date range.
	 * @return begin date range.
	 */
	public int getBegin() {
		return begin;
	}

	/**
	 * get end date range.
	 * @return end date range.
	 */
	public int getEnd() {
		return end;
	}
}
