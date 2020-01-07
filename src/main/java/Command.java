
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    /*
      DST,  SRC,   TIME,       TYPE,   PRIORITY,   SENS_ID,    VAL
      STR, STR,    TIMESTAMP,  STR,    INT,        STR,        STR
     */
    private String destination;
    private String source;
    private String type;
    private String value;

    @Override
    public String toString() {
        return "Command{" +
                "destination='" + destination + '\'' +
                ", source='" + source + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String getEntireString() {
        return destination + "," + source + "," + type + "," + value;
    }

    public Command(String[] str) {
        // TODO CHANGE THIS INDEXES
        destination = str[0];
        source = str[1];
        /*if (!str[2].equals("") && !str[2].equals("-1"))
            time = Long.parseLong(str[2]);
        else*/
        type = str[3];
        value = str[6];
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
