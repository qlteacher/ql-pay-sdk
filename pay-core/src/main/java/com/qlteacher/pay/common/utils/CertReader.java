package com.qlteacher.pay.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CertReader {
    
    public static byte[] readCertFileByte(String certPath) {
        byte[] certData = null;
        File file = new File(readCertpath(certPath));
        InputStream certStream = null;
        try {
            certStream = new FileInputStream(file);
            certData = new byte[(int) file.length()];
            certStream.read(certData);
        } catch (FileNotFoundException e) {
             e.printStackTrace();
        } catch (IOException e) {
             e.printStackTrace();
        } finally {
            if(null!=certStream) {
                try {
                    certStream.close();
                } catch (IOException e) {
                     e.printStackTrace();
                } 
            }
        }
        
        return certData;
    }

    public static String readCertpath(String certPath) {
        if (existsInFileSystem(certPath)) {
            return readFromFileSystem(certPath);
        }
        return readFromClassPath(certPath);
    }

    private static String readFromFileSystem(String certPath) {
        return new File(certPath).getPath();
    }

    private static String readFromClassPath(String certPath) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(certPath);
        return new File(resource.getFile()).getPath();
    }

    private static boolean existsInFileSystem(String certPath) {
        try {
            return new File(certPath).exists();
        } catch (Throwable e) {
            return false;
        }
    }

}
