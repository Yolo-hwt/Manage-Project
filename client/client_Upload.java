package client;

import base.Doc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class client_Upload extends Thread
{
    private String uploadPath;
    private DataOutputStream dos;

    public client_Upload(String uploadPath,DataOutputStream dos) {
        this.uploadPath=uploadPath;
        this.dos=dos;
    }
        public void run()
            {
                DataInputStream dis = null;
                File file = new File(uploadPath);
                System.out.println(file);

                if (file != null)
                {
                    String fileAbosolutePath = file.getAbsolutePath();
                    try
                    {
                        //发送文件大小
                        dos.writeLong(file.length());
                        // 获取文件的输入流
                        dis = new DataInputStream(new FileInputStream(fileAbosolutePath));//读取待上传文件

                        // 传输文件
                        byte[] sendBytes = new byte[50000];
                        int length = 0;
                        while ((length = dis.read(sendBytes, 0, sendBytes.length)) > 0) {
                            dos.write(sendBytes, 0, length);
                        }
                        //清空缓冲区发送文件
                        dos.flush();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
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
}
