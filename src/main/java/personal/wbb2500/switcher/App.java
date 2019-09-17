package personal.wbb2500.switcher;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

public class App implements ComponentListener {
    private static Dimension size;
    private static double tileHeight;
    private static double tileWidth;

    private static Robot robot;

    private static boolean right_switch = false;
    private static boolean left_switch = false;
    private static boolean create_switch = false;
    private static boolean delete_switch = false;

    // frame
    private static JFrame f;
    // buttons
    static JButton ss;
    static JCheckBox cb;

    private static boolean run = false;
    private static Thread thread;

    private static GlobalKeyboardHook keyboardHook;

    App() {
        f = new JFrame("DesktopSwitcher");
        ss = new JButton("Start");
        cb = new JCheckBox("Use CTRL+ALT+RIGHT/LEFT/UP/DOWN");

        // set-up frame
        f.setMinimumSize(new Dimension(400, 300));
        f.setUndecorated(false);
        f.setLayout(null);
        f.setVisible(true);

        // set-up stop/start button
        ss.addActionListener((ActionEvent e) -> {
            if (run) {
                ss.setText("Start");
                run = false;
            } else {
                run = true;
                ss.setText("Stop");
                startThread();
            }
        });

        // sets up the spacing
        updateTiles();
        buildButtons();

        // add listener
        f.addComponentListener(this);

        // add buttons and checkboxes
        f.add(ss);
        f.add(cb);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Program is running");
        keyboardHook = new GlobalKeyboardHook(true);
        robot = new Robot();
        new App();
    }

    public static void startThread() {
        thread = new Thread(new Runnable() {
            public void run() {
                keyboardHook.addKeyListener(new GlobalKeyAdapter() {
                    public void keyPressed(GlobalKeyEvent event) {
                        int keyCode = event.getVirtualKeyCode();
                        boolean isControlPressed = event.isControlPressed();

                        if (keyCode == GlobalKeyEvent.VK_RIGHT && isControlPressed && !right_switch) {
                            keybindingPress(right_switch, KeyEvent.VK_WINDOWS, KeyEvent.VK_RIGHT);
                        }
                        if (keyCode == GlobalKeyEvent.VK_LEFT && isControlPressed && !left_switch) {
                            keybindingPress(left_switch, KeyEvent.VK_WINDOWS, KeyEvent.VK_LEFT);
                        }
                        if (keyCode == GlobalKeyEvent.VK_UP && isControlPressed && !create_switch) {
                            keybindingPress(create_switch, KeyEvent.VK_WINDOWS, KeyEvent.VK_D);
                        }
                        if (keyCode == GlobalKeyEvent.VK_DOWN && isControlPressed && !delete_switch) {
                            keybindingPress(delete_switch, KeyEvent.VK_WINDOWS, KeyEvent.VK_F4);
                        }
                    }

                    public void keyReleased(GlobalKeyEvent event) {
                        if (right_switch && event.getVirtualKeyCode() == KeyEvent.VK_RIGHT) {
                            right_switch = false;
                        }
                        if (left_switch && event.getVirtualKeyCode() == KeyEvent.VK_LEFT) {
                            left_switch = false;
                        }
                        if (create_switch && event.getVirtualKeyCode() == KeyEvent.VK_UP) {
                            create_switch = false;
                        }
                        if (delete_switch && event.getVirtualKeyCode() == KeyEvent.VK_DOWN) {
                            delete_switch = false;
                        }
                    }

                    // sets off the keybindings and prevents long loops of creation or deletion
                    public void keybindingPress(boolean b, int windows, int other) {
                        b = true;
                        robot.keyPress(windows);
                        robot.keyPress(other);
                        robot.keyRelease(windows);
                        robot.keyRelease(other);
                    }
                });

                try {
                    while (run) {
                        Thread.sleep(128);
                    }
                } catch (InterruptedException e1) {

                } finally {
                    System.out.println("Stop Running");
                    keyboardHook.shutdownHook();
                }
            }
        });

        thread.start();
    }

    public static void updateTiles() {
        size = f.getSize();
        tileWidth = size.getWidth() / 5;
        tileHeight = size.getHeight() / 4;
    }

    public static void buildButtons() {
        ss.setBounds((int) tileWidth, (int) tileHeight, (int) tileWidth * 3, (int) tileHeight);
        cb.setBounds((int) tileWidth, (int) tileHeight * 2, (int) tileWidth * 3, (int) tileHeight);
    }

    public void componentResized(ComponentEvent e) {
        updateTiles();
        buildButtons();
    }

    public void componentHidden(ComponentEvent e) {
        // don't need
    }

    public void componentMoved(ComponentEvent e) {
        // don't need
    }

    public void componentShown(ComponentEvent e) {
        // don't need
    }
}