package jp.co.comona.dmarcviewer.xml;

import org.xml.sax.Attributes;

/**
 * row element.
 * @author kageyama
 * date: 2025/05/09
 */
public class DmarcRow extends XmlItemBase {

	// MARK: - Static Properties
	private static final String ROW = DmarcRecord.ROW;
	private static final String SOURCE_IP = "source_ip";
	private static final String COUNT = "count";
	private static final String DISPOSITION = "disposition";
	protected static final String DKIM = "dkim";
	protected static final String SPF = "spf";

	// MARK: - Properties
	private String source_ip = null;
	private int count = 0;
	private String disposition = null;
	private String dkim = null;
	private String spf = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	protected DmarcRow() {
		super();
	}

	// MARK: - XML Parser
	/**
	 * start of element.
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
	}

	/**
	 * end of element.
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName, String content) {
		switch (qName) {
			case SOURCE_IP:
				source_ip = content;
				break;
			case COUNT:
				count = Integer.parseInt(content);
				break;
			case DISPOSITION:
				disposition = content;
				break;
			case DKIM:
				dkim = content;
				break;
			case SPF:
				spf = content;
				break;

			case ROW:
				open = false;
				break;
			default:
				break;
		}
	}

	// MARK: - Getters
	/**
	 * get source_ip.
	 * @return source_ip.
	 */
	public String getSourceIp() {
		return source_ip;
	}

	/**
	 * get count.
	 * @return count.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * get disposition.
	 * @return disposition.
	 */
	public String getDisposition() {
		return disposition;
	}

	/**
	 * get dkim.
	 * @return dkim.
	 */
	public String getDkim() {
		return dkim;
	}

	/**
	 * get spf.
	 * @return spf.
	 */
	public String getSpf() {
		return spf;
	}
}
