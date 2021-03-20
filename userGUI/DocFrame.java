package userGUI;

import base.*;
import client.*;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class DocFrame extends JFrame {
    /**
     * 文件界面组件
     */
//主界面
    public JPanel docMenuPanel;
    private JTabbedPane docMenu_Table;
    //文件列表
    private JTable Files_table;
    //上传文件
    private JPanel upload_Doc;
    private JComboBox up_NameChoose;
    private JTextField up_IdField;
    private JTextField up_DescribeField;
    private JTextField up_PrePathField;
    private JButton up_PreviewFile;
    private JLabel upload_User;
    private JLabel uploadFile_Id;
    private JLabel uploadFile_Describe;
    private JLabel uploadFile_Path;
    private JButton up_Ok;
    private JButton up_Return;
    //下载文件
    private JPanel download_Doc;
    private JButton down_PreSaveFile;
    private JTextField down_PreSavePathField;
    private JButton down_Ok;
    private JButton down_Return;
    private JLabel down_SavePath;
    private JScrollPane downDoc_Zone;
    private JTextField down_PreLoadPathField;
    private JButton down_PreLoadFile;
    private JLabel down_LoadPath;

    /**
     * TODO 构建用户界面
     * Achieve Frame
     */
    public DocFrame(AbstractUser user, int choice) {
//主界面
        docMenu_Table.setTitleAt(0, "上传文件");
        docMenu_Table.setTitleAt(1, "下载文件");
/**
 * TODO 实现上传文件操作
 * uploadFile
 *
 */
        /*按钮事件*/
//确定按钮
        up_Ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 上传文件事件
                UploadActionPerformed(user, e);
                Clear_Up();
            }
        });
//返回按钮
        up_Return.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 返回事件
                ReturnActionPerformed(e);
            }
        });
//预览文件按钮
        up_PreviewFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 打开文件事件
                OpenFileActionPerformed(e, 1);
            }
        });

