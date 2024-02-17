//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//
//
//class ConnectionHandler implements Runnable {
//
//    private Socket client;
//    private BufferedReader in;
//    private PrintWriter out;
//    private String nickname;
//
//    private boolean hasJoined;
//    private boolean quit;
//
//    public ConnectionHandler(Socket client){
//        this.client = client;
//        hasJoined = false;
//        quit = false;
//        nickname = "User joining...";
//    }
//
//    @Override
//    public void run() {
//        try{
//
//            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//            out = new PrintWriter(client.getOutputStream(), true);
//
//            out.println("Enter a nickname: ");
//
//            nickname = in.readLine();
//            hasJoined = true;
//            activeUsers_broadcast(activeUsers_Display());
//
//            System.out.println(nickname + " connected...");
//            broadcast(nickname + " joined the chat...");
//
//            String message;
//
//            while((message = in.readLine()) != null){
//
//                if(message.startsWith("/nick ")){
//
//                    String[] messageSplit = message.split(" ", 2);
//
//                    if(messageSplit.length == 2){
////                            broadcast(serverAnnouncement() + nickname + " renamed themselves to: " + messageSplit[1]);
//                        announcementMessage(serverAnnouncement() + nickname + " renamed themselves to: " + messageSplit[1]);
//                        System.out.println(nickname + " renamed themselves to: " + messageSplit[1]);
//                        nickname = messageSplit[1];
//                        out.println("Successfully changed nickname to: " + nickname);
//                        activeUsers_broadcast(activeUsers_Display());
//                    }
//                    else{
//                        out.println("Invalid nickname...");
//                    }
//
//                }
//                else if(message.startsWith("/connections")){
//                    out.println(numPeopleOnline());
//                }
//                else if(message.startsWith("/whisper")){
//                    String[] sentence = message.split(" ");
//                    StringBuilder real_message = new StringBuilder(nickname + " whispered: ");
//
//                    for(int i = 2; i < sentence.length; i++){
//                        real_message.append(sentence[i]).append(" ");
//                    }
//
//                    directMessage(sentence[1], real_message.toString().trim());
//                }
//                else if(message.startsWith("/kick")){
//
//
//                    String[] sentence = message.split(" ");
//                    StringBuilder reason = new StringBuilder();
//
//                    for(int i = 2; i < sentence.length; i++){
//                        reason.append(sentence[i]).append(" ");
//                    }
//
//                    //no reason provided
//                    if(sentence.length <= 2){
//                        directMessage(sentence[1], "/:kick:/ " + nickname);
//                    }
//                    //reason provided
//                    else{
//                        directMessage(sentence[1], "/:kick:/ " + nickname + " | Reason: " + reason);
//                    }
//                    broadcast(serverAnnouncement() + sentence[1] + " was kicked by " + nickname);
//                    activeUsers_broadcast(activeUsers_Display());
//                }
//                else if(message.equals(":/kicked/:")){
//                    quit = true;
//                    shutDown();
//                }
//                else if(message.startsWith("/quit")){
//                    System.out.println(nickname + " disconnected...");
//                    broadcast(nickname + " left the chat...");
//                    shutDown();
//                    quit = true;
//                    System.out.println(numPeopleOnline());
//                    activeUsers_broadcast(activeUsers_Display());
//                }
//                else{
//                    broadcast(nickname + ": " + message);
//                }
//            }
//
//        }catch(Exception e) {
//            shutDown();
//        }
//        if(!quit){
//            System.out.println(nickname + " disconnected unexpectedly...");
//            broadcast(nickname + " disconnected...");
//            shutDown();
//        }
//
//
//    }
//
//    public String activeUsers_Display(){
//        StringBuilder str = new StringBuilder();
//        str.append("/:users:/ ");
//
//        if (connections != null) {
//            for (int i = 0; i < connections.size(); i++) {
//                if(connections.get(i).hasJoined){
////                        if(connections.get(i).nickname.equals(nickname)){
////                            str.append(connections.get(i).nickname).append("-(You) ");
////                        }
////                        else{
//                    str.append(connections.get(i).nickname).append(" ");
////                        }
//                }
//            }
//        } else {
//            str.append("No active users.\n");
//        }
//        return str.toString();
//    }
//
//
//
//    public void sendMessage(String message){
//        out.println(message);
//    }
//
//    public void directMessage(String recipient, String message){
//        for(int i = 0; i < connections.size(); i++){
//            if(connections.get(i).nickname.equals(recipient)){
//                connections.get(i).sendMessage(message);
//            }
//        }
//    }
//
//    public void announcementMessage(String message){
//        for(int i = 0; i < connections.size(); i++){
//            if(!connections.get(i).nickname.equals(nickname)){
//                connections.get(i).sendMessage(message);
//            }
//        }
//    }
//
//    public void shutDown(){
//        try{
//            in.close();
//            out.close();
//
//            if(!client.isClosed()){
//                client.close();
//            }
//
//            connections.remove(this);
//        }catch(IOException e){
//            //ignore
//        }
//    }
//}
