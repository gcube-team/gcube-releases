package org.apache.commons.vfs2.provider;



import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.util.Cryptor;
import org.apache.commons.vfs2.util.CryptorFactory;


public class SmpFileNameParser extends HostFileNameParser
{
	private static final SmpFileNameParser INSTANCE = new SmpFileNameParser();

	private static final int PORT = 21;

	public SmpFileNameParser()
	{
		super(PORT);
	}
	
	@Override
	public FileName parseUri(final VfsComponentContext context, FileName base, final String filename)
            throws FileSystemException
    {
        // FTP URI are generic URI (as per RFC 2396)
        final StringBuilder name = new StringBuilder();

        // Extract the scheme and authority parts
        final Authority auth = extractToPath(filename, name);

        // Decode and normalise the file name
        UriParser.canonicalizePath(name, 0, name.length(), this);
        UriParser.fixSeparators(name);
        FileType fileType = UriParser.normalisePath(name);
        final String path = name.toString();

        return new GenericFileName(
            auth.scheme,
            auth.hostName,
            auth.port,
            getDefaultPort(),
            auth.userName,
            auth.password,
            path,
            fileType);
    }
	@Override
	protected Authority extractToPath(final String uri,
			final StringBuilder name)
					throws FileSystemException
		{
		Authority auth = new Authority();

		// Extract the scheme
		auth.scheme = UriParser.extractScheme(uri, name);

		// Expecting "//"
		if (name.length() < 2 || name.charAt(0) != '/' || name.charAt(1) != '/')
		{
			throw new FileSystemException("vfs.provider/missing-double-slashes.error", uri);
		}
		name.delete(0, 2);

		// Extract userinfo, and split into username and password
		final String userInfo = extractUserInfo(name);
		final String userName;
		final String password;
		if (userInfo != null)
		{
			int idx = userInfo.indexOf(':');
			if (idx == -1)
			{
				userName = userInfo;
				password = null;
			}
			else
			{
				userName = userInfo.substring(0, idx);
				password = userInfo.substring(idx + 1);
			}
		}
		else
		{
			userName = null;
			password = null;
		}
		auth.userName = UriParser.decode(userName);
		auth.password = UriParser.decode(password);

		if (auth.password != null && auth.password.startsWith("{") && auth.password.endsWith("}"))
		{
			try
			{
				Cryptor cryptor = CryptorFactory.getCryptor();
				auth.password = cryptor.decrypt(auth.password.substring(1, auth.password.length() - 1));
			}
			catch (Exception ex)
			{
				throw new FileSystemException("Unable to decrypt password", ex);
			}
		}

		// Extract hostname, and normalise (lowercase)
		final String hostName = extractHostName(name);
		if (hostName == null)
		{
			throw new FileSystemException("vfs.provider/missing-hostname.error", uri);
		}
		//auth.hostName = hostName.toLowerCase();
		auth.hostName = hostName;
		// Extract port
		auth.port = extractPort(name, uri);

		// Expecting '/' or empty name
		/*if (name.length() > 0 && name.charAt(0) != '/')
		{throw new FileSystemException("vfs.provider/missing-hostname-path-sep.error", uri);
		}*/
		return auth;
	}

	  /**
     * Parsed authority info (scheme, hostname, userinfo, port)
     */
    protected static class Authority extends HostFileNameParser.Authority  {
        private String scheme;
        private String hostName;
        private String userName;
        private String password;
        private int port;


        public String getScheme()
        {
            return scheme;
        }

        public void setScheme(String scheme)
        {
            this.scheme = scheme;
        }

        public String getHostName()
        {
            return hostName;
        }

        public void setHostName(String hostName)
        {
            this.hostName = hostName;
        }

        public String getUserName()
        {
            return userName;
        }

        public void setUserName(String userName)
        {
            this.userName = userName;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }

        public int getPort()
        {
            return port;
        }

        public void setPort(int port)
        {
            this.port = port;
        }
    }
    
	public static FileNameParser getInstance()
	{
		return INSTANCE;
	}
}
