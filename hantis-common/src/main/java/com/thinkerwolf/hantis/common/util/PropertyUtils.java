package com.thinkerwolf.hantis.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyUtils {

    public static void setProperties(Object target, Properties props) {
        if (props.size() <= 0) {
            return;
        }
        ClassMetaData metaData = ClassUtils.getClassMetaData(target.getClass());
        for (Entry<Object, Object> entry : props.entrySet()) {
            String propertyName = String.valueOf(entry.getKey());
            String setterName = ReflectionUtils.getPropertySetterName(propertyName);
            Method method = metaData.getMethod(setterName);
            Object value = entry.getValue();
            if (method != null && method.getParameterCount() == 1) {
                Class<?> pt = method.getParameterTypes()[0];
                if (pt == value.getClass()) {
                    ReflectionUtils.callMethod(target, method, value);
                } else {
                    ReflectionUtils.callMethod(target, method, convertStringValue(value.toString(), pt));
                }
            }
        }
    }

    public static Object convertStringValue(String s, Class<?> clazz) {
        if (clazz == Boolean.class || clazz == boolean.class) {
            return Boolean.valueOf(s);
        }
        if (clazz == Byte.class || clazz == byte.class) {
            return Byte.valueOf(s);
        }
        if (clazz == Short.class || clazz == short.class) {
            return Short.valueOf(s);
        }
        if (clazz == Integer.class || clazz == int.class) {
            return Integer.valueOf(s);
        }
        if (clazz == Long.class || clazz == long.class) {
            return Long.valueOf(s);
        }
        if (clazz == Float.class || clazz == float.class) {
            return Float.valueOf(s);
        }
        if (clazz == Double.class || clazz == double.class) {
            return Double.valueOf(s);
        }
        return s;
    }

    public static void setProperty(Object target, String propertyName, Object propertyValue) {
        final Class<?> clazz = target.getClass();
        try {
            Field f = clazz.getDeclaredField(propertyName);
            if (f != null) {
                f.setAccessible(true);
                f.set(target, propertyValue);
            }
        } catch (Exception e) {
            Method setterMethod = ReflectionUtils.getSetterMethod(clazz, propertyName, propertyValue.getClass());
            try {
                ReflectionUtils.callMethod(target, setterMethod, propertyValue);
            } catch (Exception e1) {

            }
        }
    }

    private static final Pattern PLACE_HOLDER = Pattern.compile("\\$\\s*\\{.*\\}");
    private static final Pattern PROP_NAME = Pattern.compile("[^\\$\\s\\{\\}]+");

    public static Object getPropertyValue(Properties variables, String originValue) {
        if (PLACE_HOLDER.matcher(originValue).find()) {
            Matcher m = PROP_NAME.matcher(originValue);
            m.find();
            String propName = m.group().trim();
            if (!variables.containsKey(propName)) {
                throw new RuntimeException("Not find such property [" + propName + "], please check your config file!");
            }
            originValue = variables.getProperty(propName);
        }
        return originValue;
    }
}
