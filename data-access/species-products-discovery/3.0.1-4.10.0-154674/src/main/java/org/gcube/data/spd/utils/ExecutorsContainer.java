package org.gcube.data.spd.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ExecutorsContainer {

	private static final int MAX_SEARCH_THREAD_POOL= 100;

	private static final int MAX_JOB_POOL= 10;

	private static ExecutorService searchThreadPool = Executors.newFixedThreadPool(MAX_SEARCH_THREAD_POOL, new ThreadFactoryBuilder().setNameFormat("spd-search-thread-%d").build());

	private static ExecutorService jobThreadPool = Executors.newFixedThreadPool(MAX_JOB_POOL,new ThreadFactoryBuilder().setNameFormat("spd-job-thread-%d").build()); 
	
	
	public static void execSearch(Runnable runnable){
		searchThreadPool.execute(runnable);
	}
	
	public static void execJob(Runnable runnable){
		jobThreadPool.execute(runnable);
	}
	
	public static void stopAll(){
		if (searchThreadPool!=null && jobThreadPool!=null){
			searchThreadPool.shutdownNow();
			jobThreadPool.shutdownNow();
			searchThreadPool = null;
			jobThreadPool = null;
		}
	}
}
