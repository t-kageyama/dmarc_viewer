package jp.co.comona.dmarcviewer.xml;

import org.xml.sax.Attributes;

/**
 * policy_published element.
 * @author kageyama
 * date: 2025/05/09
 */
public class DmarcPolicyPublished extends XmlItemBase {

	// MARK: - Static Properties
	private static final String POLICY_PUBLISHED = DmarcFeedback.POLICY_PUBLISHED;
	protected static final String DOMAIN = "domain";
	private static final String ADKIM = "adkim";
	private static final String ASPF = "aspf";
	private static final String P = "p";
	private static final String SP = "sp";
	private static final String PCT = "pct";
	private static final String NP = "np";

	// MARK: - Properties
	private String domain = null;
	private String adkim = null;
	private String aspf = null;
	private String p = null;
	private String sp = null;
	private int pct = 0;
	private String np = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	protected DmarcPolicyPublished() {
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
			case ADKIM:
				adkim = content;
				break;
			case ASPF:
				aspf = content;
				break;
			case P:
				p = content;
				break;
			case SP:
				sp = content;
				break;
			case PCT:
				pct = Integer.parseInt(content);
				break;
			case NP:
				np = content;
				break;

			case POLICY_PUBLISHED:
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
	 * get adkim.
	 * @return adkim.
	 */
	public String getAdkim() {
		return adkim;
	}

	/**
	 * get aspf.
	 * @return aspf.
	 */
	public String getAspf() {
		return aspf;
	}

	/**
	 * get p.
	 * @return p.
	 */
	public String getP() {
		return p;
	}

	/**
	 * get sp.
	 * @return sp.
	 */
	public String getSp() {
		return sp;
	}

	/**
	 * get pct.
	 * @return pct.
	 */
	public int getPct() {
		return pct;
	}

	/**
	 * get np.
	 * @return np.
	 */
	public String getNp() {
		return np;
	}
}
