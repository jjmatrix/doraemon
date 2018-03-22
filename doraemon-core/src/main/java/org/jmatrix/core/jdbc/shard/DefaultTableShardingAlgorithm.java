package org.jmatrix.core.jdbc.shard;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.SingleKeyTableShardingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by matrix on 2017/9/20.
 */
public class DefaultTableShardingAlgorithm implements SingleKeyTableShardingAlgorithm<String> {

    private final Logger logger = LoggerFactory.getLogger(DefaultTableShardingAlgorithm.class);

    @Override
    public String doEqualSharding(Collection<String> availableTargetNames, ShardingValue<String> shardingValue) {
        logger.info("DefaultDatabaseAlgorithm doEqualSharding, availableTargetNames:{}, shardingValue:{}", availableTargetNames, shardingValue);
        return availableTargetNames.isEmpty() ? null : availableTargetNames.iterator().next();
    }

    @Override
    public Collection<String> doInSharding(Collection<String> availableTargetNames, ShardingValue<String> shardingValue) {
        logger.info("DefaultDatabaseAlgorithm doInSharding, availableTargetNames:{}, shardingValue:{}", availableTargetNames, shardingValue);
        return null;
    }

    @Override
    public Collection<String> doBetweenSharding(Collection<String> availableTargetNames, ShardingValue<String> shardingValue) {
        logger.info("DefaultDatabaseAlgorithm doBetweenSharding, availableTargetNames:{}, shardingValue:{}", availableTargetNames, shardingValue);
        return null;
    }
}
