package gui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import core.Constants;

public class OutputPane extends JPanel {

    final private StyledDocument terminal;
    final Map<String, Style> stylesMap = new HashMap<>();

    public OutputPane() {
        setLayout(new BorderLayout());

        final JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        terminal = textPane.getStyledDocument();

        final JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Define styles (text colors)
        stylesMap.put(Constants.RED, textPane.addStyle("red", null));
        stylesMap.put(Constants.GREEN, textPane.addStyle("green", null));
        stylesMap.put(Constants.YELLOW, textPane.addStyle("yellow", null));
        stylesMap.put(Constants.BLUE, textPane.addStyle("blue", null));
        stylesMap.put(Constants.PURPLE, textPane.addStyle("magenta", null));
        stylesMap.put(Constants.GRAY, textPane.addStyle("gray", null));
        stylesMap.put(Constants.DEFAULT, textPane.addStyle("black", null));

        StyleConstants.setForeground(stylesMap.get(Constants.RED), Color.RED);
        StyleConstants.setForeground(stylesMap.get(Constants.GREEN), Color.GREEN);
        StyleConstants.setForeground(stylesMap.get(Constants.YELLOW), Color.YELLOW);
        StyleConstants.setForeground(stylesMap.get(Constants.BLUE), Color.BLUE);
        StyleConstants.setForeground(stylesMap.get(Constants.PURPLE), Color.MAGENTA);
        StyleConstants.setForeground(stylesMap.get(Constants.GRAY), Color.GRAY);
        StyleConstants.setForeground(stylesMap.get(Constants.DEFAULT), Color.BLACK);

        // Make all text bold
        for (Style style : stylesMap.values()) {
            StyleConstants.setBold(style, true);
        }

    }

    public void append(String data) {
        append(data, Constants.DEFAULT);
    }

    public void append(String data, String termColor) {

        SwingUtilities.invokeLater(() -> {
            try {
                Style style = stylesMap.get(Constants.DEFAULT);
                if (stylesMap.containsKey(termColor)) style = stylesMap.get(termColor);
                terminal.insertString(terminal.getLength(), data, style);
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
