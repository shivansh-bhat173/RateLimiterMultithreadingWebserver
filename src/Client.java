import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Client client = new Client();
        for (int i = 0; i < 10; i++) {
            try {
                Thread thread = new Thread(client.getRunnable());
                thread.start();
//                thread.sleep(2000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public Runnable getRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                int port = 8011;
                try {
                    InetAddress address = InetAddress.getByName("localhost");
                    Socket socket = new Socket(address, port);
                    try(
                            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    ){
                        toServer.println("Namaste from the client!");
                        String line = fromServer.readLine();
                        System.out.println("Response from the Server is " + line);
                    }
                    //Input from the Client

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
