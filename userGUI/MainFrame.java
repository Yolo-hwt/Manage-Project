package userGUI;

import base.*;
import client.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    //用户菜单
    JMenu userMenu;
    JMenuItem addUser;//添加用户
    JMenuItem updateUser;//更新用户信息
    JMenuItem delUser;//删除用户
    // 文档管理
    JMenu DocMenu;
    JMenuItem updateDoc;//上传文件
    JMenuItem downloadDoc;//下载文件
    // 个人信息
    JMenu selfMenu;
    JMenuItem changePassword;//更改个人密码
    JMenuItem exitMenu;//退出菜单

    private AbstractUser user;
    public MainFrame(AbstractUser user) throws HeadlessException {
        this.user=user;
//用户操作菜单
        JMenuBar menuBar=new JMenuBar();
        setJMenuBar(menuBar);
//用户管理
        userMenu=new JMenu("用户管理");
        menuBar.add(userMenu);
        addUser=new JMenuItem("新增用户");
        updateUser=new JMenuItem("更新用户");
        delUser=new JMenuItem("删除用户");
        userMenu.add(addUser);
        userMenu.add(updateUser);
        userMenu.add(delUser);
//文档管理
        DocMenu =new JMenu("文档管理");
        menuBar.add(DocMenu);
        updateDoc =new JMenuItem("上传文档");
        downloadDoc =new JMenuItem("下载文档");
        DocMenu.add(downloadDoc);
        DocMenu.add(updateDoc);
//个人信息
        selfMenu=new JMenu("个人信息");
        menuBar.add(selfMenu);
        exitMenu=new JMenuItem("退出登录");
        changePassword=new JMenuItem("更改个人密码");
        selfMenu.add(changePassword);
        selfMenu.add(exitMenu);
//设置权限
        SetRights(user.getRole());
// 增添用户事件
        addUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddUserActionPerformed(user, e);
            }
        });
// 删除用户事件
        delUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DelUserActionPerformed(user, e);
            }
        });
// 修改用户事件
        updateUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UpdateUserActionPerformed(user, e);
            }
        });
// 上传文件事件
        updateDoc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UploadFileActionPerformed(user, e);
            }
        });
// 下载文件事件
        downloadDoc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DownloadFileActionPerformed(user, e);
            }
        });
// 修改密码事件
        changePassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChangeSelfActionPerformed(user, e);
            }
        });
