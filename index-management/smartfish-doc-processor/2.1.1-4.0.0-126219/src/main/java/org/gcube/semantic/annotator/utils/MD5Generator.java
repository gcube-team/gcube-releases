/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gcube.semantic.annotator.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * (c) 2013 FAO / UN (project: vrmf-core)
 */
/**
 * Place your class / interface description here.
 *
 * History:
 *
 * ------------- --------------- ----------------------- Date Author Comment
 * ------------- --------------- ----------------------- 12 Jul 2013 Fiorellato
 * Creation.
 *
 * @version 1.0
 * @since 12 Jul 2013
 */
public class MD5Generator {

    static private String convertToHexString(byte[] toConvert) {
        assert toConvert.length == 16 : "Result length (in bytes) should be 16 instead of " + toConvert.length;

        StringBuffer result = new StringBuffer();

        for (int i = 0; i < toConvert.length; i++) {
            result.append(Integer.toString((toConvert[i] & 0xff)
                    + 0x100, 16).substring(1));
        }

        assert result.length() == 32 : "Converted result length should be 32 instead of " + result.length();

        return result.toString();
    }

    static public String getMD5Sum(byte[] content) throws
            NoSuchAlgorithmException {
        MessageDigest complete = MessageDigest.getInstance("MD5");

        complete.update(content);

        return MD5Generator.convertToHexString(complete.digest());
    }

    /**
     * @param content
     * @return
     * @throws NoSuchAlgorithmException
     */
    static public String getMD5Sum(String content) throws
            NoSuchAlgorithmException {
        return MD5Generator.getMD5Sum(content.getBytes());
    }

    static final public void main(String[] args) throws Throwable {
        String test = "sardinella sp";

        System.out.println("Java hashcode: " + test.hashCode());
        System.out.println("MD5  hashcode: "
                + MD5Generator.getMD5Sum(test));
    }
}
