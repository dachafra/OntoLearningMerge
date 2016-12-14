package com.oeg;


import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;


public class MergeOL
{
    private static Double similarityThreshold = 0.95;
    private static HashMap<String,Boolean[]> nouns = new HashMap<String, Boolean[]>();
    private static ArrayList<String> text2onto = new ArrayList<String>();
    private static ArrayList<String> sketch = new ArrayList<String>();
    private static ArrayList<String> our = new ArrayList<String>();

    public static void main( String[] args ){
    try {
        FileWriter file = new FileWriter("merge.csv");
        PrintWriter pw = new PrintWriter(file);

        readApproaches();
        merge();
        findSimilarities();

        pw.println(",TextToOnto,Skecth,Our,");
        for (Map.Entry<String,Boolean[]> entry : nouns.entrySet()) {
            String key = entry.getKey();
            Boolean[] value = entry.getValue();
            String values="";
            for(int i=0; i<3; i++){
                if(value[i]==true){
                    values+="1,";
                }
                else{
                    values+=",";
                }
            }
            pw.println(key+","+values);
        }

        pw.close();
        file.close();
    }catch (IOException e){
        System.out.println("Error");
    }



    }

    public static double compareStrings(String stringA, String stringB) {
        return StringUtils.getJaroWinklerDistance(stringA, stringB);
    }

    public static void merge(){
        boolean flag=true;
        for(String t : text2onto){
            nouns.put(t,new Boolean[3]);
            nouns.get(t)[0]=true;
            nouns.get(t)[1]=false;
            nouns.get(t)[2]=false;
            for(String s : sketch){
                if(t.equals(s)){
                    for(String o: our){
                        if(t.equals(o)){
                            nouns.get(t)[1]=true;
                            nouns.get(t)[2]=true;
                            our.remove(o);
                            sketch.remove(s);
                            flag=false;
                            break;
                        }
                    }
                    if(flag==true) {
                        nouns.get(t)[1]=true;
                        sketch.remove(s);
                    }
                    break;

                }
            }
            flag=true;
            for(String o : our){
                if(t.equals(o)){
                    nouns.get(t)[2]=true;
                    our.remove(o);
                    break;
                }
            }

        }

        for(String s : sketch){
            nouns.put(s,new Boolean[3]);
            nouns.get(s)[0]=false;
            nouns.get(s)[1]=true;
            nouns.get(s)[2]=false;
            for(String o : our){
                if(s.equals(o)){
                    nouns.get(s)[2]=true;
                    our.remove(o);
                    break;
                }
            }
        }

        for(String o : our){
            nouns.put(o,new Boolean[3]);
            nouns.get(o)[0]=false;
            nouns.get(o)[1]=false;
            nouns.get(o)[2]=true;
        }

    }

    public static void readApproaches(){
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("termsByApproach.csv"));
            br.readLine();
            String text = br.readLine();
            while (text != null) {
                StringTokenizer tokenizer = new StringTokenizer(text, ",");
                String t2o = tokenizer.nextToken();
                String sk = tokenizer.nextToken();
                String o = tokenizer.nextToken();
                if (!text2onto.contains(t2o))
                    text2onto.add(t2o);
                if (!sketch.contains(sk))
                    sketch.add(sk);
                if (!our.contains(o))
                    our.add(o);
                text = br.readLine();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void findSimilarities(){
        try {
            FileWriter file = new FileWriter("deletes.txt");
            PrintWriter pw = new PrintWriter(file);
            ArrayList<String> semanticFilter = new ArrayList<String>();
            ArrayList<String> deletes = new ArrayList<String>();
            ArrayList<String> terms = new ArrayList<String>(nouns.keySet());
            Collections.sort(terms, new Comparator<String>() {
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            for (String t : terms) {
                for (String t1 : terms) {
                    double d = compareStrings(t, t1);
                    if (d > similarityThreshold && !t.equals(t1) && !semanticFilter.contains(t)) {
                        semanticFilter.add(t1);
                        for (int i = 0; i < 3; i++) {
                            if (nouns.get(t1)[i] == true)
                                nouns.get(t)[i] = true;
                        }
                        deletes.add("Value:" + t + "\t\tDelete:" + t1 + "\t\tSimilarity:" + d);
                    }
                }
            }

            for (int i = 0; i < semanticFilter.size(); i++) {
                if (nouns.keySet().contains(semanticFilter.get(i))) {
                    nouns.remove(semanticFilter.get(i));
                    pw.println(deletes.get(i));
                }
            }
            pw.close();
            file.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
