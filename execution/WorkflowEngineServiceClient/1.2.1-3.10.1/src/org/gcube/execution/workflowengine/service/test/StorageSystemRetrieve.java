package org.gcube.execution.workflowengine.service.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.ss.StorageSystem;

public class StorageSystemRetrieve extends TestAdaptorBase
{
	
	private static void retrieveFromLocation(String urlLocation, File output) throws Exception
	{
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		byte[] buf = new byte[1024];
		try
        {
			URL url = new URL(urlLocation);
            URLConnection conn = url.openConnection();
            
            bos = new BufferedOutputStream(new FileOutputStream(output));
            bis = new BufferedInputStream(conn.getInputStream());

            int i;
            // read byte by byte until end of stream
            while ((i = bis.read(buf)) != -1)
            {
               bos.write(buf, 0, i);
            }
         }
         finally
         {
            if (bis != null)
            {
               try { bis.close(); }
               catch (IOException e){ }
            }
            if (bos != null)
            {
               try { bos.close(); }
               catch (IOException ioe) { }
            }
         }
	}
	private static void PrintHelp()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("Usage:\n");
		buf.append("Two arguments are needed\n");
		buf.append("1) The object identifier\n");
		buf.append("2) The scope of the execution that created the identifier\n");
		System.out.println(buf.toString());
	}
	
	public static void main(String[] args) throws Exception
	{
		if(args.length!=2)
		{
			StorageSystemRetrieve.PrintHelp();
			return;
		}
		TestAdaptorBase.Init();
		EnvHintCollection hints=new EnvHintCollection();
		hints.AddHint(new NamedEnvHint("GCubeActionScope",new EnvHint(args[1])));
		System.out.println("Contacting Storage System");
		File tmp = null;
		URL urlLoc = null;
		if(!args[0].startsWith("cms"))
		{
			try
			{
				urlLoc = new URL(args[0]);
			}catch(Exception e)
			{
				throw new Exception("Provided identifier was neither cms not url reference", e);
			}
			tmp = new File("/tmp", args[0].substring(args[0].lastIndexOf("/")+1) + ".ss.tmp");
			retrieveFromLocation(args[0], tmp);
		}
		else
			tmp=StorageSystem.Retrieve(args[0], hints);
		System.out.println("Retrieved content stored in "+tmp.toString());
	}
}
