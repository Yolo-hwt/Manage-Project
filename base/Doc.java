package base;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * TODO 档案文件类
 *
 * @author hwt
 * @date 2020/11/25
 *
 */
public class Doc implements Serializable {
    /**volatile修饰符用来保证其它线程读取的总是该变量的最新的值
     *
     * 文件传输标识
     * //true 传输完毕，未占用IO流
     * //false 正在传输，占用IO流
     */
    public static volatile boolean  fileisOk=true;
    //阻塞当前进程
    public static void WaittoDo(){
        while (!Doc.fileisOk) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print("1s...");
        }
    }
    //文件类参数
    public static final String whereToSaveFileFromClient ="W:\\java\\Manage Project06_03\\upLoadFile\\";
    private String id;//文件id
    private String creator;//文件创建者
    private Timestamp timestamp;//文件创建时间
    private String description;//文件描述
    private String filename;//文件名

    public Doc(String id, String creator, Timestamp timestamp, String description, String filename) {
        super();
        this.id = id;
        this.creator = creator;
        this.timestamp = timestamp;
        this.description = description;
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "Doc{" +
                "id='" + id + '\'' +
                ", creator='" + creator + '\'' +
                ", timestamp=" + timestamp +
                ", description='" + description + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }
}