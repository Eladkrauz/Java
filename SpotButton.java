package mines;

import javafx.scene.control.Button;

public class SpotButton extends Button { // a class for a button with specific location on the board
	private int x, y;

	public SpotButton(String text, int x, int y) {
		super(text);
		this.x = x;
		this.y = y;
		setEllipsisString(""); // so the "..." won't be shown
		setPrefSize(20, 20);
		setMaxSize(20, 20);
		setMinSize(20, 20);
		setStyle("-fx-background-radius: 0; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