// 退出登录事件
        exitMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ExitActionPerformed(e);
            }
        });
    }

    /**提示窗口（全局）
     *
     * @param component
     * @param msg
     * @param title
     */
    public static void showMessage(Component component, String msg, String title) {
        JOptionPane.showMessageDialog(component, msg, title, JOptionPane.YES_NO_OPTION);
    }

    /**
     * 启动MainFrame
     * @param user
     */
     public static void runMainFrame(AbstractUser user){
         new Thread(() -> {
             //进入登录界面
             MainFrame mainframe = new MainFrame(user);
             mainframe.setVisible(true);
             mainframe.setLocationRelativeTo(null);//屏幕当中打开
             mainframe.setBounds(200,200,300,200);
             //仅关闭当前窗口setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
             mainframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         }).start();
    }

    /**
     * 设置对应Frame界面参数
     * @param userframe
     */
    private void SetUserFrame(UserFrame userframe){
        userframe.setVisible(true);
        userframe.pack();
        userframe.setLocationRelativeTo(null);
        userframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    private void SetSelfPsdFrame(SelfPsdFrame selfframe){
        selfframe.setVisible(true);
        selfframe.pack();
        selfframe.setLocationRelativeTo(null);
        selfframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    private void SetDocFrame(DocFrame docframe){
        docframe.setVisible(true);
        docframe.pack();
        docframe.setLocationRelativeTo(null);
        docframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * addUser
     * @param user
     * @param evt
     */
    private void AddUserActionPerformed(AbstractUser user, ActionEvent evt) {
        // 选项编号 0
        new Thread(()->{
            this.dispose();
            UserFrame userframe;
            userframe = new UserFrame(user,0);
            userframe.setTitle("用户管理");
            userframe.setContentPane(userframe.UserMenuTable);
            SetUserFrame(userframe);
        }).start();
    }

    /**
     * delUser
     * @param user
     * @param evt
     */
    private void DelUserActionPerformed(AbstractUser user, ActionEvent evt) {
        // 选项编号 1
        new Thread(()->{
            this.dispose();
            UserFrame userframe = new UserFrame(user,1);
            userframe.setTitle("用户管理");
            userframe.setContentPane(userframe.UserMenuTable);
            SetUserFrame(userframe);
        }).start();
    }

    /**
     * updateUser
     * @param user
     * @param evt
     */
    private void UpdateUserActionPerformed(AbstractUser user, ActionEvent evt) {
        // 选项编号 2
        new Thread(()->{
            this.dispose();
            UserFrame userframe = new UserFrame(user,2);
            userframe.setTitle("用户管理");
            userframe.setContentPane(userframe.UserMenuTable);
            SetUserFrame(userframe);
        }).start();
    }

    /**
     * updateDoc
     * @param user
     * @param evt
     */
    private void UploadFileActionPerformed(AbstractUser user, ActionEvent evt) {
        new Thread(()->{
            this.dispose();
            DocFrame docframe = new DocFrame(user,0);
            docframe.setTitle("文档管理");
            docframe.setContentPane(docframe.docMenuPanel);
            SetDocFrame(docframe);
        }).start();
    }

    /**
     * dowloadDoc
     * @param user
     * @param evt
     */
    private void DownloadFileActionPerformed(AbstractUser user, ActionEvent evt) {
        new Thread(()->{
            this.dispose();
            DocFrame docframe = new DocFrame(user,1);
            docframe.setTitle("文档管理");
            docframe.setContentPane(docframe.docMenuPanel);
            SetDocFrame(docframe);
        }).start();
    }

    /**
     * changePassword
     * @param user
     * @param evt
     */
    private void ChangeSelfActionPerformed(AbstractUser user, ActionEvent evt) {
        new Thread(()->{
            this.dispose();
            SelfPsdFrame selfframe = new SelfPsdFrame(user);
            selfframe.setTitle("个人密码修改");
            selfframe.setContentPane(selfframe.SelfInfoPanel);
            SetSelfPsdFrame(selfframe);
        }).start();
    }

    /**
     * 设置用户权限
     * @param role
     */
    private void SetRights(String role) {

        if (role.equalsIgnoreCase("administrator")) {

            addUser.setEnabled(true);
            delUser.setEnabled(true);
            updateUser.setEnabled(true);
            changePassword.setEnabled(true);
            updateDoc.setEnabled(false);
            downloadDoc.setEnabled(true);
            exitMenu.setEnabled(true);

        } else if (role.equalsIgnoreCase("browser")) {

            addUser.setEnabled(false);
            delUser.setEnabled(false);
            updateUser.setEnabled(false);
            updateDoc.setEnabled(false);
            downloadDoc.setEnabled(true);
            changePassword.setEnabled(true);
            exitMenu.setEnabled(true);

        } else if (role.equalsIgnoreCase("operator")) {

            addUser.setEnabled(false);
            delUser.setEnabled(false);
            updateUser.setEnabled(false);
            updateDoc.setEnabled(true);
            downloadDoc.setEnabled(true);
            changePassword.setEnabled(true);
            exitMenu.setEnabled(true);
        } else if (role.equalsIgnoreCase("boss")) {

            addUser.setEnabled(true);
            delUser.setEnabled(true);
            updateUser.setEnabled(true);
            changePassword.setEnabled(true);
            updateDoc.setEnabled(true);
            downloadDoc.setEnabled(true);
            exitMenu.setEnabled(true);
        }
    }//end setright
    /**
     * 退出程序（返回按钮）
     * @param evt
     */
    private void ExitActionPerformed(ActionEvent evt) {
        String operation="loginShift";
        String state="fail";
        this.user.setLogin("off");
        Message message=new Message(operation,this.user,state);//向服务器发送请求
        client.Myclient.sendData(message);
        Message result=  client.Myclient.getData();
        new Thread(() -> {
            //关闭当前界面，打开登录界面
            this.dispose();
            //进入登录界面
            LoginFrame frame = new LoginFrame();
            frame.setTitle("登录页面");
            frame.setContentPane(frame.loginPanel);
            frame.setLocationRelativeTo(null);//屏幕当中打开
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }).start();
    }
}//end MainFrame class

