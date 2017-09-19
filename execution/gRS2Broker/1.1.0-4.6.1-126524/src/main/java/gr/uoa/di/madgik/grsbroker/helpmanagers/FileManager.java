package gr.uoa.di.madgik.grsbroker.helpmanagers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileManager {
	private static Logger logger = Logger.getLogger(FileManager.class.getName());
	private static Map<String, Map<String, FileMapping>> fileMap = new HashMap<String, Map<String, FileMapping>>();
	private static Map<String, Long> leases = new HashMap<String, Long>();

	static long leasePeriod = 60000;
	static long gcPeriod = 60000;

	static Runnable gc = new Runnable() {
		@Override
		public void run() {

			while (true) {
				logger.log(Level.FINE, "--- FILE GC START ---");

				long currentTime = System.currentTimeMillis();

				Map<String, Long> oldEntries = new HashMap<String, Long>();

				for (Map.Entry<String, Long> lease : leases.entrySet())
					if (currentTime - lease.getValue() > leasePeriod)
						oldEntries.put(lease.getKey(), lease.getValue());

				for (Map.Entry<String, Long> lease : oldEntries.entrySet()) {
					for (String fileName : fileMap.get(lease.getKey()).keySet()) {
						File f = new File(fileName);
						if (f.exists())
							f.delete();
					}
				}
				for (String key : oldEntries.keySet())
					leases.remove(key);

				try {
					Thread.sleep(gcPeriod);
				} catch (InterruptedException e) {
					logger.log(Level.FINE, "error while thread sleep", e);
				}

				logger.log(Level.FINE, "--- FILE GC END ---");
			}
		}
	};

	static {
		Thread t = new Thread(gc);

		t.setDaemon(true);
		t.start();
	}

	public static String getFilePath(String key, String fname) {
		logger.log(Level.FINE, "fileMap : " + fileMap);

		try {
			if (fileMap.containsKey(key))
				if (fileMap.get(key).containsKey(fname)) {
					leases.put(key, System.currentTimeMillis());
					return fileMap.get(key).get(fname).getSavePath();
				}
		} catch (Exception e) {
		}
		return null;
	}

	public static String getFileRealName(String key, String fname) {
		logger.log(Level.FINE, "fileMap : " + fileMap);

		try {
			if (fileMap.containsKey(key))
				if (fileMap.get(key).containsKey(fname)) {
					leases.put(key, System.currentTimeMillis());
					return fileMap.get(key).get(fname).getRealName();
				}
		} catch (Exception e) {
		}
		return null;
	}

	public static String putFileInMap(File file, String filename, String key) throws IOException {
		File temp = File.createTempFile(key + "_", ".servlet_tmp");
		temp.deleteOnExit();
		copyFile(file, temp);

		if (!fileMap.containsKey(key))
			fileMap.put(key, new HashMap<String, FileMapping>());
		leases.put(key, System.currentTimeMillis());
		fileMap.get(key).put(temp.getName(), new FileMapping(temp.getAbsolutePath(), filename));

		logger.log(Level.FINE, "Created a temp file with name : " + temp.getName());

		return temp.getName();
	}

	private static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!sourceFile.exists()) {
			return;
		}
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileChannel source = null;
		FileChannel destination = null;
		source = new FileInputStream(sourceFile).getChannel();
		destination = new FileOutputStream(destFile).getChannel();
		if (destination != null && source != null) {
			destination.transferFrom(source, 0, source.size());
		}
		if (source != null) {
			source.close();
		}
		if (destination != null) {
			destination.close();
		}

	}
}
