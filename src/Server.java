import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket; //asteapta mesaj de la alti utilizatori sau conexiuni posibile

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }


    public void startServer(){

        //Tinem serverul pornit atata timp cat serverSocket nu este oprit

        try{

            while (!serverSocket.isClosed()){

                Socket socket = serverSocket.accept();
                System.out.println("Un nou utilizator s-a conectat!"); //Cand adaugam un username nou acest mesaj va aparea
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();

            }

        }catch (IOException e){

        }

    }

    public void closeServerSocket(){

        try{

            if (serverSocket != null){
                serverSocket.close();
            }

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException{

        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();

    }


}
