package server;

import base.Message;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;

public class serverFrame extends JFrame{
    private JPanel serverPanel;
    //显示区域
    private JTextField portField;
    private JTextField ipField;
    private JTextArea server_Message;
    //按钮
    private JButton launch_Button;
    private JButton exit_Button;
    //标签
    private JLabel serverTitle;
    private JLabel server_Port;
    private JLabel server_Ip;

    private Message message;
    public  ServerConnection serverConnection;
    private ServerSocket server; // server socket

    //public static serverFrame frame;

    /**
     * TODO launch Server
     * test
     * @param args
     */
    public  static void main(String[] args) {
        new Thread(() -> {
        serverFrame frame=new serverFrame();
        frame.setContentPane(frame.serverPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }).start();
    }

    /**
     * TODO 主界面
     */
    public serverFrame(){
//事件监听
        launch_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchPerformed(e);
            }
        });
        exit_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitPerformed(e);
            }
        });
    }

    /**
     * 事件响应
     * @param evt
     */
    //启动服务器
    public void launchPerformed(ActionEvent evt){
        new Thread(() -> {
            try {
                displayMessage("服务器已启动，等待连接...");
                ipField.setText("");
                portField.setText("");
                launch_Button.setEnabled(true);
                JOptionPane.showMessageDialog(null, "启动成功！");
                //进入serverConnection
                serverConnection = new ServerConnection(serverFrame.this);
                serverConnection.start();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            launch_Button.setEnabled(false);//隐藏启动按钮
        }).start();
    }

    //断开服务器
    public void exitPerformed(ActionEvent evt){
        ipField.setText("");
        portField.setText("");
        serverConnection.exit=false;//用以断开连接 false
        launch_Button.setEnabled(true);//设置可用启动按钮
        disMessageinArea("");
        JOptionPane.showMessageDialog(null,"服务器中断");
        serverConnection.closeStreams("断开连接");
    }

    /**
     * 显示信息
     * @param messageToDisplay
     */
    public static void displayMessage( final String messageToDisplay )
    {
        System.out.println(messageToDisplay);
    } // end method displayMessage.
    public void disMessageinArea(final String messageToDisplay){
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run() // updates displayArea
                    {
                        server_Message.append( messageToDisplay ); // append message
                    } // end method run
                } // end anonymous inner class
        ); // end call to SwingUtilities.invokeLater
    }

    public JTextArea getServer_Message() {
        return server_Message;
    }
    public JTextField getIpField() {
        return ipField;
    }
    public JTextField getPortField(){return portField; }


}
