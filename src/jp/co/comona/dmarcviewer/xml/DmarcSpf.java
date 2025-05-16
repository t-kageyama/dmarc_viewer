package jp.co.comona.dmarcviewer.xml;

import org.xml.sax.Attributes;

/**
 * spf element.
 * @author kageyama
 * date: 2025/05/09
 */
public class DmarcSpf extends XmlItemBase {

	// MARK: - Static Properties
	private static final String SPF = DmarcAuthResults.SPF;
	protected static final String DOMAIN = DmarcPolicyPublished.DOMAIN;
	protected static final String RESULT = "result";

	// MARK: - Static Properties
	protected String domain = null;
	protected String result = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	protected DmarcSpf() {
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
			case DOMAIN:
				domain = content;
				break;
			case RESULT:
				result = content;
				break;

			case SPF:
				open = false;
				break;
			default:
				break;
		}
	}

	// MARK: - Getters
	/**
	 * get domain.
	 * @return domain.
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * get selector.
	 * @return selector.
	 */
	public String getSelector() {
		return null;
	}

	/**
	 * get result.
	 * @return result.
	 */
	public String getResult() {
		return result;
	}
}
