package com.highestpeak.dimlight;

import code.TextRank;

import java.io.IOException;

public class LibTest {
    public static void main(String[] args) throws IOException {
        String[] arg = new String[13];
        //String type = "1";
        //String inputPath = "/testdoc.html";

        String type = "-1", topic = "-1", inputPath = "-1", outputFile = "-1";
        String language = "-1", abNum = "-1", method = "-1", stopwordPath = "-1";
        String stemmerOrNot = "1", ReMethod = "1", RePara = "0.7", beta = "0.1"/*, alpha = "0.85", eps = "0.00001"*/;
        String linkThresh = "0.1", AlphaC = "0.1", LambdaC = "0.8", op = "2", AlphaS = "0.5", LambdaS = "-1";

        arg[0] = inputPath;
        arg[1] = outputFile;
        arg[2] = language;
        arg[3] = type;
        arg[4] = abNum;
        arg[5] = stemmerOrNot;
        arg[6] = stopwordPath;

        TextRank textrank = new TextRank();
        arg[7] = ReMethod;
        arg[8] = RePara;
        arg[9] = beta;
        textrank.Summarize(arg);
    }
}
