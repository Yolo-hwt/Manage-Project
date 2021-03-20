package base;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Vector;

/**
 * TODO 数据处理类
 *
 * @author hwt
 * @date 2020/11/25
 */
public  class DataProcessing {
    private static boolean connectToDB=false;
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;
    private static PreparedStatement preStatement;
    //数据库连接
    public static String driverName = "com.mysql.cj.jdbc.Driver"; // 加载数据库驱动类
    public static String url = "jdbc:mysql://localhost:3306/document?serverTimezone=GMT%2B8"; // 声明数据库的URL
    public static  String user="root";                                      // 数据库用户
    public static String password="090014";  //密码

    /**
     * TODO 关闭数据库连接
     * @param
     * @return void
     * @throws
     */
    public static void disconnectFromDataBase() {
        if (connectToDB) {
            // close Statement and Connection
            try {
                preStatement.close();
                statement.close();
                resultSet.close();
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }finally {
                connectToDB = false;
            }
        }
    }

    /**
     * TODO 创建数据库连接
     * @param driverName
     * @param url
     * @param passWord
     * @throws Exception
     */
    public static boolean connectToDatebase(String driverName,String url,String user,String passWord)throws Exception{
        Class.forName(driverName);//加载数据库
        connection=DriverManager.getConnection(url,user,passWord);//建立数据库连接
        connectToDB=true;
        return connectToDB;
    }

