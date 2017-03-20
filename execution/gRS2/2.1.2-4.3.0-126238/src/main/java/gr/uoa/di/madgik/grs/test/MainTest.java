package gr.uoa.di.madgik.grs.test;

import gr.uoa.di.madgik.commons.server.ConnectionManagerConfig;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.http.HTTPConnectionManager;
import gr.uoa.di.madgik.commons.server.http.IHTTPConnectionManagerEntry;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.proxy.IProxy.ProxyType;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.proxy.http.HTTPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.http.HTTPStoreConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.http.HTTPStoreWriterProxy;
import gr.uoa.di.madgik.grs.proxy.http.HTTPWriterProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalStoreWriterProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreWriterProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.registry.LifecycleManager;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore.MultiplexType;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * test class utility
 * 
 * @author gpapanikos
 *
 */
public class MainTest
{
	public enum TestType
	{
		full_reader_test,
		iterator_reader_test,
		for_each_reader_test,
		iterator_two_readers_test,
		timeout_test,
		url_test,
		file_test,
		file_mediation_test,
		string_random_test,
		string_random_iterator_test,
		store_simple_test,
		store_file_test,
		object_test,
		event_test,
		event_store_test
	}
	
	private static ProxyType pType=ProxyType.HTTP;
	private static TestType tType=TestType.string_random_test;//TestType.string_random_test;
	private static int WriterPremature=-1;
	private static int ReaderPremature=-1;
	private static int WriterItems=50000;
	private static boolean DoCompress=false;
	private static TransportDirective FileTransportDirective=TransportDirective.Full;
	private static int Capacity=10;
	private static String StoreBase="/home/nikolas/Desktop/testData";
	private static boolean DoStore=true;
	private static boolean sendEvents=true;
	private static IBufferStore.MultiplexType Multiplex=MultiplexType.FIFO;
//	private static String TestFileName="/home/gpapanikos/Desktop/testData/example.txt";
//	private static String TestFileName="/home/gpapanikos/Desktop/testData/example.big.txt";
	private static String TestFileName="/home/nikolas/Desktop/AliceWonderland.txt";
	private static String TestURI="http://www.google.com";
//	private static String TestFileName="/home/gpapanikos/Desktop/testData/example.big.cr2";
//	private static String TestFileName="/home/gpapanikos/Desktop/testData/example.m4v";
	
	private static URI toStore(URI[] locators,IBufferStore.MultiplexType multiplex, long timeout, TimeUnit unit) throws Exception
	{
		switch(MainTest.pType)
		{
			case Local: { return LocalStoreWriterProxy.store(locators, multiplex, timeout, unit); }
			case TCP: { return TCPStoreWriterProxy.store(locators, multiplex, timeout, unit); }
			case HTTP: { return HTTPStoreWriterProxy.store(locators, multiplex, timeout, unit); }
		}
		throw new Exception("Unrecognized proxy type");
	}
	
	private static IWriterProxy getWriterProxy() throws Exception
	{
		switch(MainTest.pType)
		{
			case Local: { return new LocalWriterProxy(); }
			case TCP: { return new TCPWriterProxy(); }
			case HTTP: { return new HTTPWriterProxy(); }
		}
		throw new Exception("Unrecognized proxy type");
	}
	
	/**
	 * test entry point
	 * 
	 * @param args no arguments expected
	 * @throws Exception Anything that might go wrong
	 */

