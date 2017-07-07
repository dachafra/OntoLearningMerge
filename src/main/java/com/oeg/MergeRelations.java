package com.oeg;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by dchaves on 7/7/17.
 */
public class MergeRelations {
    private ArrayList<File> files;
     private HashMap<String,boolean[]> merge;


    public void run(){
        files = new ArrayList<File>();
        merge = new HashMap<String, boolean[]>();
        this.readApproaches("/Users/dchaves/Documents/Otros/Approaches");
        this.merge();
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
        String output="Word1,Word2,";
        for(File f : files){
            output+=f.getName()+",";
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



}
