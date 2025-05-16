package jp.co.comona.dmarcviewer.xml;

/**
 * spf element.
 * @author kageyama
 * date: 2025/05/09
 */
public class DmarcDkim extends DmarcSpf {

	// MARK: - Static Properties
	private static final String DKIM = DmarcAuthResults.DKIM;
	private static final String SELECTOR = "selector";

	// MARK: - Static Properties
	private String selector = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	protected DmarcDkim() {
		super();
	}

	// MARK: - XML Parser
	/**
	 * end of element.
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName, String content) {
		switch (qName) {
			case DOMAIN:
				domain = content;
				break;
			case SELECTOR:
				selector = content;
				break;
			case RESULT:
				result = content;
				break;

			case DKIM:
				open = false;
				break;
			default:
				break;
		}
	}

	// MARK: - Getters
	/**
	 * get selector.
	 * @return selector.
	 */
	@Override
	public String getSelector() {
		return selector;
	}
}
