package org.jack.common.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassLoaderFactory {



    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderFactory.class);

    // --------------------------------------------------------- Public Methods
    public static  ClassLoader createClassLoader(File unpacked[],
            File packed[],Repository[] repositories,
            final ClassLoader parent) throws IOException {
    	 Set<URL> set = new LinkedHashSet<>();
         unpackedToUrl(set, unpacked);
         packedToUrl(set, packed);
         toUrl(set, repositories);
         return createClassLoader(parent, set);
	}
    public static  ClassLoader createClassLoader(File unpacked[],
           Repository[] repositories,
            final ClassLoader parent) throws IOException {
    	 Set<URL> set = new LinkedHashSet<>();
         unpackedToUrl(set, unpacked);
         toUrl(set, repositories);
         return createClassLoader(parent, set);
	}
    public static  ClassLoader createClassLoader(final ClassLoader parent,Set<URL> set) {
    	return createClassLoader(parent, set.toArray(new URL[set.size()]));
	}
    public static  ClassLoader createClassLoader(final ClassLoader parent,final URL...urls) {
    	return AccessController.doPrivileged(
                new PrivilegedAction<URLClassLoader>() {
                    @Override
                    public URLClassLoader run() {
                        if (parent == null)
                            return new URLClassLoader(urls);
                        else
                            return new URLClassLoader(urls, parent);
                    }
                });
	}
    
    /**
     * Create and return a new class loader, based on the configuration
     * defaults and the specified directory paths:
     *
     * @param unpacked Array of pathnames to unpacked directories that should
     *  be added to the repositories of the class loader, or <code>null</code>
     * for no unpacked directories to be considered
     * @param packed Array of pathnames to directories containing JAR files
     *  that should be added to the repositories of the class loader,
     * or <code>null</code> for no directories of JAR files to be considered
     * @param parent Parent class loader for the new class loader, or
     *  <code>null</code> for the system class loader.
     * @return the new class loader
     *
     * @exception Exception if an error occurs constructing the class loader
     */
    public static ClassLoader createClassLoader(File unpacked[],
                                                File packed[],
                                                final ClassLoader parent)
        throws Exception {
        if (logger.isDebugEnabled())
        	logger.debug("Creating new class loader");

        // Construct the "class path" for this class loader
        Set<URL> set = new LinkedHashSet<>();
        unpackedToUrl(set, unpacked);
        packedToUrl(set, packed);
        // Construct the class loader itself
        return createClassLoader(parent, set.toArray(new URL[set.size()]));
    }


    /**
     * Create and return a new class loader, based on the configuration
     * defaults and the specified directory paths:
     *
     * @param repositories List of class directories, jar files, jar directories
     *                     or URLS that should be added to the repositories of
     *                     the class loader.
     * @param parent Parent class loader for the new class loader, or
     *  <code>null</code> for the system class loader.
     * @return the new class loader
     *
     * @exception Exception if an error occurs constructing the class loader
     */
    public static ClassLoader createClassLoader(List<Repository> repositories,
                                                final ClassLoader parent)
        throws Exception {
        if (logger.isDebugEnabled())
        	logger.debug("Creating new class loader");

        // Construct the "class path" for this class loader
        Set<URL> set = new LinkedHashSet<>();
        toUrl(set, repositories);
        // Construct the class loader itself
        final URL[] array = set.toArray(new URL[set.size()]);
        if (logger.isDebugEnabled()){
        	for (int i = 0; i < array.length; i++) {
            	logger.debug("  location " + i + " is " + array[i]);
            }
        }
        return createClassLoader(parent, array);
    }
    public static void toUrl(Set<URL> set,List<Repository> repositories) throws IOException {
    	toUrl(set, repositories.toArray(new Repository[repositories.size()]));
	}
    public static void toUrl(Set<URL> set,Repository...repositories) throws IOException {
    	if (repositories != null) {
            for (Repository repository : repositories)  {
                if (repository.getType() == RepositoryType.URL) {
                    URL url = buildClassLoaderUrl(repository.getLocation());
                    if (logger.isDebugEnabled())
                    	logger.debug("  Including URL " + url);
                    set.add(url);
                } else if (repository.getType() == RepositoryType.DIR) {
                    File directory = new File(repository.getLocation());
                    directory = directory.getCanonicalFile();
                    if (!validateFile(directory, RepositoryType.DIR)) {
                        continue;
                    }
                    URL url = buildClassLoaderUrl(directory);
                    if (logger.isDebugEnabled())
                    	logger.debug("  Including directory " + url);
                    set.add(url);
                } else if (repository.getType() == RepositoryType.JAR) {
                    File file=new File(repository.getLocation());
                    file = file.getCanonicalFile();
                    if (!validateFile(file, RepositoryType.JAR)) {
                        continue;
                    }
                    URL url = buildClassLoaderUrl(file);
                    if (logger.isDebugEnabled())
                    	logger.debug("  Including jar file " + url);
                    set.add(url);
                } else if (repository.getType() == RepositoryType.GLOB) {
                    File directory=new File(repository.getLocation());
                    directory = directory.getCanonicalFile();
                    if (!validateFile(directory, RepositoryType.GLOB)) {
                        continue;
                    }
                    if (logger.isDebugEnabled())
                    	logger.debug("  Including directory glob "
                            + directory.getAbsolutePath());
                    String filenames[] = directory.list();
                    if (filenames == null) {
                        continue;
                    }
                    for (int j = 0; j < filenames.length; j++) {
                        String filename = filenames[j].toLowerCase(Locale.ENGLISH);
                        if (!filename.endsWith(".jar"))
                            continue;
                        File file = new File(directory, filenames[j]);
                        file = file.getCanonicalFile();
                        if (!validateFile(file, RepositoryType.JAR)) {
                            continue;
                        }
                        if (logger.isDebugEnabled())
                        	logger.debug("    Including glob jar file "
                                + file.getAbsolutePath());
                        URL url = buildClassLoaderUrl(file);
                        set.add(url);
                    }
                }
            }
        }
	}
    public static void packedToUrl(Set<URL> set,File...packed) throws IOException{
    	if (packed != null) {
            for (int i = 0; i < packed.length; i++) {
                File directory = packed[i];
                if (!directory.isDirectory() || !directory.canRead())
                    continue;
                String filenames[] = directory.list();
                if (filenames == null) {
                    continue;
                }
                for (int j = 0; j < filenames.length; j++) {
                    String filename = filenames[j].toLowerCase(Locale.ENGLISH);
                    if (!filename.endsWith(".jar"))
                        continue;
                    File file = new File(directory, filenames[j]);
                    if (logger.isDebugEnabled())
                    	logger.debug("  Including jar file " + file.getAbsolutePath());
                    URL url = file.toURI().toURL();
                    set.add(url);
                }
            }
        }
    }
    public static void unpackedToUrl(Set<URL> set,File...unpacked) throws IOException{
   	 if (unpacked != null) {
            for (int i = 0; i < unpacked.length; i++)  {
                File file = unpacked[i];
                if (!file.canRead())
                    continue;
                file = new File(file.getCanonicalPath() + File.separator);
                URL url = file.toURI().toURL();
                if (logger.isDebugEnabled())
                	logger.debug("  Including directory " + url);
                set.add(url);
            }
        }
   }
    private static boolean validateFile(File file,
            RepositoryType type) throws IOException {
        if (RepositoryType.DIR == type || RepositoryType.GLOB == type) {
            if (!file.isDirectory() || !file.canRead()) {
                String msg = "Problem with directory [" + file +
                        "], exists: [" + file.exists() +
                        "], isDirectory: [" + file.isDirectory() +
                        "], canRead: [" + file.canRead() + "]";
                String homePath="";
                String basePath="";
                File home = new File(homePath);
                home = home.getCanonicalFile();
                File base = new File (basePath);
                base = base.getCanonicalFile();
                File defaultValue = new File(base, "lib");

                // Existence of ${catalina.base}/lib directory is optional.
                // Hide the warning if Tomcat runs with separate catalina.home
                // and catalina.base and that directory is absent.
                if (!home.getPath().equals(base.getPath())
                        && file.getPath().equals(defaultValue.getPath())
                        && !file.exists()) {
                	logger.debug(msg);
                } else {
                	logger.warn(msg);
                }
                return false;
            }
        } else if (RepositoryType.JAR == type) {
            if (!file.canRead()) {
            	logger.warn("Problem with JAR file [" + file +
                        "], exists: [" + file.exists() +
                        "], canRead: [" + file.canRead() + "]");
                return false;
            }
        }
        return true;
    }


    /*
     * These two methods would ideally be in the utility class
     * org.apache.tomcat.util.buf.UriUtil but that class is not visible until
     * after the class loaders have been constructed.
     */
    private static URL buildClassLoaderUrl(String urlString) throws MalformedURLException {
        // URLs passed to class loaders may point to directories that contain
        // JARs. If these URLs are used to construct URLs for resources in a JAR
        // the URL will be used as is. It is therefore necessary to ensure that
        // the sequence "!/" is not present in a class loader URL.
        String result = urlString.replaceAll("!/", "%21/");
        return new URL(result);
    }


    private static URL buildClassLoaderUrl(File file) throws MalformedURLException {
        // Could be a directory or a file
        String fileUrlString = file.toURI().toString();
        fileUrlString = fileUrlString.replaceAll("!/", "%21/");
        return new URL(fileUrlString);
    }


    public enum RepositoryType {
        DIR,
        GLOB,
        JAR,
        URL
    }

    public static class Repository {
        private final String location;
        private final RepositoryType type;

        public Repository(String location, RepositoryType type) {
            this.location = location;
            this.type = type;
        }

        public String getLocation() {
            return location;
        }

        public RepositoryType getType() {
            return type;
        }
    }
}
