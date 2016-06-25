package org.atline.jarupdater;

import org.atline.jarupdater.utils.Finder;

import java.io.*;
import java.util.Properties;

public class AppMain {
    public static void main(String[] args) {
        String settings = "";
        if (0 == args.length) {
            String found = Finder.findSettings();
            if ("".equals(found)) {
                System.out.println("Failure to find settings.conf.");
                System.exit(1);
            } else {
                settings = found;
            }
        } else {
            settings = args[0];
        }

        try {
            InputStream is = new BufferedInputStream(new FileInputStream(settings));
            Properties p = new Properties();
            p.load(is);
            String v = p.getProperty("updatesite");
            if (null != v) {
                Updater.update(v);
            } else {
                System.out.println("Wrong updatesite settings.");
                System.exit(1);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