    /**
     * TODO 按档案编号搜索档案信息，返回null时表明未找到
     * @param id
     * @return Doc
     * @throws SQLException
     */
    public static Doc searchDoc(String id) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        Doc temp=null;
        statement=connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        String sql="SELECT * FROM doc_info WHERE Id='"+id+"'";//字符串拼接
        resultSet= statement.executeQuery(sql);
        if(resultSet.next()!=false){
            String Id=resultSet.getString("Id");
            String creator=resultSet.getString("creator");
            Timestamp timestamp=resultSet.getTimestamp("timestamp");
            String description=resultSet.getString("description");
            String filename=resultSet.getString("filename");

            temp=new Doc(Id,creator,timestamp,description,filename);
        }
        return temp;
    }

    /**
     * TODO 列出所有档案信息
     * @param
     * @return Enumeration<Doc>
     * @throws SQLException
     *
     */
    public static Enumeration<Doc> listDoc() throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        Vector<Doc>Users=new Vector<Doc>();
        statement=connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        String sql="select * from doc_info";
        resultSet= statement.executeQuery(sql);
        while(resultSet.next()){
            String Id=resultSet.getString("Id");
            String creator=resultSet.getString("creator");
            Timestamp timestamp=resultSet.getTimestamp("timestamp");
            String description=resultSet.getString("description");
            String filename=resultSet.getString("filename");

            Doc temp=new Doc(Id,creator,timestamp,description,filename);
            Users.addElement(temp);
        }
        Enumeration<Doc> e = Users.elements();
        return e;
    }

    /**
     * TODO 插入新的档案
     *
     * @param id
     * @param creator
     * @param timestamp
     * @param description
     * @param filename
     * @return boolean
     * @throws SQLException
     */
    public static boolean insertDoc(String id, String creator, Timestamp timestamp,
                                    String description, String filename) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        preStatement =connection.prepareStatement("INSERT INTO doc_info VALUES(?,?,?,?,?)");
        preStatement.setString(1,id);
        preStatement.setString(2,creator);
        preStatement.setTimestamp(3,timestamp);
        preStatement.setString(4,description);
        preStatement.setString(5,filename);
        int result=preStatement.executeUpdate();
        if(result==1)
            return true;

        return false;
    }
    /**
     * TODO 按用户名搜索用户，返回null时表明未找到符合条件的用户
     * @param name 用户名
     * @return AbstractUser
     * @throws SQLException
     */
    public static AbstractUser searchUser(String name) throws SQLException{
        if ( !connectToDB )
            throw new SQLException( "Not Connected to Database" );
        AbstractUser temp=null;
        String sql="SELECT * FROM user_info WHERE username=?";
        preStatement=connection.prepareStatement(sql);
        preStatement.setString(1,name);
        resultSet= preStatement.executeQuery();

        if(resultSet.next()!=false){
            String username=resultSet.getString("username");
            String password=resultSet.getString("password");
            String role=resultSet.getString("role");
            String login=resultSet.getString("login");
            temp=new AbstractUser(username,password,role,login);
            }
        return temp;
    }

    /**
     * TODO 按用户名、密码搜索用户，返回null时表明未找到符合条件的用户
     * @param name     用户名
     * @param password 密码
     * @return AbstractUser
     * @throws SQLException
     */
    public static AbstractUser search(String name, String password) throws SQLException {
        if ( !connectToDB )
            throw new SQLException( "Not Connected to Database" );
        AbstractUser temp=null;
        String sql="select * from user_info where username = ? AND password = ?";
        preStatement=connection.prepareStatement(sql);
        preStatement.setString(1,name);
        preStatement.setString(2,password);
        resultSet= preStatement.executeQuery();

        if(resultSet.next()!=false){
            String Role=resultSet.getString("role");
            String Login=resultSet.getString("login");
            temp=new AbstractUser(name,password,Role,Login);
        }
        return temp;
    }

    /**
     * TODO 取出所有的用户
     *
     * @param
     * @return Enumeration<AbstractUser>
     * @throws SQLException
     */
    public static Enumeration<AbstractUser> listUser()throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database//数据库未连接！");
        }
        Vector<AbstractUser>Users=new Vector<AbstractUser>();
        statement=connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        String user_sql="select * from user_info";
        resultSet= statement.executeQuery(user_sql);
        while(resultSet.next()){
            String username=resultSet.getString("username");
            String password=resultSet.getString("password");
            String role=resultSet.getString("role");
            String login=resultSet.getString("login");
            AbstractUser usertemp=new AbstractUser(username,password,role,login);
            Users.addElement(usertemp);
        }
        Enumeration<AbstractUser> e = Users.elements();
        return e;
    }

    /**
     * TODO 修改用户信息
     *
     * @param name     用户名
     * @param password 密码
     * @param role     角色
     * @return boolean
     * @throws SQLException
     */
    public static boolean update(String name, String password, String role) throws SQLException{
        boolean result=false;
        if(searchUser(name)!=null){
            String user_sql="UPDATE user_info SET password=?,role=? WHERE username=?";
            preStatement=connection.prepareStatement(user_sql);
            preStatement.setString(1,password);
            preStatement.setString(2,role);
            preStatement.setString(3,name);
            int re = preStatement.executeUpdate();
            if(re!=0)
                result=true;
        }
        return result;
    }

    /**
     * TODO 修改登录状态
     * @param name
     * @param login
     * @return
     * @throws SQLException
     */
    public static boolean upLogin(String name,String login)throws SQLException{
        boolean result=false;
        if(searchUser(name)!=null){
            String user_sql="UPDATE user_info SET login =? WHERE username=?";
            preStatement=connection.prepareStatement(user_sql);
            preStatement.setString(1,login);
            preStatement.setString(2,name);
            int re = preStatement.executeUpdate();
            if(re!=0)
                result=true;
        }
        return result;
    }

    /**
     * TODO 插入新用户
     *
     * @param name     用户名
     * @param password 密码
     * @param role     角色
     * @return boolean
     * @throws SQLException
     */
    public static boolean insertUser(String name, String password, String role) throws SQLException {
        boolean result=false;
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        if(searchUser(name)==null){
            String off="off";
            String user_sql="INSERT INTO user_info VALUES(?,?,?,?)";
            preStatement=connection.prepareStatement(user_sql);
            preStatement.setString(1,name);
            preStatement.setString(2,password);
            preStatement.setString(3,role);
            preStatement.setString(4,off);
            int re= preStatement.executeUpdate();
            if(re!=0)
                result=true;
        }
        return result;
    }

    /**
     * TODO 删除指定用户
     *
     * @param name 用户名
     * @return boolean
     * @throws SQLException
     */
    public static boolean deleteUser(String name) throws SQLException {
        boolean result=false;
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        if(searchUser(name)!=null){
            String user_sql="delete from user_info where username=?";
            preStatement=connection.prepareStatement(user_sql);
            preStatement.setString(1,name);
            int re= preStatement.executeUpdate();

            if(re!=0)
                result=true;
        }
        return result;
    }

}//end Dataprocessing class





