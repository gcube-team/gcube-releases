package org.gcube.common.utils.encryption;

import java.security.Key;

import org.apache.xml.security.encryption.XMLCipher;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Encrypter for {@link Element} objects
 * @author Manuele Simi (CNR)
 *
 */
public class XMLDocumentEncrypter implements Encrypter<Element> {
	
	static final private String docAlgorithmURI = XMLCipher.AES_256;
	
	private boolean encryptElementNames = false;
	
	private static XMLDocumentEncrypter singleton;
	
	static org.apache.commons.logging.Log log = 
	        org.apache.commons.logging.LogFactory.getLog(
	        		XMLDocumentEncrypter.class.getName());
	static {
        org.apache.xml.security.Init.init();
    }
	
	XMLDocumentEncrypter() {}
	
	/**
	 * Gets the Encrypter for {@link Element}s
	 * @return the encrypter
	 */
	public static XMLDocumentEncrypter getEncrypter() {
		if (singleton == null) singleton = new XMLDocumentEncrypter();
		return singleton;
	}
	
	/**
	 * Decrypts the element and all its descendants (if any)
	 */
	public Element decrypt(Element encryptedDataElement, Key ... key) throws Exception {
		
		NodeList encryptedDataNodes = /*encryptedDataElement.getElementsByTagNameNS(
				  EncryptionConstants.EncryptionSpecNS,
				  EncryptionConstants._TAG_ENCRYPTEDDATA);*/
				encryptedDataElement.getChildNodes();
		 if (encryptedDataNodes.getLength() == 0) {
			 System.out.println("Nothing to decrypt");
			 return encryptedDataElement;
		 }
		 System.out.println("To decrypt " + encryptedDataNodes.getLength());

		Key ekey = (key!=null && key.length>0)? key[0] : SymmetricKey.getKey();
        XMLCipher xmlCipher = XMLCipher.getInstance(docAlgorithmURI);
        xmlCipher.init(XMLCipher.DECRYPT_MODE, ekey);
		for (int i=0;i<encryptedDataNodes.getLength();i++ ) 
			xmlCipher.doFinal(encryptedDataElement.getOwnerDocument(), 
					(Element)encryptedDataNodes.item(i));
		
		return encryptedDataElement;
		
	}
	/**
	 * Encrypts the element with the given key or the default key
	 */
	public Element encrypt(Element element,Key... key) throws Exception {
		Key ekey = (key!=null && key.length>0)? key[0] : SymmetricKey.getKey();
        XMLCipher xmlCipher = XMLCipher.getInstance(docAlgorithmURI);
        xmlCipher.init(XMLCipher.ENCRYPT_MODE, ekey);
        /* "true" below indicates that we want to encrypt element's content
         * and not the element itself. 
         */
        xmlCipher.doFinal(element.getOwnerDocument(), element,  encryptElementNames?false:true);
       
        return element;
	}

	/**
	 * Decides if the element names must be also encrypted or only their content
	 * @param encryptElementNames<tt>true</tt> if the element names have to be encrypted, <tt>false</tt> if only their content has to be
	 */
	public void setEncryptElementNames(boolean encryptElementNames) {
		this.encryptElementNames = encryptElementNames;
	}
	
}
