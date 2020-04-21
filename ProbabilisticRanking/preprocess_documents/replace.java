import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;

import java.io.File;
import java.io.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;

public class replace{

public static void main(String[] args) {
	ArrayList<String> files = new ArrayList<String>();
    File file = new File("/Users/yibowang/Desktop/IRproject1/text/fbis");
    File[] tempList = file.listFiles();

    for (int i = 0; i < tempList.length; i++) {
        if (tempList[i].isFile()) {
		//String data = "C:\\Users\\lenovo\\Desktop\\lucene-8.4.1\\docs\\subdocs\\problemdoc\\fb396003";
		//File file = new File(data);
		BufferedReader reader = null;
		ArrayList<String> arraylist = new ArrayList<String>();
		try {
			reader = new BufferedReader(new FileReader(tempList[i].toString()));
			System.out.println(tempList[i].toString());
			String tempString = null;
            int line = 1;
            String filePar = "/Users/yibowang/Desktop/IRproject1/text/fbisnew/";
            BufferedWriter out = new BufferedWriter(new FileWriter(filePar + "\\fb3960" + i));
            
            while ((tempString = reader.readLine()) != null) {
                String docu = tempString.replace("<F P=100>", " ");
                docu = docu.replace("<F P=101>", " ");
                docu = docu.replace("<F P=102>", " ");
                docu = docu.replace("<F P=103>", " ");
                docu = docu.replace("<F P=104>", " ");
                docu = docu.replace("<F P=105>", " ");
                docu = docu.replace("<F P=106>", " ");
                docu = docu.replace("<F P=107>", " ");
                docu = docu.replace("</F>", " ");
                out.write(docu);
                out.newLine();
                //out.flush();
                //out.close();
                //arraylist.add(tempString);
                //System.out.println("line " + line + ": " + tempString);
                line++;
            }
            out.flush();
            out.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
    }
    }
}