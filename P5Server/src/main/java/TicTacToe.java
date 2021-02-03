

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
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.HashMap;



public class TicTacToe extends Application {

	HashMap<String, Scene> sceneMap;
	Server serverConnection;
	ListView<String> serverListView, scoresListView;
	TextField portTextField;
	//HBox portArea;
	Button startBtn;
	//VBox serverScreen;
	Text title, Scores;
	Scene startScene;

	EventHandler<ActionEvent> startBtnHandler;
	int portNum;
	GameInfo gameState = new GameInfo();
	ArrayList<Integer> leaderboard = new ArrayList<Integer>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Let's Play TicTacToe!");

		sceneMap = new HashMap<String,Scene>();
		sceneMap.put("startGUI", startGUI());
		primaryStage.setScene(sceneMap.get("startGUI"));
		primaryStage.show();


		startBtnHandler = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				try {
					portNum = Integer.parseInt(portTextField.getText());
					sceneMap.put("serverScene", getServerScene());
					primaryStage.setScene(sceneMap.get("serverScene"));
					primaryStage.show();
				}
				catch(Exception e) {}
				serverConnection = new Server(data -> {
					Platform.runLater(()->{
						GameInfo gameState = (GameInfo) data;

						//Check if a new player connected
						if(gameState.newPlayer == true) {
							serverListView.getItems().add("Player " + gameState.playerID + " has connected");
						}

						else if(gameState.winnerFound == true) {

							String winner = "draw";

							//Check if a player or computer won
							if (gameState.whoWon.equals("Computer"))
								winner = "Computer";
							else if (gameState.whoWon.equals("Player"))
								winner = "Player " + gameState.playerID;

							//Check if game ended in a draw
							if (winner == "draw") {
								serverListView.getItems().add("Player " + gameState.playerID + " vs Computer game has ended in a draw!");
							} else {
								serverListView.getItems().add("Player " + gameState.playerID + " vs Computer game has ended, " + winner + " wins!");
							}
							
							if (gameState.whoWon.equals("Player")) {
								
								scoresListView.getItems().clear();
								if(gameState.firstPlace != -1){
									scoresListView.getItems().add("1. Player " + gameState.firstPlace + ": " + gameState.firstPlaceScore);
								} else{
									scoresListView.getItems().add("1.");
								}
								if(gameState.secondPlace != -1){
									scoresListView.getItems().add("2. Player " + gameState.secondPlace + ": " + gameState.secondPlaceScore);
								} else{
									scoresListView.getItems().add("2.");
								}
								if(gameState.thirdPlace != -1){
									scoresListView.getItems().add("3. Player " + gameState.thirdPlace + ": " + gameState.thirdPlaceScore);
								} else{
									scoresListView.getItems().add("3.");
								}
							}
						}

						//Check if a player disconnected
						else if(gameState.isDisconnect == true) {
							serverListView.getItems().add("Player " + gameState.whoDisconnected + " has disconnected!");
						}

					});
				}, portNum);
			}
		};

		startBtn.setOnAction(startBtnHandler);

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public Scene startGUI() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: lightgoldenrodyellow");

		// title section
		VBox titleVbox = new VBox();
		Text titleText = new Text("TicTacToe");
		titleText.setFont(Font.font("Arial",53));
		titleText.setFill(Color.color(215/255.0,169/255.0,32/255.0));

		Text titleText2 = new Text("Server");
		titleText2.setFont(Font.font("Arial", FontPosture.ITALIC,23));
		titleText2.setFill(Color.color(215/255.0,169/255.0,32/255.0));

		titleVbox.setSpacing(-10);
		titleVbox.getChildren().addAll(titleText,titleText2);
		titleVbox.setAlignment(Pos.CENTER);

		HBox portArea = new HBox();
		portArea.setAlignment(Pos.CENTER);

		Label portLabel = new Label("Port");
		portLabel.setAlignment(Pos.CENTER);
		portLabel.setPrefHeight(65);
		portLabel.setPrefWidth(110);
		portLabel.setStyle("-fx-background-color: gold;");
		portLabel.setFont(Font.font("Arial",26));

		portTextField = new TextField();
		portTextField.setAlignment(Pos.CENTER);
		portTextField.setPrefHeight(65);
		portTextField.setPrefWidth(215);
		portTextField.setPromptText("5555");
		portTextField.setFont(Font.font("Arial",30));

		startBtn = new Button("Start");
		startBtn.setAlignment(Pos.CENTER);
		startBtn.setPrefHeight(65);
		startBtn.setPrefWidth(110);
		startBtn.setStyle("-fx-background-color: palegreen;");
		startBtn.setFont(Font.font("Arial",26));

		portArea.getChildren().addAll(portLabel,portTextField,startBtn);

		pane.setCenter(portArea);
		pane.setTop(titleVbox);

		return new Scene(pane, 550, 330);
	}


	public Scene getServerScene() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(40));
		pane.setStyle("-fx-background-color: lightgoldenrodyellow");

		//this.serverScreen = new VBox();
		//this.serverScreen.setStyle("-fx-background-color: lightgoldenrodyellow");
		//this.serverScreen.setPadding(new Insets(20));

		/*Label titleLabel = new Label("TicTacToe Server");
		titleLabel.setAlignment(Pos.CENTER);
		titleLabel.setPrefSize(500,40);
		titleLabel.setStyle("-fx-background-color: lightblue;");
		titleLabel.setFont(new Font("Arial", 40));*/

		HBox titleHbox = new HBox(15);
		Text titleText = new Text("TicTacToe");
		titleText.setFont(Font.font("Arial",53));
		titleText.setFill(Color.color(215/255.0,169/255.0,32/255.0));

		Text titleText2 = new Text("Server");
		titleText2.setFont(Font.font("Arial", FontPosture.ITALIC,53));
		titleText2.setFill(Color.color(215/255.0,169/255.0,32/255.0));
		titleHbox.setAlignment(Pos.CENTER);
		titleHbox.getChildren().addAll(titleText,titleText2);
		pane.setTop(titleHbox);

		this.serverListView = new ListView<String>();
		this.serverListView.setPrefWidth(600);
		this.serverListView.setPrefHeight(800);

		HBox bodyHbox= new HBox();
		bodyHbox.setAlignment(Pos.CENTER);

		VBox actionsVBox = new VBox(10);
		actionsVBox.setMaxHeight(600);
		actionsVBox.setMaxWidth(600);
		actionsVBox.setAlignment(Pos.CENTER);

		Label actionsLabel = new Label("- Action List -");
		actionsLabel.setPrefSize(600, 25);
		actionsLabel.setFont(Font.font("Arial",25));
		actionsLabel.setAlignment(Pos.CENTER);
		actionsLabel.setTextFill(Color.color(215/255.0,169/255.0,32/255.0));
		actionsVBox.getChildren().addAll(actionsLabel, serverListView);

		VBox scoresVBox = new VBox(10);
		scoresVBox.setMaxHeight(600);
		scoresVBox.setMaxWidth(200);
		scoresVBox.setAlignment(Pos.CENTER);

		Label scoresLabel = new Label(" - Scores -  ");
		scoresLabel.setPrefSize(260, 25);
		scoresLabel.setAlignment(Pos.CENTER);
		scoresLabel.setFont(new Font("Arial", 25));
		scoresLabel.setTextFill(Color.color(215/255.0,169/255.0,32/255.0));

		this.scoresListView = new ListView<String>();
		this.scoresListView.setPrefHeight(600);
		this.scoresListView.setPrefWidth(260);

		scoresVBox.getChildren().addAll(scoresLabel, this.scoresListView);
		bodyHbox.getChildren().addAll(actionsVBox, scoresVBox);
		bodyHbox.setSpacing(10);

		titleHbox.setMargin(bodyHbox, new Insets(0,0,40,0));
		//titleHbox.setPadding(new Insets(-20));
		bodyHbox.setPadding(new Insets(43));

		pane.setCenter(bodyHbox);

		return new Scene(pane, 900, 600);
	}

}
