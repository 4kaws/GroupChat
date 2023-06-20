import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    //Aratam mesaj tuturor utilizatorilor
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket; //stabilim conexiune dintre client si server
    private BufferedReader bufferedReader; //Citim mesajele trimise de alti clienti
    private BufferedWriter bufferedWriter;  //Trimitem mesaje catre ceilalti clienti
    private String clientUsername;

    public ClientHandler(Socket socket){

        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine(); //citim numele dorit de utilizator
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUsername + " s-a alaturat conversației!");
        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

    }

    @Override
    public void run() {

        String mesajClient;
        //asteptam mesaje de la client atata timp cat este conectat la server
        while (socket.isConnected()){

            try{
                mesajClient = bufferedReader.readLine(); //rulam pe un thread separat pentru a nu se bloca restul aplicatiei
                broadcastMessage(mesajClient);
            }catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }

        }

    }

    public void broadcastMessage(String mesajDeTrimis){
        //trimite mesajul catre ceilalti clienti in afara de cine a scris mesajul
        for (ClientHandler clientHandler : clientHandlers){
            try{
                if (!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(mesajDeTrimis);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }

    }

    public void removeClientHandler(){
        //utilizatorul care paraseste conversatia nu mai primeste mesaje
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " a părăist conversația!");
    }

    public void closeEverything(Socket socket,BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
