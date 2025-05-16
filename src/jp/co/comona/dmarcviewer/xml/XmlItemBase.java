package jp.co.comona.dmarcviewer.xml;

import org.xml.sax.Attributes;

/**
 * XML item base class.
 * @author kageyama
 * date: 2025/05/09
 */
public abstract class XmlItemBase {

	// MARK: - Properties
	protected boolean open = false;

	// MARK: - Constructor
	/**
	 * constructor.
	 */
	protected XmlItemBase() {
		super();
		open = true;
	}

	// MARK: - Getters & Setters
	/**
	 * @return the open
	 */
	public boolean isOpen() {
		return open;
	}

	// MARK: - XML Parser
	/**
	 * XML element start.
	 * @param namespaceURI name space URI.
	 * @param localName local name.
	 * @param qName qualified name.
	 * @param atts attributes.
	 */
	abstract public void startElement(String namespaceURI, String localName, String qName, Attributes atts);

	/**
	 * XML element end.
	 * @param namespaceURI name space URI.
	 * @param localName local name.
	 * @param qName qualified name.
	 * @param content content of element.
	 */
	abstract public void endElement(String namespaceURI, String localName, String qName, String content);
}
