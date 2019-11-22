/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util;

import java.io.IOException;
import java.io.InputStream;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 23, 2019
 */
public class ResetOnCloseInputStream extends InputStream {

    private final InputStream decorated;

    public ResetOnCloseInputStream(InputStream anInputStream) {
        if (!anInputStream.markSupported()) {
            throw new IllegalArgumentException("marking not supported");
        }

        anInputStream.mark( 1 << 24); // magic constant: BEWARE
        decorated = anInputStream;
    }

    @Override
    public void close() throws IOException {
        decorated.reset();
    }

    @Override
    public int read() throws IOException {
        return decorated.read();
    }
}
