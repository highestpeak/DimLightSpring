package code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Lead {
	public Doc myDoc = new Doc();
    public ArrayList<Integer> summaryId = new ArrayList<>();
    public int sumNum = 0;
    
    public void Summarize(String args[]) throws IOException
    {
    	
    	/* Read files */
    	if (args[3].equals("1"))
        {
    		String[] singleFile = new String[1];
            singleFile[0] = args[0];
            myDoc.maxlen = Integer.parseInt(args[4]);
            myDoc.readfile(singleFile, " ", args[2], args[6]);
        }
    	else if (args[3].equals("2")|| args[3].equals("3"))
        {
    		File myfile = new File(args[0]);
            myDoc.maxlen = Integer.parseInt(args[4]);
            myDoc.readfile(myfile.list(),args[0],args[2], args[6]);
        }
    	
    	/* Get abstract */
    	int tmp = 0;
        while(sumNum <= myDoc.maxlen && tmp < myDoc.snum) {
        	summaryId.add(tmp);
        	
        	sumNum += myDoc.senLen.get(tmp);
        	tmp++;
        }
        
        /* Output the abstract */
    	try{
    		File outfile = new File(args[1]);
    		OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(outfile),"utf-8");
    		BufferedWriter writer = new BufferedWriter(write);
    		for (int i : summaryId){
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
