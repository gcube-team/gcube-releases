/**
 *
 */
package org.gcube.portlets.user.workspace;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.workspace.server.util.StringUtil;


/**
 * The Class TestEncodeDecodeBase64.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 19, 2016
 */
public class TestEncodeDecodeBase64 {

	static final String SCOPE = "/gcube";
//	static String folderId = "e7b6bc31-8c35-4398-a7fd-492e391e17d2";
	static String folderId = "ce4866ee-8079-4acf-bcd6-1c9dd786eb73";

	static String encrypted="";
	static String encoded = "";
	static String decoded = "";
	static String decrypted = "";

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		try {
			ScopeProvider.instance.set(SCOPE);
			System.out.println("Folder Id: "+folderId);
			encode();
			decode();

			if(decrypted.compareTo(folderId)==0)
				System.out.println("Encrypt/Decript works!");
			else
				System.out.println("Encrypt/Decript doesn't work!");
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Encode.
	 *
	 * @throws Exception the exception
	 */
	private static void encode() throws Exception {

		encrypted = StringEncrypter.getEncrypter().encrypt(folderId);
		System.out.println("Encrypted folder Id: "+encrypted);
		encoded = StringUtil.base64EncodeStringURLSafe(encrypted);
		System.out.println("Encoded folder Id: "+encoded);

	}

	/**
	 * Decode.
	 *
	 * @throws Exception the exception
	 */
	private static void decode() throws Exception {

		decoded = StringUtil.base64DecodeString(encoded);
		System.out.println("Decoded folder Id: "+decoded);
		decrypted = StringEncrypter.getEncrypter().decrypt(decoded);
		System.out.println("Decrypted folder Id: "+decrypted);
	}
}
