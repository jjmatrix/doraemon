package org.jmatrix.core.utils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jmatrix
 * @date 16/8/11
 */
public class ReflectUtils {

    private static Map<String, Method> methodCaches = new ConcurrentHashMap<>(16, 0.75f, 8);

    public static Method getMethod(Class<?> cls, String name) {
        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                methodCaches.put(name, method);
                return method;
            }
        }
        return null;
    }

    public static void clearCaches() {
        methodCaches.clear();
    }
}
