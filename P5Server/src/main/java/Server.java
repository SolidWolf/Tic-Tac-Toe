import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;


public class Server {

	//Store the clients in the arraylist
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();

	private int newIDNumber = 1;

	//Receive information from the client
	private Consumer<Serializable> callback;

	ServerThread mainServer;

	FindNextMove findNextMove;

	//The port the server is running on
	int serverPort;

	GameInfo gameState;
	ArrayList<Integer> leaderboard = new ArrayList<Integer>();

	//Constructor for the server class
	Server(Consumer<Serializable> call, int port) {
		callback = call;					//Set the call
		serverPort = port;					//Get the port
		gameState = new GameInfo();			//Get a new gameInfo state
		mainServer = new ServerThread();	//Create the server thread
		mainServer.start();					//Start the server thread
		findNextMove = new FindNextMove();	///////////////////////////////////////////
		findNextMove.start();//////////////////////////////////////////////////////
	}

	private int checkWinner(String[] board,String checkFor) {

		//check if a winner was found
		String winner = winnerFound(board);

		if (winner.equals(checkFor))
			return 0;
		else if (winner.equals("draw"))
			return 1;
		else
			return 2;
	}

	private String winnerFound(String[] board) {

		if(board[0].equals("O") && board[1].equals("O") && board[2].equals("O")) //horizontal top
		{
			return "O";
		}

		if(board[3].equals("O") && board[4].equals("O") && board[5].equals("O"))//horizontal middle
		{
			return "O";
		}

		if(board[6].equals("O") && board[7].equals("O") && board[8].equals("O"))//horizontal bottom
		{
			return "O";
		}

		if(board[0].equals("O") && board[3].equals("O") && board[6].equals("O"))//vert right
		{
			return "O";
		}

		if(board[1].equals("O") && board[4].equals("O") && board[7].equals("O"))//vert middle
		{
			return "O";
		}

		if(board[2].equals("O") && board[5].equals("O") && board[8].equals("O"))//vert left
		{
			return "O";
		}

		if(board[0].equals("O") && board[4].equals("O") && board[8].equals("O"))// diag from top left
		{
			return "O";
		}

		if(board[2].equals("O") && board[4].equals("O") && board[6].equals("O"))// diag from top right
		{
			return "O";
		}

		if(board[0].equals("X") && board[1].equals("X") && board[2].equals("X")) //horizontal top
		{
			return "X";
		}

		if(board[3].equals("X") && board[4].equals("X") && board[5].equals("X"))//horizontal middle
		{
			return "X";
		}

		if(board[6].equals("X") && board[7].equals("X") && board[8].equals("X"))//horizontal bottom
		{
			return "X";
		}

		if(board[0].equals("X") && board[3].equals("X") && board[6].equals("X"))//vert right
		{
			return "X";
		}

		if(board[1].equals("X") && board[4].equals("X") && board[7].equals("X"))//vert middle
		{
			return "X";
		}

		if(board[2].equals("X") && board[5].equals("X") && board[8].equals("X"))//vert left
		{
			return "X";
		}

		if(board[0].equals("X") && board[4].equals("X") && board[8].equals("X"))// diag from top left
		{
			return "X";
		}

		if(board[2].equals("X") && board[4].equals("X") && board[6].equals("X"))// diag from top right
		{
			return "X";
		}

		for (int i = 0; i < 9; i++) {
			if(board[i].equals("b")) {
				return "N/A";
			}
		}

		return "draw";
	}

	class ServerThread extends Thread {
		public void run() {
			try(ServerSocket serverSocket = new ServerSocket(serverPort)) {

				while(true) {

					ClientThread client = new ClientThread(serverSocket.accept());
					clients.add(client);
					client.start();

					newIDNumber++;				//Increment ID
				}

			}catch(Exception e) {

			}
		}
	}


	class ClientThread extends Thread {

		//Set the socket to connect
		Socket connection;

		//Set the input and the output streams
		ObjectInputStream input;
		ObjectOutputStream output;

		//Determine which thread it is
		int threadNum;

		ClientThread(Socket server) {
			this.connection = server;
			this.threadNum = newIDNumber;
		}

