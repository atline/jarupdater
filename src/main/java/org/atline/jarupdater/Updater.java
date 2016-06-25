package org.atline.jarupdater;

import org.atline.jarupdater.utils.Finder;
import org.atline.jarupdater.utils.HttpClientUtil;
import org.atline.jarupdater.utils.PathUtil;

import java.io.*;
import java.util.*;

public class Updater {
    private static void updateLocalVersion(Properties properties) {
        try {
            OutputStream os = new FileOutputStream(PathUtil.getPath() + "/version.txt");
            properties.store(os, "This file is auto generated, do not modify it.");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadJar(String updateSite, String jarString) {
        String str = HttpClientUtil.httpPost(updateSite + "/" + jarString, null);

        try {
            FileOutputStream fos = new FileOutputStream(PathUtil.getPath() + "/" + jarString);
            fos.write(str.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void update(String updateSite) {
        // get remote version info
        String remoteVersion = "";
        HashMap<String , String> remoteJarMap = new HashMap<String , String>();
        String str = HttpClientUtil.httpPost(updateSite + "/version.txt", null);

        Properties pOutput = new Properties();
        Properties pRemote = new Properties();
        try {
            pRemote.load(new StringReader(str));
            Enumeration en = pRemote.propertyNames();
            while (en.hasMoreElements()){
                String k = (String) en.nextElement();
                if ("version".equals(k)) {
                    remoteVersion = pRemote.getProperty(k);
                } else {
                    remoteJarMap.put(k, pRemote.getProperty(k));
                }
                pOutput.setProperty(k, pRemote.getProperty(k));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get local version info
        String found = Finder.findVersions();

        if ("".equals(found)) {
            found = PathUtil.getPath() + "/version.txt";
            File f = new File(found);
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Properties pLocal = new Properties();
            pLocal.load(new FileInputStream(found));
            String localVersion = pLocal.getProperty("version", "") ;
            HashMap<String , String> localJarMap = new HashMap<String , String>();

            if (!localVersion.equals(remoteVersion)) {
                Enumeration en = pLocal.propertyNames();
                while (en.hasMoreElements()){
                    String k = (String) en.nextElement();
                    if (!"version".equals(k)) {
                        localJarMap.put(k, pLocal.getProperty(k));
                    }
                }

                // md5 compare and download jar if needed
                Iterator remoteIter = remoteJarMap.entrySet().iterator();
                while (remoteIter.hasNext()) {
                    Map.Entry entry = (Map.Entry) remoteIter.next();
                    Object key = entry.getKey();
                    Object val = entry.getValue();

                    if (!val.equals(localJarMap.get(key))) {
                        downloadJar(updateSite, (String) key);
                    }

                    localJarMap.remove(key);
                }

                // delete unused jar
                Iterator localIter = localJarMap.entrySet().iterator();
                while (localIter.hasNext()) {
                    Map.Entry entry = (Map.Entry) localIter.next();
                    Object key = entry.getKey();
                    System.out.println(key);

                    File f = new File(PathUtil.getPath() + "/" + key);
                    if (f.exists()) {
                        f.delete();
                    }
                }

                // update local versions
                Updater.updateLocalVersion(pOutput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
