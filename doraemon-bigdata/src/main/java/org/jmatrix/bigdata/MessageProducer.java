package org.jmatrix.bigdata;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author jmatrix
 * @date 16/9/8
 */
public class MessageProducer {

    private Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    /**
     * produce message to kafka
     *
     * @param mSize message ratio
     */
    public void producerMessage(long mSize) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer kafkaProducer = new KafkaProducer(properties);

        try (FileMessageList fileMessageList = new FileMessageList(this.getClass().getResourceAsStream("/messagelist.txt"))) {
            Iterator<KMessage<String, String>> iter = fileMessageList.iterator();
            while (iter.hasNext()) {
                KMessage<String, String> kMessage = iter.next();
                ProducerRecord<String, String> record = new ProducerRecord<>(kMessage.getTopic(), kMessage.getKey(), kMessage.getValue());
                kafkaProducer.send(record, (recordMetadata, ex) -> {
                    if (ex != null) {
                        logger.error("send message to kafka failed.", ex);
                    }
                });
            }
        }
    }

    class FileMessageList extends MessageList<KMessage<String, String>> implements AutoCloseable {

        private BufferedReader bufferedReader;

        private volatile boolean initSuc = false;

        public FileMessageList(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
            initSuc = true;
        }

        public FileMessageList(InputStream inputStream) {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                initSuc = true;
            } catch (Exception e) {
                logger.error("create bufferedReader failed.", e);
            }
        }

        @Override
        protected KMessage<String, String> readNextOne() {
            if (!initSuc) {
                throw new IllegalStateException("object do not init success.");
            }
            try {
                String line = bufferedReader.readLine();
                if (line != null) {
                    KMessage<String, String> message = new KMessage<>();
                    String[] lineArr = line.split("\\s+");
                    if (lineArr.length == 3) {
                        message.setTopic(lineArr[0]);
                        message.setKey(lineArr[1]);
                        message.setValue(lineArr[2]);
                        return message;
                    }
                }
            } catch (IOException ex) {
                logger.error("read file error", ex);
            }
            return null;
        }

        @Override
        public void close() {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                logger.error("close bufferReader failed.", ex);
            }
        }

    }

    abstract class MessageList<E> {

        public Iterator<E> iterator() {
            return new FileMessageIter();
        }

        abstract protected E readNextOne();

        class FileMessageIter implements Iterator<E> {

            private E nextOne = null;

            @Override
            public boolean hasNext() {
                if (nextOne != null) {
                    return true;
                } else {
                    nextOne = readNextOne();
                    return nextOne != null;
                }
            }

            @Override
            public E next() {
                if (nextOne != null || hasNext()) {
                    E nextOneTemp = nextOne;
                    nextOne = null;
                    return nextOneTemp;
                } else {
                    throw new IllegalStateException("next message don't existed.");
                }
            }
        }
    }

    interface Iterator<M> {
        boolean hasNext();

        M next();
    }

    public static class KMessage<K, V> {
        private String topic;

        private K key;

        private V value;

        public KMessage() {
        }

        public KMessage(String topic, K key, V value) {
            this.topic = topic;
            this.key = key;
            this.value = value;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
