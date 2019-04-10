package org.jack.common.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

public class ClassScaner implements ResourceLoaderAware {

	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();   
	  
    private final List<TypeFilter> includeFilters = new LinkedList<TypeFilter>();   
  
    private final List<TypeFilter> excludeFilters = new LinkedList<TypeFilter>();
    private ClassLoader classLoader;
  
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(   
            this.resourcePatternResolver);   
  
    public ClassScaner() {   
  
    }   
  
    public void setResourceLoader(ResourceLoader resourceLoader) {   
        this.resourcePatternResolver = ResourcePatternUtils   
                .getResourcePatternResolver(resourceLoader);   
        this.metadataReaderFactory = new CachingMetadataReaderFactory(   
                resourceLoader);   
    }   
    public void setClassLoader(ClassLoader classLoader){
    	this.classLoader=classLoader;
    	setResourceLoader(new DefaultResourceLoader(classLoader));
    }
    public final ResourceLoader getResourceLoader() {   
        return this.resourcePatternResolver;   
    }   
  
    public void addIncludeFilter(TypeFilter includeFilter) {   
        this.includeFilters.add(includeFilter);   
    }   
  
    public void addExcludeFilter(TypeFilter excludeFilter) {   
        this.excludeFilters.add(0, excludeFilter);   
    }   
  
    public void resetFilters(boolean useDefaultFilters) {   
        this.includeFilters.clear();   
        this.excludeFilters.clear();   
    }   
  
    public static Set<Class<?>> scan(String basePackage,   
            @SuppressWarnings("unchecked") Class<? extends Annotation>...annotations) {   
        ClassScaner cs = new ClassScaner();   
        for (Class<? extends Annotation> anno : annotations)   
            cs.addIncludeFilter(new AnnotationTypeFilter(anno));   
        return cs.doScan(basePackage);   
    }   
  
    public static Set<Class<?>> scan(String[] basePackages,   
            @SuppressWarnings("unchecked") Class<? extends Annotation>... annotations) {   
        ClassScaner cs = new ClassScaner();   
        for (Class<? extends Annotation> anno : annotations)   
            cs.addIncludeFilter(new AnnotationTypeFilter(anno));   
        Set<Class<?>> classes = new HashSet<Class<?>>();   
        for (String s : basePackages)   
            classes.addAll(cs.doScan(s));   
        return classes;
    }   
  
    public Set<Class<?>> doScan(String basePackage) {   
        Set<Class<?>> classes = new HashSet<Class<?>>();   
        try {   
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX   
                    + org.springframework.util.ClassUtils   
                            .convertClassNameToResourcePath(SystemPropertyUtils   
                                    .resolvePlaceholders(basePackage))   
                    + "/**/*.class";   
            Resource[] resources = this.resourcePatternResolver   
                    .getResources(packageSearchPath);   
  
            for (int i = 0; i < resources.length; i++) {   
                Resource resource = resources[i];   
                if (resource.isReadable()) {   
                    MetadataReader metadataReader = this.metadataReaderFactory   
                            .getMetadataReader(resource);   
                    if ((includeFilters.size() == 0 && excludeFilters.size() == 0)   
                            || matches(metadataReader)) {   
                        try {   
                        	Class<?> clazz=ClassUtils.forName(metadataReader   
                                    .getClassMetadata().getClassName(), classLoader);
                            classes.add(clazz);   
                        } catch (ClassNotFoundException e) {   
                            e.printStackTrace();   
                        }   
  
                    }   
                }   
            }   
        } catch (IOException ex) {   
            throw new BeanDefinitionStoreException(   
                    "I/O failure during classpath scanning", ex);   
        }   
        return classes;   
    }   
  
    protected boolean matches(MetadataReader metadataReader) throws IOException {   
        for (TypeFilter tf : this.excludeFilters) {   
            if (tf.match(metadataReader, this.metadataReaderFactory)) {   
                return false;   
            }   
        }   
        for (TypeFilter tf : this.includeFilters) {   
            if (tf.match(metadataReader, this.metadataReaderFactory)) {   
                return true;   
            }   
        }   
        return false;   
    }   
	

}
