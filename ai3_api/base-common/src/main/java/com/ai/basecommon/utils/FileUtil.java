package com.ai.basecommon.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class FileUtil {



    /**
     * 读取bytes
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] readBytes(File file) throws IOException {

        //check the file is Exists
        if (file == null || !file.exists()) {
            //System.err.println("file is not null or exist !");
            throw new FileNotFoundException();
        }

        // check the file is too long, if the file length is too long ,returned. because the byte array can not buffered.
        // byte array bufferSize=file.lenght,and  must between 0 and Integer_MAX_VALUE
        if (file.length() > Integer.MAX_VALUE) {
            //System.err.println("file is too big ,not to read !");
            throw new IOException(file.getName() + " is too big ,not to read ");
        }
        int _bufferSize = (int) file.length();
        //定义buffer缓冲区大小
        byte[] buffer = new byte[_bufferSize];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            int len = 0;
            if ((len = in.available()) <= buffer.length) {
                in.read(buffer, 0, len);
            }
        } finally {
            in.close();
        }
        return buffer;
    }


    /**
     * 读取bytes 用nio
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] readBytesByNIO(File file) throws IOException {
        //check the file is Exists
        if (file == null || !file.exists()) {
            System.err.println("file is not null or exist !");
            throw new FileNotFoundException();
        }
        //1、定义一个File管道，打开文件输入流，并获取该输入流管道。
        //2、定义一个ByteBuffer，并分配指定大小的内存空间
        //3、while循环读取管道数据到byteBuffer，直到管道数据全部读取
        //4、将byteBuffer转换为字节数组返回
        FileChannel fileChannel = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            fileChannel = in.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate((int) fileChannel.size());

            while (fileChannel.read(buffer) > 0) {
            }
            return buffer.array();
        } finally {
            fileChannel.close();
            in.close();
        }
    }



}
