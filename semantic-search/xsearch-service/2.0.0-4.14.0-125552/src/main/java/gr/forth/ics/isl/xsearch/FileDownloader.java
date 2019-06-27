/*
 * 
 * Copyright 2012 FORTH-ICS-ISL (http://www.ics.forth.gr/isl/) 
 * Foundation for Research and Technology - Hellas (FORTH)
 * Institute of Computer Science (ICS) 
 * Information Systems Laboratory (ISL)
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent 
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * 
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 * 
 */
package gr.forth.ics.isl.xsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

/**
 * @author Yannis Marketakis (yannismarketakis 'at' gmail 'dot' com)
 */
public class FileDownloader {

    private final int bufferSize = 153600;
    private final String tarSuffix = ".tar.gz";

    public FileDownloader() {
    }

	/**
	This method is responsible for downloading a file from one location to a local one.
	The remote location should be given using an appropriate url, while the destination should be 
	specified as the path in the filesystem where the downloaded file should be stored.
	@param getFrom the url where the file should be downloaded from
	@param storeTo the path where the file should be downlowded
	*/
    public FileDownloader download(String getFrom, String storeTo) {
        byte[] buffer = new byte[this.bufferSize];
        int bytesRead = 0;
        try {
            URL url = new URL(getFrom);
            url.openConnection();
            InputStream inStream = url.openStream();

            FileOutputStream outStream = new FileOutputStream(storeTo);

            while ((bytesRead = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, bytesRead);
                buffer = new byte[this.bufferSize];
            }
            outStream.close();
            inStream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

	/**
	This method is responsible for decompressing a file (in tar.gz format) in a specific 
	location in the filesystem.
	@param rarFile the compressed file
	@param destFold the path where the contents of the compressed file should be decompressed
	*/
    public FileDownloader decompress(String rarFile, String destFold) {
        try {

            File dest = new File(destFold);
            dest.mkdir();

            TarInputStream tin = new TarInputStream(new GZIPInputStream(new FileInputStream(new File(rarFile))));
            TarEntry tarEntry = tin.getNextEntry();
            while (tarEntry != null) {
                File destPath = new File(dest.toString() + File.separatorChar + tarEntry.getName());

                if (tarEntry.isDirectory()) {
                    destPath.mkdir();
                } else {
                    FileOutputStream fout = new FileOutputStream(destPath);
                    tin.copyEntryContents(fout);
                    fout.close();
                }
                tarEntry = tin.getNextEntry();
            }
            tin.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public static void main(String[] args) {
        String preUrl = "http://svn.research-infrastructures.eu/public/d4science/gcube/trunk/semantic-search/XSearch-Service-conf/";
        String prePath = "C:/Downloader/";
        String xsearchProperties = "x-search.properties";
        String miningProperties = "mining.properties";
        String clusteringProperties = "clustering.properties";
        String confFiles = "XSearch-Service-conf.tar.gz";

		
        new FileDownloader().download(preUrl + clusteringProperties, prePath + clusteringProperties).
                download(preUrl + miningProperties, prePath + miningProperties).
                download(preUrl + xsearchProperties, prePath + xsearchProperties).
                download(preUrl + confFiles, prePath + confFiles).
                decompress(prePath + confFiles, prePath);

    }
}
