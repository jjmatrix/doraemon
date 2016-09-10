package org.jmatrix.core.utils;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author jmatrix
 * @date 16/8/11
 */
public class ReflectUtilsTest {

    @Test
    public void testGetMethod() throws Exception {
        assertNull(ReflectUtils.getMethod(ReflectStub.class, "testMethod"));
        assertTrue(ReflectUtils.getMethod(ReflectStub.class, "testMethod1") instanceof Method);
    }
}