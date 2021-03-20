package server;

import java.io.*;
import base.*;
public class ServerSend extends Thread {
    private DataOutputStream dos;
    private String fileInfo;

    private int modole;
    //下载模式标识(){
    // 1:限定数据库表格内文件下载
    // 2:任意本机文件下载
    // }

    public ServerSend(String fileInfo, DataOutputStream dos, int module)
    {
        this.dos = dos;
        this.fileInfo = fileInfo;
        this.modole=module;
    }
    public void run(){
        DataInputStream dis=null;
        try
        {
            File file=null;
            if(modole==1)
                //1:限定数据库表格内文件下载
                // 此时fileInfo为下载文件文件名
                file = new File(Doc.whereToSaveFileFromClient+ fileInfo);
            if(modole==2)
                // 2:任意本机文件下载
                //此时fileInfo为文件下载路径
                file = new File(fileInfo);

            long filelong=file.length();
            dos.writeLong(filelong);//发送文件大小
            //获取下载文件 流
            dis = new DataInputStream(new FileInputStream(file));

            byte[] sendBytes = new byte[50000];
            int read;
            while ((read=dis.read(sendBytes))!=-1)
            {
                dos.write(sendBytes, 0, read);
                dos.flush();
            }
            dos.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("文件下载出错");
        }
        finally
        {
            try
            {
                if (dis != null)
                    dis.close();
                //if (dos != null)
                   // dos.close();
                Doc.fileisOk=true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
