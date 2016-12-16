package gr.uoa.di.madgik.workflow.test;

import gr.uoa.di.madgik.execution.utils.ThreadBufferedReader;
import java.io.IOException;

public class TestProcessBuilder
{
	public static void main(String []args) throws IOException, InterruptedException
	{
		ProcessBuilder build = new ProcessBuilder(new String[]{"/home/gpapanikos/wgetExample.sh"});
		Process p = build.start();
		Object lockMe=new Object();
		int count=0;
		synchronized (lockMe)
		{
			ThreadBufferedReader r=new ThreadBufferedReader(p.getInputStream(), true, "/home/gpapanikos/wgetExample.out", null,lockMe,null);
			r.Do();
			System.out.println(r.Output);
			r=new ThreadBufferedReader(p.getErrorStream(), true, "/home/gpapanikos/wgetExample.err", null,lockMe,null);
			r.Do();
			System.out.println(r.Output);
			while(count<2)
			{
				try{lockMe.wait();}catch(Exception ex){}
				count+=1;
			}
		}
		p.waitFor();
	}
}
