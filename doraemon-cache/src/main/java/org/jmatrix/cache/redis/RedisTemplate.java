package org.jmatrix.cache.redis;

/**
 * @author jmatrix
 * @date 16/11/24
 */
public class RedisTemplate<T> {

    private ConnectionFactory connectionFactory;

    protected HashOperations hashOperations;

    protected ValueOperations valueOperations;

    public HashOperations hashOperations() {
        return hashOperations;
    }

    public ValueOperations valueOperations() {
        return valueOperations;
    }

    public RedisTemplate(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public <T> T execute(Callback<T> callback) {
        Connection connection = connectionFactory.getConnection();
        try {
            return callback.doInRedis(connection);
        } finally {
            connection.close();
        }
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setHashOperations(HashOperations hashOperations) {
        this.hashOperations = hashOperations;
    }

    public void setValueOperations(ValueOperations valueOperations) {
        this.valueOperations = valueOperations;
    }
}
