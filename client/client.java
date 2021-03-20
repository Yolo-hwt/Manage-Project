package client;

import base.AbstractUser;
import base.Doc;
import base.Message;
import userGUI.LoginFrame;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 客户端类
 * client
 */
public class client
{
    //客户端全局变量
    public static client Myclient;
    //传递信息
    private ObjectOutputStream output; // 输出流 (to server)
    private ObjectInputStream input; // 输入流 (from server)
    //上传下载文件
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    //接口信息
    private String chatServer; // host server for this application
    private Socket client; // socket to communicate with server
    private static final int port=31010;//程序接口(port)

    /**
     *TODO 测试 launch program
     * @param args
     */
    public static void main( String args[] )
    {
        Myclient= new client( "127.0.0.1"); // connect to localhost
        Myclient.runClient(); // run client application
    } // end main

    public client( String host ) { chatServer = host; } // end Client constructor

    /**
     * TODO achieve client
     *实现客户端
     */
    public void runClient() {
        new Thread(() -> {
            try {
                connectToServer(); // 连接服务器
                getStreams(); // 获取流
                getData();
                processConnection(); // process connection
            } // end try
            catch (IOException ioException) {
                ioException.printStackTrace();
            } // end catch
     }).start();
    }// end method runClient

    /**
     *TODO 连接服务器
     * @throws IOException
     */
    private void connectToServer() throws IOException {
        displayMessage( "尝试连接服务器" );

        // create Socket to make connection to server
        client = new Socket( chatServer, port );

        // display connection information
        displayMessage( "Connected to: " +
                client.getInetAddress().getHostName());
    } // end method connectToServer

    /**
     *TODO 获得输入输出流
     * @throws IOException
     */
    private void getStreams() throws IOException {

        dataIn=new DataInputStream(client.getInputStream());
        input = new ObjectInputStream( client.getInputStream() );

        dataOut=new DataOutputStream(client.getOutputStream());
        dataOut.flush();
        output = new ObjectOutputStream( client.getOutputStream() );
        output.flush(); // flush output buffer to send header information

        displayMessage( "Got I/O streams" );

    } // end method getStreams

    /**
     *TODO 创建用户登录GUI界面
     * @throws IOException
     */
    private static void processConnection() {
        LoginFrame.runFrame();
    } // end method processConnection

    /**
     * 接收服务器下发数据
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    //获取所有用户
    public ArrayList<AbstractUser> getAllUserInfo(){
        try{
            ArrayList<AbstractUser>users=(ArrayList<AbstractUser>)input.readObject();
            displayMessage("用户信息数据");
            PrintUser(users);
            return users;
        }catch(ClassNotFoundException|IOException e){
            e.toString();
            e.printStackTrace();
            displayMessage( "Error writing object in getAllUserInfo" );
        }finally {
           // closeConnection("getAllUserInfo IO连接关闭");
        }
        return null;
    }// end method getAllUserInfo
    //获取所有档案
    public ArrayList<Doc> getAllDocInfo(){
        try{
            ArrayList<Doc>docs=(ArrayList<Doc>)input.readObject();
            displayMessage("档案信息数据");
            PrintDoc(docs);
            return docs;
        }catch(ClassNotFoundException|IOException e){
            e.toString();
            e.printStackTrace();
            displayMessage( "Error writing object in getAllDocInfo" );
        }finally {
            // closeConnection("getAllDocInfo IO连接关闭");
        }
        return null;
    }// end method getAllDocInfo
    //打印信息
    private void PrintUser(ArrayList<AbstractUser>data){
        AbstractUser user=null;
        for(int i=0;;i++){
            if((user=data.get(i))!=null)
                System.out.println(user);
            else break;
        }
        System.out.println();
    }
    private void PrintDoc(ArrayList<Doc>data){
        Doc doc=null;
        for(int i=0;;i++){
            if((doc=data.get(i))!=null)
                System.out.println(doc);
            else break;
        }
        System.out.println();
    }
    /**
     * TODO 获取服务器单个Message信息
     * @return
     */
    //接收信息
    public Message getData(){
        Message message=null;
        try{
            message=(Message)input.readObject();
            displayMessage("收到来自服务器如下数据");
            displayMessage(""+message.toString()+"\n");
        }catch(ClassNotFoundException|IOException e){
            e.toString();
            e.printStackTrace();
            displayMessage( "Error writing object in getData" );
        }finally {
            //closeConnection("getData IO连接关闭");
        }
        return message;
    }// end method getData
    //发送信息
    public void sendData(Message message) {
        try
        {
            output.writeObject( message);
            output.flush(); // flush output to client
           displayMessage( "CLIENT>>> " + message.toString() );
        } // end try
        catch ( IOException ioException )
        {
            ioException.toString();
            ioException.printStackTrace();
            displayMessage( "Error writing object in sendData" );
        } // end catch
        finally {
            //closeConnection("sentData IO连接关闭");
        }
    } // end method sendData

    /**
     *TODO 关闭连接
     */
    private void closeConnection(String  closeInfo) {
        displayMessage( closeInfo );
        try
        {
            if(output!=null)
            output.close(); // close output stream
            if(input!=null)
            input.close(); // close input stream
        } // end try
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        } // end catch
    } // end method closeConnection

    /**
     *TODO 终端显示信息
     * @param messageToDisplay
     */
    public static void displayMessage( final String messageToDisplay ) {
        System.out.println(messageToDisplay);
    }// end method displayMessage

    /**
     * Get IO Stream to deli
     */
    public ObjectInputStream getClientInput() {
        return input;
    }
    public ObjectOutputStream getClientOutput() {
        return output;
    }
    public DataInputStream getClientDataIn() {
        return dataIn;
    }
    public DataOutputStream getClientDataOut() {
        return dataOut;
    }
} // end class Client
