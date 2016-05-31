package org.gcube.textextractor.helpers;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class Utils {
	public static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}
        
       public static void timerStart(){
           time = System.currentTimeMillis();
       }
       
       public static double timerInterval(){
           long interval =  System.currentTimeMillis() - time;
           time = System.currentTimeMillis();
           return interval / 1000.0;
       }
    private static long time;
}

