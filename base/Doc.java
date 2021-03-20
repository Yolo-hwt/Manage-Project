package base;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * TODO �����ļ���
 *
 * @author hwt
 * @date 2020/11/25
 *
 */
public class Doc implements Serializable {
    /**volatile���η�������֤�����̶߳�ȡ�����Ǹñ��������µ�ֵ
     *
     * �ļ������ʶ
     * //true ������ϣ�δռ��IO��
     * //false ���ڴ��䣬ռ��IO��
     */
    public static volatile boolean  fileisOk=true;
    //������ǰ����
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
    //�ļ������
    public static final String whereToSaveFileFromClient ="W:\\java\\Manage Project06_03\\upLoadFile\\";
    private String id;//�ļ�id
    private String creator;//�ļ�������
    private Timestamp timestamp;//�ļ�����ʱ��
    private String description;//�ļ�����
    private String filename;//�ļ���

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