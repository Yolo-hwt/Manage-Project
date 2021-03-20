package server;
import base.Doc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ServerReceive extends Thread {
    private DataInputStream dis;
    private String fileName;
    public ServerReceive(String fileName, DataInputStream dis) {
        this.dis = dis;
        this.fileName=fileName;
    }
    @Override
    public void run() {
        DataOutputStream dos = null;
        try {
            long filelong=dis.readLong();
            // 获取服务器端的输出流，保存为服务器上指定文件夹中的文件
            dos = new DataOutputStream(new FileOutputStream(Doc.whereToSaveFileFromClient + fileName));
            byte[] sendBytes = new byte[50000];
            int read;
            long index=0;
            while ((read=dis.read(sendBytes))>0){
                index+=read;
                dos.write(sendBytes, 0, read);
                if(index>=filelong)
                    break;
            }
            System.out.println("读写完毕");
            dos.flush();
            Doc.fileisOk=true;
        }catch (Exception ev)
        {
            System.out.println("文件上传出错");
            ev.printStackTrace();
        }finally {
            try {
                if(dos!=null)
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
            }
        }
    }
}

