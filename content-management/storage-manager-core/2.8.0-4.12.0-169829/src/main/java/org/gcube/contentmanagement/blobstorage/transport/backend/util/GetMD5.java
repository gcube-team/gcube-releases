/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */

public class GetMD5 {

    private File file;

    public GetMD5(String filePath) {
        this.file = new File(filePath);
    }

    public GetMD5(File file) {
        this.file = file;
    }

    public String getMD5() {
        String md5 = null;

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(this.file);

            // md5Hex converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
            // The returned array will be double the length of the passed array, as it takes two characters to represent any given byte.

            md5 = DigestUtils.md5Hex(IOUtils.toByteArray(fileInputStream));

            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();            
        }

        return md5;
    }
}