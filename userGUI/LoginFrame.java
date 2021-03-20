package userGUI;

import base.*;
import client.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame{
    //登录面板
    public JPanel loginPanel;
    private JPanel loginFrame_Body;
    private JPanel loginFrame_Button;
    //信息输入
    private JTextField loginNameField;
    private JPasswordField loginPsdField;
    //按钮及标签
    private JButton loginButton;//登录
    private JButton exitButton;//退出
    private JLabel loginFrame_Title;
    private JLabel loginName;
    private JLabel loginPsd;

    /**
     * TODO 启动loginFrame
     */
    public static void runFrame(){
        new Thread(() -> {
            //进入登录界面
            LoginFrame frame = new LoginFrame();
            frame.setTitle("登录页面");
            frame.setContentPane(frame.loginPanel);
            frame.setLocationRelativeTo(null);//屏幕当中打开
            frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }).start();
    }

    public LoginFrame() {
    /**
     * TODO 按钮事件响应
     * login_Button
     */
        //登录按钮
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                        LoginPerformed(e);
            }
        });
        //退出按钮
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                        ExitActionPerformed(e);
            }
        });

    }//public LoginFrame()


    /**
     * TODO 清空文本框
     */
    private void Clear_Login(){
        loginNameField.setText("");
        loginPsdField.setText("");
    }
    /**
     * TODO 登录按钮相应
     * Login_Button Performed
     * @param evt
     */
    private void LoginPerformed(ActionEvent evt){
        String username = this.loginNameField.getText();
        String password = new String(this.loginPsdField.getPassword()); // 获取输入内容

        if (username==null||"".equals(username)) {
            JOptionPane.showMessageDialog(null, "未输入用户名！"); // 显示对话框
            Clear_Login();
            return;
        }
        if (password==null||"".equals(password)) {
            JOptionPane.showMessageDialog(null, "未输入密码！"); // 显示对话框
            Clear_Login();
            return;
        }

        try {
            AbstractUser loginuser=new AbstractUser(username,password,"","");
            String operation="searchUserByNameAndPassword";
            String state="fail";
            Message message=new Message(operation,loginuser,state);//向服务器发送请求
            client.Myclient.sendData(message);
            Message result=  client.Myclient.getData();

            if (result == null||result.getState().equals("fail"))
            {
                JOptionPane.showMessageDialog(null, "用户名与密码不匹配！"); // 显示对话框
                Clear_Login();
                return;
            }
            else if(result.getState().equals("relogin"))
            {
                // 显示确认界面: 信息, 标题, 选项个数
                int value = JOptionPane.showConfirmDialog
                        (null, "是否退出当前登录？", "重复登录", 2);
                // Yes=0 No=1
                if (value == 0) {
                    Message loginshift=new Message("",null,"");
                    AbstractUser temp=(AbstractUser) result.getData();
                    loginshift.setOperation("loginShift");
                    AbstractUser shift =new AbstractUser(temp.getName(),temp.getPassword(),temp.getRole(),"off");
                    loginshift.setData(shift);
                    loginshift.setState("fail");
                    client.Myclient.sendData(loginshift);
                    Message getresult = client.Myclient.getData();
                    if (getresult.getState().equals("success")) {
                        JOptionPane.showMessageDialog(null, "当前登录已退出！请重新登录");
                      Clear_Login();
                    }
                }else Clear_Login();
            }
            else
            {
                // 导入用户
                AbstractUser user = (AbstractUser) result.getData();
                if(user.getRole().equalsIgnoreCase("boss"))
                    JOptionPane.showMessageDialog(null, "欢迎Boss,随便修改！！！");
                // 令当前界面消失
                this.dispose();
                // 跳转至主界面,新建对象并传入用户参数
                MainFrame.runMainFrame(user);
            }//else
        }//try
        catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
        }
    }//end LoginFrame

    /**
     * TODO 退出按钮相应
     * Exit_Button Performed
     * @param e
     */
    private void ExitActionPerformed(ActionEvent e) {
        DataProcessing.disconnectFromDataBase();
       System.exit(1);
    }
}
