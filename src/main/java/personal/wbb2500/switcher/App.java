package personal.wbb2500.switcher;

import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
    
    private static Prefs prefs;
    
    // frame
    private static JFrame f;
    // buttons
    static JButton ss;
    static JCheckBox cb;
    static JLabel info;
    static JButton eb;
    static JCheckBox cbp;
    
    private static boolean run = false;
    private static Thread thread;
    
    private static GlobalKeyboardHook keyboardHook;
    
    App() throws Exception {
        f = new JFrame("DesktopSwitcher");
        ss = new JButton("Start");
        eb = new JButton("Exit");
        cbp = new JCheckBox("Minimize on start");
        cb = new JCheckBox("Use CTRL+ALT+RIGHT/LEFT/UP/DOWN");
        info = new JLabel(
        "<html> If CTRL+ALT+RIGHT/LEFT/UP/DOWN causes the screen to rotate, you may have to turn off hotkeys in graphics options for intel. </html>");
        
        // set-up frame
        f.setMinimumSize(new Dimension(400, 300));
        f.setUndecorated(false);
        f.setLayout(null);
        if (prefs.minimizeStart()) {
            f.setVisible(false);
        } else {
            f.setVisible(true);
        }
        
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
        
        // set-up checkbox
        if (prefs.linuxControls()) {
            cb.setSelected(true);
        } else {
            cb.setSelected(false);
        }
        
        if (prefs.minimizeStart()) {
            cbp.setSelected(true);
        } else {
            cbp.setSelected(false);
        }
        
        cb.addActionListener((ActionEvent e) -> {
            if (cb.isSelected()) {
                prefs.useLinuxControls(true);
            } else {
                prefs.useLinuxControls(false);
            }
        });
        
        cbp.addActionListener((ActionEvent e) -> {
            if (cbp.isSelected()) {
                prefs.minimizeOnStart(true);
            } else {
                prefs.minimizeOnStart(false);
            }
        });
        
        eb.addActionListener((ActionEvent  e) -> {
            this.close();
        });
        
        // sets up the spacing
        updateTiles();
        buildButtons();
        
        // add listener
        f.addComponentListener(this);
        
        // add buttons and checkboxes
        f.add(ss);
        f.add(cb);
        f.add(eb);
        f.add(info);
        f.add(cbp);
        
        new SysTray(this);
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println("Program is running");
        keyboardHook = new GlobalKeyboardHook(true);
        robot = new Robot();
        prefs = new Prefs();
        new App();
    }
    
    public static void startThread() {
        thread = new Thread(new Runnable() {
            public void run() {
                keyboardHook.addKeyListener(new GlobalKeyAdapter() {
                    public void keyPressed(GlobalKeyEvent event) {
                        int keyCode = event.getVirtualKeyCode();
                        boolean isControlPressed = event.isControlPressed();
                        boolean isMenuPressed = event.isMenuPressed();
                        
                        if (prefs.linuxControls()) {
                            if (keyCode == GlobalKeyEvent.VK_RIGHT && isMenuPressed && isControlPressed
                            && !right_switch) {
                                robot.keyRelease(KeyEvent.VK_ALT);
                                keybindingPress(right_switch, KeyEvent.VK_WINDOWS, KeyEvent.VK_RIGHT);
                                robot.keyPress(KeyEvent.VK_ALT);
                            }
                            if (keyCode == GlobalKeyEvent.VK_LEFT && isMenuPressed && isControlPressed
                            && !left_switch) {
                                robot.keyRelease(KeyEvent.VK_ALT);
                                keybindingPress(left_switch, KeyEvent.VK_WINDOWS, KeyEvent.VK_LEFT);
                                robot.keyPress(KeyEvent.VK_ALT);
                            }
                            if (keyCode == GlobalKeyEvent.VK_UP && isMenuPressed && isControlPressed
                            && !create_switch) {
                                robot.keyRelease(KeyEvent.VK_ALT);
                                keybindingPress(create_switch, KeyEvent.VK_WINDOWS, KeyEvent.VK_D);
                                robot.keyPress(KeyEvent.VK_ALT);
                            }
                            if (keyCode == GlobalKeyEvent.VK_DOWN && isMenuPressed && isControlPressed
                            && !delete_switch) {
                                robot.keyRelease(KeyEvent.VK_ALT);
                                keybindingPress(delete_switch, KeyEvent.VK_WINDOWS, KeyEvent.VK_F4);
                                robot.keyPress(KeyEvent.VK_ALT);
                            }
                        } else {
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
        tileHeight = size.getHeight() / 8;
    }
    
    public static void buildButtons() {
        cbp.setBounds((int) tileWidth, 0, (int) tileWidth * 3, (int) tileHeight);
        ss.setBounds((int) tileWidth, (int) tileHeight, (int) (tileWidth * 1.5), (int) tileHeight);
        eb.setBounds((int) (tileWidth * 2.5), (int) tileHeight, (int) (tileWidth * 1.5), (int) tileHeight);
        cb.setBounds((int) tileWidth, (int) tileHeight * 2, (int) tileWidth * 3, (int) tileHeight);
        info.setBounds((int) tileWidth, (int) tileHeight * 3, (int) tileWidth * 3, (int) tileHeight * 2);
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
    
    public void hide() {
        f.setVisible(false);
    }
    
    public void show() {
        f.setVisible(true);
    }
    
    public void close() {
        run = false;
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
    }
}

class Prefs {
    private Preferences prefs;
    private String linuxPrefName = "UseLinuxKey";
    private String minimizePrefName = "minimizeOnStart";
    
    Prefs() {
        prefs = Preferences.userNodeForPackage(App.class);
    }
    
    public boolean linuxControls() {
        return prefs.getBoolean(linuxPrefName, false);
    }
    
    public void useLinuxControls(boolean use) {
        if (use) {
            prefs.putBoolean(linuxPrefName, true);
        } else {
            prefs.putBoolean(linuxPrefName, false);
        }
    }
    
    public boolean minimizeStart() {
        return prefs.getBoolean(minimizePrefName, false);
    }
    
    public void minimizeOnStart(boolean use) {
        if (use) {
            prefs.putBoolean(minimizePrefName, true);
        } else {
            prefs.putBoolean(minimizePrefName, false);
        }
    }
}

class SysTray {
    private PopupMenu popup;
    private TrayIcon trayIcon;
    private SystemTray tray;
    
    final private App app;
    
    SysTray(App a) throws Exception {
        app = a;
        
        popup = new PopupMenu();
        BufferedImage image = ImageIO.read(new File("src/main/resources/java.png"));
        trayIcon = new TrayIcon(image);
        tray = SystemTray.getSystemTray();
        
        MenuItem openItem = new MenuItem("Open");
        MenuItem exitItem = new MenuItem("Exit");
        
        exitItem.addActionListener((ActionEvent e) -> {
            tray.remove(trayIcon);
            app.close();
        });
        
        openItem.addActionListener((ActionEvent e) -> {
            app.show();
        });
        
        trayIcon.addActionListener((ActionEvent e) -> {
            app.show();
        });
        
        //Add components to pop-up menu
        popup.add(openItem);
        popup.addSeparator();
        popup.add(exitItem);
        
        trayIcon.setPopupMenu(popup);
        
        tray.add(trayIcon);
    }
}