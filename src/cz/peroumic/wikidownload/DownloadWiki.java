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
    protected BufferedReader localFile;
    protected long counter = 0;
    protected long bytes = 0;
    protected long readBytes = 0;
    protected String language = "cs";


    public DownloadWiki(int threads, String inputFilePath,String language) throws FileNotFoundException {
        this.threads = threads;
        this.localFile = new BufferedReader(new FileReader(inputFilePath));
        File ff = new File(inputFilePath);
        this.bytes = ff.length();
        outputFolderPath = ff.getAbsolutePath();
        outputFolderPath = outputFolderPath.substring(0,outputFolderPath.lastIndexOf('\\'));
        File newFolder = new File(outputFolderPath+"\\"+language);
        if(!newFolder.isFile()){
            newFolder.mkdir();
        }
        outputFolderPath = newFolder.getPath();
        this.language = language;
    }

    public String getOutputFolderPath(){
        return outputFolderPath;
    }


    public synchronized List<String> getLinks(int i) throws IOException {
        String line = localFile.readLine();
        this.readBytes += line.getBytes("UTF-8").length;
        List<String> list = new ArrayList<String>();
        int y = 0;
        while (line != null && i != y) {
            list.add(line);
            line = localFile.readLine();
            this.readBytes += line.getBytes("UTF-8").length;
            y++;
        }
        if (list.size() == 0) {
            return null;
        }
        counter+=list.size();
        //System.out.println(counter);
        System.out.format("%d%% [%d/%d]\r\n", (this.readBytes / this.bytes), this.readBytes, this.bytes);
        return list;
    }

    private void mergeFiles() throws IOException
    {
        File finalFile = new File ( getOutputFolderPath() + "\\" + this . language + ".txt" );
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(finalFile, true);
            for (int i = 0; i < threads; i++) {
                File localFile = new File(getOutputFolderPath() + "\\" + "T" + i + ".txt");
                FileInputStream fis = new FileInputStream(localFile);
                byte[] buffer = new byte[8192];
                int count;
                while ((count = fis.read(buffer)) > 0)
                    fos.write(buffer, 0, count);
                fos.flush();
                fis.close();
                localFile . delete ();
            }
        } catch (Exception exception){
            exception.printStackTrace();
        }
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
            localFile.close();
        }
        mergeFiles();
    }
}
