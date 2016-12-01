package org.jmatrix.cache.util;

import java.util.Map;

/**
 * @author jmatrix
 * @date 16/11/24
 */
public class RedisSupport {

    private static String RET_STR_OK = "OK";

    private static Long RET_L_OK = 1L;

    public static Boolean toBoolean(Long redisRet) {
        if (redisRet == null)
            return false;
        return RET_L_OK.equals(redisRet) ? true : false;
    }

    public static Boolean toBoolean(String redisRet) {
        if (redisRet == null)
            return false;
        return RET_STR_OK.equals(redisRet) ? true : false;
    }

    public static byte[][] convertToKeyValues(Map<byte[], byte[]> keyValueMap) {
        byte[][] result = new byte[keyValueMap.size() * 2][];
        int idx = 0;
        for (Map.Entry<byte[], byte[]> keyValue : keyValueMap.entrySet()) {
            result[idx++] = keyValue.getKey();
            result[idx++] = keyValue.getValue();
        }
        return result;
    }
}
