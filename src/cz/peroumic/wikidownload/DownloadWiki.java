package cz.peroumic.wikidownload;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michal
 * Date: 3.10.14
 * Time: 15:39
 * To change this template use File | Settings | File Templates.
 */
public class DownloadWiki {
    protected int threads;
    protected String outputFolderPath;
    protected BufferedReader file;
    protected long counter = 0;

    public DownloadWiki(int threads, String inputFilePath,String language) throws FileNotFoundException {
        this.threads = threads;
        this.file = new BufferedReader(new FileReader(inputFilePath));
        File ff = new File(inputFilePath);
        outputFolderPath = ff.getAbsolutePath();
        outputFolderPath = outputFolderPath.substring(0,outputFolderPath.lastIndexOf('\\'));
        File newFolder = new File(outputFolderPath+"\\"+language);
        if(!newFolder.isFile()){
            newFolder.mkdir();
        }
        outputFolderPath = newFolder.getPath();
    }

    public String getOutputFolderPath(){
        return outputFolderPath;
    }


    public synchronized List<String> getLinks(int i) throws IOException {
        String line = file.readLine();
        List<String> list = new ArrayList<>();
        int y = 0;
        while (line != null && i != y) {
            list.add(line);
            line = file.readLine();
            y++;
        }
        if (list.size() == 0) {
            return null;
        }
        counter+=list.size();
        System.out.println(counter);
        return list;
    }


    public void start() throws IOException {
        try {
            List<Thread> threadPool = new ArrayList<Thread>();
            for (int i = 0; i < threads; i++) {
                Thread t = new Thread(new Download(i, this));
                threadPool.add(t);
                t.start();
            }
            for (Thread thread : threadPool) {
                thread.join();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            file.close();
        }
    }
}
