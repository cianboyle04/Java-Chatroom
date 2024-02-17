//public class GuessTheNumber {
//
//    Client player1;
//    Client player2;
//    Client activePlayer;
//    int number;
//
//    boolean isGame;
//
//    public GuessTheNumber(Client player1, Client player2){
//        this.player1 = player1;
//        this.player2 = player2;
//        isGame = true;
//        number = 3;
//    }
//
//    public void playGame(){
//        activePlayer = player1;
//
//        while(isGame){
//
//             int guess = activePlayer.makeGuess(3);
//
//             if(guess == number){
//                 System.out.println(activePlayer + " guessed the number");
//                 isGame = false;
//                 break;
//             }
//             else if(player1 == activePlayer){
//                 activePlayer = player2;
//             }
//             else{
//                 activePlayer = player1;
//             }
//        }
//
//    }
//
//    public static void main(String[] args) {
//        Client cian = new Client();
//        Client john = new Client();
//
//        GuessTheNumber guessTheNumber = new GuessTheNumber(cian, john);
//        guessTheNumber.playGame();
//
//    }
//
//}
