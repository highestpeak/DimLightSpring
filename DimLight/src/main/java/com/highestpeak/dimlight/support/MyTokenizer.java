package com.highestpeak.dimlight.support;

import code.Stemmer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("DuplicatedCode")
public class MyTokenizer {
    public ArrayList<String> passage=new ArrayList<String>();
    public ArrayList<Integer> senLen=new ArrayList<Integer>();
    public ArrayList<ArrayList<String>> word=new ArrayList<ArrayList<String>>();
    public ArrayList<ArrayList<String>> stemmerWord=new ArrayList<ArrayList<String>>();
    public ArrayList<String> sentence=new ArrayList<String>();

    public void stemmerWord() {
        int numOfWord = word.size();
        for(int i = 0; i < numOfWord; ++i) {
            ArrayList<String> stemmerW = new ArrayList<String>();
            for(int j = 0; j < word.get(i).size(); ++j) {
                Stemmer stemmer = new Stemmer();
                int letterNumOfWord = word.get(i).get(j).length();
                for(int k = 0; k < letterNumOfWord; ++k) {
                    stemmer.add(word.get(i).get(j).charAt(k));
                }
                stemmer.stem();
                String tmpW = stemmer.toString();
                stemmerW.add(tmpW);
            }
            stemmerWord.add(stemmerW);
        }
    }

    /**
     * 临时：没有stopwords参与
     */
    public ArrayList<String> tokenizeChn(String line) throws IOException
    {
        StringBuffer buffer=new StringBuffer(line);
        Pattern pattern = Pattern.compile(".*?[。？！]");
        Matcher matcher = pattern.matcher(buffer);
        Pattern p2=Pattern.compile("[\u4e00-\u9fa5]");
        while (matcher.find()) {
            String sen=matcher.group();
            passage.add(sen);
            senLen.add(sen.length());
            Result parse = ToAnalysis.parse(sen);
            ArrayList<String> tmpsen=new ArrayList<>();
            for (Term x:parse){
                Matcher m2=p2.matcher(x.getName());
                if (m2.find()) {
                    tmpsen.add(x.getName());
                }
            }
            word.add(tmpsen);
        }
        stemmerWord();
        return passage;
    }

    public ArrayList<String> tokenizeEng(String content) throws IOException
    {
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(content),
                new CoreLabelTokenFactory(), "");
        int len=0;
        int wlen=0;
        String token,tmpSen;
        tmpSen=new String();
        boolean ifend=false;
        while (ptbt.hasNext())
        {
            CoreLabel label = ptbt.next();
            token=label.toString();

            if (ifend==false)
            {

                if (token.equals(".") || token.equals("?") ||token.equals("!"))
                {
                    ifend=true;
                }
                //remove some invalit symbols
                if (token.equals("-LRB-") || token.equals("-RRB-") || token.equals("-LCB-")|| token.equals("-RCB-") || token.equals("\""))
                    continue;
                if (token.equals("'") || token.equals("`") || token.equals("''") || token.equals("``") || token.equals("_") || token.equals("--") || token.equals("-")){
                    continue;
                }
                if (token.equals("'s") || token.equals(".") || token.equals("?") || token.equals("!") || token.equals(",") || token.equals("'re") || (token.equals("'ve")))
                    tmpSen+=token;
                else
                    tmpSen+=" "+token;

                if (!token.equals("'s"))
                    len++;
            }else
            {
                if (token.equals("'") || token.equals("`") || token.equals("''") || token.equals("``") || token.equals(" ")){
                    continue;
                }
                if (token.equals("."))
                {

                    tmpSen+=token;
                    len++;
                }else
                {
                    if (len>1 && wlen*2>=len) {
                        passage.add(tmpSen);
                        senLen.add(len);
                        word.add(sentence);
                    }
                    ifend=false;
                    tmpSen=token;
                    sentence=new ArrayList<String>();
                    wlen=0;
                    if (ifWordsEng(token))
                        wlen++;
                    len=1;
                }
            }
        }
        if (ifend && len>1 && wlen*2>=len)
        {
            passage.add(tmpSen);
            word.add(sentence);
            senLen.add(len);
        }
        stemmerWord();
        return passage;
    }

    public boolean ifWordsEng(String tmpWord)
    {
        if (tmpWord.charAt(0)>='A' && tmpWord.charAt(0)<='Z') return true;
        if (tmpWord.charAt(0)>='a' && tmpWord.charAt(0)<='z') return true;
        return false;
    }

}
