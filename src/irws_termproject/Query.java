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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 *
 * @author ugur_
 */
public class Query {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        File fileWords = new File("src/irws_termproject/stopwords.txt");
        File filePrep = new File("src/irws_termproject/SimilarityFile.txt");

        ArrayList<String> stopword = new ArrayList<>();
        Stemmer s = new Stemmer();

        BufferedReader br1 = new BufferedReader(new FileReader(fileWords));
        String line;
        while ((line = br1.readLine()) != null) {
            stopword.add(line);
        }
        br1.close();

        //part2/looping
        while (true) {
            ///Search Input
            System.out.println(" *type -1 to terminate* ");
            System.out.println("Search:");
            Scanner sc = new Scanner(System.in);
            String search = sc.nextLine();
            if (search.contains("-1")) {
                break;
            }
            HashMap<String, Integer> searchWords = new HashMap<>();

            for (int i = 0; i < stopword.size(); i++) {
                search = search.replaceAll(" " + stopword.get(i) + " ", " ");
            }

            search = search.toLowerCase().replaceAll("[^a-z]", " ");
            String sm[] = search.trim().split("\\s+");//remove white spaces

            for (String word : sm) {
                char ch[] = word.toCharArray();
                s.add(ch, word.length());
                s.stem();
                {
                    String u;
                    u = s.toString();
                    Integer frequency = searchWords.get(u);
                    searchWords.put(u, frequency != null ? frequency + 1 : 1);

                }
            }
            ///End Search

            //Start Ranking
            HashMap<Integer, Double> Ranked = new HashMap<>();
            TreeMap<Double, Integer> OrderedRanked = new TreeMap<>(Collections.reverseOrder());

            Iterator it = searchWords.entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry) it.next();
                BufferedReader br2 = new BufferedReader(new FileReader(filePrep));

                //line -> term/dcId_dcValue/dcId_dcValue...
                while ((line = br2.readLine()) != null) {
                    String linePart[] = line.split("/");
                    //linePart[0] contains Term
                    if (linePart[0].matches((String) pair.getKey())) {
                        for (int i = 1; i < linePart.length; i++) {
                            String dcFre[] = linePart[i].split("_");
                            Double frequency = Ranked.get(Integer.parseInt(dcFre[0]));
                            Ranked.put(Integer.parseInt(dcFre[0]), frequency != null ? frequency + Double.parseDouble(dcFre[1]) : Double.parseDouble(dcFre[1]));
                        }
                    }
                }
                br2.close();
            }

            Iterator itSort = Ranked.entrySet().iterator();
            while (itSort.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry) itSort.next();
                OrderedRanked.put((double) pair.getValue(), (int) pair.getKey());
            }
            //End Ranking

            //-Creating WriteFile
            String qfile = "query";
            int queryCnt = 1;
            File fileWrite;
            while (true) {
                fileWrite = new File("src/irws_termproject/" + qfile + queryCnt + ".txt");
                if (fileWrite.exists()) {
                    queryCnt++;
                } else {
                    fileWrite.createNewFile();
                    break;
                }
            }
            //-End Create WriteFile

            //insert 100 rank to file
            BufferedWriter out = new BufferedWriter(new FileWriter(fileWrite));
            out.write("Rank " + "DocId " + "Similarity ");
            out.newLine();

            //-WriteFile ranklist
            Iterator rk = OrderedRanked.entrySet().iterator();
            int sizer = 1;
            while (rk.hasNext()) {
                Map.Entry rkPair = (Map.Entry) rk.next();
                out.write(sizer + " " + rkPair.getValue() + " " + rkPair.getKey());
                out.newLine();
                /*insert file line by line get rk key and value*/
                sizer++;
                if (sizer == 101) {
                    break;
                }
            }
            out.close();
            //-End writing
            //End 100 rank file 
        }
        //End part2/looping

    }

}
