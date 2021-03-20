package client;

import base.Doc;
import java.io.*;

public class client_Download extends Thread{

    private String saveFilePath;//文件保存路径
    private  String FileName;//文件名
    private DataInputStream dis;

    public client_Download(String saveFilePath,String FileName,DataInputStream dis) {
        this.saveFilePath = saveFilePath;
        this.FileName=FileName;
        this.dis=dis;
    }
        public void run()
        {
            DataOutputStream dos = null;
                try
                {
                    //获取文件输出流
                    String fileAbosolutePath=saveFilePath+"\\"+FileName;//保存文件路径+文件名
                    dos =new DataOutputStream(new FileOutputStream(fileAbosolutePath));
                    // 传输文件
                    byte[] sendBytes = new byte[50000];
                    int read ;
                    long index=0;
                    long filelength=dis.readLong();
                    while ((read = dis.read(sendBytes, 0, sendBytes.length))!=-1)
                    {
                        index+=read;
                        dos.write(sendBytes, 0,read);
                        dos.flush();
                        if(index>=filelength)
                            break;
                    }
                    dos.flush();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    Doc.fileisOk=true;
                    try
                    {
                        //if (dis != null)
                            //dis.close();
                        if (dos != null)
                            dos.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
        }//end run
}//end class
