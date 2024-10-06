package singleThreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

 public class Server1 {
    public static void main(String[] args) {
    System.out.println("java");
    Server1 server = new Server1();
    try{
        server.run();
    }catch(IOException e){
        e.printStackTrace();
    }
}

    public void run() throws IOException{
        //initializing a port and a Server socket on that port
        int port = 8010;
        ServerSocket socket = new ServerSocket(8010);
        socket.setSoTimeout(100000);
        while(true){
            try{
                System.out.println("Server is Listening on Port: "+ port);
                //Accept income client socket connection
                Socket acceptedSocket = socket.accept();
                System.out.println("Connection accepted from Client : "+acceptedSocket.getRemoteSocketAddress());
                //Get output from the client
                PrintWriter toClient = new PrintWriter(acceptedSocket.getOutputStream());
                //input to the CLient
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(acceptedSocket.getInputStream()));
                toClient.println("Konichiwa From the Server!");
                toClient.close();
                fromClient.close();
                acceptedSocket.close();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
}
