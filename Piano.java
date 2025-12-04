import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.midi.*;

public class Piano extends JFrame implements ActionListener {

    private static final String[] NOTES = { "A", "U", "G", "C", "T" };

    private JButton[] keys = new JButton[NOTES.length];
    private Synthesizer synthesizer;
    private MidiChannel channel;

    private JTextArea display; // <-- DISPLAY SCREEN

    public Piano() {
        setTitle(" Piano Game");
        setSize(600, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // manual layout

        // --- Display Screen ---
        display = new JTextArea();
        display.setEditable(false);
        display.setFont(new Font("Consolas", Font.BOLD, 22));
        display.setBackground(Color.BLACK);
        display.setForeground(Color.GREEN);
        display.setBounds(20, 20, 550, 50);
        display.setLineWrap(true);
        add(display);

        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            channel = synthesizer.getChannels()[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- Piano Keys ---
        int startX = 20;
        int width = 100;
        int height = 150;

        for (int i = 0; i < NOTES.length; i++) {
            keys[i] = new JButton(NOTES[i]);
            keys[i].setFont(new Font("Arial", Font.BOLD, 24));
            keys[i].setBounds(startX + (i * width), 100, width, height);
            keys[i].setBackground(Color.WHITE);
            keys[i].setForeground(Color.BLUE);

            keys[i].addActionListener(this);
            add(keys[i]);
        }

        // KEYBOARD SUPPORT
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char ch = Character.toUpperCase(e.getKeyChar());
                handleKeyboardPress(String.valueOf(ch));
            }
        });

        setFocusable(true);
        setVisible(true);
    }

    // Mouse click
    public void actionPerformed(ActionEvent e) {
        JButton pressedKey = (JButton) e.getSource();
        String note = pressedKey.getText();
        processNote(note, pressedKey);
    }

    // Keyboard press
    private void handleKeyboardPress(String key) {
        JButton btn = null;

        // Match keyboard key to button
        for (JButton b : keys) {
            if (b.getText().equalsIgnoreCase(key)) {
                btn = b;
                break;
            }
        }

        if (btn == null)
            return; // no matching key

        processNote(key, btn);
    }

    // Combined logic for mouse + keyboard
    private void processNote(String note, JButton btn) {
        display.append(note + " "); // display on screen
        playNote(note); // sound
        highlightKey(btn); // highlight
    }

    // Highlight animation
    private void highlightKey(JButton btn) {
        Color original = btn.getBackground();
        btn.setBackground(Color.YELLOW);

        new Timer(150, e -> btn.setBackground(original)).start();
    }

    private void playNote(String note) {
        int noteNumber = getNoteNumber(note);
        if (noteNumber != -1) {
            channel.noteOn(noteNumber, 600);
            new Timer(250, e -> channel.noteOff(noteNumber)).start();
        }
    }

    private int getNoteNumber(String note) {
        int base = 60;
        switch (note) {
            case "A": return base;
            case "U": return base + 1;
            case "G": return base + 2;
            case "C": return base + 3;
            case "T": return base + 4;
            default: return -1;
        }
    }

public static void main(String[] args) {
        SwingUtilities.invokeLater(Piano::new);
}
}