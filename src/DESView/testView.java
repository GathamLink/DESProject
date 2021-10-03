package DESView;

import DESAlgorithm.DES;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class testView {

    public static void main(String[] args) {
        JFrame jf = new JFrame("Test Window");
        jf.setSize(600, 400);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setLocationRelativeTo(null);

        final JTabbedPane tabbedPane = new JTabbedPane();


        // 创建第 1 个选项卡（选项卡只包含 标题）
        tabbedPane.addTab("Text DES Encryption & Decryption", createTextPanel());
        tabbedPane.addTab("File DES Encryption & Decryption", createEncryptionPanel());

        tabbedPane.setSelectedIndex(0);
        jf.setContentPane(tabbedPane);
        jf.setVisible(true);
    }


    private static JComponent createTextPanel() {
        JTabbedPane pane = new JTabbedPane();

        pane.addTab("DES Encryption Part", createEncryptionPanel());
        pane.addTab("DES Decryption Part", createDecryptionPanel());

        pane.setSelectedIndex(0);
        return pane;
    }

    private static JComponent createEncryptionPanel() {
        JPanel panel = new JPanel(null);

        JTextArea enterArea = new JTextArea("***Please enter origin text!***");
        enterArea.setLineWrap(true);
        enterArea.setWrapStyleWord(true);
        JScrollPane enterPanel = new JScrollPane(enterArea);
        enterPanel.setSize(500, 120);
        enterPanel.setLocation(50, 10);

        JTextField keyField = new JTextField("***Please enter key!***");
        keyField.setForeground(Color.red);
        keyField.setSize(380, 30);
        keyField.setLocation(50, 140);

        JButton button = new JButton("Encryption");
        button.setSize(100, 30);
        button.setLocation(450, 140);
        button.setBackground(Color.CYAN);

        JTextArea contentArea = new JTextArea("Encryption Content is displayed here!");
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(Color.decode("#D3D3D3"));
        JScrollPane contentPanel = new JScrollPane(contentArea);
        contentPanel.setSize(500, 120);
        contentPanel.setLocation(50, 180);


        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("***Please enter origin text!***".equals(enterArea.getText()) || "***Please enter text!***".equals(keyField.getText())) {
                    contentArea.setForeground(Color.red);
                    contentArea.setText("***Please change the default content!!!***");
                } else {
                    String origin_Text = enterArea.getText();
                    String key = keyField.getText();
                    BASE64Encoder encoder = new BASE64Encoder();
                    DES des = new DES(key, origin_Text);
                    byte[] context = des.deal(origin_Text.getBytes(), 1);
                    contentArea.setForeground(Color.decode("#008B45"));
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

    private static JComponent createDecryptionPanel() {
        JPanel panel = new JPanel(null);

        JTextArea enterArea = new JTextArea("***Please enter encryption codes!***");
        enterArea.setLineWrap(true);
        enterArea.setWrapStyleWord(true);
        JScrollPane enterPanel = new JScrollPane(enterArea);
        enterPanel.setSize(500, 120);
        enterPanel.setLocation(50, 10);

        JTextField keyField = new JTextField("***Please enter key!***");
        keyField.setForeground(Color.red);
        keyField.setSize(380, 30);
        keyField.setLocation(50, 140);

        JButton button = new JButton("Decryption");
        button.setSize(100, 30);
        button.setLocation(450, 140);
        button.setBackground(Color.GREEN);

        JTextArea contentArea = new JTextArea("Decryption Content is displayed here!");
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(Color.decode("#D3D3D3"));
        JScrollPane contentPanel = new JScrollPane(contentArea);
        contentPanel.setSize(500, 120);
        contentPanel.setLocation(50, 180);


        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("***Please enter encryption codes!***".equals(enterArea.getText()) || "***Please enter text!***".equals(keyField.getText())) {
                    contentArea.setForeground(Color.red);
                    contentArea.setText("***Please change the default content!!!***");
                } else {
                    String encryption_Code = enterArea.getText();
                    String key = keyField.getText();
                    BASE64Decoder decoder = new BASE64Decoder();
                    try {
                        byte[] text = decoder.decodeBuffer(encryption_Code);
                        DES des = new DES(key, new String(text));
                        byte[] decryption = des.deal(text, 0);
//                        byte[] dec_text = new byte[decryption.length];
//                        System.arraycopy(decryption, 0, dec_text, 0, text.length);
                        contentArea.setForeground(Color.decode("#008B45"));
                        contentArea.setText("Content After Decryption:\n" + new String(decryption).replace("\u0006", ""));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
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
