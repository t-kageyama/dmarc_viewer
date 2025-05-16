package jp.co.comona.dmarcviewer.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

/**
 * auth_results element.
 * @author kageyama
 * date: 2025/05/09
 */
public class DmarcAuthResults extends XmlItemBase {

	// MARK: - Static Properties
	private static final String AUTH_RESULTS = DmarcRecord.AUTH_RESULTS;
	protected static final String DKIM = DmarcRow.DKIM;
	protected static final String SPF = DmarcRow.SPF;

	// MARK: - Properties
	private DmarcDkim dkim = null;
	private DmarcSpf spf = null;
	private List<DmarcDkim> dkims = null;
	private List<DmarcSpf> spfs = null;

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	protected DmarcAuthResults() {
		super();
		dkims = new ArrayList<>();
		spfs = new ArrayList<>();
	}

	// MARK: - XML Parser
	/**
	 * start of element.
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
		boolean dkimOpen = (dkim != null) && dkim.isOpen();
		if (dkimOpen) {
			dkim.startElement(namespaceURI, localName, qName, atts);
		}
		boolean spfOpen = (spf != null) && spf.isOpen();
		if (spfOpen) {
			spf.startElement(namespaceURI, localName, qName, atts);
		}

		if ((dkimOpen == false) && (spfOpen == false)) {
			switch (qName) {
				case DKIM:
					dkim = new DmarcDkim();
					dkim.startElement(namespaceURI, localName, qName, atts);
					break;
				case SPF:
					spf = new DmarcSpf();
					spf.startElement(namespaceURI, localName, qName, atts);
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
		boolean dkimOpen = (dkim != null) && dkim.isOpen();
		if (dkimOpen) {
			dkim.endElement(namespaceURI, localName, qName, content);
			if (dkim.isOpen() == false) {
				dkims.add(dkim);
				dkim = null;
			}
		}
		boolean spfOpen = (spf != null) && spf.isOpen();
		if (spfOpen) {
			spf.endElement(namespaceURI, localName, qName, content);
			if (spf.isOpen() == false) {
				spfs.add(spf);
				spf = null;
			}
		}

		if ((dkimOpen == false) && (spfOpen == false)) {
			switch (qName) {
				case AUTH_RESULTS:
					open = false;
					break;
				default:
					break;
			}
		}
	}

	// MARK: - Getters
	/**
	 * get dkim count.
	 * @return dkim count.
	 */
	public int getDkimCount() {
		return dkims.size();
	}

	/**
	 * get dkim.
	 * @param index dkim index.
	 * @return dkim.
	 */
	public DmarcDkim getDkim(int index) {
		return dkims.get(index);
	}

	/**
	 * get spf count.
	 * @return spf count.
	 */
	public int getSpfCount() {
		return spfs.size();
	}

	/**
	 * get spf.
	 * @param index spf index.
	 * @return spf.
	 */
	public DmarcSpf getSpf(int index) {
		return spfs.get(index);
	}
}
