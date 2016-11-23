package org.apache.commons.vfs2.provider;


import java.util.Collection;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileSystem;
import org.apache.commons.vfs2.provider.GenericFileName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Smp file system.
 *
 */
public class SmpFileSystem extends AbstractFileSystem
{
    private static final Logger LOG = LoggerFactory.getLogger(SmpFileSystem.class);

    /*// An idle client
    private final AtomicReference<SmpClient> idleClient = new AtomicReference<SmpClient>();
*/
    
    public SmpFileSystem(final GenericFileName rootName, 
                         final FileSystemOptions fileSystemOptions)
    {
        super(rootName, null, fileSystemOptions);
        // hostname = rootName.getHostName();
        // port = rootName.getPort();
        
      
    }

    @Override
    protected void doCloseCommunicationLink()
    {
        //
    }

    /**
     * Adds the capabilities of this file system.
     */
    @Override
    protected void addCapabilities(final Collection<Capability> caps)
    {
        caps.addAll(SmpFileProvider.capabilities);
    }

  
    /**
     * Creates a file object.
     */
    @Override
    protected FileObject createFile(final AbstractFileName name)
        throws FileSystemException
    {
        return new SmpFileObject(name, this, getRootName());
    }
}
