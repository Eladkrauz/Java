package mines;

import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {
	private int heightValue, widthValue, minesNumberValue;
	private Mines boardGame;
	private GridPane grid = new GridPane();
	private static Map<String, ImageView> images; // a map: strings to imageViews

	@FXML
	private VBox vbox;
	@FXML
	private HBox hbox;
	@FXML
	private StackPane stackPane;
	@FXML
	private Label heightLabel;
	@FXML
	private TextArea heightTextArea;
	@FXML
	private ImageView minesSweeperLogo;
	@FXML
	private Label numberOfMinesLabel;
	@FXML
	private TextArea numberOfMinesTextArea;
	@FXML
	private Button startGameButton;
	@FXML
	private Label widthLabel;
	@FXML
	private TextArea widthTextArea;

	// this method maps every string (name) to an imageView
	public void uploadImages() {
		images = new HashMap<>();
		images.put("X", new ImageView("/mines/Images/mineIcon.png")); // a mine
		images.put("F", new ImageView("/mines/Images/flagIcon.png")); // a flag
		for (int i = 1; i <= 8; i++) {
			images.put(i + "", new ImageView("/mines/Images/number" + i + ".png")); // a number
		}
	}

	@FXML
	// this method checks the values in the text areas (height, width and mines).
	// If these values are valid, calls a method to start a new game.
	void checkValues(ActionEvent event) {
		int tempHeightValue, tempWidthValue, tempMinesNumberValue;
		// first checking if the values in the text areas are not integers
		try {
			tempHeightValue = Integer.parseInt(heightTextArea.getText());
			tempWidthValue = Integer.parseInt(widthTextArea.getText());
			tempMinesNumberValue = Integer.parseInt(numberOfMinesTextArea.getText());
		} catch (NumberFormatException e) {
			showMessage("Error", "Please make sure the values are positive integers only", AlertType.ERROR);
			return;
		}

		// now checking if the values are valid
		if (tempHeightValue <= 0 || tempWidthValue <= 0 || tempMinesNumberValue <= 0) {
			showMessage("Error", "Please make sure the values are positive integers only", AlertType.ERROR);
			return;
		}
		if (tempMinesNumberValue >= tempHeightValue * tempWidthValue) {
			showMessage("Error", "Please make sure the number of mines is smaller than the\nsize of the board",
					AlertType.ERROR);
			return;
		}

		// if all values are valid, updating and starting
		heightValue = tempHeightValue;
		widthValue = tempWidthValue;
		minesNumberValue = tempMinesNumberValue;
		startGame();
	}

	private void startGame() {
		// creating the logic board
		boardGame = new Mines(heightValue, widthValue, minesNumberValue);
		// creating the visual board
		grid.getChildren().clear(); // for clearing previous button, if exist
		stackPane.getChildren().clear(); // for clearing previous grid, if exists
		grid.setAlignment(Pos.CENTER);
		SpotButton sb;
		for (int i = 0; i < heightValue; i++) {
			for (int j = 0; j < widthValue; j++) {
				sb = new SpotButton("   ", i, j);
				sb.setOnContextMenuRequested(new RightClickFlag());
				sb.setOnAction(new LeftClick());
				grid.add(sb, j, i);
			}
		}

		stackPane.getChildren().add(grid);

		// modifing the size of the window
		Stage stage = (Stage) startGameButton.getScene().getWindow();
		stage.setWidth(vbox.getWidth() + 25 * widthValue + 100);
		stage.setHeight(Math.max(vbox.getPrefHeight(), 25 * heightValue + 150));
	}

	// this event happens if the player clicked the right button of the mouse on a
	// button on the grid, and it puts a flag or removes a flag from this button
	public class RightClickFlag implements EventHandler<ContextMenuEvent> {
		@Override
		public void handle(ContextMenuEvent event) {
			SpotButton sb = (SpotButton) event.getSource();
			// if the spot is open - does nothing
			if (boardGame.getSpot(sb.getX(), sb.getY()).isOpen())
				return;
			// if already has a flag - will remove it
			if (boardGame.getSpot(sb.getX(), sb.getY()).hasFlag()) {
				boardGame.toggleFlag(sb.getX(), sb.getY());
				sb.setStyle(changeImage(" ")); // the string " " means - without image
			} else { // if does not have a flag - will add it
				boardGame.toggleFlag(sb.getX(), sb.getY());
				sb.setStyle(changeImage("F")); // the string "F" means - flag icon
			}
		}
	}

	// this event happens if the player clicked the left button of the mouse on a
	// button on the grid, and it changes the button accordingly (number/mine...)
	public class LeftClick implements EventHandler<ActionEvent> {
		@SuppressWarnings("static-access")
		@Override
		public void handle(ActionEvent event) {
			SpotButton sb = (SpotButton) event.getSource();
			// opens the spot
			boardGame.open(sb.getX(), sb.getY());
			sb.setStyle(changeImage(boardGame.getSpot(sb.getX(), sb.getY()).toString()));
			sb.setDisable(true); // can't be clicked anymore

			// if it is a mine - game over
			if (boardGame.getSpot(sb.getX(), sb.getY()).toString() == "X") {
				// revealing all spots
				boardGame.setShowAll(true);
				for (Node reveal : grid.getChildren()) {
					reveal = (SpotButton) reveal;
					int row = grid.getRowIndex(reveal), column = grid.getColumnIndex(reveal);
					reveal.setStyle(changeImage(boardGame.get(row, column)));
					reveal.setDisable(true);
				}
				// showing game over message
				showMessage("Game Over", "You activated a mine - you lost!", AlertType.INFORMATION);
			}

			// if all spots are open - win
			if (boardGame.isDone()) {
				// disabling all spots
				for (Node reveal : grid.getChildren()) {
					reveal = (SpotButton) reveal;
					reveal.setDisable(true);
				}
				// showing winning message
				showMessage("Game Over", "You revealed all the squares withoud activating a mine - you won!",
						AlertType.INFORMATION);
			}

			// if it is not a mine and the game is not over - continuing.
			// opens all the other spots that opened recursively
			for (int i = 0; i < heightValue; i++) {
				for (int j = 0; j < widthValue; j++) {
					if (boardGame.get(i, j) != "." && boardGame.get(i, j) != "F") { // open and not a flag
						for (Node change : grid.getChildren()) {
							// if the current node (child) has the same indinces as the spot on the board
							if (grid.getRowIndex(change) == i && grid.getColumnIndex(change) == j) {
								change = (SpotButton) change;
								change.setStyle(changeImage(boardGame.getSpot(i, j).toString()));
								change.setDisable(true);
							}
						}
					}
				}
			}
			sb.setFocusTraversable(false);
		}
	}

	// this method gets a string (for mapping an imageView) and returns a string for
	// the setStyle method of a button on the grid
	private String changeImage(String string) {
		if (string == " ")
			return "-fx-background-image: none; -fx-background-repeat: no-repeat;" + "-fx-background-radius: 0;";

		else if (string == "F") {
			String url = (images.get(string)).getImage().getUrl();
			return "-fx-background-image: url('" + url + "'); -fx-background-radius: 0;"
					+ "-fx-background-size: 15px, 15px; -fx-background-repeat: no-repeat;"
					+ "-fx-background-position: center center;";
		}

		else {
			String url = (images.get(string)).getImage().getUrl();
			return "-fx-background-image: url('" + url + "'); -fx-background-radius: 0;"
					+ "-fx-background-size: 15px, 15px; -fx-background-repeat: no-repeat;"
					+ "-fx-background-position: center center; -fx-opacity: 1;";
		}
	}

	// this method shows an alert message on the screen
	private void showMessage(String title, String message, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.showAndWait();
	}
}
