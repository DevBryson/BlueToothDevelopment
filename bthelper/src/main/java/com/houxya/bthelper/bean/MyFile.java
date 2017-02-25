package com.houxya.bthelper.bean;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Basil on 2017/2/21.
 */

public class MyFile {
    FileOutputStream fout;
    public MyFile(String fileName) throws FileNotFoundException {
        fout = new FileOutputStream(fileName,false);
    }
    public void Write( String str) throws IOException {
        byte[] bytes = str.getBytes();
        fout.write(bytes);
    }
    public void Close() throws IOException{
        fout.close();
        fout.flush();
    }
}