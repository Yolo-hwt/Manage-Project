package userGUI;

import base.*;
import client.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SelfPsdFrame extends JFrame{
    /**
     * 个人信息面板组件
     */
    public JPanel SelfInfoPanel;
    private JPanel SelfInfo_Input;
    //标签及按钮
    private JLabel SelfInfo_LastPsd;
    private JLabel SelfInfo_NewPas;
    private JLabel SelfInfo_Title;
    private JLabel SelfInfo_ComfirmPsd;
    private JButton SelfInfo_Ok;
    private JButton SelfInfo_Exit;
    //信息输入
    private JPasswordField Self_LastPsdField;
    private JPasswordField Self_NewPsdField;
    private JPasswordField Self_ComfirmPsdField;
    private AbstractUser user;

    public SelfPsdFrame(AbstractUser user){
        this.user=user;
        /**
         * TODO 按钮事件响应
         * 按钮事件
         */
        //确认修改按钮
        SelfInfo_Ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 修改密码事件
                ChangeSelfInfoActionPerformed(user, e);
            }
        });
        // 返回按钮
        SelfInfo_Exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 返回事件
                ReturnActionPerformed(e);
            }
        });
    }//public SelfPsdFrame()


     //清空文本框
    private void Clear_Self(){
        this.Self_LastPsdField.setText("");
        this.Self_NewPsdField.setText("");
        this.Self_ComfirmPsdField.setText("");
    }

    /**
     * TODO 修改个人信息 实现
     * @param user
     * @param evt
     */
    private void ChangeSelfInfoActionPerformed(AbstractUser user, ActionEvent evt) {
        String lastpassword = new String(Self_LastPsdField.getPassword()).trim();//原密码
        String newpassword = new String(Self_NewPsdField.getPassword()).trim();//新密码
        String confirmpassword = new String(Self_ComfirmPsdField.getPassword()).trim();//确认密码
        // 检查是否为空
        if (lastpassword==null||"".equals(lastpassword)) {
            MainFrame.showMessage(null, "未输入旧密码！","提示信息");
            Clear_Self();
            return;
        }if (newpassword==null||"".equals(newpassword)) {
            MainFrame.showMessage(null, "未输入新密码！","提示信息");
            Clear_Self();
            return;
        }if (confirmpassword==null||"".equals(confirmpassword)) {
            MainFrame.showMessage(null, "请输入确认密码！","提示信息");
            Clear_Self();
            return;
        }
        // 新旧密码匹配
        try {
            AbstractUser userTemp=new AbstractUser(user.getName(),"","","");
            String operation="searchUserByName";
            String state="fail";
            Message message=new Message(operation,userTemp,state);
            //向服务器发送请求获取原数据
            client.Myclient.sendData(message);
            Message result= (Message) client.Myclient.getData();

            //提取用户信息
            AbstractUser lastUser=(AbstractUser) result.getData();
            if (!(lastUser.getPassword().equals(lastpassword))) {
                MainFrame.showMessage(null, "原密码输入错误！","提示信息");
                Clear_Self();
                return;
            }if (!newpassword.equals(confirmpassword)) {
                MainFrame.showMessage(null, "两次输入的密码不相同！","提示信息");
                Clear_Self();
                return;
            }

            //信息填充
            Message upmessage=new Message("serverUpdateUser",
                    new AbstractUser(user.getName(), newpassword, user.getRole(),""),
                    "fail");
            //发送信息请求修改密码
            client.Myclient.sendData(upmessage);
            //获取结果
            Message backdata = client.Myclient.getData();

            if (backdata.getState().equals("success")) {
                MainFrame.showMessage(null, "修改成功!","提示信息");
                Clear_Self();
                return;
            }else {
                MainFrame.showMessage(null, "修改失败!","提示信息");
                Clear_Self();
                return;
            }
            
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
        }
    }

    /**
     * TODO 返回 实现
     * @param evt
     */
    private void ReturnActionPerformed(ActionEvent evt) {
        this.dispose(); //隐藏并释放窗体
        MainFrame.runMainFrame(this.user);
    }
}
