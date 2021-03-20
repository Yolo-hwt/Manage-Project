package server;

import base.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection extends Thread{
    /**
     *  volatile修饰符用来保证其它线程读取的总是该变量的最新的值
     *  程序连接标识
     */
    public static volatile boolean exit = true;

    //用户信息
    private String clientIP;
    private int clientPort;
    //端口连接
    private  int SERVER_PORT=31010;
    private Socket connection; // connection to client
    private ServerSocket serverSocket;
    private serverFunction function;
    private serverFrame serverFrame;
    //输入输出流
    private  ObjectOutputStream output; // output stream to client
    private  ObjectInputStream input; // input stream from client

    private DataInputStream din ;//File upload
    private DataOutputStream dout ;//File download

    //构造连接
    public ServerConnection(serverFrame serverFrame)throws IOException{
        serverSocket=new ServerSocket(SERVER_PORT,100);
        this.function=new serverFunction(ServerConnection.this);
        this.serverFrame=serverFrame;
    }

    /**
     * TODO 连接数据库
     */
    public void connectDataBase(){
        try{
            boolean result=DataProcessing.connectToDatebase(DataProcessing.driverName,DataProcessing.url,DataProcessing.user,DataProcessing.password);
            if(result)
                serverFrame.displayMessage("数据库已连接");
        }catch (Exception e){
            serverFrame.displayMessage("无法连接数据库");
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * 线程实现交互
     */
    public void run(){

            try{
                connectDataBase();
                waitForConnection();
                getStreams();
                processConnection();
            }catch (IOException e)
            {
                e.printStackTrace();
                e.toString();
                serverFrame.displayMessage( "Server terminated connection" );
            } // end catch
    }

    /**
     * 等待连接
     * @throws IOException
     */
    private void waitForConnection() throws IOException
    {
        connection=serverSocket.accept();
        //获取设置ip地址，端口号
        clientIP = connection.getInetAddress().getHostAddress();
        clientPort =connection.getLocalPort() ;
        System.out.println("服务器端口号连接为"+clientPort);
        serverFrame.getIpField().setText(clientIP);
        serverFrame.getPortField().setText(String.valueOf(clientPort));

        //提示信息
        serverFrame.displayMessage( "Connection received from: " +
                connection.getInetAddress().getHostName() );
    } // end method waitForConnection

    /**
     * 获取输入输出流
     * @throws IOException
     */
    private void getStreams() throws IOException
    {
        // set up Objectstream for objects
        output = new ObjectOutputStream( connection.getOutputStream() );
        input = new ObjectInputStream( connection.getInputStream() );
        output.flush();

        //set up DataStream for File
        din = new DataInputStream(connection.getInputStream());
        dout = new DataOutputStream(connection.getOutputStream());
        dout.flush();

        serverFrame.displayMessage( "Got I/O streams\n" );
    } // end method getStreams

    /**
     * process connection with client
     *
     *TODO 与客户端交互
     * @throws IOException
     */
    private void processConnection() throws IOException
    {
        Message message=new Message("连接成功！",null,"success");
        sendData( message ); // send connection successful message
        while(exit) //利用进程标识 exit 控制连接  exit=true 无限循环
        {
            try
            {
               if(Doc.fileisOk) {//文件标识为true时  无上传下载文件流占用  开始接收客户端信息
                   Message getmessage = getData();
                   String operation, Tip;
                   operation = getmessage.getOperation();

                   switch (OPERATION_ENUM.valueOf(operation)) {
                       //更改登录状态
                       case loginShift:
                           Tip = "loginShift comes from [ " + clientIP + " : " + clientPort + " ]\n";
                           serverFrame.disMessageinArea(Tip);

                           function.login_Shift(getmessage);
                           break;
                       //根据用户名查找用户
                       case searchUserByName:
                           Tip = "searchByN comes from [ " + clientIP + " : " + clientPort + " ]\n";
                           serverFrame.disMessageinArea(Tip);

                           function.searchBy_N(getmessage);
                           break;
                       //根据用户名密码查找用户 登录
                       case searchUserByNameAndPassword:
                           Tip = "searchByN and P comes from [ " + clientIP + " : " + clientPort + " ]\n";
                           serverFrame.disMessageinArea(Tip);

                           function.searchBy_NP(getmessage);
                           break;
                       //更改个人密码
                       case serverChangeSelfInfo:
                           Tip = "changeSelfInfo comes from [ " + clientIP + " : " + clientPort + " ]\n";
                           serverFrame.disMessageinArea(Tip);

                           function.serverUpdateUser(getmessage);
                           break;
                       //上传文件
                       case serverUpLoadFile:
                           Tip = "UpLoadFile comes from [ " + clientIP + " : " + clientPort + " ]\n";
                           serverFrame.disMessageinArea(Tip);

                           function.serverUpLoadFile(getmessage, din);
                           break;
                       //下载文件
                       case serverDownLoadFile:
                           Tip = "DownLoadFile comes from[ " + clientIP + " : " + clientPort + " ]\n";
                           serverFrame.disMessageinArea(Tip);

                           function.serverDownLoadFile(getmessage, dout);
                           break;
                       //添加用户
                       case serverAddUser:
                           Tip = "AdderUser comes from [ " + clientIP + " : " + clientPort + " ]\n";
                           serverFrame.disMessageinArea(Tip);

                           function.serverAdderUser(getmessage);
                           break;
                       //删除用户
                       case serverDelUser:
                           Tip = "DelUser comes from [ " + clientIP + " : " + clientPort + " ]\n";
                           serverFrame.disMessageinArea(Tip);

                           function.serverDelUser(getmessage);
                           break;
                       //更新用户
                       case serverUpdateUser:
                           Tip = "UpdateUser comes from [ " + clientIP + " : " + clientPort + " ]\n";
                           serverFrame.disMessageinArea(Tip);

                           function.serverUpdateUser(getmessage);
                           break;
                       //获取文档列表
                       case serverGetDocList:
                           Tip = "GetDocList comes from [ " + clientIP + " : " + clientPort + " ]\n";
                           serverFrame.disMessageinArea(Tip);

                           function.serverGetDataList(getmessage, output);
                           break;
                       //获取用户列表
                       case serverGetUserList:
                           Tip = "GetUserList comes from [ " + clientIP + " : " + clientPort + " ]\n";
                           serverFrame.disMessageinArea(Tip);

                           function.serverGetDataList(getmessage, output);
                           break;
                       default:
                           break;
                   }
               }
            } // end try
            catch ( ClassNotFoundException classNotFoundException )
            {
                serverFrame.displayMessage( "Unknown object type received" );
            } // end catch
        }
    } // end method processConnection

    /** close streams and socket
     * 关闭连接
     */
    public void closeStreams(String closeInfo)
    {
        serverFrame.displayMessage( closeInfo );
        try
        {
            output.close(); // close output stream
            input.close(); // close input stream

        } // end try
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        } // end catch
    } // end method closeStreams

    /** send ro get message via client
     * 接发信息经由客户端
     * @param message
     */
    public  void sendData(Message message)
    {
        try
        {
            output.writeObject(  message );
            output.flush(); // flush output to client
            serverFrame.displayMessage( "SERVER>>> " + message +"\n");
        } // end try
        catch ( IOException ioException )
        {
            ioException.toString();
            ioException.printStackTrace();
            serverFrame.displayMessage( "Error writing object in sendData" );
        } // end catch
        finally {
            //closeStreams("sendData IO close");
        }
    } // end method sendData

    public  Message getData()throws ClassNotFoundException,IOException
    {
        Message message=null;
        try {
            //getStreams();
            message = (Message) input.readObject();
            serverFrame.displayMessage("收到来客户端信息：");
            serverFrame.displayMessage((message.toString()));

        }catch(IOException ioException){
            ioException.printStackTrace();
            ioException.toString();
            serverFrame.displayMessage( "Error writing object in getData" );
        }finally {
            //closeStreams("sendData IO close");
        }
        return message;
    } // end method getData

    /**枚举操作集
     *
     */
    public static enum OPERATION_ENUM{
            //更改登录状态
            loginShift("loginShift"),
            //按用户名查找用户
            searchUserByName("searchUserByName"),
            //按用户名密码查找用户
            searchUserByNameAndPassword("searchUsrByNameAndPassword"),
            //上传文件
            serverUpLoadFile("upLoadFile"),
            //下载文件
            serverDownLoadFile("downLoadFile"),
            //增加用户
            serverAddUser("serverAddUser"),
            //删除用户
            serverDelUser("serverDelUser"),
            //更新用户
            serverUpdateUser("serverUpdateUser"),
            //更改个人密码
            serverChangeSelfInfo("serverChangeSelfInfo"),
            //获取用户列表
            serverGetUserList("serverGetUserList"),
            //获取档案
            serverGetDocList("serverGetDocList");

            private String operation;
            OPERATION_ENUM(String operation) {
                this.operation = operation;
            }
            public String getOperation() {
                return operation;
            }
        }
    }
