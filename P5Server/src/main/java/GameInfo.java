import java.io.Serializable;

public class GameInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    //Store the state of the board
    String[] TicTacToeBoard = {"b","b","b","b","b","b","b","b","b"};

    //Update server or client GUI
    boolean updateServerUI;
    boolean updateClientUI;

    //Stores if the game ended
    boolean winnerFound;

    //Stores if the client is a new player
    boolean newPlayer;

    //Stores if a player disconnected
    boolean isDisconnect;

    //Stores the ID of the player that disconnected
    int whoDisconnected;

    //Stores the game difficulty
    int gameDifficulty;

    //Stores the playerID
    int playerID;

    // Current score
    int playerScore;

    //Stores the amount of players on the server
    int playerCount;

    //Stores who won the game
    String whoWon;

    //check to see which one the server updated
    int whoMadeMove;

    // which player is on top
    int firstPlace = -1;
    int secondPlace = -1;
    int thirdPlace = -1;

    // scores of top players
    int firstPlaceScore = -1;
    int secondPlaceScore = -1;
    int thirdPlaceScore = -1;
}
