package GeneticAlgorithm;

import javax.swing.*;

public class TextScroll extends JScrollPane {
	private JTextArea textArea;

	public TextScroll() {
        super(new JTextArea(""));

        textArea = (JTextArea)this.getViewport().getView();

		this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
		this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
	}

	public void addText(String text) {
		textArea.setText("  " + text + "\n" + textArea.getText());
        textArea.setCaretPosition(2 + text.length());
	}
}