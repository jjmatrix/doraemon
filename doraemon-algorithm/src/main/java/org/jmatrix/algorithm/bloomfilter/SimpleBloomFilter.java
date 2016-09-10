package org.jmatrix.algorithm.bloomfilter;

import java.io.*;
import java.util.*;

/**
 * @author jmatrix
 * @date 16/3/3
 */
public class SimpleBloomFilter {
    private static final int DEFAULT_SIZE = 2 << 36;
    private static final int[] seeds = new int[]{3, 5, 7, 11, 13, 31, 37, 61};

    private BitSet bits = new BitSet(DEFAULT_SIZE);
    private SimpleHash[] func = new SimpleHash[seeds.length];

    public static void main(String[] args) throws IOException {
        // createFile();

        // testSimpleBloomFilter();
        // testSet();
        List<String> moblieList = new ArrayList<String>();
        SimpleBloomFilter filter = new SimpleBloomFilter();
        initBloomFilter(filter, moblieList);
        //
        // // testExists(filter);
        for (int i = 0; i < 10000000; i++) {
            String tel = getTel();
            if (filter.contains(tel)) {
                if (!moblieList.contains(tel)) {
                    System.out.println(tel);
                }
            }
        }
    }

    /**
     * @throws IOException
     */
//    private static void createFile() throws IOException {
//        File file = new File("D:\\Users\\guangyuan.cao\\Desktop\\mobile.txt");
//        List<String> mobiles = new ArrayList<String>();
//        for (int i = 0; i < 10000000; i++) {
//            mobiles.add(getTel());
//        }
//        FileUtils.writeLines(file, mobiles);
//    }

    /**
     * @param filter
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void testExists(SimpleBloomFilter filter) throws FileNotFoundException, IOException {
        File file = new File("D:\\Users\\guangyuan.cao\\Desktop\\mobile.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            if (!filter.contains(line)) {
                System.out.println(line);
            }
        }
    }

    /**
     * @param filter
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void initBloomFilter(SimpleBloomFilter filter, List<String> moblieList)
            throws FileNotFoundException, IOException {
        File file = new File("/Users/coral/project/doc/mobile.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;

        while ((line = bufferedReader.readLine()) != null) {
            filter.add(line);
            moblieList.add(line);
        }
    }

    public static int getNum(int start, int end) {
        return (int) (Math.random() * (end - start + 1) + start);
    }

    private static String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153"
            .split(",");

    private static String getTel() {
        int index = getNum(0, telFirst.length - 1);
        String first = telFirst[index];
        String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
        String thrid = String.valueOf(getNum(1, 9100) + 10000).substring(1);
        return first + second + thrid;
    }

    private static void testSimpleBloomFilter() {
        SimpleBloomFilter filter = new SimpleBloomFilter();
        for (int i = 0; i < 20000000; i++) {
            filter.add(String.valueOf(i));
        }
        System.out.println(filter.contains("9999999"));
        System.out.println(filter.contains("100000001"));
    }

    private static void testSet() {
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < 10000000; i++) {
            set.add(String.valueOf(i));
        }
        System.out.println(set.contains("9999999"));
        System.out.println(set.contains("100000001"));
    }

    public SimpleBloomFilter() {
        for (int i = 0; i < seeds.length; i++) {
            func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
        }
    }

    public void add(String value) {
        for (SimpleHash f : func) {
            bits.set(f.hash(value), true);
        }
    }

    public boolean contains(String value) {
        if (value == null) {
            return false;
        }
        boolean ret = true;
        for (SimpleHash f : func) {
            ret = ret && bits.get(f.hash(value));
        }
        return ret;
    }

    public static class SimpleHash {

        private int cap;
        private int seed;

        public SimpleHash(int cap, int seed) {
            this.cap = cap;
            this.seed = seed;
        }

        public int hash(String value) {
            int result = 0;
            int len = value.length();
            for (int i = 0; i < len; i++) {
                result = seed * result + value.charAt(i);
            }
//            if (result > cap) {
//                System.out.println("result:" + value);
//            }
            return (cap - 1) & result;
        }

    }
}
