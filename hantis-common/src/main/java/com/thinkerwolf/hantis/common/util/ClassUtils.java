package com.thinkerwolf.hantis.common.util;

import com.thinkerwolf.hantis.common.io.Resources;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ClassUtils {

    public static final Map<Class<?>, ClassMetaData> classMetaDataCache = new ConcurrentHashMap<>();

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
        }
        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                }
            }
        }
        return cl;
    }

    public static Class<?> forName(String name) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static <T> T newInstance(Class<T> clazz, Object... args) {
        if (args.length == 0) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
            try {
                return clazz.getConstructor(parameterTypes).newInstance(args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T newInstance(String name, Object... args) {
    	return newInstance((Class<T>) forName(name), args);
    }

    /**
     * Scan Class
     *
     * @param basePackage package name，可以包含通配符
     * @return
     */
    public static Set<Class<?>> scanClasses(String basePackage) {
        basePackage = basePackage.replaceAll("\\.", "/").replace(File.separatorChar, '/');
        String rootDir = Resources.getRootDir(basePackage);
        Pattern p = Pattern.compile(basePackage.replaceAll("\\*", ".*"));
        Set<String> set = ResourceUtils.findClasspathFilePaths(rootDir, "class");
        Set<Class<?>> result = new HashSet<>();
        for (Iterator<String> iter = set.iterator(); iter.hasNext(); ) {
            String s = iter.next();
            if (p.matcher(s).matches()) {
                String classname = s.replaceAll("/", ".").replaceAll(".class", "");
                result.add(forName(classname));
            }
        }
        return result;
    }

    /**
     *
     * @param clazz
     * @return
     */
    public static ClassMetaData getClassMetaData(Class<?> clazz) {
        ClassMetaData metaData = classMetaDataCache.get(clazz);
        if (metaData == null) {
            metaData = new ClassMetaData(clazz);
            classMetaDataCache.put(clazz, metaData);
        }
        return metaData;
    }


}
