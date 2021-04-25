package code;

import java.io.File;

public class Main {

	public static void main(String args[])throws Exception{
		/* Params and default value, you can modify them */
		String language = "1", type = "1", abNum = "100", stopwordPath = "y", stemmerOrNot = "1";
		String topic = "-1", ReMethod = "1", RePara = "0.7", beta = "0";
		String linkThresh = "0.1", AlphaC = "0.1", LambdaC = "0.8", op = "2", AlphaS = "-1", LambdaS = "-1";
		String[] arg;

		String file = "D:\\research\\reviewGeneration\\data\\wikinews\\pureNews";
		String outFile = "D:\\research\\reviewGeneration\\data\\wikinews\\ClusterCMRW";
		
		File dir = new File(file);
		File[] files = dir.listFiles();
			
		File outDir = new File(outFile);
		if(!outDir.exists()){
			boolean file_true = outDir.mkdir(); 
			if (!file_true) {
				System.out.println("No valid dir!");
			}
		}
		/**
		 *  arg[0] = inputPath;
			arg[1] = outputFile;
			arg[2] = language;
			arg[3] = type;
			arg[4] = abNum;
			arg[5] = stemmerOrNot;
			arg[6] = stopwordPath;
		 * */
		for(File fOrd : files) {
			if (fOrd.getName().equals(".DS_Store")) {
				continue;
			}
			/*ILP ilp = new ILP();
			arg = new String[7];
			arg[0] = file + System.getProperty("file.separator") + fOrd.getName();
			arg[1] = outFile + System.getProperty("file.separator") + fOrd.getName();
			arg[2] = language;
			arg[3] = type;
			arg[4] = abNum;
			arg[5] = stemmerOrNot;
			arg[6] = stopwordPath;
 			ilp.Summarize(arg);*/
			
			/*Lead lead = new Lead();
			arg = new String[7];
			arg[0] = file + System.getProperty("file.separator") + fOrd.getName();
			arg[1] = outFile + System.getProperty("file.separator") + fOrd.getName().replace("utf8", "");
			arg[2] = language;
			arg[3] = type;
			arg[4] = abNum;
			arg[5] = stemmerOrNot;
			arg[6] = stopwordPath;
 			lead.Summarize(arg);*/
			
			/*Coverage coverage = new Coverage();
			arg = new String[7];
			arg[0] = file + System.getProperty("file.separator") + fOrd.getName();
			arg[1] = outFile + System.getProperty("file.separator") + fOrd.getName().replace("utf8", "");
			arg[2] = language;
			arg[3] = type;
			arg[4] = abNum;
			arg[5] = stemmerOrNot;
			arg[6] = stopwordPath;
			coverage.Summarize(arg);*/
			
			/*LexPageRank lexpagerank = new LexPageRank();
			arg = new String[13];
			arg[0] = file + System.getProperty("file.separator") + fOrd.getName();
			arg[1] = outFile + System.getProperty("file.separator") + fOrd.getName().replace("utf8", "");
			arg[2] = language;
			arg[3] = type;
			arg[4] = abNum;
			arg[5] = stemmerOrNot;
			arg[6] = stopwordPath;
			arg[7] = ReMethod;
			arg[8] = RePara;
			arg[9] = beta;
			arg[10] = linkThresh;
 			lexpagerank.Summarize(arg);*/
			
 			/*TextRank.java textrank = new TextRank.java();
 			arg = new String[10];
 			arg[0] = file + System.getProperty("file.separator") + fOrd.getName();
			arg[1] = outFile + System.getProperty("file.separator") + fOrd.getName().replace("utf8", "");
			arg[2] = language;
			arg[3] = type;
			arg[4] = abNum;
			arg[5] = stemmerOrNot;
			arg[6] = stopwordPath;
			arg[7] = ReMethod;
			arg[8] = RePara;
			arg[9] = beta;
			textrank.Summarize(arg);*/
			
			/*MEAD mead = new MEAD();
 			arg = new String[11];
 			arg[0] = file + System.getProperty("file.separator") + fOrd.getName();
			arg[1] = outFile + System.getProperty("file.separator") + fOrd.getName().replace("utf8", "");
			arg[2] = language;
			arg[3] = type;
			arg[4] = abNum;
			arg[5] = stemmerOrNot;
			arg[6] = stopwordPath;
			arg[7] = ReMethod;
			arg[8] = RePara;
			arg[9] = beta;
			arg[10] = "D:\\research\\PKUSUMSUM\\PKUSUMSUM\\Data\\topicAll\\topic\\" + fOrd.getName() + ".topic";
			mead.Summarize(arg);*/
			
			/*ManifoldRank manifoldRank = new ManifoldRank();
			arg = new String[11];
			arg[0] = file + System.getProperty("file.separator") + fOrd.getName();
			arg[1] = outFile + System.getProperty("file.separator") + fOrd.getName();
			arg[2] = language;
			arg[3] = single;
			arg[4] = abNum;
			arg[5] = ReMethod;
			arg[6] = RePara;
			arg[7] = beta;
			arg[8] = alpha;
			arg[9] = eps;
			arg[10] = "D:\\research\\PKUSUMSUM\\PKUSUMSUM\\Data\\topicAll\\topic\\" + fOrd.getName() + ".topic";
 			manifoldRank.Summarize(arg);*/
 			
 			/*Submodular submodular = new Submodular();
			arg = new String[11];
			arg[0] = file + System.getProperty("file.separator") + fOrd.getName();
			arg[1] = outFile + System.getProperty("file.separator") + fOrd.getName().replace("utf8", "");
			arg[2] = language;
			arg[3] = type;
			arg[4] = abNum;
			arg[5] = stemmerOrNot;
			arg[6] = stopwordPath;
			arg[7] = op;
			arg[8] = beta;
			arg[9] = AlphaS;
			arg[10] = LambdaS;
			submodular.Summarize(arg);*/
			
			/*ClusterCMRW clusterCMRW = new ClusterCMRW();
			arg = new String[12];
			arg[0] = file + System.getProperty("file.separator") + fOrd.getName();
			arg[1] = outFile + System.getProperty("file.separator") + fOrd.getName().replace("utf8", "");
			arg[2] = language;
			arg[3] = type;
			arg[4] = abNum;
			arg[5] = stemmerOrNot;
			arg[6] = stopwordPath;
			arg[7] = ReMethod;
			arg[8] = RePara;
			arg[9] = beta;
			arg[10] = AlphaC;
			arg[11] = LambdaC;
			clusterCMRW.Summarize(arg);*/
			
		}
		
	}
}
