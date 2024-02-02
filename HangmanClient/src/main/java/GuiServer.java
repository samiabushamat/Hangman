
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{

	
	TextField s1,s2,s3,s4, c1;
	Button clientChoice,b1;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Client clientConnection;
	TextField portInput;
	
	ListView<String> listItems, listItems2;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Hangman Game");
		portInput = new TextField();
		this.clientChoice = new Button("Enter a Valid Port");
		this.clientChoice.setStyle("-fx-pref-width: 100px");
		this.clientChoice.setStyle("-fx-pref-height: 25px");
		TextFlow textFlow = new TextFlow();

		// Creating a paragraph of text using Text objects

		Text text1 = new Text("Welcome to the Hangman Game! \n");
		Text text2 = new Text("You will select from one of the three following categories\n");
		Text text3 = new Text("cars, animals, and names\n");
		Text text4 = new Text("After a category is selected you enter a lowercase letter and see if that letter exists in the random word and what indices it is at\n");
		Text text5 = new Text("If you believe that you are ready to guess the word type 'G yourGuess'\n");
		Text text6 = new Text("Once you guess the word correct in each category you can either play again by inputting 'A' or quit by inputting 'Q' Have Fun!\n");

		text1.setFont(Font.font(15));
		text2.setFont(Font.font(15));
		text3.setFont(Font.font(15));
		text4.setFont(Font.font(15));
		text5.setFont(Font.font(15));
		text6.setFont(Font.font(15));


		// Adding the Text objects to the TextFlow
		textFlow.getChildren().addAll(text1,text2,text3,text4,text5,text6);

		// Creating a VBox layout and adding the TextFlow to it
		VBox vbox = new VBox(textFlow);
		
		this.clientChoice.setOnAction(e-> {primaryStage.setScene(sceneMap.get("client"));
											primaryStage.setTitle("Player");
			String port = portInput.getText();
			int portNumber = Integer.parseInt(port);
											clientConnection = new Client(portNumber,data->{
							Platform.runLater(()->{listItems2.getItems().add(data.toString());
											});
							});
							
											clientConnection.start();
		});

		this.buttonBox = new HBox(portInput, clientChoice);
		buttonBox.setSpacing(10);

		startPane = new BorderPane();
		startPane.setPadding(new Insets(100));

		startPane.setBottom(buttonBox);
		startPane.setTop(vbox);
		startPane.setStyle("-fx-background-color: red");

		startScene = new Scene(startPane, 700,500);
		
		listItems = new ListView<String>();
		listItems2 = new ListView<String>();
		
		c1 = new TextField();
		b1 = new Button("Send");
		b1.setOnAction(e->{clientConnection.send(c1.getText()); c1.clear();});
		
		sceneMap = new HashMap<String, Scene>();

		sceneMap.put("client",  createClientGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
		 
		
		primaryStage.setScene(startScene);
		primaryStage.show();
		
	}

	public Scene createClientGui() {
		
		clientBox = new VBox(10, c1,b1,listItems2);
		clientBox.setStyle("-fx-background-color: blue");
		return new Scene(clientBox, 500, 400);

	}

}
