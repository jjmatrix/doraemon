package org.jmatrix.algorithm.bloomfilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * @author jmatrix
 * @date 16/2/23
 */
public class BloomFilter<T> {

    private Logger logger = LoggerFactory.getLogger(BloomFilter.class);

    private BitSet bitSet = null;

    private List<Function<T, Integer>> hashFunctions = new ArrayList<Function<T, Integer>>();

    public BloomFilter() {
        bitSet = new BitSet();
    }

    public BloomFilter(int nbits) {
        bitSet = new BitSet(nbits);
    }

    public void addHashFunction(Function hashFunction) {
        hashFunctions.add(hashFunction);
    }

    public void add(T element) {
        Iterator<Function<T, Integer>> iter = hashFunctions.iterator();
        while (iter.hasNext()) {
            this.bitSet.set((iter.next()).apply(element));
        }
    }

    public boolean exist(T element) {
        Iterator<Function<T, Integer>> iter = hashFunctions.iterator();
        boolean ret = true;
        while (iter.hasNext()) {
            if (!this.bitSet.get((iter.next()).apply(element))) {
                ret = false;
                break;
            }
        }
        return ret;
    }

}
