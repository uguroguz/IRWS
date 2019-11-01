/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irws_termproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author ugur_
 */
public class Preperation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        File fileWords = new File("src/irws_termproject/stopwords.txt");
        File file = new File("src/irws_termproject/cranfield_collection.txt");
        ArrayList<String> stopword = new ArrayList<>();

        Stemmer s = new Stemmer();

        int documentCnt = 0;

        BufferedReader br1 = new BufferedReader(new FileReader(fileWords));
        BufferedReader br2 = new BufferedReader(new FileReader(file));
        String line;
        String prevline = "";
        while ((line = br1.readLine()) != null) {
            stopword.add(line);
        }
        br1.close();
        //outherhas hh(Term,Innerhash)
        HashMap<String, HashMap<Integer, Double>> hh = new HashMap<>();

        while ((line = br2.readLine()) != null) {

            if (line.contains(".I ")) {

                if (!prevline.isEmpty()) {
                    documentCnt++;

                    int i;
                    for (i = 0; i < stopword.size(); i++) {
                        prevline = prevline.replaceAll(" " + stopword.get(i) + " ", " ");
                    }

                    prevline = prevline.toLowerCase().replaceAll("[^a-z]", " ");
                    String st[] = prevline.trim().split("\\s+");//remove white spaces                                          

                    for (String word : st) {

                        char ch[] = word.toCharArray();
                        s.add(ch, word.length());
                        s.stem();
                        {
                            String u;
                            u = s.toString();
                            //Innerhash dcFq(document,Frequency)                     
                            HashMap<Integer, Double> dcFq = hh.get(u);

                            if (dcFq != null) {
                                Double frequency = dcFq.get(documentCnt);
                                dcFq.put(documentCnt, frequency != null ? frequency + 1 : 1);
                                hh.put(u, dcFq);
                            } else {
                                dcFq = new HashMap<>();
                                dcFq.put(documentCnt, 1.0);
                                hh.put(u, dcFq);
                            }
                        }
                    }
                }

                line = "";
                prevline = "";

            } else if (line.contains(".T ") | line.contains(".A ") | line.contains(".B ") | line.contains(".W ")) {
                line = "";
            }
            prevline = prevline + " " + line;
        }
        br2.close();

        //upper part last cycle W written here
        if (!prevline.isEmpty()) {
            documentCnt++;
            int i;
            for (i = 0; i < stopword.size(); i++) {
                prevline = prevline.replaceAll(" " + stopword.get(i) + " ", " ");
            }

            prevline = prevline.toLowerCase().replaceAll("[^a-z]", " ");
            String st[] = prevline.split("\\s+");//remove white spaces

            for (String word : st) {

                char ch[] = word.toCharArray();
                s.add(ch, word.length());
                s.stem();
                {
                    String u;
                    u = s.toString();

                    HashMap<Integer, Double> dcFq = hh.get(u);

                    if (dcFq != null) {
                        Double frequency = dcFq.get(documentCnt);
                        dcFq.put(documentCnt, frequency != null ? frequency + 1 : 1);
                        hh.put(u, dcFq);
                    } else {
                        dcFq = new HashMap<>();
                        dcFq.put(documentCnt, 1.0);
                        hh.put(u, dcFq);
                    }
                }
            }
        }
        //End last cycle

        //Convert TF hash to TW
        //Create partial Tw length(withouth square root)
        HashMap<Integer, Double> TwLength = new HashMap<>();
        Double n = (double) documentCnt;
        Double IDF;

        Iterator<HashMap.Entry<String, HashMap<Integer, Double>>> OutherHash = hh.entrySet().iterator();

        while (OutherHash.hasNext()) {
            HashMap.Entry<String, HashMap<Integer, Double>> OutherPair = OutherHash.next();

            Iterator< HashMap.Entry<Integer, Double>> InnerHash = (OutherPair.getValue()).entrySet().iterator();

            IDF = Math.log10(n / OutherPair.getValue().size());

            while (InnerHash.hasNext()) {
                HashMap.Entry InnerPair = InnerHash.next();
                InnerPair.setValue((Double) InnerPair.getValue() * IDF);
                Double TwValue = TwLength.get(InnerPair.getKey());
                TwLength.put((int) InnerPair.getKey(), TwValue != null ? TwValue += Math.pow(2, (Double) InnerPair.getValue()) : Math.pow(2, (Double) InnerPair.getValue()));
                //Note:Square root used when pulling TwLength line 197
            }

        }
        //End Convertion

        // Create Tw txt
        File TwlFile = new File("src/irws_termproject/TermWeightLength.txt");
        BufferedWriter bFwTwl = new BufferedWriter(new FileWriter(TwlFile));

        Iterator< HashMap.Entry<Integer, Double>> tw = TwLength.entrySet().iterator();
        while (tw.hasNext()) {
            HashMap.Entry twPair = tw.next();
            Double TwValue = TwLength.get(twPair.getKey());
            double Tw = Math.sqrt(TwValue);

            bFwTwl.write(twPair.getKey() + " : " + Tw);
            bFwTwl.newLine();
        }
        bFwTwl.close();
        //End Tw txt

        //Conver Tw to Normalize            
        //OutherHash defined at line:140
        File SimFile = new File("src/irws_termproject/SimilarityFile.txt");

        BufferedWriter bFwSimFile = new BufferedWriter(new FileWriter(SimFile));

        TwlFile.createNewFile();
        SimFile.createNewFile();

        OutherHash = hh.entrySet().iterator();
        while (OutherHash.hasNext()) {
            HashMap.Entry<String, HashMap<Integer, Double>> OutherPair = OutherHash.next();
            Iterator< HashMap.Entry<Integer, Double>> InnerHash = (OutherPair.getValue()).entrySet().iterator();
            //simfile term name
            bFwSimFile.write(OutherPair.getKey());

            while (InnerHash.hasNext()) {
                HashMap.Entry InnerPair = InnerHash.next();
                Double TwValue = TwLength.get(InnerPair.getKey());

                ///write Similarity
                bFwSimFile.write("/" + InnerPair.getKey() + "_" + (Double) InnerPair.getValue() / Math.sqrt(TwValue));

            }
            bFwSimFile.newLine();
        }
        bFwSimFile.close();
        //Normalize finisheds

        ///End Preperation
    }

}
