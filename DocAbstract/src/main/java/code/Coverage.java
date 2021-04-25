package code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Coverage {
    public Doc myDoc = new Doc();
    public ArrayList<Integer> summaryId = new ArrayList<>();
    public int sumNum = 0;

    public void Summarize(String args[]) throws IOException {
        if (args[3].equals("1")) {
            System.out.println("The Coverage method can't solve single-document summarization task.");
            return;
        }

        /* Read files */
        File myfile = new File(args[0]);
        myDoc.maxlen = Integer.parseInt(args[4]);
        myDoc.readfile(myfile.list(), args[0], args[2], args[6]);

        /* Get abstract */
        int tmpF = 0, tmpS = 0;
        int sNum = 0;
        while (sumNum <= myDoc.maxlen && sNum < myDoc.snum) {
            tmpF = sNum % myDoc.fnum;
            tmpS = sNum / myDoc.fnum;
            summaryId.add(myDoc.lRange[tmpF] + tmpS);
            sumNum += myDoc.senLen.get(myDoc.lRange[tmpF] + tmpS);
            sNum++;
        }

        /* Output the abstract */
        try {
            File outfile = new File(args[1]);
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(outfile), "utf-8");
            BufferedWriter writer = new BufferedWriter(write);
            for (int i : summaryId) {
                writer.write(myDoc.originalSen.get(i));
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("There are errors in the output.");
            e.printStackTrace();
        }
    }
}
