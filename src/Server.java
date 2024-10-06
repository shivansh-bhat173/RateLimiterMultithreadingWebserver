import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService threadPool;
    private final Map<String, SlidingWindowCounter> clientRateLimiters; // Rate limiters per client

    Server(int poolSize) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        this.clientRateLimiters = new ConcurrentHashMap<>();
    }

    public void handleClient(Socket clientSocket) {
        try (PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true)) {
            toClient.println("Hello From Server " + clientSocket.getInetAddress());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 8010;
        int poolSize = 200;
        Server server = new Server(poolSize);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(10000);
            System.out.println("Server is listening on port " + port);
            while (true) {
                //open a socket for connection
                Socket clientSocket = serverSocket.accept();

                String clientIP = clientSocket.getInetAddress().getHostAddress();
//                System.out.println(clientIP);

                // Fetch or create a rate limiter for the client
                SlidingWindowCounter rateLimiter = server.clientRateLimiters.computeIfAbsent(
                        clientIP, ip -> new SlidingWindowCounter(10, 1000)
                );
//                System.out.println("rate Limiter: "+rateLimiter.toString());
                if (rateLimiter.allowRequest()) {
                    server.threadPool.execute(() -> server.handleClient(clientSocket));
                    System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());
                } else {
//                    System.out.println("Rate limit exceeded for client: " + clientSocket.getRemoteSocketAddress());
                    clientSocket.close(); // Close connection for rate-limited clients
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            server.threadPool.shutdown();
        }
    }
}
