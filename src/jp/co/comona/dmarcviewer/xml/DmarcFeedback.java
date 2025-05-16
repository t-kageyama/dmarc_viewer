package jp.co.comona.dmarcviewer.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * DMARC feedback parser.
 * @author kageyama
 * date: 2025/05/09
 */
public class DmarcFeedback extends DefaultHandler {

	// MARK: - Static Properties
	private static final String FEEDBACK = "feedback";
	protected static final String REPORT_METADATA = "report_metadata";
	protected static final String POLICY_PUBLISHED = "policy_published";
	protected static final String RECORD = "record";

	// MARK: - Properties
	private StringBuilder buffer = null;
	private boolean open = false;

	private DmarcReportMetadata metadata = null;
	private DmarcPolicyPublished policyPublished = null;
	private List<DmarcRecord> records = null;
	private DmarcRecord currentRecord = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	private DmarcFeedback() {
		super();
		buffer = new StringBuilder();
		records = new ArrayList<>();
	}

	/**
	 * parse XML file.
	 * @param filePath XML file path.
	 * @return response object.
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	public static DmarcFeedback parse(String filePath) throws ParserConfigurationException, SAXException, IOException {
		File file = new File(filePath);
		if (file.exists() == false) {
			throw new IOException("File '" + filePath + "' not Found!");
		}
		return parseXML(file);
	}

	/**
	 * parse XML file.
	 * @param file XML file.
	 * @return response object.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static synchronized DmarcFeedback parseXML(File file) throws ParserConfigurationException, SAXException, IOException {
		DmarcFeedback response = new DmarcFeedback();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		parser.parse(file, response);
		return response;
	}

	// MARK: - XML Parser
	/**
	 * start of element.
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
		if (FEEDBACK.equals(qName) == false) {
			assert(open);
		}

		boolean metadataOpen = (metadata != null) && metadata.open;
		if (metadataOpen) {
			metadata.startElement(namespaceURI, localName, qName, atts);
		}
		boolean policyPublishedOpen = (policyPublished != null) && policyPublished.isOpen();
		if (policyPublishedOpen) {
			policyPublished.startElement(namespaceURI, localName, qName, atts);
		}
		boolean recordOpen = (currentRecord != null) && currentRecord.isOpen();
		if (recordOpen) {
			currentRecord.startElement(namespaceURI, localName, qName, atts);
		}

		if ((metadataOpen == false) && (policyPublishedOpen == false) && (recordOpen == false)) {
			switch (qName) {
				case REPORT_METADATA:
					metadata = new DmarcReportMetadata(); 
					metadata.startElement(namespaceURI, localName, qName, atts);
					break;
				case POLICY_PUBLISHED:
					policyPublished = new DmarcPolicyPublished(); 
					policyPublished.startElement(namespaceURI, localName, qName, atts);
					break;
				case RECORD:
					currentRecord = new DmarcRecord();
					currentRecord.startElement(namespaceURI, localName, qName, atts);
					break;

				case FEEDBACK:
					open = true;
					break;
				default:
					break;
			}
		}

		buffer.setLength(0);
	}

	/**
	 * end of element.
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) {
		boolean metadataOpen = (metadata != null) && metadata.open;
		if (metadataOpen) {
			metadata.endElement(namespaceURI, localName, qName, buffer.toString());
		}
		boolean policyPublishedOpen = (policyPublished != null) && policyPublished.isOpen();
		if (policyPublishedOpen) {
			policyPublished.endElement(namespaceURI, localName, qName, buffer.toString());
		}
		boolean recordOpen = (currentRecord != null) && currentRecord.isOpen();
		if (recordOpen) {
			currentRecord.endElement(namespaceURI, localName, qName, buffer.toString());
			if (currentRecord.isOpen() == false) {
				records.add(currentRecord);
				currentRecord = null;
			}
		}

		if ((metadataOpen == false) && (policyPublishedOpen == false) && (recordOpen == false)) {
			switch (qName) {
				case FEEDBACK:
					open = false;
					break;
				default:
					break;
			}
		}
	}

	/**
	 * character found.
	 */
	@Override
	public void characters(char[] ch, int start, int length) {
		char[] cnt = new char[length];
		System.arraycopy(ch, start, cnt, 0, length);
		buffer.append(cnt);
	}

	// MARK: - Getters
	/**
	 * get org_name.
	 * @return org_name.
	 */
	public String getOrgName() {
		if (metadata != null) {
			return metadata.getOrgName();
		}
		else {
			return null;
		}
	}

	/**
	 * get report_id.
	 * @return report_id.
	 */
	public String getReportId() {
		if (metadata != null) {
			return metadata.getReportId();
		}
		else {
			return null;
		}
	}

	/**
	 * get email.
	 * @return email.
	 */
	public String getEmail() {
		if (metadata != null) {
			return metadata.getEmail();
		}
		else {
			return null;
		}
	}

	/**
	 * get extra_contact_info.
	 * @return extra_contact_info.
	 */
	public String getExtraContactInfo() {
		if (metadata != null) {
			return metadata.getExtraContactInfo();
		}
		else {
			return null;
		}
	}

	/**
	 * get begin.
	 * @return begin.
	 */
	public int getBegin() {
		if (metadata != null) {
			return metadata.getBegin();
		}
		else {
			return 0;
		}
	}

	/**
	 * get end.
	 * @return end.
	 */
	public int getEnd() {
		if (metadata != null) {
			return metadata.getEnd();
		}
		else {
			return 0;
		}
	}

	/**
	 * get domain.
	 * @return domain.
	 */
	public String getDomain() {
		if (policyPublished != null) {
			return policyPublished.getDomain();
		}
		else {
			return null;
		}
	}

	/**
	 * get adkim.
	 * @return adkim.
	 */
	public String getAdkim() {
		if (policyPublished != null) {
			return policyPublished.getAdkim();
		}
		else {
			return null;
		}
	}

	/**
	 * get aspf.
	 * @return aspf.
	 */
	public String getAspf() {
		if (policyPublished != null) {
			return policyPublished.getAspf();
		}
		else {
			return null;
		}
	}

	/**
	 * get p.
	 * @return p.
	 */
	public String getP() {
		if (policyPublished != null) {
			return policyPublished.getP();
		}
		else {
			return null;
		}
	}

	/**
	 * get sp.
	 * @return sp.
	 */
	public String getSp() {
		if (policyPublished != null) {
			return policyPublished.getSp();
		}
		else {
			return null;
		}
	}

	/**
	 * get pct.
	 * @return pct.
	 */
	public int getPct() {
		if (policyPublished != null) {
			return policyPublished.getPct();
		}
		else {
			return 0;
		}
	}

	/**
	 * get np.
	 * @return np.
	 */
	public String getNp() {
		if (policyPublished != null) {
			return policyPublished.getNp();
		}
		else {
			return null;
		}
	}

	/**
	 * get record count.
	 * @return record count.
	 */
	public int getRecordCount() {
		return records.size();
	}

	/**
	 * get record.
	 * @param index index of record.
	 * @return record.
	 */
	public DmarcRecord getRecord(int index) {
		return records.get(index);
	}
}
