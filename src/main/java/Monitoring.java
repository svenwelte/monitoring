import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Monitoring implements Runnable {

    public final long SECONDS_TO_WAIT = 30;

    private final Logger logger;
    private final String url;

    public Monitoring(String url) throws IOException {
        this.url = url;
        this.logger = buildLogger();
    }

    @Override
    public void run() {
        while(true) {
            check();
            sleep();
        }
    }

    private void check() {
        try {
            HttpClient client = buildHttpClient();
            HttpResponse response = client.execute(new HttpGet(url));

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= 200 && statusCode < 300) {
                logger.info(url + " -> erreichbar!");
            }
            else {
                logger.info(url + " -> nicht erreichbar!");
            }

        } catch (IOException e) {
            logger.info(url + " -> nicht erreichbar!");
        }
    }

    private void sleep() {
        try {
            Thread.sleep(SECONDS_TO_WAIT * 1000);
        } catch (InterruptedException e) {
        }
    }

    public void start() {
        verifyUrl();
        new Thread(this).start();
    }

    public Logger buildLogger() throws IOException {
        Logger logger = Logger.getLogger("monitoring");
        logger.setUseParentHandlers(false);

        // Logging into File:
        // FileHandler handler = new FileHandler("logfile");

        // Logging to Console:
        ConsoleHandler handler = new ConsoleHandler();

        handler.setFormatter(new CustomLogFormatter());
        logger.addHandler(handler);
        return logger;
    }

    public HttpClient buildHttpClient() {
        return HttpClientBuilder.create()
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .build();
    }

    void verifyUrl() {
        try {
            URL url = new URL(this.url);
            if (url.getProtocol().equals("http") || url.getProtocol().equals("https")) {
                return;
            }
        } catch (MalformedURLException e) {}

        logger.info("This url does not seem right. Please enter a valid url like http://example.com");
        System.exit(-1);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Terminal terminal = TerminalBuilder.terminal();
        LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();

        String url = lineReader.readLine("Please enter http url to monitor:");
        Monitoring monitoring = new Monitoring(url);
        monitoring.start();

        terminal.enterRawMode();
        terminal.reader().read(Long.MAX_VALUE);

        System.exit(0);
    }

}