	 public static synchronized void initialiseRS() {
			
			if (TCPConnectionManager.IsInitialized())
				return;
			
			
			try {
				TCPConnectionManager.Init(new TCPConnectionManagerConfig(InetAddress.getLocalHost().getHostName(),new ArrayList<PortRange>(),true));
				TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	public static void main(String []args) throws Exception
	{
		System.out.println("Initializing TCP Connection Manager");
		HTTPConnectionManager.Init(new ConnectionManagerConfig("localhost", new ArrayList<PortRange>(), true));
		HTTPConnectionManager.RegisterEntry(new HTTPConnectionHandler());
		HTTPConnectionManager.RegisterEntry(new HTTPStoreConnectionHandler());
		
//		TCPConnectionManager.Init(new ConnectionManagerConfig("localhost", new ArrayList<PortRange>(), true));
//		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
//		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
		
		
		
		
		
		System.out.println(MainTest.tType.toString());
		long startTime=System.currentTimeMillis();
		switch(MainTest.tType)
		{
			case full_reader_test:
			{
				WriterMultiFieldThread wt=new WriterMultiFieldThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.Capacity);
				wt.prepare();
				wt.start();
				URI loc=wt.writer.getLocator();
				System.out.println(loc.toString());
				ReaderFullThread rt=new ReaderFullThread(loc, MainTest.ReaderPremature);
				rt.start();
				wt.join();
				rt.join();
				break;
			}
			case for_each_reader_test:
			{
				WriterMultiFieldThread wt=new WriterMultiFieldThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.Capacity);
				wt.prepare();
				wt.start();
				URI loc=wt.writer.getLocator();
				System.out.println(loc.toString());
				ReaderForeachThread rt=new ReaderForeachThread(loc, MainTest.ReaderPremature,MainTest.sendEvents);
				rt.start();
				wt.join();
				rt.join();
				break;
			}
			case iterator_reader_test:
			{
				WriterMultiFieldThread wt=new WriterMultiFieldThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.Capacity);
				wt.prepare();
				wt.start();
				URI loc=wt.writer.getLocator();
				System.out.println(loc.toString());
				ReaderIteratorThread rt=new ReaderIteratorThread(loc, MainTest.ReaderPremature,-1);
				rt.start();
				wt.join();
				rt.join();
				break;
			}
			case iterator_two_readers_test:
			{
				WriterMultiFieldThread wt=new WriterMultiFieldThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.Capacity);
				wt.prepare();
				wt.start();
				URI loc=wt.writer.getLocator();
				System.out.println(loc.toString());
				ReaderIteratorThread rt1=new ReaderIteratorThread(loc, MainTest.ReaderPremature,1);
				ReaderIteratorThread rt2=new ReaderIteratorThread(loc, MainTest.ReaderPremature,2);
				rt1.start();
				rt2.start();
				wt.join();
				rt1.join();
				rt2.join();
				break;
			}
			case timeout_test:
			{
				System.out.println("test should end between 10000 milliseconds to "+LifecycleManager.DefaultCheckPeriod+" milliseconds");
				long start=System.currentTimeMillis();
				WriterMultiFieldThread wt=new WriterMultiFieldThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,10000,TimeUnit.MILLISECONDS,MainTest.Capacity);
				wt.prepare();
				wt.start();
				URI loc=wt.writer.getLocator();
				System.out.println(loc.toString());
				wt.join();
				System.out.println("test ended in "+(System.currentTimeMillis()-start)+" milliseconds");
			}
			case url_test:
			{
				WriterURLThread wt=new WriterURLThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.Capacity,MainTest.TestURI);
				wt.prepare();
				wt.start();
				
				URI loc=wt.writer.getLocator();
				System.out.println(loc.toString());
				ReaderURLThread rt=new ReaderURLThread(loc, MainTest.ReaderPremature);
				rt.start();
				
				
				wt.join();
				rt.join();
				break;
			}
			case file_test:
			{
				WriterFileThread wt=new WriterFileThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.TestFileName,MainTest.FileTransportDirective,MainTest.DoCompress,MainTest.Capacity);
				wt.prepare();
				wt.start();
				URI loc=wt.writer.getLocator();
				System.out.println(loc.toString());
				ReaderFileThread rt=new ReaderFileThread(loc, MainTest.ReaderPremature);
				rt.start();
				wt.join();
				rt.join();
				break;
			}
			case file_mediation_test:
			{
				WriterFileThread wt=new WriterFileThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.TestFileName,MainTest.FileTransportDirective,MainTest.DoCompress,MainTest.Capacity);
				wt.prepare();
				wt.start();
				URI loc=wt.writer.getLocator();
				System.out.println(loc.toString());
				ReaderFileMediationThread rt=new ReaderFileMediationThread(loc, MainTest.ReaderPremature,MainTest.StoreBase,MainTest.DoStore,0);
				rt.start();
				wt.join();
				rt.join();
				break;
			}
			case string_random_test:
			{
				WriterSimpleThread wt=new WriterSimpleThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.Capacity,MainTest.sendEvents);
				wt.prepare();
				wt.start();
				URI loc=wt.writer.getLocator();
				System.out.println(loc.toString());
				ReaderRandomThread rt=new ReaderRandomThread(loc);
				rt.start();
				wt.join();
				rt.join();
				break;
			}
			case string_random_iterator_test:
			{
				WriterSimpleThread wt=new WriterSimpleThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.Capacity,MainTest.sendEvents);
				wt.prepare();
				wt.start();
				URI loc=wt.writer.getLocator();
				System.out.println(loc.toString());
				ReaderRandomIteratorThread rt=new ReaderRandomIteratorThread(loc);
				rt.start();
				wt.join();
				rt.join();
				break;
			}
			case store_simple_test:
			{
				WriterSimpleThread wt1=new WriterSimpleThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.Capacity,MainTest.sendEvents);
				wt1.prepare();
				wt1.start();
				URI loc1=wt1.writer.getLocator();
				WriterSimpleThread wt2=new WriterSimpleThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.Capacity,MainTest.sendEvents);
				wt2.prepare();
				wt2.start();
				URI loc2=wt2.writer.getLocator();
				WriterSimpleThread wt3=new WriterSimpleThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.Capacity,MainTest.sendEvents);
				wt3.prepare();
				wt3.start();
				URI loc3=wt3.writer.getLocator();
				System.out.println(loc1.toString());
				System.out.println(loc2.toString());
				System.out.println(loc3.toString());
				URI storeloc= MainTest.toStore(new URI[]{loc1,loc2,loc3}, MainTest.Multiplex, 60, TimeUnit.SECONDS);
				System.out.println(storeloc);
				ReaderForeachThread rt1=new ReaderForeachThread(storeloc, MainTest.ReaderPremature,MainTest.sendEvents);
				ReaderForeachThread rt2=new ReaderForeachThread(storeloc, MainTest.ReaderPremature,MainTest.sendEvents);
				rt1.start();
				rt2.start();
				wt1.join();
				wt2.join();
				wt3.join();
				rt1.join();
				rt2.join();
				break;
			}
			case store_file_test:
			{
				WriterFileThread wt1=new WriterFileThread(new LocalWriterProxy()/*MainTest.getWriterProxy()*/,MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.TestFileName,MainTest.FileTransportDirective,MainTest.DoCompress,MainTest.Capacity);
				wt1.prepare();
				wt1.start();
				URI loc1=wt1.writer.getLocator();
				WriterFileThread wt2=new WriterFileThread(new LocalWriterProxy()/*MainTest.getWriterProxy()*/,MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.TestFileName,MainTest.FileTransportDirective,MainTest.DoCompress,MainTest.Capacity);
				wt2.prepare();
				wt2.start();
				URI loc2=wt2.writer.getLocator();
				WriterFileThread wt3=new WriterFileThread(new LocalWriterProxy()/*MainTest.getWriterProxy()*/,MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.TestFileName,MainTest.FileTransportDirective,MainTest.DoCompress,MainTest.Capacity);
				wt3.prepare();
				wt3.start();
				URI loc3=wt3.writer.getLocator();
				System.out.println(loc1.toString());
				System.out.println(loc2.toString());
				System.out.println(loc3.toString());
				URI storeloc= MainTest.toStore(new URI[]{loc1,loc2,loc3}, MainTest.Multiplex, 60, TimeUnit.SECONDS);
				System.out.println(storeloc);
//				Thread.sleep(30000);
//				System.out.println("---------------starting reading-------------");
				ReaderFileMediationThread rt1=new ReaderFileMediationThread(storeloc, MainTest.ReaderPremature,MainTest.StoreBase,MainTest.DoStore,0);
				rt1.start();
				ReaderFileMediationThread rt2=new ReaderFileMediationThread(storeloc, MainTest.ReaderPremature,MainTest.StoreBase,MainTest.DoStore,1);
				rt2.start();
				rt1.join();
				rt2.join();
				wt1.join();
				wt2.join();
				wt3.join();
				break;
			}
			case object_test:
			{
				WriterObjectThread wt=new WriterObjectThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.Capacity);
				wt.prepare();
				wt.start();
				URI loc=wt.writer.getLocator();
				System.out.println(loc.toString());
				ReaderObjectThread rt=new ReaderObjectThread(loc, MainTest.ReaderPremature);
				rt.start();
				wt.join();
				rt.join();
				break;
			}
			case event_test:
			{
				WriterSimpleThread wt=new WriterSimpleThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.Capacity,MainTest.sendEvents);
				wt.prepare();
				wt.start();
				URI loc=wt.writer.getLocator();
				System.out.println(loc.toString());
				ReaderForeachThread rt=new ReaderForeachThread(loc, MainTest.ReaderPremature,MainTest.sendEvents);
				rt.start();
				wt.join();
				rt.join();
				break;
			}
			case event_store_test:
			{
				WriterSimpleThread wt=new WriterSimpleThread(MainTest.getWriterProxy(),MainTest.WriterItems,MainTest.WriterPremature,RecordWriter.DefaultInactivityTimeout,RecordWriter.DefaultInactivityTimeUnit,MainTest.Capacity,MainTest.sendEvents);
				wt.prepare();
				wt.start();
				URI loc=wt.writer.getLocator();
				System.out.println(loc.toString());
				URI storeloc= MainTest.toStore(new URI[]{loc}, MainTest.Multiplex, 60, TimeUnit.SECONDS);
				System.out.println(storeloc.toString());
				ReaderForeachThread rt1=new ReaderForeachThread(storeloc, MainTest.ReaderPremature,MainTest.sendEvents);
				ReaderForeachThread rt2=new ReaderForeachThread(storeloc, MainTest.ReaderPremature,MainTest.sendEvents);
				rt1.start();
				rt2.start();
				wt.join();
				rt1.join();
				rt2.join();
				break;
			}
		}
		System.out.println("Test took "+(System.currentTimeMillis()-startTime));
		System.out.println("Sleeping for 2000 to allow for any background errors");
		
		Thread.sleep(2000);
	}
}
