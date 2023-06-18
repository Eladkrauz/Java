package mines;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MinesFX extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		HBox hbox;
		MainController controller;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("MinesMain.fxml"));
			hbox = loader.load();
			controller = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Scene scene = new Scene(hbox);
		controller.uploadImages(); // upload all images
		stage.getIcons().add((new ImageView("/mines/Images/mineIcon.png")).getImage());
		stage.setScene(scene);
		stage.setTitle("Minesweeper");
		stage.setResizable(false);
		stage.show();
	}
}