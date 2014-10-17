package cz.peroumic.wikidownload.rhymes;

import java.io.*;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Michal
 * Date: 13.10.14
 * Time: 19:14
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String file = "C:\\Users\\Michal\\Dropbox\\EDU\\DIS\\DAT500\\tests_create\\bg1.txt";
        File inFile = new File(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "UTF8"));
        String s = "";
        while ((s = reader.readLine()) != null) {
            if (s.length() > 0) {
                if ((s.charAt(s.length() - 1) == ',')||
                (s.charAt(s.length() - 1) == '!')||
                (s.charAt(s.length() - 1) == '?')||
                (s.charAt(s.length() - 1) == '\"')||
                (s.charAt(s.length() - 1) == '\'')
                        ) {
                    System.out.println(s.substring(0, s.length() - 2) + ".");

                }   else{
                    if (s.charAt(s.length() - 1) == '.'){
                        System.out.println(s);
                    }else{
                        System.out.println(s+".");
                    }
                }
            }
        }
    }
}

