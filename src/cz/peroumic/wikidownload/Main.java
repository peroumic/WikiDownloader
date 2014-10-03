package cz.peroumic.wikidownload;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Michal
 * Date: 3.10.14
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    private static final int DEFAULT_THREAD_NUMBER = 8;
    private static final String DEFAULT_INPUT_FILE = "cs.links.txt";
    private static final String DEFAULT_LANGUAGE = "CS";

    /**
     * inputFilePath threads language
      * @param args
     */
    public static void main(String[] args)
    {
        int threads;
        String inputFilePath = "";
        String language = "";
        try {
            if (args.length == 3) {
                inputFilePath = args[0];
                threads = Integer.valueOf(args[1]);
                language = args[2];
            } else if (args.length == 2) {
                inputFilePath = args[0];
                threads = Integer.valueOf(args[1]);
                language = DEFAULT_LANGUAGE;
            }  else if (args.length == 1) {
                inputFilePath = args[0];
                threads = DEFAULT_THREAD_NUMBER;
                language = DEFAULT_LANGUAGE;
            } else {
                threads = DEFAULT_THREAD_NUMBER;
                inputFilePath = DEFAULT_INPUT_FILE;
                language = DEFAULT_LANGUAGE;
            }

            Logger logger = Logger.getLogger("");
            logger.setLevel(Level.OFF);

            DownloadWiki dw = new DownloadWiki(threads,inputFilePath,language);
            dw.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error :(");
        }
    }
}
