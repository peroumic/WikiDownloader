package cz.peroumic.wikidownload;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michal
 * Date: 3.10.14
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */
public class Download implements Runnable {
    protected int ID;
    protected  DownloadWiki downloadWiki;
    protected WebDriver driver;
    protected long startTime = 0;

    public Download(int ID, DownloadWiki downloadWiki) {
        this.ID = ID;
        this.downloadWiki = downloadWiki;
        driver = new HtmlUnitDriver();
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        System.out.println("T" + ID + " starts");
        List<String> s;
        PrintWriter writer;
        boolean run = false;
        try {
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(downloadWiki.getOutputFolderPath()+ "\\" + "T" + ID + ".txt", true), "UTF-8"));
            while ((s = downloadWiki.getLinks(500)) != null) {
                //System.out.println("T" + this.ID + " gets more links "+s.size());
                run = true;
                for (String link : s) {
                    downloadAndWriteText(link, writer);
                    String g = "\r\n";
                    downloadWiki.addReadBytes( link . getBytes() . length + g . getBytes() . length );
                    downloadWiki.addCounter(1);
                    downloadWiki.printStats();
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if ( run )
            System.out.format("\n");
        System.out.println("T" + this.ID + " ends");
    }

    private void downloadAndWriteText(String localLink, PrintWriter writer) {
        String s = "";
        try{
            driver.get(localLink);
            WebElement el = driver.findElement(By.id("mw-content-text"));
            s = el.getText();
        }catch (Exception e){ //tady by i to jinak padalo protoze nekdo vygeneroval blbe linky :P
        }
        writer.println(s);
    }
}
