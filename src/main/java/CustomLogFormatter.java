import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class CustomLogFormatter extends Formatter {

    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-LLL-YYYY HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(dateFormat.format(LocalDateTime.now()));
        buffer.append(" Uhr");
        buffer.append(" - ");
        buffer.append(record.getMessage());
        buffer.append("\n");
        return buffer.toString();
    }

    public String getHead(Handler h) {
        return "";
    }

    public String getTail(Handler h) {
       return "";
    }

}
