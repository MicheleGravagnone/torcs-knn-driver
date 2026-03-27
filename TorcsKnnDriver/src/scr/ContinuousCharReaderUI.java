package scr;

import javax.swing.SwingUtilities;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ContinuousCharReaderUI extends JFrame {
    private SimpleDriver simpleDriver;

    public ContinuousCharReaderUI(SimpleDriver simpleDriver) {
        this.simpleDriver = simpleDriver;

        // Set up the frame
        setTitle("Continuous Character Reader");
        setSize(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Initialize the text field for input
        JTextField inputField = new JTextField(20);
        add(inputField);

        // Add key listener to the text field
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char ch = e.getKeyChar();
                if (ch == 'w') {
                    simpleDriver.setAccelerate(true);
                } else if (ch == 'd') {
                    simpleDriver.setSteerRight(true);
                } else if (ch == 'a') {
                    simpleDriver.setSteerLeft(true);
                } else if (ch == 's') {
                    simpleDriver.setBrake(true);
                } else if (ch == 'q') {
                    System.exit(0);
                }
                // Clear the text field
                inputField.setText("");
            }

            @Override
            public void keyReleased(KeyEvent e) {
                char ch = e.getKeyChar();
                if (ch == 'w') {
                    simpleDriver.setAccelerate(false);
                } else if (ch == 'd') {
                    simpleDriver.setSteerRight(false);
                } else if (ch == 'a') {
                    simpleDriver.setSteerLeft(false);
                } else if (ch == 's') {
                    simpleDriver.setBrake(false);
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimpleDriver simpleDriver = new SimpleDriver();
            new ContinuousCharReaderUI(simpleDriver);
        });
    }
}
