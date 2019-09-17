package personal.wbb2500.switcher;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

public class App implements ComponentListener {
    private static Dimension size;
    private static double tileHeight;
    private static double tileWidth;

    private static Robot robot;

    //frame
    private static JFrame f;
    //buttons
    static JButton ss;
    static JCheckBox cb;

    private static boolean run = true;
    private static Thread thread;

    App() {
        f = new JFrame("DesktopSwitcher");
        ss = new JButton("Stop");
        cb = new JCheckBox("Use CTRL+ALT+RIGHT/LEFT/UP/DOWN");

        //set-up frame
        f.setMinimumSize(new Dimension(400, 300));
        f.setUndecorated(false);
        f.setLayout(null);
        f.setVisible(true);

        //sets up the spacing
        updateTiles();
        buildButtons();

        //add listener
        f.addComponentListener(this);

        //add buttons and checkboxes
        f.add(ss);
        f.add(cb);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Program is running");
        new App();
    }

    public static void startThread() {
        if (!thread.isAlive()) {
            thread = new Thread(new Runnable() {
                public void run() {
                    
                }
            });
        }
    }

    public static void updateTiles() {
        size = f.getSize();
        tileWidth = size.getWidth() / 5;
        tileHeight = size.getHeight() / 4;
    }

    public static void buildButtons() {
        ss.setBounds((int) tileWidth,(int) tileHeight,(int) tileWidth * 3,(int) tileHeight);
        cb.setBounds((int) tileWidth,(int) tileHeight * 2,(int) tileWidth * 3,(int) tileHeight);
    }

    public void componentHidden(ComponentEvent e) {
        // don't need
    }

    public void componentMoved(ComponentEvent e) {
        // don't need
    }

    public void componentResized(ComponentEvent e) {
        updateTiles();
        buildButtons();
    }

    public void componentShown(ComponentEvent e) {
        // don't need
    }
}