package DESView;

import DESAlgorithm.DES;
import sun.misc.BASE64Encoder;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class testView {

    public static void main(String[] args) {
        JFrame jf = new JFrame("Test Window");
        jf.setSize(600, 400);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setLocationRelativeTo(null);

        final JTabbedPane tabbedPane = new JTabbedPane();


        // 创建第 1 个选项卡（选项卡只包含 标题）
        tabbedPane.addTab("Tab01", createTextPanel("TAB 01"));
        tabbedPane.addTab("Tab02", createEncryptionPanel("nihao"));

        tabbedPane.setSelectedIndex(0);
        jf.setContentPane(tabbedPane);
        jf.setVisible(true);
    }


    private static JComponent createTextPanel(String text) {
        JTabbedPane pane = new JTabbedPane();

        pane.addTab("DES Encryption Part", createEncryptionPanel(text));
        pane.addTab("DES Decryption Part", createEncryptionPanel("iojikj"));

        pane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println("test: " + pane.getSelectedIndex());
            }
        });

        pane.setSelectedIndex(0);
        return pane;
    }

    private static JComponent createEncryptionPanel(String text) {
        JPanel panel = new JPanel(null);

        JTextArea enterArea = new JTextArea("***Please enter text!***");
        enterArea.setLineWrap(true);
        enterArea.setWrapStyleWord(true);
        JScrollPane enterPanel = new JScrollPane(enterArea);
        enterPanel.setSize(500, 120);
        enterPanel.setLocation(50, 10);

        JTextField keyField = new JTextField("***Please enter key!***");
        keyField.setSize(380, 30);
        keyField.setLocation(50, 140);

        JButton button = new JButton("Encryption");
        button.setSize(100, 30);
        button.setLocation(450, 140);

        JTextArea contentArea = new JTextArea("Encryption Content is displayed here!");
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentPanel = new JScrollPane(contentArea);
        contentPanel.setSize(500, 120);
        contentPanel.setLocation(50, 180);


        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("***Please enter text!***".equals(enterArea.getText()) || "***Please enter text!***".equals(keyField.getText())) {
                    contentArea.setText("***Please change the default content!!!***");
                } else {
                    String origin_Text = enterArea.getText();
                    String key = keyField.getText();
                    BASE64Encoder encoder = new BASE64Encoder();
                    DES des = new DES(key, origin_Text);
                    byte[] context = des.deal(origin_Text.getBytes(), 1);
                    contentArea.setText("Encryption Code:\n" + encoder.encode(context));
                }
            }
        });

        panel.add(enterPanel);
        panel.add(keyField);
        panel.add(button);
        panel.add(contentPanel);

        return panel;
    }
}
