package org.gcube.common.encryption.keytool;

import java.io.IOException;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.Cipher;


@Deprecated
public class KeyTool {

    // Keystore settings
    private static final String KEYSTORE = "/home/rcirillo/my.keystore";
    private static final String KEYSTORE_PASS = "mysecret";
    private static final String KEYALIAS = "myalias";
    private static final String KEYPASS = "mysecret";  
    
    /* 
     * Convert into hex values
     */
    private static String hex(String  binStr) {

        String newStr = new String();
 
        try {
            String hexStr = "0123456789ABCDEF";
            byte [] p = binStr.getBytes();
            for(int k=0; k < p.length; k++ ){
                int j = ( p[k] >> 4 )&0xF;
                newStr = newStr + hexStr.charAt( j );
                j = p[k]&0xF;
                newStr = newStr + hexStr.charAt( j ) + " ";
            }   
        } catch (Exception e) {
            System.out.println("Failed to convert into hex values: " + e);
        } 
        
        return newStr;
    }
 
    /* 
     * Get private key from keystore
     */
    private static Key getPrivateKey( String keyname, String password, 
        String keystore) throws IOException, KeyStoreException, 
        NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException{

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(keystore), KEYSTORE_PASS.toCharArray());
        Key result = ks.getKey(keyname, password.toCharArray());
        return result;
    } 
  
    /* 
     * Get certficate from keystore.
     */  
    private static X509Certificate getCertificate(String keyname, 
        String keystore) throws IOException, KeyStoreException, 
        NoSuchAlgorithmException, CertificateException {

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(keystore), KEYSTORE_PASS.toCharArray());
        X509Certificate result = (X509Certificate) ks.getCertificate(keyname);
        return result;
    } 

    /* 
     * Get public key from keystore.
     * The public key is in the certificate. 
     */
    private static Key getPublicKey(String keyname, String keystore) 
        throws IOException, KeyStoreException, NoSuchAlgorithmException, 
        CertificateException {

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(keystore), KEYSTORE_PASS.toCharArray());
        X509Certificate cert = (X509Certificate) ks.getCertificate(keyname);

        if (cert != null) {
            return cert.getPublicKey();
        } 
        return null;
    } 

    /* 
     * Encrypt a message using the public key
     * Decrypt the encrypted message using the private key.
     */
    public static void main(String [] args){
    
        String message = "";
        byte[] messageBytes;    
        byte [] tempPub = null;
        String sPub = null;
        byte[] ciphertextBytes = null;
        byte[] textBytes = null;
   
        try {   
            
            // The source of randomness
            SecureRandom secureRandom = new SecureRandom();
            
            // Obtain a RSA Cipher Object
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding","BC");    
                                            
            // Loading certficate from keystore
            X509Certificate keystorecert = getCertificate(KEYALIAS, KEYSTORE);

            // Read the public key from keystore certificate
            RSAPublicKey keystorepub = (RSAPublicKey) keystorecert.getPublicKey();
            tempPub = keystorepub.getEncoded();
            sPub = new String( tempPub );
            System.out.println("Public key from keystore:\n" + hex(sPub) + "\n");

            // Read the pivate key from keystore certificate
            PrivateKey keystorepriv = (PrivateKey)getPrivateKey(KEYALIAS, KEYPASS, KEYSTORE);   
                 
            // Set plain message
            message = "This is my secret message.";
            messageBytes = message.getBytes();
            System.out.println("Plain message:\n" + message + "\n" );
            
            // Initialize the cipher for encryption
            cipher.init(Cipher.ENCRYPT_MODE, keystorepub, secureRandom); 
                                
            // Encrypt the message
            ciphertextBytes = cipher.doFinal(messageBytes);      
            System.out.println("Message encrypted with keystore public key:\n" + new String(ciphertextBytes) + "\n");  
    
            // Initialize the cipher for decryption
            cipher.init(Cipher.DECRYPT_MODE, keystorepriv, secureRandom); 
                                
            // Encrypt the message
            textBytes = cipher.doFinal(ciphertextBytes);     
            System.out.println("Message decrypted with keystore private key:\n" + new String(textBytes) + "\n");                    
                        
        }catch( IOException e ){
            System.out.println( "IOException:" + e );
        }catch( CertificateException e ){
            System.out.println( "CertificateException:" + e );
        }catch( NoSuchAlgorithmException e ){
            System.out.println( "NoSuchAlgorithmException:" + e );
        } catch (Exception e) {
            System.out.println( "Exception:" + e );
        }
   }
}