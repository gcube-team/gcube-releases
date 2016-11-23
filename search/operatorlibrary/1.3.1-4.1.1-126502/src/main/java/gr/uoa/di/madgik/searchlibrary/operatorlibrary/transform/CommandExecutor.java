package gr.uoa.di.madgik.searchlibrary.operatorlibrary.transform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import org.apache.lucene.store.OutputStreamDataOutput;

public class CommandExecutor {
	private Process proc;
	private String command;

	private InputStream sErr;
	private OutputStream sIn;
	private InputStream sOut;

	private BufferedReader br;
	private BufferedWriter bw;

	public CommandExecutor(String command) {
		this.command = command;
	}

	public void execute() throws IOException, InterruptedException {
		proc = Runtime.getRuntime().exec(command);

		sErr = proc.getErrorStream();
		sIn = proc.getOutputStream();
		sOut = proc.getInputStream();

		br = new BufferedReader(new InputStreamReader(sOut));
		bw = new BufferedWriter(new OutputStreamWriter(sIn));

		new Thread() {
			public void run() {
				pipe(sErr, System.err);
			}
		}.start();
	}

	public void transform(String str) throws IOException {
		bw.write(str);
		bw.newLine();
//		bw.flush();
	}

	public int waitFor() throws IOException, InterruptedException {
		return proc.waitFor();
	}

	public int exitValue() throws IllegalThreadStateException {
		return proc.exitValue();
	}

	public void finishedWriting() throws IOException {
		bw.flush();
		bw.close();
		sIn.close();
	}

	public void finishedReading() throws IOException {
		br.close();
		sOut.close();
	}

	public BufferedReader getBufferedReader() {
		return br;
	}

	public static void pipe(InputStream in, OutputStream out) {
		try {
			int i = -1;

			byte[] buf = new byte[1024];

			while ((i = in.read(buf)) != -1) {
				out.write(buf, 0, i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test() {
		try {
			maain(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		CommandExecutor.test();
		System.out.println("Total time: " + (System.currentTimeMillis() - start));
	}

	public static void maain(String[] args) throws IOException, InterruptedException {
		final CommandExecutor exec = new CommandExecutor("python /home/jgerbe/Desktop/datasets/wiki/multi/counter.py");
//		final CommandExecutor exec = new CommandExecutor("time python /home/jgerbe/Desktop/counter.py");
//		final CommandExecutor exec = new CommandExecutor("cat");
		try {
			exec.execute();

			new Thread() {
				public void run() {
					try {
						BufferedReader br = new BufferedReader(new FileReader("/home/jgerbe/Desktop/datasets/wiki/multi/input.dat.10"));
						String line;
						int i = 0;
						while ((line = br.readLine()) != null) {
							System.out.println("in: " + line);
//							System.out.print(i + ": ");
							String[] ls = line.split("\t");
							line =  ls[0] + "\t" + ls[2] + "\t" + ls[3] + "\t";
							exec.transform(line);
							if (++i % 1000 == 0)
								System.out.println(i);
//								try {
//									Thread.sleep(1000);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
						}
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						exec.finishedWriting();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();

			BufferedReader br = exec.getBufferedReader();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/home/jgerbe/Desktop/output.dat")));
			
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				System.out.println("out: " + line);
				bw.write(line);
				bw.newLine();
			}
			exec.finishedReading();
			System.out.println(exec.waitFor());

			bw.flush();
			bw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
