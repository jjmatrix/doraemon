package org.jmatrix.cache;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author jmatrix
 * @date 16/11/22
 */
public class GuavaCacheAllTest {

    private List<String> loadKeys = new ArrayList<>();

    private LoadingCache<String, String> loadingCache = CacheFactory.Instance().createGuavaCache(new CacheLoader<String, String>() {
        @Override
        public Map<String, String> loadAll(Iterable<? extends String> keys) throws Exception {
            loadKeys.clear();

            Map<String, String> result = new HashMap<>();
            keys.forEach(key -> {
                result.put(key, key);
                loadKeys.add(key);
            });

            return result;
        }

        @Override
        public String load(String key) throws Exception {
            return key;
        }
    });

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetAll() throws Exception {
        loadingCache.get("key1");
        List<String> allKeys = Arrays.asList(new String[]{"key1", "key2", "key3"});
        loadingCache.getAll(allKeys);
        Assert.assertTrue(loadKeys.size() == 2);
        loadKeys.forEach(key -> {
            Assert.assertTrue(key.equals("key2") || key.equals("key3"));
        });
    }

}
