package userGUI;

import base.*;
import client.client;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class UserFrame extends JFrame {
    /**
     * 面板组件
     */
    // 用户列表
    private JTable Users_table;
    //用户操作表（添加，删除，更新）
    public JTabbedPane UserMenuTable;
    //添加用户
    private JPanel addUser;
    private JTextField addNameField;
    private JPasswordField addPasswordField;
    private JComboBox addroleComboBox;
    private JLabel addName;
    private JLabel addPsd;
    private JLabel addRole;
    private JButton addOk;
    private JButton addExit;
    //删除用户
    private JPanel delUser;
    private JButton delOk;
    private JButton delExit;
    private JScrollPane delUserZone;//用户信息容器
    //更改用户
    private JPanel updateUser;
    private JPanel UserMenuPanel;
    private JTextField updateNameField;
    private JPasswordField updatePsdField;
    private JComboBox updateRoleBox;
    private JLabel updateName;
    private JLabel updatePsd;
    private JLabel updateRole;
    private JButton updateOk;
    private JButton updateExit;
    private JComboBox updateNameBox;

    private AbstractUser user;
    /**
     * TODO 构建用户界面
     * Achieve Frame
     */
    public UserFrame(AbstractUser user, int choice) {
        this.user=user;
        //主界面
        UserMenuTable.setTitleAt(0, "新增用户");
        UserMenuTable.setTitleAt(1, "修改用户");
        UserMenuTable.setTitleAt(2, "删除用户");
        /**
         * addUser
         */
//addUser角色复选框
        if(user.getRole().equalsIgnoreCase("boss"))
        addroleComboBox.setModel(new DefaultComboBoxModel(new String[]{"", "Administrator", "Browser", "Operator","Boss"}));
        else addroleComboBox.setModel(new DefaultComboBoxModel(new String[]{"", "Administrator", "Browser", "Operator"}));
        //addUser按钮
        //确定按钮
        addOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 增添用户事件
                AddUserActionPerformed(user, e);
            }
        });
        //返回按钮
        addExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 返回事件
                ReturnActionPerformed(e);
            }
        });

        /**
         * updateUser
         *
         */
//updateUser角色复选框
        if(user.getRole().equalsIgnoreCase("boss"))
        updateRoleBox.setModel(new DefaultComboBoxModel(new String[]{"", "Administrator", "Browser", "Operator","Boss"}));
        else updateRoleBox.setModel(new DefaultComboBoxModel(new String[]{"", "Administrator", "Browser", "Operator"}));
//updateUser按钮
        /*确定按钮*/
        updateOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 修改用户事件
                UpdateUserActionPerformed(user, e);
            }
        });
        /* 返回按钮*/
        updateExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 返回事件
                ReturnActionPerformed(e);
            }
        });

        /**
         * delUser
         *
         */
