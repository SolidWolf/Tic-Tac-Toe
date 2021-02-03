import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TicTacToe extends Application {
	
	//Hash map to store all the scenes
	HashMap<String, Scene> sceneMap = new HashMap<String, Scene>();
	
	//Store the IP and Port
	String IPAddress;
	int portNum;
	
	//client
	Client clientConnection;
	
	String gameDifficulty;
	int difficultyInt;
	String[] board;
	
	ListView<String> top3List = new ListView<String>();
	
	TextField portTextField;
	TextField IPTextField;
	
	//Declare the buttons
	Button loginBtn;
	Button easyBtn;
	Button mediumBtn;
	Button expertBtn;
	Button playAgainBtn;
	Button quitBtn;
	
	//image views
	ImageView boardPos00;
	ImageView boardPos01;
	ImageView boardPos02;
	ImageView boardPos10;
	ImageView boardPos11;
	ImageView boardPos12;
	ImageView boardPos20;
	ImageView boardPos21;
	ImageView boardPos22;
	
	//create an array to store all image views
	//ImageView[] imageBoard = {boardPos00, boardPos01, boardPos02,
	//		boardPos10, boardPos11, boardPos12, boardPos20, boardPos21, boardPos22};
	ArrayList<ImageView> imageBoard;
	
	int playerID;
	GameInfo gameState = new GameInfo();
	String tictactoeboard[];

	//Create the symbols for the X and O
	Image availible = new Image("ChooseText.JPG", 150,130, false,false);
	Image XImage = new Image("XSymbol.JPG", 150,130,false,false);
	Image OImage = new Image("OSymbol.JPG", 150,130,false,false);
	
	Label top3Label;
	MenuBar menuBar;
	Menu menu;
	
	//Event Handlers
	EventHandler<ActionEvent> difficultySelect;
	EventHandler<ActionEvent> quitBtnHandler;
	EventHandler<ActionEvent> playAgainBtnHandler;
	EventHandler<MouseEvent> placementSelection;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Let's Play Tic Tac Toe!!!");
		
		top3Label = new Label("Top 3 Players");
		menuBar = new MenuBar();
		menu = new Menu("", top3Label);
		menuBar.getMenus().add(menu);
		
		sceneMap.put("StartGUI", getStartScene());
		sceneMap.put("difficultyGUI", getDifficultyScene());
		sceneMap.put("top3GUI", getTop3Scene());

		Scene startScene = sceneMap.get("StartGUI");
		primaryStage.setScene(startScene);
		primaryStage.show();
		
		//Event handler for the login button
		loginBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				
				try {
					//Get the port and IP address
					IPAddress = IPTextField.getText();
					portNum = Integer.parseInt(portTextField.getText());
					
					//Change the scene for users to select the difficulty
					Scene difficultyScene = sceneMap.get("difficultyGUI");
					
					primaryStage.setScene(difficultyScene);
				}catch(Exception e) {}
				
				clientConnection = new Client(data->{
					Platform.runLater(()->{
						gameState = (GameInfo) data;
						
						//Check if a new player connected
						if(gameState.newPlayer == true) {
							playerID = gameState.playerID;
						}
						else {
							//Check if that was the player that connected
							if(playerID == gameState.whoMadeMove) {

								if(!gameState.whoWon.equals("Player")) {
									
									//Update the current board with the gameState board
									for(int i = 0; i < 9; i++) {
										tictactoeboard[i] = gameState.TicTacToeBoard[i];
									}
									
									//update the board accordingly
									for(int i = 0; i < 9; i++) {
										//check if it is an X
										if(tictactoeboard[i].equals("X")) {
											imageBoard.get(i).setImage(XImage);
										}
									}
								}
								
								//Check if a winner was found
								if (gameState.winnerFound == true) {
									//re-enable the buttons
									playAgainBtn.setDisable(false);
									quitBtn.setDisable(false);
								}
								else {
									for(int i = 0; i < 9; i++) {
										//re-enable the images to be clickable again
										if(tictactoeboard[i].equals("b")) {
											imageBoard.get(i).setDisable(false);
										}
									}
								}
							}
						}
												
						//update leaderboards////////////////////////////////////////////////////////
						top3List.getItems().clear();
						if(gameState.firstPlace != -1){
							top3List.getItems().add("1. Player " + gameState.firstPlace + ": " + gameState.firstPlaceScore);
						} else{
							top3List.getItems().add("1.");
						}
						if(gameState.secondPlace != -1){
							top3List.getItems().add("2. Player " + gameState.secondPlace + ": " + gameState.secondPlaceScore);
						} else{
							top3List.getItems().add("2.");
						}
						if(gameState.thirdPlace != -1){
							top3List.getItems().add("3. Player " + gameState.thirdPlace + ": " + gameState.thirdPlaceScore);
						} else{
							top3List.getItems().add("3.");
						}
					});
				}, IPAddress, portNum);
				
				clientConnection.start();
			}
		});
		
		top3Label.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event) {
				//Create a new stage 
				Stage top3Stage = new Stage();
				top3Stage.setTitle("Top 3 Players");

				top3Stage.setScene(getTop3Scene());
				top3Stage.show();
			}
			
		});
		
		difficultySelect = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				Button difficulty = (Button) event.getSource();

				gameDifficulty = difficulty.getText();
				if(gameDifficulty.equals("Easy")){
					difficultyInt = 1;
				} else if(gameDifficulty.equals("Medium")){
					difficultyInt = 2;
				} else if(gameDifficulty.equals("Expert")){
					difficultyInt = 3;
				}
				
				tictactoeboard = new String[9];
				for (int i = 0; i < 9; i++) {
					tictactoeboard[i] = "b";
				}
				
				imageBoard = new ArrayList<ImageView>();
				
				Scene playingScene = getPlayingScene();
				primaryStage.setScene(playingScene);
			}
		};
		
		//Attach event handlers
		easyBtn.setOnAction(difficultySelect);
		mediumBtn.setOnAction(difficultySelect);
		expertBtn.setOnAction(difficultySelect);
		
		placementSelection = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				ImageView selectedPos = (ImageView) event.getSource();
				
				selectedPos.setImage(OImage);
				boardPos00.setDisable(true);
				boardPos01.setDisable(true);
				boardPos02.setDisable(true);
				boardPos10.setDisable(true);
				boardPos11.setDisable(true);
				boardPos12.setDisable(true);
				boardPos20.setDisable(true);
				boardPos21.setDisable(true);
			    boardPos22.setDisable(true);
			    
			    for(int i = 0; i < 9; i++) {
			    	if(selectedPos.equals(imageBoard.get(i))) {
			    		tictactoeboard[i] = "O";
			    		break;
			    	}
			    }
			    
			    //update the string array to send to server//////////////////////////////////////////////
			    for(int i = 0; i < 9; i++) {
			    	gameState.TicTacToeBoard[i] = tictactoeboard[i];
			    }
			    			    
			    //send information to server
			    gameState.whoMadeMove = playerID;
			    gameState.gameDifficulty = difficultyInt;
			    
			    clientConnection.send(gameState);	
			}
		};

		//Event handler for the quit button
		quitBtnHandler = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Platform.exit();
				System.exit(0);
			}
		}; 
				
		//Event handler for the play again button
		playAgainBtnHandler = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Scene difficultyScene = getDifficultyScene();
				primaryStage.setScene(difficultyScene);
			} 
		};
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
	}
	
	public Scene getStartScene() {
		VBox startVBox = new VBox();
		startVBox.setStyle("-fx-background-color: lightblue");

		//Declare the title
		Label title = new Label("Tic-Tac-Toe Client Login");
		
		//Style the title
		title.setAlignment(Pos.CENTER);
		title.setPrefSize(500,40);
		title.setStyle("-fx-background-color: lightblue;");
		title.setFont(new Font("Arial", 40));
		
		//Declare HBox for the port line
		HBox portArea = new HBox();			
		
		//Set text for the port
		Text portText = new Text("Port:");
		portText.setFont(new Font("Arial", 30));
				
		//Declare textField for the port number
		portTextField = new TextField("5555");
		portTextField.setPrefHeight(40);
		portTextField.setPrefWidth(100);
				
		//Add to the port HBox
		portArea.getChildren().addAll(portText,portTextField);		
		
		//Declare HBox for the IP line
		HBox IPArea = new HBox();			
		
		//Set text for the port
		Text IPText = new Text("IP Address:");
		IPText.setFont(new Font("Arial", 30));
		
		//Declare Textfield for the IP address
		IPTextField = new TextField("127.0.0.1");
		IPTextField.setPrefHeight(40);
		IPTextField.setPrefWidth(100);
		
		//Add to the IP HBox
		IPArea.getChildren().addAll(IPText, IPTextField);
				
		//Declare login in button and set style
		loginBtn = new Button("Login");
		loginBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
		loginBtn.setFont(new Font(18));
		loginBtn.setPrefHeight(40);
		loginBtn.setPrefWidth(100);

		//Add to the start VBox
		startVBox.getChildren().addAll(title, IPArea, portArea, loginBtn);
		
		//Set margins for the VBOx
		VBox.setMargin(title, new Insets(10,0,0,0));
		VBox.setMargin(IPArea, new Insets(40,0,0,68));		
		VBox.setMargin(portArea, new Insets(30,0,0,160));
		VBox.setMargin(loginBtn, new Insets(45,0,0,225));

		return new Scene(startVBox, 500, 350);
	}
	
	public Scene getTop3Scene() {
		
		VBox top3VBox = new VBox();
		top3VBox.setStyle("-fx-background-color: lightblue;");

		Label top3Text = new Label("Top 3 Players");
		top3Text.setFont(new Font("Arial",40));

		top3List.setMaxHeight(150);
		top3List.setMaxWidth(200);
		
		top3VBox.getChildren().addAll(top3Text,top3List);
		
		//Set margins for the VBox
		VBox.setMargin(top3Text, new Insets(10,0,0,22));
		VBox.setMargin(top3List, new Insets(40,0,0,50));
		
		return new Scene(top3VBox, 300, 350);
	}
	
	public Scene getDifficultyScene() {
		
		VBox difficultyVBox = new VBox();
		difficultyVBox.setStyle("-fx-background-color: lightblue;");
		
		Label difficultyText = new Label("Select Game Difficulty");
		
		//Style the title
		difficultyText.setAlignment(Pos.CENTER);
		difficultyText.setPrefSize(500,40);
		difficultyText.setStyle("-fx-background-color: lightblue;");
		difficultyText.setFont(new Font("Arial", 40));
		
		//Create easy button and style it
		easyBtn = new Button("Easy");
		easyBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
		easyBtn.setPrefHeight(40);
		easyBtn.setPrefWidth(120);
		easyBtn.setFont(new Font("Arial",20));
		
		//Create medium button and style it
		mediumBtn = new Button("Medium");
		mediumBtn.setStyle("-fx-background-color: yellow; -fx-text-fill: white;");
		mediumBtn.setPrefHeight(40);
		mediumBtn.setPrefWidth(120);
		mediumBtn.setFont(new Font("Arial",20));
		
		//Create expert button and style it
		expertBtn = new Button("Expert");
		expertBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
		expertBtn.setPrefHeight(40);
		expertBtn.setPrefWidth(120);
		expertBtn.setFont(new Font("Arial",20));
		
		easyBtn.setOnAction(difficultySelect);
		mediumBtn.setOnAction(difficultySelect);
		expertBtn.setOnAction(difficultySelect);
		
		//Set margins for the VBox
		VBox.setMargin(difficultyText, new Insets(10,0,0,0));
		VBox.setMargin(easyBtn, new Insets(40,0,0,190));
		VBox.setMargin(mediumBtn, new Insets(30,0,0,190));
		VBox.setMargin(expertBtn, new Insets(30,0,0,190));

		difficultyVBox.getChildren().addAll(menuBar,difficultyText, easyBtn, mediumBtn, expertBtn);
		
		return new Scene(difficultyVBox, 500, 350);
	}
	
	public Scene getPlayingScene() {
		
		//Create the background image of the board
		BackgroundImage gameBoard= new BackgroundImage(new Image("gameBackground.JPG",900,600,false,false),
		        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
		          BackgroundSize.DEFAULT);
		
		VBox playingVBox = new VBox();
		playingVBox.setBackground(new Background(gameBoard));
		
		Text displayDifficulty = new Text("Current Difficulty: " + gameDifficulty);
		displayDifficulty.setFont(new Font("Arial",16));
		
		//Create play again button and set style for it
		playAgainBtn = new Button("Play Again");
		playAgainBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
		playAgainBtn.setPrefHeight(40);
		playAgainBtn.setPrefWidth(100);
		playAgainBtn.setFont(new Font("Arial",16));

		//Create quit button and set style for it
		quitBtn = new Button("Quit");
		quitBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
		quitBtn.setPrefHeight(40);
		quitBtn.setPrefWidth(100);
		quitBtn.setFont(new Font("Arial",16));

		playAgainBtn.setOnAction(playAgainBtnHandler);
		quitBtn.setOnAction(quitBtnHandler);
		
		playAgainBtn.setDisable(true);
		quitBtn.setDisable(true);
		
		HBox playAndQuitHBox = new HBox();
		playAndQuitHBox.getChildren().addAll(displayDifficulty, playAgainBtn, quitBtn);
		
		//Create the imageViews for the board
		boardPos00 = new ImageView(availible);
		boardPos01 = new ImageView(availible);
		boardPos02 = new ImageView(availible);
		boardPos10 = new ImageView(availible);
		boardPos11 = new ImageView(availible);
		boardPos12 = new ImageView(availible);
		boardPos20 = new ImageView(availible);
		boardPos21 = new ImageView(availible);
		boardPos22 = new ImageView(availible);
		
		imageBoard.add(boardPos00);
		imageBoard.add(boardPos01);
		imageBoard.add(boardPos02);
		imageBoard.add(boardPos10);
		imageBoard.add(boardPos11);
		imageBoard.add(boardPos12);
		imageBoard.add(boardPos20);
		imageBoard.add(boardPos21);
		imageBoard.add(boardPos22);
		
		//Add Event Handlers for the board placements
		boardPos00.setOnMouseClicked(placementSelection); 
		boardPos01.setOnMouseClicked(placementSelection); 
		boardPos02.setOnMouseClicked(placementSelection); 
		boardPos10.setOnMouseClicked(placementSelection); 
		boardPos11.setOnMouseClicked(placementSelection); 
		boardPos12.setOnMouseClicked(placementSelection); 
		boardPos20.setOnMouseClicked(placementSelection); 
		boardPos21.setOnMouseClicked(placementSelection); 
		boardPos22.setOnMouseClicked(placementSelection); 

		HBox row1Images = new HBox();
		row1Images.getChildren().addAll(boardPos00,boardPos01,boardPos02);
		
		HBox row2Images = new HBox();
		row2Images.getChildren().addAll(boardPos10,boardPos11,boardPos12);
		
		HBox row3Images = new HBox();
		row3Images.getChildren().addAll(boardPos20,boardPos21,boardPos22);
		
		playingVBox.getChildren().addAll(menuBar,playAndQuitHBox, row1Images, row2Images,row3Images);

		//Set margins for the HBox
		HBox.setMargin(displayDifficulty, new Insets(0,0,0, 50));
		HBox.setMargin(playAgainBtn, new Insets(0,0,0,250));
		HBox.setMargin(quitBtn, new Insets(0,0,0,100));
		HBox.setMargin(boardPos00, new Insets(0,0,0,160));
		HBox.setMargin(boardPos01, new Insets(0,0,0,60));
		HBox.setMargin(boardPos02, new Insets(0,0,0,60));
		HBox.setMargin(boardPos10, new Insets(0,0,0,160));
		HBox.setMargin(boardPos11, new Insets(0,0,0,60));
		HBox.setMargin(boardPos12, new Insets(0,0,0,60));
		HBox.setMargin(boardPos20, new Insets(0,0,0,160));
		HBox.setMargin(boardPos21, new Insets(0,0,0,60));
		HBox.setMargin(boardPos22, new Insets(0,0,0,60));

		//Set margins for the VBox
		VBox.setMargin(playAndQuitHBox, new Insets(20,0,0,0));
		VBox.setMargin(row1Images, new Insets(25,0,0,0));
		VBox.setMargin(row2Images, new Insets(25,0,0,0));
		VBox.setMargin(row3Images, new Insets(20,0,0,0));
		
		return new Scene(playingVBox, 900, 600);
	}
}
