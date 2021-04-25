package code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Submodular {
    public Doc myDoc = new Doc();//some basic information about doc
    public ArrayList<Integer> summaryId = new ArrayList<>();//the index of the sentence picked in the summary
    public double Alpha, Beta, Lambda;//parameter
    int op;//op 1 represents use submodular function 1;else use submodular function 2
    public double[] sumSim;//the sum of similarity
    public void Summarize(String args[]) throws IOException
    {
    	if(args[3].equals("3")){
			System.out.println("The Submodular method can't solve topic-based multi-document summarization task.");
			return;
		}
    	
        summaryId = new ArrayList<>();
        myDoc = new Doc();
        if (args[3].equals("1"))//single document
        {
            Alpha = 1.0/myDoc.snum*10;
            Beta = 0.1;
            Lambda = 0.5;
            String[] singleFile = new String[1];
            singleFile[0] = args[0];
            op = Integer.parseInt(args[7]);
            if (op == 1){
                Alpha = 1;
            }
            // get the parameter
            if (Double.parseDouble(args[9]) >= 0){
                Alpha = Double.parseDouble(args[9]);
            }
            if (Double.parseDouble(args[8]) >= 0){
                Beta = Double.parseDouble(args[8]);
            }
            if (Double.parseDouble(args[10]) >= 0){
                Lambda = Double.parseDouble(args[10]);
            }
            //allow the length of summary to exceed 20
            myDoc.maxlen = Integer.parseInt(args[4])+20;
            myDoc.readfile(singleFile," ",args[2],args[6]);
			int sx = Integer.parseInt(args[5]);
            myDoc.calcTfidf(1, sx);
            myDoc.calcSim();
            //pick sentence with greed algorithm
            greedy();
            
            /* Output the abstract */
        	try{
        		File outfile = new File(args[1]);
        		OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(outfile),"utf-8");
        		BufferedWriter writer = new BufferedWriter(write);
        		for (int i : myDoc.summaryId){
                    //System.out.println(myDoc.originalSen.get(i));
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
        /* Multi document */
        else if (args[3].equals("2")) {
            File myfile = new File(args[0]);
            myDoc.maxlen = Integer.parseInt(args[4])+20;
            myDoc.readfile(myfile.list(),args[0],args[2],args[6]);
            int sx = Integer.parseInt(args[5]);
            myDoc.calcTfidf(1, sx);
            myDoc.calcSim();
            op = Integer.parseInt(args[7]);
            Alpha = 1.0 / myDoc.snum*10;
            Beta = 0.1;
            Lambda = 0.15;
            if (op == 1){
                Alpha = 1;
            }
            if (Double.parseDouble(args[9]) >= 0){
                Alpha = Double.parseDouble(args[9]);
            }
            if (Double.parseDouble(args[8]) >= 0){
                Beta = Double.parseDouble(args[8]);
            }
            if (Double.parseDouble(args[10]) >= 0){
                Lambda = Double.parseDouble(args[10]);
            }
            greedy();
           
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

    // submodular function 1
    public double submod1(int id)
    {
        double score = 0;
        for (int i = 0; i < myDoc.snum; i++){
            if (i == id) continue;
            double sum = 0;
            for (int j : myDoc.summaryId)
            if (i != j)
                sum += myDoc.sim[i][j];
            if (id != -1)
                sum += myDoc.sim[i][id];

            if (sum > sumSim[i] * Alpha)
                sum = sumSim[i] * Alpha;
            score += sum;
        }
        return score;
    }

    // submodular function 2
    public double submod2(int id){
        double score=0;
        for (int i : myDoc.summaryId) {
            if (op == 1) {
                score += myDoc.sim[id][i];
            } else {
                if (myDoc.sim[id][i] > score)
                    score = myDoc.sim[id][i];
            }
        }
        return -score;
    }

    public void calcSumSim(){
        sumSim = new double[myDoc.snum];
        for (int i = 0; i < myDoc.snum; i++){
            sumSim[i] = 0;
            for (int j = 0; j < myDoc.snum; j++)
            if (i!=j)
                sumSim[i] += myDoc.sim[i][j];
        }
    }

    /* pick sentence using greedy algorithm */
    public void greedy(){
        boolean[] chosen = new boolean[myDoc.snum];
        int len=0;
        calcSumSim();
        while (true){
            double maxInc = -10, initScore = submod1(-1);
            int maxId = -1;
            for (int i = 0; i < myDoc.snum; i++){
                if (!chosen[i] && len+myDoc.senLen.get(i)<myDoc.maxlen){
                    double inc = (Lambda * submod1(i) +(1- Lambda) * submod2(i) - initScore* Lambda)/Math.pow(myDoc.senLen.get(i), Beta);
                    if (inc > maxInc){
                        maxInc = inc;
                        maxId = i;
                    }
                }
            }

            if (maxId == -1) break;
            chosen[maxId] = true;
            len += myDoc.senLen.get(maxId);
            myDoc.summaryId.add(maxId);
            if (len >= myDoc.maxlen-20)
                break;
        }
    }
}
