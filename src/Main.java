import net.java.base.HTTPClient;

public class Main {

    public static void main(String[] args) throws Exception {
        //new PortScanner().scan("localhost");
        //new ConnectTest().connect("www.javathinker.org", 80);
        HTTPClient client = new HTTPClient();
        client.createSocket();
        client.communicate();
    }
}
