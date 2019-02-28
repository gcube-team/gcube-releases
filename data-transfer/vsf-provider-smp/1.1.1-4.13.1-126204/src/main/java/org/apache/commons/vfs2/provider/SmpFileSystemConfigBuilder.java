package org.apache.commons.vfs2.provider;


import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemOptions;


public final class SmpFileSystemConfigBuilder extends FileSystemConfigBuilder
{
    private static final SmpFileSystemConfigBuilder BUILDER = new SmpFileSystemConfigBuilder();

    private static final String PASSIVE_MODE = SmpFileSystemConfigBuilder.class.getName() + ".PASSIVE";
    private static final String USER_DIR_IS_ROOT = SmpFileSystemConfigBuilder.class.getName() + ".USER_DIR_IS_ROOT";
    private static final String DATA_TIMEOUT = SmpFileSystemConfigBuilder.class.getName() + ".DATA_TIMEOUT";
    private static final String SO_TIMEOUT = SmpFileSystemConfigBuilder.class.getName() + ".SO_TIMEOUT";
    
    private static final String SERVER_LANGUAGE_CODE =
            SmpFileSystemConfigBuilder.class.getName() + ".SERVER_LANGUAGE_CODE";
    private static final String DEFAULT_DATE_FORMAT =
            SmpFileSystemConfigBuilder.class.getName() + ".DEFAULT_DATE_FORMAT";
    private static final String RECENT_DATE_FORMAT =
            SmpFileSystemConfigBuilder.class.getName() + ".RECENT_DATE_FORMAT";
    private static final String SERVER_TIME_ZONE_ID =
            SmpFileSystemConfigBuilder.class.getName() + ".SERVER_TIME_ZONE_ID";
    private static final String SHORT_MONTH_NAMES =
            SmpFileSystemConfigBuilder.class.getName() + ".SHORT_MONTH_NAMES";
    private static final String ENCODING =
            SmpFileSystemConfigBuilder.class.getName() + ".ENCODING";

    private SmpFileSystemConfigBuilder()
    {
        super("smp.");
    }

    public static SmpFileSystemConfigBuilder getInstance()
    {
        return BUILDER;
    }

    

    

    @Override
    protected Class<? extends FileSystem> getConfigClass()
    {
        return SmpFileSystem.class;
    }