//delUSer按钮
        //确定按钮
        delOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 删除事件
                DelUserActionPerformed(user, e);
            }
        });
        //返回按钮
        delExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 返回事件
                ReturnActionPerformed(e);
            }
        });

        // 删除用户_信息容器
        Users_table = new JTable();
        // 构造用户信息表格//updateUser用户名复选框
        ConstructUserTable();
        // 表格插入删除信息选择容器
        delUserZone.setViewportView(Users_table);
        //跳转choice所指定界面
        SetPane(choice);
    }//end build UserFrame

     //清空文本框
    private void Clear_add() {
        addNameField.setText("");
        addPasswordField.setText("");
        addroleComboBox.setSelectedIndex(0);
    }
    private void Clear_update() {
        updateNameBox.setSelectedIndex(0);
        updatePsdField.setText("");
        updateRoleBox.setSelectedIndex(0);
    }

    /**
     * TODO 处理addUser确认按钮事件
     * addUser performed
     *
     * @param user
     * @param e
     */
    private void AddUserActionPerformed(AbstractUser user, ActionEvent e) {

        // 获取选项内容
        String username = this.addNameField.getText();
        String password = new String(this.addPasswordField.getPassword());
        String role = (String) this.addroleComboBox.getSelectedItem();

        if (username == null || "".equals(username)) {
            JOptionPane.showMessageDialog(null, "未输入用户名！");
            Clear_add();
            return;
        }
        if (password == null || "".equals(password)) {
            JOptionPane.showMessageDialog(null, "未输入密码！");
            Clear_add();
            return;
        }
        if (role == null || "".equals(role)) {
            JOptionPane.showMessageDialog(null, "未选择身份！");
            Clear_add();
            return;
        }

        try {
            Message message = new Message("",null,"");
            message.setData(new AbstractUser(username, password, role,""));
            message.setOperation("serverAddUser");
            message.setState("fail");
            //发送请求
            client.Myclient.sendData(message);
            Message result = client.Myclient.getData();//获取结果
            AbstractUser usertemp = (AbstractUser) result.getData();

            if (usertemp != null && result.getState().equals("success")) {
                // 更新表格数据//updateUser用户名复选框更新
                ConstructUserTable();
                MainFrame.showMessage(null, "添加成功！", "提示信息");
                Clear_add();
                return;
            } else {
                MainFrame.showMessage(null, "添加失败！用户名已存在！", "提示信息");
                Clear_add();
                return;
            }
        } catch (HeadlessException error) {
            JOptionPane.showMessageDialog(null, error.getLocalizedMessage());
        }

    }

    /**
     * TODO 处理delUser确认按钮事件
     * delUser performed
     *
     * @param user
     * @param e
     */
    private void DelUserActionPerformed(AbstractUser user, ActionEvent e) {
        // 获取所选行序号,若未选择其值为-1
        int selectedrow = Users_table.getSelectedRow();
        // 未选择用户的情况
        if (selectedrow == -1) {
            JOptionPane.showMessageDialog(null, "未选择用户！");
            return;
        } else {
            // 获取所选行的用户名
            String username = (String) Users_table.getValueAt(selectedrow, 0);
            String role=(String) Users_table.getValueAt(selectedrow, 2);
            // 若选择空行
            if (username == null || "".equals(username)) {
                return;
            }
            // 选择自身用户的情况
            if (username.equals(user.getName())) {
                JOptionPane.showMessageDialog(null, "不能删除自身用户！");
                return;
            }
            if(!user.getRole().equalsIgnoreCase("boss")&&role.equalsIgnoreCase("boss")){
                JOptionPane.showMessageDialog(null, "No Root to del,It is a BOSS!!!respect");
                return;
            }
            // 显示确认界面: 信息, 标题, 选项个数
            int value = JOptionPane.showConfirmDialog
                    (null, "确定要删除用户吗？", "用户删除确认界面", 2);
            // Yes=0 No=1
            if (value == 0) {
                Message message = new Message("",null,"");
                    message.setData(new AbstractUser(username, "", "",""));
                    message.setOperation("serverDelUser");
                    message.setState("fail");

                    //接收结果
                    client.Myclient.sendData(message);
                    Message result = client.Myclient.getData();
                    if (result.getState().equals("success")) {
                        // 更新表格数据//updateUser用户名复选框更新
                        ConstructUserTable();
                        JOptionPane.showMessageDialog(null, "删除成功！");
                        return;
                    } else {
                        JOptionPane.showMessageDialog(null, "删除失败！");
                        return;
                    }

            } else if (value == 1) {
                return;
            }
        }
    }

    /**
     * TODO 处理updateUser确认按钮事件
     * updateUser performed
     *
     * @param user
     * @param e
     */
    private void UpdateUserActionPerformed(AbstractUser user, ActionEvent e) {
        String username = (String) this.updateNameBox.getSelectedItem();
        String password = new String(this.updatePsdField.getPassword());
        String role = (String) this.updateRoleBox.getSelectedItem();

        if (username == null || "".equals(username)) {
            JOptionPane.showMessageDialog(null, "未选择用户！");
            Clear_update();
            return;
        }
        if (password == null || "".equals(password)) {
            JOptionPane.showMessageDialog(null, "未输入密码！");
            Clear_update();
            return;
        }
        if (role == null || "".equals(role)) {
            JOptionPane.showMessageDialog(null, "未选择身份！");
            Clear_update();
            return;
        }
        try {
            Message message = new Message("",null,"");
            message.setData(new AbstractUser(username, password, role,""));
            message.setOperation("serverUpdateUser");
            message.setState("fail");
            // 显示确认界面：信息，标题，选项个数
            int value = JOptionPane.showConfirmDialog
                    (null, "确定要修改信息吗？", "信息修改确认界面", 2);
            // Yes=0 No=1
            if (value == 0) {
                //发送信息请求修改
                client.Myclient.sendData(message);
                //获取结果
                Message result = client.Myclient.getData();
                if (result.getData()!=null && result.getState().equals("success")) {
                    ConstructUserTable(); // 更新表格数据
                    if(user.getName().equalsIgnoreCase(username)&&!user.getRole().equalsIgnoreCase(role)){
                        if(user.getRole().equalsIgnoreCase("administrator"))
                        MainFrame.showMessage(null, "修改成功！但您已不是管理员", "提示信息");
                        if(user.getRole().equalsIgnoreCase("boss"))
                            MainFrame.showMessage(null, "修改成功！但您已不是Boss", "提示信息");
                        AbstractUser newuser=new AbstractUser(user.getName(),password,role,"");
                        MainFrame.runMainFrame(newuser);
                    }

                    MainFrame.showMessage(null, "修改成功！", "提示信息");
                    Clear_update();
                    return;
                } else {
                    MainFrame.showMessage(null, "修改失败！", "提示信息");
                    Clear_update();
                    return;
                }
            } else if (value == 1) {
                Clear_update();
                return;
            }
        } catch (HeadlessException exp) {
            JOptionPane.showMessageDialog(null, exp.getLocalizedMessage());
        }
    }

        /**
         * 返回按钮
         * @param evt
         */
        private void ReturnActionPerformed (ActionEvent evt){
            this.dispose();
            MainFrame.runMainFrame(this.user);
        }

        /**
         * TODO 生成用户表格useTable
         * other:同时填充updateUser用户名复选框
         *
         */
        private void ConstructUserTable () {
            // 表头数据
            String[] columnNames = {"用户名", "密码", "角色"};
            // 表格数据
            String[][] rowData = new String[20][3];
            Message message=new Message("",null,"");

                // 向服务器发送信息获取用户列表
                message.setOperation("serverGetUserList");
                message.setState("fail");
                client.Myclient.sendData(message);
                message= client.Myclient.getData();

                //获取数据结果
                if(message.getState().equals("success")) {

                    ArrayList<AbstractUser>users = client.Myclient.getAllUserInfo();//得到用户数据
                    // 行数
                    int row = 0;
                    AbstractUser user=null;
                    updateNameBox.removeAllItems();
                    updateNameBox.addItem("");
                    // 将用户信息导入至表格数据
                    for (int i = 0; ; i++) {
                        if ((user = users.get(i)) != null) {
                            if(user.getRole().equalsIgnoreCase("boss")&&this.user.getRole().equalsIgnoreCase("boss"))
                            //填充updateUser复选框
                            updateNameBox.addItem(user.getName());
                            if(!user.getRole().equalsIgnoreCase("boss"))
                                updateNameBox.addItem(user.getName());
                            //填充delUser表格
                            rowData[row][0] = user.getName();
                            rowData[row][1] = user.getPassword();
                            rowData[row][2] = user.getRole();
                            row++;
                        } else
                            break;
                    }
                }
            // 构造表格
            Users_table.setModel(new DefaultTableModel(rowData, columnNames) {
                boolean[] columnEditables = new boolean[]{false, false, false};
                public boolean isCellEditable(int row, int column) {
                    return columnEditables[column];
                }
            });
        }
        // 设置页面
        private void SetPane( int value){
            if (value == 0) {
                UserMenuTable.setSelectedComponent(addUser);
            } else if (value == 1) {
                UserMenuTable.setSelectedComponent(delUser);
            } else if (value == 2) {
                UserMenuTable.setSelectedComponent(updateUser);
            }
        }
}//end class UserFrame

