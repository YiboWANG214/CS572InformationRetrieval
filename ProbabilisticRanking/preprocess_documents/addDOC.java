//package com.xtm.test;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import java.io.RandomAccessFile;
//import ReadFolders.java;
 
public class addDOC {
	//public static final String PATH = "C:/Users/lenovo/Desktop/lucene-8.4.1/docs/subdocs/text/fbis/fb396003";
 
	public static void main(String[] args) throws Exception{
		String header = "<DOC>";
		String end = "</DOC>";
		String path = "/Users/yibowang/Desktop/IRproject1/text";
        //ArrayList<String> files = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
        if (tempList[i].isFile()) {
		 appendFileHeader(header.getBytes(),tempList[i].toString());
		 File eachfile =new File(tempList[i].toString());
		 FileWriter fileWritter = new FileWriter(eachfile,true);
		 BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
         bufferWritter.write(end);
         bufferWritter.close();
		}
		if (tempList[i].isDirectory()) {
			System.out.println("Folder: " + tempList[i]);
			String path1 = tempList[i].toString();
        //ArrayList<String> files = new ArrayList<String>();
        File file1 = new File(path1);
        File[] tempList1 = file1.listFiles();

        for (int j = 0; j < tempList1.length; j++) {
        if (tempList1[j].isFile()) {
        	System.out.println("File: " + tempList1[j]);
		 appendFileHeader(header.getBytes(),tempList1[j].toString());
		 File eachfile1 =new File(tempList1[j].toString());
		 FileWriter fileWritter1 = new FileWriter(eachfile1,true);
		 BufferedWriter bufferWritter1 = new BufferedWriter(fileWritter1);
         bufferWritter1.write(end);
         bufferWritter1.close();
		}
	}
    }
	}

        
        //String templist = getFiles(path);
        //appendFileHeader(header.getBytes(),templist);

	//}
}
 
	/**
	 * 向src文件添加header
	 * @param header
	 * @param srcPath
	 * @throws Exception
	 */
	private static void appendFileHeader(byte[] header,String srcPath) throws Exception{
		RandomAccessFile src = new RandomAccessFile(srcPath, "rw");
		int srcLength = (int)src.length() ;
		byte[] buff = new byte[srcLength];
			src.read(buff , 0, srcLength);
			src.seek(0);
			src.write(header);
			src.seek(header.length);
			src.write(buff);
			src.close();
	}

public static String getFiles(String path) {
    ArrayList<String> files = new ArrayList<String>();
    File file = new File(path);
    File[] tempList = file.listFiles();
    String a = "no";

    for (int i = 0; i < tempList.length; i++) {
        if (tempList[i].isFile()) {
            return tempList[i].toString();
            //System.out.println("File: " + tempList[i]);
            //files.add(tempList[i].toString());
        }
        if (tempList[i].isDirectory()) {
              //System.out.println("Folder: " + tempList[i]);
            getFiles(tempList[i].toString());
        }
    }
    return a;
}
	
}