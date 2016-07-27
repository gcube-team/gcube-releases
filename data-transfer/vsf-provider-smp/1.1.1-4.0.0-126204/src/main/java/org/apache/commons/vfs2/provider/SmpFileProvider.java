package org.apache.commons.vfs2.provider;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.provider.GenericFileName;

/**
 * A provider for SMP
 *  file systems.
 *
 */
public class SmpFileProvider
    extends AbstractOriginatingFileProvider
{
   
    static final Collection<Capability> capabilities = Collections.unmodifiableCollection(Arrays.asList(new Capability[]
    {
        Capability.GET_TYPE,
        Capability.LIST_CHILDREN,
        Capability.READ_CONTENT,
        Capability.URI
    }));

    public SmpFileProvider()
    {
        super();
        setFileNameParser(SmpFileNameParser.getInstance());
    }

    /**
     * Creates the filesystem.
     */
    @Override
    protected FileSystem doCreateFileSystem(final FileName name, final FileSystemOptions fileSystemOptions)
        throws FileSystemException
    {
        // Create the file system
        final GenericFileName rootName = (GenericFileName) name;
        

        return new SmpFileSystem(rootName,  fileSystemOptions);
    }

    @Override
    public FileSystemConfigBuilder getConfigBuilder()
    {
        return SmpFileSystemConfigBuilder.getInstance();
    }

    public Collection<Capability> getCapabilities()
    {
        return capabilities;
    }
    
}