/**
 * TODO 实现下载文件操作
 * downloadFile
 */
        //确定下载按钮
        down_Ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 下载文件事件
                DownloadActionPerformed(user, e);
                Clear_Down();
            }
        });
        //返回按钮
        down_Return.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 返回事件
                ReturnActionPerformed(e);
            }
        });
        //预览下载文件位置
        down_PreLoadFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 打开文件事件
                OpenFileActionPerformed(e, 2);
            }
        });
        //预览保存位置
        down_PreSaveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 打开文件事件
                OpenFileActionPerformed(e, 0);
            }
        });

        // 下载文件列表
        Files_table = new JTable();
        // 构造表格
        ConstructFileTable();
        // 加入可下拉区域
        downDoc_Zone.setViewportView(Files_table);
        //复选框更新
        up_NameChoose();
        // 设置权限及页面
        setPane(user, choice);
    }

    /**
     * TODO TO clear the dialog box
     * 重置对话框
     */
    private void Clear_Up() {
        up_NameChoose.setSelectedIndex(0);
        up_DescribeField.setText("");
        up_IdField.setText("");
        up_PrePathField.setText("");
    }
    private void Clear_Down() {
        down_PreSavePathField.setText("");
    }

    /**
     * TODO doc构造表格
     */
    private void ConstructFileTable() {

        // 表头数据
        String[] columnNames = {"档案号", "创建者", "创建时间", "文件名", "文件描述"};
        // 表格数据
        String[][] rowData = new String[20][5];

            // 向服务器发送信息获取档案列表
            Message message=new Message("",null,"");
            message.setOperation("serverGetDocList");
            message.setState("fail");
            client.Myclient.sendData(message);
            message= client.Myclient.getData();

            //获取结果
            if(message.getState().equals("success")) {
                ArrayList<Doc> docs = client.Myclient.getAllDocInfo();
                if (docs != null) {
                    Doc doc = null;
                    // 行数
                    int row = 0;
                    // 将哈希表信息导入至表格
                    for (int i = 0; ; i++) {
                        if ((doc = docs.get(i)) != null) {
                            //
                            rowData[row][0] = doc.getId();
                            rowData[row][1] = doc.getCreator();
                            // Time to String
                            rowData[row][2] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(doc.getTimestamp());
                            rowData[row][3] = doc.getFilename();
                            rowData[row][4] = doc.getDescription();
                            row++;
                        } else
                            break;
                    }
                }
            }
        // 填充表格内容
        Files_table.setModel(new DefaultTableModel(rowData, columnNames) {

            boolean[] columnEditables = new boolean[]{false, false, false, false, false};

            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }

        });

    }

    /**
     * 打开文件
     *
     * @param evt
     * @param select
     */
    private void OpenFileActionPerformed(ActionEvent evt, int select) {
        /**
         * 1:uploadFIle
         * 2:DownloadFile beyond the table
         * other:DownloadFile in the table
         */
        String filepath = null;
        if (select == 1 || select == 2) {
            // 弹出文件选择框
            FileDialog OpenFileDialog = new FileDialog(this, "选择资源位置");
            OpenFileDialog.setVisible(true);
            //uploadFile ||downLoadFile for free 打开文件
            filepath = OpenFileDialog.getDirectory() + OpenFileDialog.getFile();
        }
        if (select == 0) {
            //downloadFile 打开保存文件预览位置
            int result = 0;
            JFileChooser fileChooser = new JFileChooser();
            FileSystemView fsv = FileSystemView.getFileSystemView();//获取本机文件
            System.out.println(fsv.getHomeDirectory());//得到桌面路径

            fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
            fileChooser.setDialogTitle("请选择文件...");
            fileChooser.setApproveButtonText("确定");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//限定选择文件夹目录DIRECTORIES_ONLY
            //获取结果
            result = fileChooser.showOpenDialog(fileChooser);

            if (JFileChooser.APPROVE_OPTION == result) {
                filepath = fileChooser.getSelectedFile().getPath();
                System.out.println("path: " + fileChooser.getSelectedFile().getPath());
            }

        }
        //由select 设置对应文本区域内容
        switch (select) {
            case 1:
                up_PrePathField.setText(filepath);
                break;
            case 2:
                down_PreLoadPathField.setText(filepath);
                break;
            case 0:
                down_PreSavePathField.setText(filepath);
            default:
                System.out.print("自由下载" + select);
                break;
        }
    }

    /**
     * 上传文件
     *
     * @param user
     * @param evt
     */
    private void UploadActionPerformed(AbstractUser user, ActionEvent evt) {
        if (!user.getRole().equalsIgnoreCase("operator"))
            return;
        //获取参数
        String creator = (String) this.up_NameChoose.getSelectedItem();
        String filepath = up_PrePathField.getText();
        String fileID = up_IdField.getText();
        String filedescription = up_DescribeField.getText();

        if (creator == null || "".equals(creator)) {
            JOptionPane.showMessageDialog(null, "未选择创建者！");
            return;
        }
        if (filepath == null || "".equals(filepath)) {
            JOptionPane.showMessageDialog(null, "未选择文件！");
            return;
        }
        if (fileID == null || "".equals(fileID)) {
            JOptionPane.showMessageDialog(null, "未输入档案号！");
            return;
        }
        if (filedescription == null || "".equals(filedescription)) {
            JOptionPane.showMessageDialog(null, "未输入文件描述！");
            return;
        }
        //创建目标文件对象  获取文件名称
        File getname;
        getname = new File(filepath);
        String filename = getname.getName();//文件名赋值
        //文件上传时间
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Doc doc = new Doc(fileID, creator, timestamp, filedescription, filename);
        Message message = new Message("", null, "");
        message.setData(doc);
        message.setOperation("serverUpLoadFile");
        message.setState("success");

        //客户端上传发送信息
        client.Myclient.sendData(message);
        //客户端上传文件
        Doc.fileisOk = false;//文件标识false
        client_Upload upload = new client_Upload(filepath, client.Myclient.getClientDataOut());
        upload.start();

        System.out.print("等待本地上传: ");
        //当文档上传完成后Doc.fileisOk=true 退出循环
        Doc.WaittoDo();System.out.print("\n上传完成！等待服务器接收: ");
        //Thread.currentThread().sleep(1000);

        //当服务器接收文档完成后Doc.fileisOk=true 退出循环
        Doc.WaittoDo();System.out.println("\n服务器接收完成！");

        //获取结果
        // Thread.currentThread().sleep(2000);
        if (Doc.fileisOk) {
            //返回结果
            Message result = client.Myclient.getData();
            if (result.getState().equals("success")) {
                ConstructFileTable(); // 更新表格数据
                JOptionPane.showMessageDialog(null, "上传成功！");
                return;
            } else {
                JOptionPane.showMessageDialog(null, "上传失败！");
                return;
            }
        }
    }

    /**
     * 下载文件
     *
     * @param user
     * @param evt
     */
    private void DownloadActionPerformed(AbstractUser user, ActionEvent evt) {
        String downfilepath = null;
        String savefilepath=null;
        String fileName=null;
        Doc doc = null;

        int flag = 1;//是否选择非表格内文件 1:true other:false

        downfilepath = down_PreLoadPathField.getText();
        savefilepath=down_PreSavePathField.getText();

        if (downfilepath == null || "".equals(downfilepath))
            flag = 0;//downfilepath为空 则选择表格内文件

        if (flag == 0) {
            /*选择表格内文件*/
            // 获取所选行序号, 若未选择其值为-1
            int selectedrow = Files_table.getSelectedRow();
            // 未选择文件的情况
            if (selectedrow == -1) {
                JOptionPane.showMessageDialog(null, "未选择文件！");
                return;
            } else {
                // 获取档案号
                String fileID = (String) Files_table.getValueAt(selectedrow, 0);
                // 获取文件名
                fileName= (String) Files_table.getValueAt(selectedrow,3);
                doc = new Doc(fileID, "", null, "", "");
                // 若选择空行
                if (fileID == null || "".equals(fileID)) {
                    JOptionPane.showMessageDialog(null, "档案号为空！");
                    return;
                }
            }
        } else {//任意文件下载 downfilepath保存下载文件路径
            /**/
            //创建文件对象  将文件下载路径放入文件名中保存
            doc = new Doc("", "", null, "", downfilepath);
            File getFilename=new File(downfilepath);
            fileName=getFilename.getName();//文件名
        }//end if-else and get doc via this part
        
        Message message = new Message("",null,"");
            message.setData(doc);
            message.setOperation("serverDownLoadFile");
            message.setState("fail");
            //向服务器发送下载信息
            client.Myclient.sendData(message);

            //接收服务器响应
        Message result = client.Myclient.getData();
        if(result.getState().equals("success"))

            System.out.print("等待服务器传输...");
        //当服务器传输完成后 Doc.fileisOk=true 退出循环
        Doc.WaittoDo();System.out.print("\n服务器传输完毕，接收中...");

        //获取下载内容
        Doc.fileisOk=false;
        new client_Download(savefilepath,fileName,client.Myclient.getClientDataIn()).start();
        //当客户端接收文档完成后Doc.fileisOk=true 退出循环
        Doc.WaittoDo();
        System.out.println("\n客户端接收完成！");
        JOptionPane.showMessageDialog(null, "下载成功！");
    }

    /**
     * 设置页面
     * @param user
     * @param choice
     */
    private void setPane(AbstractUser user, int choice) {
        //非operator用户无法上传
        if (!user.getRole().equalsIgnoreCase("operator"))
            docMenu_Table.setEnabledAt(0,false);

        if (choice == 0) {
            docMenu_Table.setSelectedComponent(upload_Doc);
        } else if (choice == 1) {
            docMenu_Table.setSelectedComponent(download_Doc);
        }

    }

    /**
     * TODO 填充文件上传者复选框
     */
    private void up_NameChoose(){
        Message message=new Message("",null,"");
        message.setOperation("serverGetUserList");
        message.setState("fail");
        client.Myclient.sendData(message);
        message= client.Myclient.getData();

        //获取数据结果
        if(message.getState().equals("success")) {
            ArrayList<AbstractUser>users = client.Myclient.getAllUserInfo();//得到用户数据
            AbstractUser user=null;
            up_NameChoose.removeAllItems();
            up_NameChoose.addItem("");
            // 将用户信息导入至表格数据
            for (int i = 0; ; i++) {
                if ((user = users.get(i)) != null) {
                    //填充updateUser复选框
                    up_NameChoose.addItem(user.getName());
                } else
                    break;
            }
        }
    }
    /**
     * 返回
     * @param evt
     */
    private void ReturnActionPerformed(ActionEvent evt) {
        this.dispose();
    }
}

