package org.gcube.data.spd.utils;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.ResultWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultWrapperMantainer {

	private static Logger log = LoggerFactory.getLogger(ResultWrapperMantainer.class);

	private static Map<String, ResultStreamingThread<?>> writerMap = new HashMap<String, ResultStreamingThread<?>>();


	public static <T extends ResultElement> ResultWrapper<T> getWrapper(Class<T> _clazz){
		ResultStreamingThread<T> retrieverThread = new ResultStreamingThread<T>(_clazz);
		ResultWrapper<T> wrapper = new ResultWrapper<T>(retrieverThread.getWriter());
		retrieverThread.start();
		writerMap.put(wrapper.getLocator(), retrieverThread);
		return wrapper;
	}

	public static ResultStreamingThread<?> getWriterById(String locator){
		return writerMap.get(locator);
	}

	public static void remove(String locator){
		if (writerMap.containsKey(locator)){
			writerMap.get(locator).getWriter().close();
			writerMap.remove(locator);
		} else log.warn("wrapper already closed");
	}
}
