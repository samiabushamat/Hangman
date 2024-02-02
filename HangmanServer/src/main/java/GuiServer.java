
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{

	
	TextField s1,s2,s3,s4, c1;
	Button serverChoice,b1;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	VBox buttonBox;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	TextField portInput;
	
	ListView<String> listItems, listItems2;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Server");
		portInput = new TextField();
		this.serverChoice = new Button("Enter a Valid Port");
		this.serverChoice.setStyle("-fx-pref-width: 100px");
		this.serverChoice.setStyle("-fx-pref-height: 28px");

		this.serverChoice.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));
											primaryStage.setTitle("This is the Server");
			String port = portInput.getText();
			int portNumber = Integer.parseInt(port);
				serverConnection = new Server(portNumber,data -> {
					Platform.runLater(()->{
						listItems.getItems().add(data.toString());
					});

				});
											
		});

		HBox hbox = new HBox(portInput,serverChoice); // 10 is the spacing between the TextField and Button
		hbox.setSpacing(10);


		BorderPane startPane = new BorderPane();

		startPane.setStyle("-fx-background-color: blue;");
		startPane.setPadding(new Insets(100));
		startPane.setCenter(hbox);

		startScene = new Scene(startPane, 500,400);
		
		listItems = new ListView<String>();
		listItems2 = new ListView<String>();
		
		c1 = new TextField();
		b1 = new Button("Send");
		
		sceneMap = new HashMap<String, Scene>();
		
		sceneMap.put("server",  createServerGui());
		
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
	
	public Scene createServerGui() {
		
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: coral");
		
		pane.setCenter(listItems);
	
		return new Scene(pane, 500, 400);
		
		
	}

}
