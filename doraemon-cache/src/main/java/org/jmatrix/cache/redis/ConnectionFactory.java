package org.jmatrix.cache.redis;

/**
 * @author jmatrix
 * @date 16/11/24
 */
public interface ConnectionFactory {
    Connection getConnection();

    void destroy() throws Exception;

    void initialize() throws Exception;
}
