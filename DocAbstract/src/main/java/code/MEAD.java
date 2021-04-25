package code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.TreeSet;

public class MEAD {
	public Doc myDoc = new Doc();
	public double[] TfIdf;
    public double[] C;
    public double[] P;
    public double[] F;
    public double[] Score;
    public int sumNum = 0;
    public void Summarize(String args[]) throws IOException
    {
    	
    	
    	/* Read files */
    	if (args[3].equals("1"))
        {
    		String[] singleFile = new String[1];
            singleFile[0] = args[0];
            myDoc.maxlen = Integer.parseInt(args[4]);
            myDoc.readfile(singleFile, " ", args[2],args[6]);
        }
    	else if (args[3].equals("2"))
        {
    		File myfile = new File(args[0]);
            myDoc.maxlen = Integer.parseInt(args[4]);
            myDoc.readfile(myfile.list(),args[0],args[2], args[6]);
            
        }else if (args[3].equals("3")){
        	/* Read topic */
        	if (!args[10].equals("-1")) {
        		myDoc.readTopic(args[10], args[2], args[6]);
        	}
        	File myfile = new File(args[0]);
            myDoc.maxlen = Integer.parseInt(args[4]);
            myDoc.readfile(myfile.list(),args[0],args[2], args[6]);
        }
    	
    	/* Calculate tf*idf */
    	myDoc.calcTfidf(Integer.parseInt(args[3]), Integer.parseInt(args[5]));
    	myDoc.calcSim();
    	int wordNum = myDoc.dTf.size();
    	
    	TfIdf = new double[wordNum];
    	double CMax = 0.0;
    	for(int i = 0; i < wordNum; ++i) {
    		TfIdf[i] = myDoc.dTf.get(i) * myDoc.idf[i];
    		CMax += TfIdf[i];
    	}
    	
    	/* Calculate CMax for each document */
    	ArrayList<TreeSet<Integer>> fVector = new ArrayList<TreeSet<Integer>>();
    	double[] fCMax = new double[myDoc.fnum];
    	int fNumNow = 0;
    	P = new double[myDoc.snum];
    	TreeSet<Integer> tmpSet = new TreeSet<Integer>();
    	for(int i = 0; i < myDoc.snum; ++i) {
    		if(i >= myDoc.rRange[fNumNow]) {
    			fNumNow++;
    			fVector.add(tmpSet);
    			tmpSet.clear();
    		}
    		for(int j : myDoc.sVector.get(i)) {
    			tmpSet.add(j);
    		}
    	}
    	fVector.add(tmpSet);
    	
    	for(int i = 0; i < myDoc.fnum; ++i) {
    		for(int j : fVector.get(i)) {
    			fCMax[i] += TfIdf[j];
    		}
    	}
    	
    	/* Calculate C score of sentences */
    	C = new double[myDoc.snum];
    	for(int i = 0; i < myDoc.snum; ++i) {
    		C[i] = 0.0;
    		for(int j = 0; j < wordNum; ++j) {
    			if(myDoc.sVector.get(i).contains(j)) {
    				C[i] += TfIdf[j];
    			}
    		}
    	}
    	
    	/* Calculate P score of sentences */
    	fNumNow = 0;
    	P = new double[myDoc.snum];
    	for(int i = 0; i < myDoc.snum; ++i) {
    		if(args[3].equals("1")) {
    			P[i] = (myDoc.snum - i) * 1.0 * CMax / (myDoc.snum * 1.0);
    		}
    		else if(args[3].equals("2") || args[3].equals("3")) {
    			if(i >= myDoc.rRange[fNumNow]) {
    				fNumNow++;
    			}
    			int fSnum = myDoc.rRange[fNumNow] - myDoc.lRange[fNumNow];
				P[i] = (fSnum - (i - myDoc.lRange[fNumNow])) * 1.0 * fCMax[fNumNow] / (fSnum * 1.0);
    		}
    	}
    	
    	/* Calculate F score of sentences */
    	F = new double[myDoc.snum];
    	for(int i = 0; i < myDoc.snum; ++i) {
    		F[i] = 0.0;
    		int k = 0;
    		for(int j : myDoc.sVector.get(i)) {
    			F[i] += myDoc.sTf.get(i).get(k) * TfIdf[j];
    			k++;
    		}
    	}
    	
    	
    	/* Calculate MEAD Score of sentences */
    	Score = new double[myDoc.snum];
    	for(int i = 0; i < myDoc.snum; ++i) {
    		/* No topic-focused */
    		if(args[10].equals("-1")) {
    			Score[i] = C[i] + P[i] + F[i];
    		}
    		/* Topic-focused document summarization */
    		else {
    			if(i == 0) 
    				Score[i] = 0.0;
    			else 
    				Score[i] = C[i] + P[i] + F[i] + myDoc.sim[i][0];
    		}
    	}
    	
    	/* Set redundancy removal method and parameter */    	
    	double threshold = 0.9, Beta = 0.1;
    			
    	if (Double.parseDouble(args[8])>=0){
    		threshold = Double.parseDouble(args[8]);
    	}
    	if (Double.parseDouble(args[9])>=0){
    		Beta = Double.parseDouble(args[9]);
    	}
    	
    	/* Remove redundancy and get the abstract */
    	if (args[7].equals("-1"))
			myDoc.pickSentenceMMR(Score, threshold, Beta);
    	else if (args[7].equals("1"))
            myDoc.pickSentenceMMR(Score, threshold, Beta);
        else if (args[7].equals("2"))
            myDoc.pickSentenceThreshold(Score, threshold, Beta);
        else if (args[7].equals("3"))
            myDoc.pickSentenceSumpun(Score, threshold);
    	
    	/* Output the abstract */
    	try{
    		File outfile = new File(args[1]);
    		OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(outfile),"utf-8");
    		BufferedWriter writer = new BufferedWriter(write);
    		for (int i : myDoc.summaryId){
    			writer.write(myDoc.originalSen.get(i));
    			writer.write("\n");
            }
    		writer.close();
    	}
    	catch(Exception e){
    		System.out.println("There are errors in the output.");
    		e.printStackTrace();
    	}
    }
}
