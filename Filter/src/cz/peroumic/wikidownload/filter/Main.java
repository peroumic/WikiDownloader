package cz.peroumic.wikidownload.filter;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Michal
 * Date: 8.10.14
 * Time: 20:11
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    static String folderPath = "C:\\WIKI\\HR";
    static String language = "HR";

    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            folderPath = args[0];
            language = args[1];
        }

         //mergeFiles();
        filter();
    }

    private static void filter() throws IOException {
        File inFile = new File(folderPath + "\\" + language + ".txt");
        File outFile = new File(folderPath + "\\" + language + "_filtered.txt");
        FileOutputStream fos;
        fos = new FileOutputStream(outFile, true);
        FileInputStream fis = new FileInputStream(inFile);
        byte[] buffer = new byte[8192];
        byte[] outBuffer = new byte[8192];
        int count;
        long readBytes = 0;
        boolean writeNext = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "UTF8"));

        String s;
        try {
            //while ((count = fis.read(buffer)) > 0) {
            while ((s = reader.readLine()) != null) {
                readBytes += s.length();
                StringBuilder sb = new StringBuilder();
               Pattern p = Pattern.compile("[\\xE2\\x86\\x91]",Pattern.UNICODE_CHARACTER_CLASS);
                p = Pattern.compile("[\\u2191\\u2022]",Pattern.UNICODE_CHARACTER_CLASS);
             //   s  = s.replaceAll("\\xE2\\x86\\x91", "");
                Matcher unicodeOutlierMatcher = p.matcher(s);
                s = unicodeOutlierMatcher.replaceAll("");
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == '[') {
                        writeNext = false;
                        continue;
                    }
                    if (s.charAt(i) == ']') {
                        writeNext = true;
                        continue;
                    }
                    if ((s.charAt(i) >= '0' && s.charAt(i) <= '9')) {
                        continue;
                    }
                    if (writeNext) {
                        sb.append(s.charAt(i));
                    }
                }
                sb.append('\n');
                fos.write(sb.toString().getBytes());
                System.out.format("\r [%d/%d]", readBytes, inFile.length());
            }
        } finally{
            fis.close();
            fos.close();
        }

    }

    private static void mergeFiles() throws IOException {
        File folder = new File(folderPath);
        File[] fp = folder.listFiles();
        File finalFile = new File(folderPath + "\\" + language + ".txt");
        FileOutputStream fos;
        System.out.println("Merging");
        try {
            fos = new FileOutputStream(finalFile, true);
            for (File localFile : fp) {
                if (localFile.getAbsolutePath().equals(finalFile.getAbsolutePath())) {
                    continue;
                }
                if (localFile.getName().contains(language + ".txt"))
                    continue;
                FileInputStream fis = new FileInputStream(localFile);
                byte[] buffer = new byte[8192];
                int count;
                while ((count = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
                fis.close();
                localFile.delete();
            }
            System.out.println("Files merged to " + finalFile.getName());
            fos.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
