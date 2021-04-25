package code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LexPageRank {
	public Doc myDoc = new Doc();
    public int sumNum = 0;
    public int[][] linkOrNot;
    public int[] C;
    
    public void Summarize(String args[]) throws IOException
    {
    	if(args[3].equals("3")){
			System.out.println("The LexPageRank method can't solve topic-based multi-document summarization task.");
			return;
		}
    	
    	/* Read files */
    	if (args[3].equals("1"))
        {
    		String[] singleFile = new String[1];
            singleFile[0] = args[0];
            myDoc.maxlen = Integer.parseInt(args[4]);
            myDoc.readfile(singleFile, " ", args[2], args[6]);
        }
    	else if (args[3].equals("2"))
        {
    		File myfile = new File(args[0]);
            myDoc.maxlen = Integer.parseInt(args[4]);
            myDoc.readfile(myfile.list(),args[0],args[2], args[6]);
        }
    	
    	/* Calculate sentences' similarity matrix and construct link or not matrix depending on the similarity matrix */
    	myDoc.calcTfidf(Integer.parseInt(args[3]), Integer.parseInt(args[5]));
    	myDoc.calcSim();
    	C = new int[myDoc.snum];
    	linkOrNot = new int[myDoc.snum][myDoc.snum];
    	double linkThresh = 0.1;
    	if (Double.parseDouble(args[10])>=0){
    		linkThresh = Double.parseDouble(args[10]);
        }
    	for (int i = 0; i < myDoc.snum; ++i) {
    		C[i] = 0;
    		for(int j = 0; j < myDoc.snum; ++j) {
    			if(100 * myDoc.sim[i][j] >= linkThresh) {
    				C[i]++;
    				linkOrNot[i][j] = 1;
    			}else {
    				linkOrNot[i][j] = 0;
    			}
    		}
    	}
    	
    	/* Calculate the lexPageRank score of sentences */
		double[] uOld = new double[myDoc.snum];
		double[] u = new double[myDoc.snum];
		for(int i = 0; i < myDoc.snum; ++i) {
			uOld[i] = 1;
			u[i] = 1;
		}
		
		double eps = 0.00001, alpha = 0.85 , minus = 1.0;
		
		while (minus > eps) {
			uOld = u.clone();
			for (int i = 0; i < myDoc.snum; ++i) {
				double nowSum = 0.0;
			    for(int j = 0; j < myDoc.snum; ++j) {
			    	if(linkOrNot[i][j] == 1) {
			    		nowSum = nowSum + uOld[j] / (1.0 * C[j]);
			    	}
			    }
			    u[i] = (1 - alpha) + alpha * nowSum;
			}
			minus = 0.0;
			for (int i = 0; i < myDoc.snum; i++) {
				double add = Math.abs(u[i] - uOld[i]);
				minus += add;
			}
		}
    	
		/* Set redundancy removal method and parameter */
		double threshold = 0.7, Beta = 0.1;
		
		if (Double.parseDouble(args[8])>=0){
			threshold = Double.parseDouble(args[8]);
        }
		
		if (Double.parseDouble(args[9])>=0){
			Beta = Double.parseDouble(args[9]);
        }
	
		/* Remove redundancy and get the abstract */
		if (args[7].equals("-1"))
			myDoc.pickSentenceMMR(u, threshold, Beta);
        if (args[7].equals("1"))
            myDoc.pickSentenceMMR(u, threshold, Beta);
        else
        if (args[7].equals("2"))
            myDoc.pickSentenceThreshold(u, threshold, Beta);
        else
        if (args[7].equals("3"))
            myDoc.pickSentenceSumpun(u, threshold);
        
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
