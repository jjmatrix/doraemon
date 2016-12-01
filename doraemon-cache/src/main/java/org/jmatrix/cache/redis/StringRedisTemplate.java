package org.jmatrix.cache.redis;

import java.nio.charset.Charset;

/**
 * @author jmatrix
 * @date 16/11/24
 */
public class StringRedisTemplate extends RedisTemplate<String> {

    public StringRedisTemplate(ConnectionFactory connectionFactory) {
        super(connectionFactory);

        hashOperations = new HashOperations(this);
        StringSerializer stringSerializer = new StringSerializer(Charset.forName("UTF-8"));
        hashOperations.setKeySerializer(stringSerializer);
        hashOperations.setValueSerializer(stringSerializer);

        valueOperations = new ValueOperations(this);
        valueOperations.setKeySerializer(stringSerializer);
        valueOperations.setValueSerializer(stringSerializer);
    }

    class StringSerializer implements Serializer<String> {

        private Charset charset;

        public StringSerializer(Charset charset) {
            this.charset = charset;
        }

        @Override
        public byte[] serialize(String key) {
            return key.getBytes(charset);
        }

        @Override
        public String unSerialize(byte[] key) {
            return new String(key, charset);
        }
    }

}
