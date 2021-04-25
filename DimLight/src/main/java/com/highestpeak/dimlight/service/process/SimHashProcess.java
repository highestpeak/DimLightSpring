package com.highestpeak.dimlight.service.process;

import com.google.common.collect.Maps;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import org.apdplat.word.analysis.SimHashPlusHammingDistanceTextSimilarity;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 计算词项列表的simhash值
 * {@link SimHashPlusHammingDistanceTextSimilarity simhash}
 * https://github.com/sing1ee/simhash-java
 * simhash算法https://www.cnblogs.com/sddai/p/10088007.html
 */
public class SimHashProcess implements InfoProcess{
    private int hashBitCount = 128;

    @Override
    public void process(ProcessContext processContext) {
        Map<Integer, List<WordSegmentProcess.WordInfo>> xmlWithItWordFreq = processContext.getTopWordFreq();
        if (processContext.getSimhashMap()==null) {
            processContext.setSimhashMap(Maps.newHashMapWithExpectedSize(xmlWithItWordFreq.size()));
        }
        Map<Integer, String> simhashMap = processContext.getSimhashMap();
        for (Map.Entry<Integer, List<WordSegmentProcess.WordInfo>> wordInfoEntry : xmlWithItWordFreq.entrySet()) {
            Integer xmlId = wordInfoEntry.getKey();
            List<WordSegmentProcess.WordInfo> wordInfoList = wordInfoEntry.getValue();
            String simHash = simHash(wordInfoList);
            simhashMap.put(xmlId, simHash);
        }
    }

    private String simHash(List<WordSegmentProcess.WordInfo> words) {
        float[] hashBit = new float[hashBitCount];
        words.forEach(word -> {
            float weight = word.getWeight();
            BigInteger hash = hash(word.getWord());
            for (int i = 0; i < hashBitCount; i++) {
                BigInteger bitMask = new BigInteger("1").shiftLeft(i);
                if (hash.and(bitMask).signum() != 0) {
                    hashBit[i] += weight;
                } else {
                    hashBit[i] -= weight;
                }
            }
        });
        StringBuffer fingerprint = new StringBuffer();
        for (int i = 0; i < hashBitCount; i++) {
            if (hashBit[i] >= 0) {
                fingerprint.append("1");
            }else{
                fingerprint.append("0");
            }
        }
        return fingerprint.toString();
    }

    private BigInteger hash(String word) {
        if (word == null || word.length() == 0) {
            return new BigInteger("0");
        }
        char[] charArray = word.toCharArray();
        BigInteger x = BigInteger.valueOf(((long) charArray[0]) << 7);
        BigInteger m = new BigInteger("1000003");
        BigInteger mask = new BigInteger("2").pow(hashBitCount).subtract(new BigInteger("1"));
        long sum = 0;
        for (char c : charArray) {
            sum += c;
        }
        x = x.multiply(m).xor(BigInteger.valueOf(sum)).and(mask);
        x = x.xor(new BigInteger(String.valueOf(word.length())));
        if (x.equals(new BigInteger("-1"))) {
            x = new BigInteger("-2");
        }
        return x;
    }
}