    /**
     * enter into passive mode.
     *
     * @param opts The FileSystemOptions.
     * @param passiveMode true if passive mode should be used.
     */
    public void setPassiveMode(FileSystemOptions opts, boolean passiveMode)
    {
        setParam(opts, PASSIVE_MODE, passiveMode ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * @param opts The FileSystemOptions.
     * @return true if passive mode is set.
     * @see #setPassiveMode
     */
    public Boolean getPassiveMode(FileSystemOptions opts)
    {
        return getBoolean(opts, PASSIVE_MODE);
    }


	/**
     * use user directory as root (do not change to fs root).
     *
     * @param opts The FileSystemOptions.
     * @param userDirIsRoot true if the user directory should be treated as the root.
     */
    public void setUserDirIsRoot(FileSystemOptions opts, boolean userDirIsRoot)
    {
        setParam(opts, USER_DIR_IS_ROOT, userDirIsRoot ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * @param opts The FileSystemOptions.
     * @return true if the user directory is treated as the root.
     * @see #setUserDirIsRoot
     */
    public Boolean getUserDirIsRoot(FileSystemOptions opts)
    {
        return getBoolean(opts, USER_DIR_IS_ROOT);
    }

    /**
     * @param opts The FileSystemOptions.
     * @return The timeout as an Integer.
     * @see #setDataTimeout
     */
    public Integer getDataTimeout(FileSystemOptions opts)
    {
        return getInteger(opts, DATA_TIMEOUT);
    }

    /**
     * set the data timeout for the ftp client.<br />
     * If you set the dataTimeout to <code>null</code> no dataTimeout will be set on the
     * ftp client.
     *
     * @param opts The FileSystemOptions.
     * @param dataTimeout The timeout value.
     */
    public void setDataTimeout(FileSystemOptions opts, Integer dataTimeout)
    {
        setParam(opts, DATA_TIMEOUT, dataTimeout);
    }

    /**
     * @param opts The FileSystem options.
     * @return The timeout value.
     * @see #getDataTimeout
     * @since 2.0
     */
    public Integer getSoTimeout(FileSystemOptions opts)
    {
        return (Integer) getParam(opts, SO_TIMEOUT);
    }

    /**
     * set the socket timeout for the ftp client.<br />
     * If you set the socketTimeout to <code>null</code> no socketTimeout will be set on the
     * ftp client.
     *
     * @param opts The FileSystem options.
     * @param soTimeout The timeout value.
     * @since 2.0
     */
    public void setSoTimeout(FileSystemOptions opts, Integer soTimeout)
    {
        setParam(opts, SO_TIMEOUT, soTimeout);
    }

    /**
     * get the language code used by the server. see {@link org.apache.commons.net.ftp.FTPClientConfig}
     * for details and examples.
     * @param opts The FilesystemOptions.
     * @return The language code of the server.
     */
    public String getServerLanguageCode(FileSystemOptions opts)
    {
        return getString(opts, SERVER_LANGUAGE_CODE);
    }

    /**
     * set the language code used by the server. see {@link org.apache.commons.net.ftp.FTPClientConfig}
     * for details and examples.
     * @param opts The FileSystemOptions.
     * @param serverLanguageCode The servers language code.
     */
    public void setServerLanguageCode(FileSystemOptions opts, String serverLanguageCode)
    {
        setParam(opts, SERVER_LANGUAGE_CODE, serverLanguageCode);
    }

    /**
     * get the language code used by the server. see {@link org.apache.commons.net.ftp.FTPClientConfig}
     * for details and examples.
     * @param opts The FileSystemOptions
     * @return The default date format.
     */
    public String getDefaultDateFormat(FileSystemOptions opts)
    {
        return getString(opts, DEFAULT_DATE_FORMAT);
    }

    /**
     * set the language code used by the server. see {@link org.apache.commons.net.ftp.FTPClientConfig}
     * for details and examples.
     * @param opts The FileSystemOptions.
     * @param defaultDateFormat The default date format.
     */
    public void setDefaultDateFormat(FileSystemOptions opts, String defaultDateFormat)
    {
        setParam(opts, DEFAULT_DATE_FORMAT, defaultDateFormat);
    }

    /**
     * see {@link org.apache.commons.net.ftp.FTPClientConfig} for details and examples.
     * @param opts The FileSystemOptions.
     * @return The recent date format.
     */
    public String getRecentDateFormat(FileSystemOptions opts)
    {
        return getString(opts, RECENT_DATE_FORMAT);
    }

    /**
     * see {@link org.apache.commons.net.ftp.FTPClientConfig} for details and examples.
     * @param opts The FileSystemOptions.
     * @param recentDateFormat The recent date format.
     */
    public void setRecentDateFormat(FileSystemOptions opts, String recentDateFormat)
    {
        setParam(opts, RECENT_DATE_FORMAT, recentDateFormat);
    }

    /**
     * see {@link org.apache.commons.net.ftp.FTPClientConfig} for details and examples.
     * @param opts The FileSystemOptions.
     * @return The server timezone id.
     */
    public String getServerTimeZoneId(FileSystemOptions opts)
    {
        return getString(opts, SERVER_TIME_ZONE_ID);
    }

    /**
     * see {@link org.apache.commons.net.ftp.FTPClientConfig} for details and examples.
     * @param opts The FileSystemOptions.
     * @param serverTimeZoneId The server timezone id.
     */
    public void setServerTimeZoneId(FileSystemOptions opts, String serverTimeZoneId)
    {
        setParam(opts, SERVER_TIME_ZONE_ID, serverTimeZoneId);
    }

    /**
     * see {@link org.apache.commons.net.ftp.FTPClientConfig} for details and examples.
     * @param opts The FileSystemOptions.
     * @return An array of short month names.
     */
    public String[] getShortMonthNames(FileSystemOptions opts)
    {
        return (String[]) getParam(opts, SHORT_MONTH_NAMES);
    }

    /**
     * see {@link org.apache.commons.net.ftp.FTPClientConfig} for details and examples.
     * @param opts The FileSystemOptions.
     * @param shortMonthNames an array of short month name Strings.
     */
    public void setShortMonthNames(FileSystemOptions opts, String[] shortMonthNames)
    {
        String[] clone = null;
        if (shortMonthNames != null)
        {
            clone = new String[shortMonthNames.length];
            System.arraycopy(shortMonthNames, 0, clone, 0, shortMonthNames.length);
        }

        setParam(opts, SHORT_MONTH_NAMES, clone);
    }

    /**
     * see {@link org.apache.commons.net.ftp.FTP#setControlEncoding} for details and examples.
     * @param opts The FileSystemOptions.
     * @param encoding the encoding to use
     * @since 2.0
     */
    public void setControlEncoding(FileSystemOptions opts, String encoding)
    {
        setParam(opts, ENCODING, encoding);
    }

    /**
     * @param opts The FileSystemOptions.
     * @return The encoding.
     * @since 2.0
     * */
    public String getControlEncoding(FileSystemOptions opts)
    {
        return  (String) getParam(opts, ENCODING);
    }
}
