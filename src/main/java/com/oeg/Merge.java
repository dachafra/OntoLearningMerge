package com.oeg;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * Created by dchaves on 7/7/17.
 */
public class Merge {
    private static final Double threshold=0.95;
    private ArrayList<File> files;
    private HashMap<String,boolean[]> merge;
    private String type;


    public Merge(String type){
        files = new ArrayList<File>();
        merge = new HashMap<String, boolean[]>();
        this.type=type;
    }


    public void run(){
        this.readApproaches("<DIR>");
        this.merge();
        if(type.equals("Terms")){
            removeSimilarities();
        }
        this.writeMerge();
    }



    private void readApproaches(String folder){
        File f = new File(folder);
        File[] listOfFiles = f.listFiles();
        for(File f1 : listOfFiles){
            files.add(f1);
        }
    }

    private void writeMerge(){
        String output;
        if(this.type.equals("Relations"))
             output="Word1,Word2,";
        else
            output="Word1,";
        for(File f : files){
            output+= (new StringTokenizer(f.getName(),".")).nextToken()+",";
        }
        output+="\n";
        ArrayList<String> words= new ArrayList<String>(merge.keySet());
        for(int i=0; i<merge.size();i++){
            output+=words.get(i)+",";
            boolean[] results = merge.get(words.get(i));
            for(int j=0; j<results.length;j++){
                if(results[j]==true){
                    output+="1,";
                }
                else{
                    output+="0,";
                }
            }
            output+="\n";
        }
        try {
            PrintWriter salida = new PrintWriter("output.csv", "UTF-8");
            salida.println(output);
            salida.close();
        }catch (IOException e){
            System.out.println("Error opening output");
        }
    }

    private void merge(){
        try {
            for(File f : files){
                List<String> s = FileUtils.readLines(f, "UTF-8");
                for(String line : s){
                    if(!merge.containsKey(line)){
                        merge.put(line,new boolean[files.size()]);
                    }
                    merge.get(line)[files.indexOf(f)]=true;
                }
            }

        }catch (IOException e){
            System.out.println("Error opening a file! "+e.getMessage());
        }

    }

    private void removeSimilarities(){
        ArrayList<String> terms = new ArrayList<String>(merge.keySet());
        Collections.sort(terms, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        for(String t: terms){
            for(String t1: terms){
                double similarity=StringUtils.getJaroWinklerDistance(t,t1);
                if(similarity>this.threshold && !t.equals(t1)){
                    boolean[] results1 = merge.get(t1);
                    boolean[] results = merge.get(t);
                    for(int j=0; j<results1.length;j++){
                        if(results1[j]==true){
                            results[j]=true;
                        }
                    }
                    merge.remove(t);
                    merge.put(t,results);
                }
            }
        }


    }



}
