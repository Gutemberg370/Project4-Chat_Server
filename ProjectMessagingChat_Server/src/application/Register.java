package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;



import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Register extends Application{
	
	private int port;
	private String name;
	
	// Criar página de login do servidor
    private Parent createLogin() {
    	Pane root = new Pane();
    	
    	BackgroundFill backgroundFill = new BackgroundFill(Color.valueOf("#6194F5"), new CornerRadii(10), new Insets(10));

    	Background background = new Background(backgroundFill);
    	
    	root.setBackground(background);
    	
    	root.setPrefSize(504, 322);
    	
    	Label serverTitle = new Label("LOGIN - SERVIDOR");
    	serverTitle.setFont(new Font("Monaco",36));
    	serverTitle.setLayoutX(110);
    	serverTitle.setLayoutY(15);
    	
    	Label title = new Label("Insira o nome do servidor e a porta em que \n ele irá operar. Logo após clique \n no botão para iniciá-lo.");
    	title.setFont(new Font("Arial",18));
    	title.setLayoutX(90);
    	title.setLayoutY(80);
    	title.setTextAlignment(TextAlignment.CENTER);
    	
    	Label name = new Label("Nome :");
    	name.setFont(new Font("Arial",13));
    	name.setLayoutX(95);
    	name.setLayoutY(175);
    	
    	TextField nameInput = new TextField("Server");
    	nameInput.setLayoutX(145);
    	nameInput.setLayoutY(170);
    	nameInput.setMinWidth(220);
    	
    	Label port = new Label("Porta  :");
    	port.setFont(new Font("Arial",13));
    	port.setLayoutX(95);
    	port.setLayoutY(225);
    	
    	TextField portInput = new TextField("6000");
    	portInput.setLayoutX(145);
    	portInput.setLayoutY(220);
    	portInput.setMinWidth(220);
    	
    	Button loginButton = new Button("Iniciar Servidor");
    	loginButton.setLayoutX(180);
    	loginButton.setLayoutY(270);
    	loginButton.setMinWidth(150);
    	loginButton.setOnAction(event -> {
    		this.name = nameInput.getText();
    		this.port = Integer.valueOf(portInput.getText());
        	Stage window = (Stage)loginButton.getScene().getWindow();
        	Scene scene = new Scene(createScene());
        	window.setScene(scene);
        	window.setResizable(false);
        	
			try {
				// Registrar o servidor no servidor de nomes
				Registry rmiregistry = LocateRegistry.createRegistry(this.port); // Número da porta
				rmiregistry.bind(this.name, new Server());
			} catch (RemoteException | AlreadyBoundException e) {
				e.printStackTrace();
			}
			System.out.println("Servidor está funcionando...");
        });
    	
    	root.getChildren().addAll(serverTitle, title, name, nameInput, port, portInput, loginButton);
    	
    	return root;
    }
	
    // Criar página "Servidor Online"
	private Parent createScene() {
    	Pane root = new Pane();
    	
    	BackgroundFill backgroundFill = new BackgroundFill(Color.valueOf("#5CDB95"), new CornerRadii(10), new Insets(10));

    	Background background = new Background(backgroundFill);
    	
    	root.setBackground(background);
    	
    	root.setPrefSize(504, 322);
    	              	
    	Label server = new Label("SERVIDOR ONLINE");
    	server.setFont(new Font("Monaco",36));
    	server.setLayoutX(100);
    	server.setLayoutY(55);
    	server.setTextAlignment(TextAlignment.CENTER);
    	
    	String serverName = String.format("NOME: %s", this.name.toUpperCase());
    	Label serverNameLabel = new Label(serverName);
    	serverNameLabel.setFont(new Font("Monaco",20));
    	serverNameLabel.setLayoutX(100);
    	serverNameLabel.setLayoutY(145);
    	
    	String serverPort = String.format("PORTA: %d", this.port);
    	Label serverPortLabel = new Label(serverPort);
    	serverPortLabel.setFont(new Font("Monaco",20));
    	serverPortLabel.setLayoutX(100);
    	serverPortLabel.setLayoutY(205);
    	serverPortLabel.setTextAlignment(TextAlignment.CENTER);
  	
    	root.getChildren().addAll(server, serverNameLabel, serverPortLabel);
    	
    	return root;
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Scene loginScene = new Scene(createLogin());
			primaryStage.setTitle("Servidor");;
			primaryStage.setScene(loginScene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static void main(String[] args) throws Exception{
		launch(args);
	}

}
