package base;
import java.io.Serializable;
public class Message implements Serializable{
    private static final long serialVersionUID=1L;
    private String operation;//操作
    private Object data;//消息
    private String state;//状态：success or fail

public Message(String operation,Object data,String state){
    this.operation=operation;
    this.data=data;
    this.state=state;
}
    public String getOperation(){
        return operation;
    }

    public void setOperation(String operation){
        this.operation=operation;
    }

    public Object getData(){
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Message{" +
                "operation='" + operation + '\'' +
                ", data=" + data +
                ", state='" + state + '\'' +
                '}';
    }
}
