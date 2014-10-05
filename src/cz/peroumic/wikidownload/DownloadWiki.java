package cz.peroumic.wikidownload;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
    protected List<File> files;
    protected long startTime = 0;

    public DownloadWiki(int threads, String inputFilePath,String language) throws FileNotFoundException {
        this.threads = threads;
        this.startTime = System.currentTimeMillis();
        this.files = new ArrayList<File>();
        File f = new File( inputFilePath );
        if ( f . isDirectory() )
        {
            this.files = new ArrayList<File>(Arrays.asList(f.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile();
                }
            })));
        }
        else
        {
            this.files = new ArrayList<File>();
            this.files.add(new File ( inputFilePath ));
        }
        if ( this.files.size() > 0  ) {
            this.localFile = new BufferedReader(new FileReader(this.files.get(this.files.size()-1).getAbsolutePath()));
            this.outputFolderPath = this.files.get(this.files.size()-1).getAbsolutePath();
        }
        for ( File tmp : this.files )
            this.bytes += tmp.length();

        this.outputFolderPath = this.outputFolderPath.substring(0,this.outputFolderPath.lastIndexOf('\\'));
        File newFolder = new File(this.outputFolderPath+"\\"+language);
        if(!newFolder.isFile()){
            newFolder.mkdir();
        }
        outputFolderPath = newFolder.getPath();
        this.language = language;
    }

    public String getOutputFolderPath(){
        return outputFolderPath;
    }

    public long getReadBytes() {
        return readBytes;
    }

    public void addReadBytes(long readBytes) {
        this.readBytes += readBytes;
    }

    public synchronized void addCounter(long counter) {
        this.counter += counter;
    }

    public long getCounter() {
        return counter;
    }

    public double getSpeed () {
        return (System.currentTimeMillis() - startTime)/getCounter();
    }

    public double getDuration () {
        return ( System.currentTimeMillis() - startTime ) * (this.bytes - getReadBytes())/getReadBytes();
    }

    public void printStats(){
        double duration = getDuration();
        char mode = 's';
        if ( duration/1000d > 60 && duration/1000d <= 3600 ) {
            mode = 'm';
            duration /= 60;
        } else if ( duration/1000d > 3600 ) {
            duration /= 3600;
            mode = 'h';
        }

        System.out.format("\r%d%% [%d/%d] %.4f l/s. Remaining %.2f%c.", (int)(getReadBytes() * 100 / (float)this.bytes),
                getReadBytes(),
                this.bytes,
                getSpeed()/1000d,
                duration/1000d,
                mode,
                getCounter());
    }


    public synchronized List<String> getLinks(int i) throws IOException {
        String line;
        List<String> list = new ArrayList<String>();
        int y = 0;
        while (i != y) {
            line = localFile.readLine();
            if ( line == null )
            {
                if ( this.files.size() <= 0 )
                    break;
                this.files.remove(this.files.size()-1);
                if ( this.files.size() > 0 ) {
                    localFile.close();
                    localFile = new BufferedReader(new FileReader(this.files.get(this.files.size() - 1).getAbsolutePath()));
                    line = localFile . readLine();
                }
            }
            if ( line == null )
                break;
            list.add(line);
            y++;
        }
        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    /**
     * Merges all files present in the outputFolderPath directory
     * to one file named <lang>.txt
     *
     * If <lang>.txt file is present in the directory, this method will append
     * all content.
     *
     * @throws IOException
     */
    private void mergeFiles() throws IOException
    {
        File folder = new File ( getOutputFolderPath() );
        File [] fp = folder.listFiles();
        File finalFile = new File ( getOutputFolderPath() + "\\" + this . language + ".txt" );
        FileOutputStream fos;
        System.out.println("Merging");
        try {
            fos = new FileOutputStream(finalFile, true);
            for ( File localFile : fp )
            {
                if ( localFile . getName() .contains( this.language + ".txt" ) )
                    continue;
                FileInputStream fis = new FileInputStream(localFile);
                byte[] buffer = new byte[8192];
                int count;
                while ((count = fis.read(buffer)) > 0)
                    fos.write(buffer, 0, count);
                fos.flush();
                fis.close();
                localFile . delete();
            }
            System.out.println("Files merged to " + finalFile . getName() );
            fos . close ();
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
