package org.gcube.common.homelibrary.jcr.workspace.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UnclosableBufferedInputStream extends BufferedInputStream {

    public UnclosableBufferedInputStream(InputStream in) {
    	super(in);
    	super.mark(Integer.MAX_VALUE);
    }

    @Override
    public void close() throws IOException {
    	super.reset();
    }
}