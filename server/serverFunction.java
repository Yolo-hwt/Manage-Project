package server;
import base.*;

import javax.swing.*;
import java.io.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;

public class serverFunction {

    private ServerConnection serverConnection;//传入serverConnection实例
    public serverFunction(ServerConnection serverConnection){
        this.serverConnection=serverConnection;
    }

    /**
     * TODO 更改登录状态
     * @param message
     */
    public void login_Shift(Message message){
        AbstractUser user = (AbstractUser) message.getData();
        String name = user.getName();
        String login=user.getLogin();
        boolean result=false;
        try {
            result = DataProcessing.upLogin(name,login);
            if (!result)
                message.setState("fail");
            else
                message.setState("success");
            message.setData(user);
            message.setOperation("loginShift");
            serverConnection.sendData(message);//send result to client

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * TODO 按用户名and密码搜索用户
     * @param message
     */
    public void searchBy_NP(Message message) {
        AbstractUser user = (AbstractUser) message.getData();
        String name = user.getName();
        String password = user.getPassword();
        try {
            user = DataProcessing.search(name, password);
            if (user == null)
                message.setState("fail");
            else if(user.getLogin().equals("on"))
                message.setState("relogin");//重复登录
            else {
                message.setState("success");
                DataProcessing.upLogin(name,"on");//更改标识为登录状态
            }
            message.setData(user);
            message.setOperation("searchUserByNamePassword");
            serverConnection.sendData(message);//send result to client

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO 按用户名搜索用户
     * @param message
     */
    public  void searchBy_N(Message message){
        AbstractUser user = (AbstractUser) message.getData();
        String name = user.getName();
        try {
            user = DataProcessing.searchUser(name);
            if (user == null)
                message.setState("fail");
            else
                message.setState("success");

            message.setData(user);
            message.setOperation("searchUserByName");
            serverConnection.sendData(message);//send result to client

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO 处理管理员增添用户
     * @param message
     */
    public void serverAdderUser(Message message){
        AbstractUser user = (AbstractUser) message.getData();
        String name = user.getName();
        String password=user.getPassword();
        String role=user.getRole();
        boolean result;
        try {
            result=DataProcessing.insertUser(name,password,role);
            if (!result)
                message.setState("fail");
            else
                message.setState("success");

            message.setData(user);
            message.setOperation("serverAdderUser");

            serverConnection.sendData(message);//send result to client

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO 修改用户 "AND" 修改个人密码
     * @param message
     */
    public void serverUpdateUser(Message message){
        AbstractUser user = (AbstractUser) message.getData();
        String name = user.getName();
        String password=user.getPassword();
        String role=user.getRole();
        boolean result;
        try {
            result=DataProcessing.update(name,password,role);
            if (!result)
                message.setState("fail");
            else
                message.setState("success");

            message.setData(user);
            message.setOperation("serverUpdateUser");

            serverConnection.sendData(message);//send result to client

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO 处理管理员删除用户
     * @param message
     */
    public void serverDelUser(Message message){
        AbstractUser user = (AbstractUser) message.getData();
        String name = user.getName();
        boolean result;
            try {
            result=DataProcessing.deleteUser(name);
            if (!result)
                message.setState("fail");
            else
                message.setState("success");

            message.setData(user);
            message.setOperation("serverDelUser");

            serverConnection.sendData(message);//send result to client

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**TODO 处理客户端上传文件
     *
     * @param message
     * @param din
     */
    public  void serverUpLoadFile(Message message, DataInputStream din){
        Doc doc = (Doc) message.getData();
        String id=doc.getId();//文件id
        String creator=doc.getCreator();//文件创建者
        Timestamp timestamp=doc.getTimestamp();//文件创建时间
        String description=doc.getDescription();//文件描述
        String filename=doc.getFilename();//文件名
        boolean result;

        try {
            System.out.print("等待客户端上传: ");
            Doc.WaittoDo();System.out.println("\n客户端上传完成！");

            //阻断processConnection占用流接收信息
            Doc.fileisOk=false;
            ServerReceive sr=new ServerReceive(filename,din);//服务器接收来自客户端文件
            sr.run();

            System.out.print("等待服务器接收: ");
           Doc.WaittoDo();System.out.println("\n服务器接收完成！");

            //向客户端发送信息
            if(Doc.fileisOk) {
                result = DataProcessing.insertDoc(id, creator, timestamp, description, filename);//数据库插入信息
                if (!result)
                    message.setState("fail");
                else
                    message.setState("success");

                message.setData(null);
                message.setOperation("serverUpLoadFile");
                serverConnection.sendData(message);//send result to client
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO 处理客户端下载文件
     * @param message
     * @param out
     */
    public  void serverDownLoadFile(Message message,DataOutputStream out){
        Doc doc = (Doc) message.getData();
        Doc temp=null;
        //若doc拥有id则为表格内部文件
        if(!doc.getId().equals("")&&"".equals(doc.getFilename())){
            try{
                temp = DataProcessing.searchDoc(doc.getId());
                if (temp != null)
                    message.setState("success");
                else
                    message.setState("fail");
                message.setOperation("serverDownLoadFile(数据库表格内部下载 正在处理...)");
                message.setData(temp);
                serverConnection.sendData(message);//send result to client

                //阻断processConnection占用流接收信息
                Doc.fileisOk=false;
                new ServerSend(temp.getFilename(),out,1).run();//发送下载文件至客户端
                System.out.println("下载传输完毕");
            }catch(SQLException e){
                System.out.println(e.toString());
            }
        }else{//任意模式下载
            message.setState("success");
            message.setOperation("serverDownLoadFile(任意模式下载 正在处理...)");
            message.setData(temp);
            serverConnection.sendData(message);//send result to client

            //选择表格之外文件下载    此时FileName存放下载文件路径
            String downloadFilePath= doc.getFilename();
            //阻断processConnection占用流接收信息
            Doc.fileisOk=false;
            new ServerSend(downloadFilePath,out,2).run();//发送下载文件至客户端
        }
    }

    /**
     * TODO 处理用户获取数据库列表
     * @param message
     * @param dataToclient
     */
    public  void serverGetDataList(Message message, ObjectOutputStream dataToclient){
        // 获取数据库信息
        try {
            //获取档案列表
            if(message.getOperation().equals("serverGetDocList")){
                Enumeration<Doc> result= DataProcessing.listDoc();

                if(result!=null){
                    //发送信息至客户端
                    message.setState("success");
                    message.setOperation("serverGetDocList(已获取档案列表！)");
                    serverConnection.sendData(message);//send result to client
                    ArrayList<Doc>Datalist=new ArrayList<>(GetDocNum());
                    // 将数据库信息导入动态数组
                    while (result.hasMoreElements()) {
                        Doc temp = result.nextElement();
                        Datalist.add(temp);
                    }
                    Datalist.add(null);
                    PrintDoc(Datalist);
                    //发送结果至客户端
                    dataToclient.writeObject(Datalist);
                    dataToclient.flush();
                }
            }
            //获取用户列表
           if(message.getOperation().equals("serverGetUserList")){
               Enumeration<AbstractUser> result= DataProcessing.listUser();
               if(result!=null){

                   //发送信息至客户端
                   message.setState("success");
                   message.setOperation("serverGetUserList(已获取用户列表！)");
                   serverConnection.sendData(message);//send result to client

                   int userNum=GetUserNum();
                   ArrayList<AbstractUser>Datalist=new ArrayList<>(userNum);

                   // 将数据库信息导入动态数组
                   while (result.hasMoreElements()) {
                       AbstractUser temp = result.nextElement();
                       Datalist.add(temp);
                   }
                    Datalist.add(null);
                   PrintUser(Datalist);
                   //发送结果至客户端
                   dataToclient.writeObject(Datalist);
                   dataToclient.flush();
               }

           }

        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
        }
    }

    /**
     * 获取数据库信息辅助函数
     * @return
     */
    //获取信息条数
    private int GetUserNum(){
        int countUser=0;
        try {
            Enumeration<AbstractUser> result= DataProcessing.listUser();
            if(result!=null){
                while(result.hasMoreElements()){
                   AbstractUser user=result.nextElement();
                    countUser++;
                }
                //////////test
                System.out.println("用户数量"+countUser);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return countUser;
    }
    private int GetDocNum(){
        int countDoc=0;
        try {
            Enumeration<Doc> result= DataProcessing.listDoc();
            if(result!=null){
                while(result.hasMoreElements()){
                    Doc doc=result.nextElement();
                    countDoc++;
                }
                //////////test
                System.out.println("文件数量"+countDoc);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return countDoc;
    }
    //打印信息
    private void PrintUser(ArrayList<AbstractUser>data){
        System.out.println("获得数据库用户信息:");
        AbstractUser user=null;
        for(int i=0;;i++){
            if((user=data.get(i))!=null)
                System.out.println(user);
            else break;
        }
        System.out.println();
    }
    private void PrintDoc(ArrayList<Doc>data){
        System.out.println("获得数据库文档信息:");
        Doc doc=null;
        for(int i=0;;i++){
            if((doc=data.get(i))!=null)
                System.out.println(doc);
            else break;
        }
        System.out.println();
    }

}//end serverFunction class