		//Send the game state to all the clients on the server
		public synchronized void updateClients(GameInfo state) {
			int numClients = clients.size();
			for(int i = 0; i < numClients; i++) {
				ClientThread c = clients.get(i);
				try {
					state.playerID = c.threadNum;				//Get the id of the thread

					if (state.playerID == state.whoMadeMove) {
						if (state.winnerFound == false) {
							findNextMove.difficulty = state.gameDifficulty;
							for(int j = 0; j < 9; j++) {
								findNextMove.board[j] = state.TicTacToeBoard[j];
							}
							findNextMove.run();
							state.TicTacToeBoard[findNextMove.moveRec - 1] = "X";

							//Check if a winner was Found for O
						    int winNum = checkWinner(state.TicTacToeBoard,"X");

						    if (winNum == 0) {
						    	state.winnerFound = true;
						    	state.whoWon = "Computer";
						    }
						}
					}

					c.output.reset();
					c.output.writeObject(state);
				}
				catch(Exception e) {}
			}
		}

		public void run() {

			try {
				//Initialize the input and output streams
				input = new ObjectInputStream(connection.getInputStream());
				output = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			}catch( Exception e) {}

				//set as a new player
				gameState.newPlayer = true;

				gameState.playerID = threadNum;

				//Store how many players are on the server
				gameState.playerCount = clients.size();

				updateClients(gameState);
				callback.accept(gameState);

			while(true) {

				try {
					gameState = (GameInfo) input.readObject();

					//Set new player to false
					gameState.newPlayer = false;
					gameState.whoWon = "N/A";
				    gameState.winnerFound = false;
				    gameState.isDisconnect = false;

					//Check if a winner was Found for O
				    int winNum = checkWinner(gameState.TicTacToeBoard,"O");

				    if (winNum == 0) {
				    	gameState.winnerFound = true;
				    	gameState.whoWon = "Player";
				    }
				    else if (winNum == 1) {
				    	gameState.winnerFound = true;
				    	gameState.whoWon = "draw";
				    }

					//update leaderboard if needed//////////////////////////////////////////////////////////////////
					if(leaderboard.size()-1 < gameState.playerID){ // if leaderboard size is less than playerID
						while(leaderboard.size()-1 < gameState.playerID){
							leaderboard.add(0);
						}
					}

					if(winNum == 0) { // if winner
						leaderboard.set(gameState.playerID, leaderboard.get(gameState.playerID) + 1); // increment score

						//for(int i = 1; i < leaderboard.size(); i++){
							if(leaderboard.get(gameState.playerID) > gameState.firstPlaceScore){ // if new first place
								if(gameState.firstPlace != -1){ // someone already had 1st place, switch places
									gameState.secondPlace = gameState.firstPlace;
									gameState.secondPlaceScore = gameState.firstPlaceScore;
								}
								gameState.firstPlace = gameState.playerID;
								gameState.firstPlaceScore = leaderboard.get(gameState.playerID);
								if(gameState.secondPlace == gameState.firstPlace){
									gameState.secondPlace = -1;
									gameState.secondPlaceScore = -1;
								}
							} else if(leaderboard.get(gameState.playerID) > gameState.secondPlaceScore){ // if new 2nd place
								if(gameState.secondPlace != -1){ // someone already had 2nd place, switch places
									gameState.thirdPlace = gameState.secondPlace;
									gameState.thirdPlaceScore = gameState.secondPlaceScore;
								}
								gameState.secondPlace = gameState.playerID;
								gameState.secondPlaceScore = leaderboard.get(gameState.playerID);
								if(gameState.thirdPlace == gameState.secondPlace){
									gameState.thirdPlace = -1;
									gameState.thirdPlaceScore = -1;
								}
							} else if(leaderboard.get(gameState.playerID) > gameState.thirdPlaceScore){ // if new 3rd place
								gameState.thirdPlace = gameState.playerID;
								gameState.thirdPlaceScore = leaderboard.get(gameState.playerID);
							}
						}
					//}

					//Update the clients and the server
					updateClients(gameState);
					callback.accept(gameState);

				} catch(Exception e) {

					synchronized(gameState) {

						//Remove the client from the arrayList
						clients.remove(this);

						gameState.newPlayer = false;
						gameState.winnerFound = false;

						//Determine who disconnected and sent it to the GUI
						gameState.whoDisconnected = this.threadNum;
						gameState.isDisconnect = true;

						gameState.playerCount = clients.size();		//update number of players

						//Update the server GUI
						callback.accept(gameState);

						//Update the clients
						updateClients(gameState);

				    	break;				//End the loop
					}
				}
			}
		}
	}

}
