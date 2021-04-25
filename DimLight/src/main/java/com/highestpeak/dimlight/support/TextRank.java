package com.highestpeak.dimlight.support;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Iterator;

@SuppressWarnings("DuplicatedCode")
public class TextRank {
    public MyDoc myDoc = new MyDoc();
    public double[][] similarity;

    public static final String RePara = "0.7";
    public static final String BetaStr = "0.1";

    public String summarize(TextRankParams textRankParams) throws IOException {
        myDoc.maxlen = textRankParams.maxLen;
        myDoc.readContent(Lists.newArrayList(textRankParams.inputContent), "1");

        /* Calculate similarity matrix of sentences */
        // type=1 stemmerOrNot=1
        myDoc.calcTfidf(1, 1);
        myDoc.calcSim();
        similarity = new double[myDoc.snum][myDoc.snum];
        for(int i = 0; i < myDoc.snum; ++i) {
            double sumISim= 0.0;
            for(int j = 0; j < myDoc.snum; ++j) {
                if(i == j) similarity[i][j] = 0.0;
                else {
                    int tmpNum = 0;
                    for(Iterator<Integer> iter = myDoc.sVector.get(i).iterator(); iter.hasNext(); ) {
                        int now = iter.next();
                        if(myDoc.sVector.get(j).contains(now)){
                            tmpNum++;
                        }
                    }
                    similarity[i][j] = tmpNum / ( Math.log(1.0 * myDoc.senLen.get(i)) + Math.log(1.0 * myDoc.senLen.get(j)));
                }
                sumISim += similarity[i][j];
            }

            /* Normalization the similarity matrix by row */
            for(int j = 0; j < myDoc.snum; ++j) {
                if(sumISim == 0.0) {
                    similarity[i][j] = 0.0;
                }else {
                    similarity[i][j] = similarity[i][j] / sumISim;
                }
            }
        }

        //Calculate the TextRank score of sentences
        double[] uOld = new double[myDoc.snum];
        double[] u = new double[myDoc.snum];
        for(int i = 0; i < myDoc.snum; ++i) {
            uOld[i] = 1.0;
            u[i] = 1.0;
        }

        double eps = 0.00001, alpha = 0.85 , minus = 1.0;

        while (minus > eps) {
            uOld = u.clone();
            for (int i = 0; i < myDoc.snum; i++) {
                double sumSim = 0.0;
                for (int j = 0; j < myDoc.snum; j++) {
                    if(j == i) continue;
                    else {
                        sumSim = sumSim + similarity[j][i] * uOld[j];
                    }

                }
                u[i] = alpha * sumSim + (1 - alpha);
            }
            minus = 0.0;
            for (int j = 0; j < myDoc.snum; j++) {
                double add = java.lang.Math.abs(u[j] - uOld[j]);
                minus += add;
            }
        }

        /* Set redundancy removal method and parameter */
        double threshold = 0.9, Beta = 0.1;

        if (Double.parseDouble(RePara)>=0){
            threshold = Double.parseDouble(RePara);
        }
        if (Double.parseDouble(BetaStr)>=0){
            Beta = Double.parseDouble(BetaStr);
        }

        /* Remove redundancy and get the abstract */
        myDoc.pickSentenceMMR(u, threshold, Beta);

        /* Output the abstract */
        try{
            StringWriter writer = new StringWriter();
            for (int i : myDoc.summaryId){
                //System.out.println(myDoc.originalSen.get(i));
                writer.write(myDoc.originalSen.get(i));
                writer.write("\n");
            }
            writer.close();
            return writer.toString();
        }
        catch(Exception e){
            System.out.println("There are errors in the output.");
            e.printStackTrace();
        }

        return StringUtils.EMPTY;
    }

    @Getter
    @Builder
    public static class TextRankParams {
        private String inputContent;
        private int maxLen;
    }
}
