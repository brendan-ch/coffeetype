import javax.swing.*;

import assets.Assets;
import renderers.*;

public class App {
  // JAR sources:
  // https://code.google.com/archive/p/json-simple/ (from https://www.geeksforgeeks.org/parse-json-java/)

	public static void main(String[] args){
		JFrame frame = new JFrame("coffeetype");
    frame.setIconImage(Assets.LOGO.getImage());
    
    MainWindow window = new MainWindow();
    frame.add(window.getPanel());
		
    frame.pack();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
		frame.setVisible(true);
	}
}
