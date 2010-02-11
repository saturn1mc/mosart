package gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class MosArtGUI extends JFrame{
	
	private MosArt worker;
	
	private JProgressBar progressBar;
	
	private JButton sourceButton;
	private JButton targetButton;
	private JButton genButton;
	
	private void buildSourceButton(){
		sourceButton = new JButton("...");
		
		MouseAdapter sourceMouse = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				
			}
		};
		
		sourceButton.addMouseListener(sourceMouse);
	}
}
