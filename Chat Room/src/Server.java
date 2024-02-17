import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{

    private ArrayList<ConnectionHandler> connections;
    private ServerSocket serverSocket;
    private boolean done;
    private ExecutorService pool;

    public Server(){
        connections = new ArrayList<>();
        done = false;
    }

    @Override
    public void run() {
        try {
            System.out.println(numPeopleOnline());
//            serverSocket = new ServerSocket(9999);
            serverSocket = new ServerSocket(9999, 0, InetAddress.getByName("0.0.0.0"));
            pool = Executors.newCachedThreadPool();
            while(!done){
                Socket client = serverSocket.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);

                System.out.println(numPeopleOnline());

            }
        } catch (Exception e) {
            shutDown();
        }

    }

    public void broadcast(String message){
        String[] name = message.split(" ", 2);

        for(int i = 0; i < connections.size(); i++){
            if(connections.get(i) != null && connections.get(i).hasJoined){
                if(connections.get(i).nickname.equals(name[0].replace(":", ""))){
                    connections.get(i).sendMessage("You: " + name[1]);
                }
                else{
                    connections.get(i).sendMessage(message);
                }

            }
        }
    }

    public void activeUsers_broadcast(String users){


        for(int i = 0; i < connections.size(); i++){
            if(connections.get(i) != null && connections.get(i).hasJoined){
                String[] users_names = users.split(" ");

                for(int j = 1; j < users_names.length; j++){
                    if(users_names[j].equals(connections.get(i).nickname)){
                        users_names[j] = users_names[j] + "-(You)";
                    }
                }
                connections.get(i).sendMessage(String.join(" ", users_names));
            }
        }
    }



    public void shutDown(){
        try{
            done = true;
            pool.shutdown();

            if(!serverSocket.isClosed()){
                serverSocket.close();
            }
            for(int i = 0; i < connections.size(); i++){
                connections.get(i).shutDown();
            }

        }catch(IOException e){
            shutDown();
        }
    }

    public String numPeopleOnline(){
        if(connections.size() == 1){
            return connections.size() + " person online...";
        }
        else{
            return connections.size() + " people online...";
        }
    }

    public String serverAnnouncement(){
        return "//Server Announcement: ";
    }





    class ConnectionHandler implements Runnable {

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        private boolean hasJoined;
        private boolean quit;

        public ConnectionHandler(Socket client){
            this.client = client;
            hasJoined = false;
            quit = false;
            nickname = "User joining...";
        }

        @Override
        public void run() {
            try{

                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(), true);

                out.println("Enter a nickname: ");

                nickname = in.readLine();
                hasJoined = true;
                activeUsers_broadcast(activeUsers_Display());

                System.out.println(nickname + " connected...");
                broadcast(nickname + " joined the chat...");

                String message;

                while((message = in.readLine()) != null){

                    if(message.startsWith("/nick ")){

                        String[] messageSplit = message.split(" ", 2);

                        if(messageSplit.length == 2){
//                            broadcast(serverAnnouncement() + nickname + " renamed themselves to: " + messageSplit[1]);
                            announcementMessage(serverAnnouncement() + nickname + " renamed themselves to: " + messageSplit[1]);
                            System.out.println(nickname + " renamed themselves to: " + messageSplit[1]);
                            nickname = messageSplit[1];
                            out.println("Successfully changed nickname to: " + nickname);
                            activeUsers_broadcast(activeUsers_Display());
                        }
                        else{
                            out.println("Invalid nickname...");
                        }

                    }
                    else if(message.startsWith("/connections")){
                        out.println(numPeopleOnline());
                    }
                    else if(message.startsWith("/whisper")){
                        String[] sentence = message.split(" ");
                        StringBuilder real_message = new StringBuilder(nickname + " whispered: ");

                        for(int i = 2; i < sentence.length; i++){
                            real_message.append(sentence[i]).append(" ");
                        }

                        directMessage(sentence[1], real_message.toString().trim());
                    }
                    else if(message.startsWith("/funny")){
                        String[] sentence = message.split(" ", 3);
                        funnyMessage(sentence[1], sentence[2]);
                    }
                    else if(message.startsWith("/kick")){


                        String[] sentence = message.split(" ");
                        StringBuilder reason = new StringBuilder();

                        for(int i = 2; i < sentence.length; i++){
                            reason.append(sentence[i]).append(" ");
                        }

                        //no reason provided
                        if(sentence.length <= 2){
                            directMessage(sentence[1], "/:kick:/ " + nickname);
                        }
                        //reason provided
                        else{
                            directMessage(sentence[1], "/:kick:/ " + nickname + " | Reason: " + reason);
                        }
                        broadcast(serverAnnouncement() + sentence[1] + " was kicked by " + nickname);
                        activeUsers_broadcast(activeUsers_Display());
                    }
                    else if(message.equals(":/kicked/:")){
                        quit = true;
                        shutDown();
                    }
                    else if(message.startsWith("/quit")){
                        System.out.println(nickname + " disconnected...");
                        broadcast(nickname + " left the chat...");
                        shutDown();
                        quit = true;
                        System.out.println(numPeopleOnline());
                        activeUsers_broadcast(activeUsers_Display());
                    }
                    else{
                        broadcast(nickname + ": " + message);
                    }
                }

            }catch(Exception e) {
                shutDown();
            }
            if(!quit){
                System.out.println(nickname + " disconnected unexpectedly...");
                broadcast(nickname + " disconnected...");
                shutDown();
            }


        }

        public String activeUsers_Display(){
            StringBuilder str = new StringBuilder();
            str.append("/:users:/ ");

            if (connections != null) {
                for (int i = 0; i < connections.size(); i++) {
                    if(connections.get(i).hasJoined){
//                        if(connections.get(i).nickname.equals(nickname)){
//                            str.append(connections.get(i).nickname).append("-(You) ");
//                        }
//                        else{
                            str.append(connections.get(i).nickname).append(" ");
//                        }
                    }
                }
            } else {
                str.append("No active users.\n");
            }
            return str.toString();
        }



        public void sendMessage(String message){
            out.println(message);
        }

        public void directMessage(String recipient, String message){
            for(int i = 0; i < connections.size(); i++){
                if(connections.get(i).nickname.equals(recipient)){
                    connections.get(i).sendMessage(message);
                }
            }
        }

        public void funnyMessage(String recipient, String message) throws InterruptedException {
            for(int i = 0; i < connections.size(); i++){
                if(connections.get(i).nickname.equals(recipient)){
                    for(int j = 0; j < 1000; j++){
                        for(int k = 0; k < 1000; k++){
                            connections.get(i).out.print(message + " ");
                        }
                        connections.get(i).out.println();
                    }
                }
            }
        }

        public void announcementMessage(String message){
            for(int i = 0; i < connections.size(); i++){
                if(!connections.get(i).nickname.equals(nickname)){
                    connections.get(i).sendMessage(message);
                }
            }
        }

        public void shutDown(){
            try{
                in.close();
                out.close();

                if(!client.isClosed()){
                    client.close();
                }

                connections.remove(this);
            }catch(IOException e){
                //ignore
            }
        }
    }

    public static void main(String[] args){
        Server server = new Server();
        server.run();
    }
}
