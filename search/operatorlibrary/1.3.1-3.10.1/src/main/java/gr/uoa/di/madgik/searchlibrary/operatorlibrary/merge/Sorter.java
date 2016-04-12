package gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sorter extends Thread {

	/**
	 * The Logger the class uses
	 */
	private Logger logger = LoggerFactory.getLogger(ReaderScan.class.getName());

	private BlockingQueue<RecordBufferEntry> buf;
	private ReaderScan[] scan;

	public Sorter(ReaderScan[] scan, BlockingQueue<RecordBufferEntry> buf) {
		this.scan = scan;
		this.buf = buf;
	}

	public void run() {
		Set<Integer> readersFinished = new HashSet<Integer>(scan.length);
		while (true) {
			int minRank = -2;
			int index = -1;
			for (int i = 0; i < scan.length; i++) {
				if (!readersFinished.contains(i)) {
					Pair<RecordBufferEntry, Integer> rr = scan[i].peek();
					if (rr != null) {
						RecordBufferEntry rec = rr.getFirst();
						if (rec.id < 0) {
							readersFinished.add(i);
						}

						else {
							if (index < 0 || rr.getSecond() < minRank) {
								minRank = rr.getSecond();
								index = i;
							}
						}
					} else {
						// null read, but scanner has not finished, hit the
						// same scanner while it is running, wait for a buffer
						// put
						i--;
					}
				}
			}
			if (readersFinished.size() == scan.length)
				break;
			else
				try {
					buf.put(scan[index].poll().getFirst());
				} catch (InterruptedException e) {
					logger.error("Could not put record into buffer", e);
				}
		}
	}
}
