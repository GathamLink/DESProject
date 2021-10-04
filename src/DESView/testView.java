package DESView;

import DESAlgorithm.DES;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class testView {

    public static void main(String[] args) {
        JFrame jf = new JFrame("Test Window");
        jf.setSize(800, 600);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setLocationRelativeTo(null);
        jf.setResizable(false);

        final JTabbedPane tabbedPane = new JTabbedPane();


        // 创建第 1 个选项卡（选项卡只包含 标题）
        tabbedPane.addTab("Text DES Encryption & Decryption", createTextPanel());
        tabbedPane.addTab("File DES Encryption & Decryption", createFilePanel());

        tabbedPane.setSelectedIndex(0);
        jf.setContentPane(tabbedPane);
        jf.setVisible(true);
    }

    private static JComponent createIntroductionPanel() {
        return null;
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
        enterPanel.setSize(700, 200);
        enterPanel.setLocation(50, 10);

        JTextField keyField = new JTextField("***Please enter key!***");
        keyField.setForeground(Color.red);
        keyField.setSize(580, 30);
        keyField.setLocation(50, 220);

        JButton button = new JButton("Encryption");
        button.setSize(100, 30);
        button.setLocation(650, 220);
        button.setBackground(Color.CYAN);

        JTextArea contentArea = new JTextArea("Encryption Content is displayed here!");
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(Color.decode("#D3D3D3"));
        JScrollPane contentPanel = new JScrollPane(contentArea);
        contentPanel.setSize(700, 230);
        contentPanel.setLocation(50, 260);


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
        enterPanel.setSize(700, 200);
        enterPanel.setLocation(50, 10);

        JTextField keyField = new JTextField("***Please enter key!***");
        keyField.setForeground(Color.red);
        keyField.setSize(580, 30);
        keyField.setLocation(50, 220);

        JButton button = new JButton("Decryption");
        button.setSize(100, 30);
        button.setLocation(650, 220);
        button.setBackground(Color.GREEN);

        JTextArea contentArea = new JTextArea("Decryption Content is displayed here!");
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(Color.decode("#D3D3D3"));
        JScrollPane contentPanel = new JScrollPane(contentArea);
        contentPanel.setSize(700, 230);
        contentPanel.setLocation(50, 260);


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
                        contentArea.setText("Content After Decryption:\n" + new String(decryption).replace("\u0006", "").replace("\u0007", ""));
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

    private static JComponent createFilePanel() {
        final int[] radioButtonSelected = {0};

        JPanel panel = new JPanel(null);

        JLabel label1 = new JLabel("File Path");
        JLabel label2 = new JLabel("Store Path");
        JLabel label3 = new JLabel("Key");
        JLabel label4 = new JLabel("Mode");
        JTextArea filePathArea = new JTextArea();
        JTextArea storagePathArea = new JTextArea();
        JTextArea displayArea = new JTextArea("***Information is displayed here!***");
        JTextField keyField = new JTextField();
        JButton selectButton = new JButton("Select file");
        JButton storeButton = new JButton("Store path");
        JButton confirmButton = new JButton("Confirm");
        JRadioButton encryptionButton = new JRadioButton("Encryption");
        JRadioButton decryptionButton = new JRadioButton("Decryption");

        displayArea.setEditable(false);
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);
        JScrollPane displayPane = new JScrollPane(displayArea);
        displayPane.setLocation(10, 170);
        displayPane.setSize(760, 350);

        label1.setLocation(10, 10);
        label1.setSize(60, 50);

        filePathArea.setLocation(75,10);
        filePathArea.setSize(590, 50);
        filePathArea.setLineWrap(true);
        filePathArea.setWrapStyleWord(true);
        filePathArea.setEditable(false);

        selectButton.setLocation(670, 10);
        selectButton.setSize(100, 50);
        selectButton.setBackground(Color.CYAN);
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                storeButton.setEnabled(true);
                showFileOpenDialog(panel, filePathArea);
                if ("".equals(filePathArea.getText())) {
                    storeButton.setEnabled(false);
                }
            }
        });

        label2.setLocation(10, 70);
        label2.setSize(60, 50);

        storagePathArea.setLocation(75, 70);
        storagePathArea.setSize(590, 50);
        storagePathArea.setEditable(false);
        storagePathArea.setLineWrap(true);
        storagePathArea.setWrapStyleWord(true);

        storeButton.setLocation(670, 70);
        storeButton.setSize(100, 50);
        storeButton.setEnabled(false);
        storeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmButton.setEnabled(true);
                shoeFileSaveDialog(panel, storagePathArea, filePathArea);
                if ("".equals(storagePathArea.getText())) {
                    confirmButton.setEnabled(false);
                }
            }
        });

        label3.setLocation(10, 130);
        label3.setSize(60, 30);

        keyField.setLocation(75, 130);
        keyField.setSize(315, 30);

        label4.setLocation(400, 130);
        label4.setSize(40, 30);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(encryptionButton);
        buttonGroup.add(decryptionButton);

        encryptionButton.setLocation(450, 130);
        encryptionButton.setSize(100, 30);
        encryptionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                radioButtonSelected[0] = 1;
            }
        });
        decryptionButton.setLocation(550, 130);
        decryptionButton.setSize(100, 30);
        decryptionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                radioButtonSelected[0] = 2;
            }
        });

        confirmButton.setLocation(670, 130);
        confirmButton.setSize(100, 30);
        confirmButton.setBackground(Color.GREEN);
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("".equals(keyField.getText()) || radioButtonSelected[0] == 0) {
                    displayArea.setForeground(Color.red);
                    displayArea.setText("Please set the Key and choose the Encryption & Decryption mode!!!");
                } else if (radioButtonSelected[0] == 1) {
                    String filename = filePathArea.getText();
                    File file = new File(filename);
                    BASE64Encoder encoder = new BASE64Encoder();
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        FileOutputStream fos = null;
                        BufferedOutputStream s_bos = null;
                        File s_File = null;

                        byte[] b = new byte[1024];
                        int len;
                        while ((len = fileInputStream.read(b)) != -1) {
                            bos.write(b, 0, len);
                        }

                        byte[] fileByte = bos.toByteArray();
                        String originContent = new String(fileByte);
                        DES des = new DES(keyField.getText(), originContent);
                        byte[] encryptionByte = des.deal(fileByte, 1);
                        String code = encoder.encode(encryptionByte);

                        String fileType = filename.split("\\.")[1];
                        String storageContent = fileType + "\n" + code;

                        Files.write(Paths.get(storagePathArea.getText()), storageContent.getBytes());
                        displayArea.setForeground(Color.BLACK);
                        displayArea.setText("The Storage file path is:\n" + storagePathArea.getText() + "\n\n");
                        displayArea.append("Encryption code of origin content from the file:\n" + code);

//                        s_File = new File(storagePathArea.getText());
//                        if (!s_File.getParentFile().exists()) {
//                            s_File.getParentFile().mkdirs();
//                        }
//
//                        fos = new FileOutputStream(s_File);
//                        s_bos = new BufferedOutputStream(fos);
//                        s_bos.write(encryptionByte);
//
//                        s_bos.close();
//                        fos.close();
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                        displayArea.setForeground(Color.RED);
                        displayArea.setText("File is not Found!!!");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } else if (radioButtonSelected[0] == 2) {
                    String filePath = filePathArea.getText();
                    BASE64Decoder decoder = new BASE64Decoder();

                    String[] arr = filePath.split("\\\\");
                    String filename = arr[arr.length - 1].split("\\.")[0];

                    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                        String line;
                        String contentCode = "";
                        LinkedList<String> contentList = new LinkedList<>();
                        while ((line = br.readLine()) != null) {
                            contentList.add(line);
                        }

                        String fileType = contentList.get(0);
                        contentList.remove(0);
                        for (String item: contentList) {
                            contentCode += item;
                        }

                        byte[] code = decoder.decodeBuffer(contentCode);
                        DES des = new DES(keyField.getText(), new String(code));
                        byte[] contentByte = des.deal(code, 0);

                        BufferedOutputStream bos;
                        FileOutputStream fos;
                        File file = new File(storagePathArea.getText() + "\\" + filename + "." + fileType);
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }

                        fos = new FileOutputStream(file);
                        bos = new BufferedOutputStream(fos);
                        bos.write(contentByte);

                        bos.close();
                        fos.close();

                        displayArea.setForeground(Color.black);
                        displayArea.setText("The encryption file is decrypted successfully!\n");
                        displayArea.append("The storage path of the file is:\n" + storagePathArea.getText() + "\\" + filename + "." + fileType);

                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                        displayArea.setForeground(Color.RED);
                        displayArea.setText("File is not Found!!!");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });

        panel.add(filePathArea);
        panel.add(selectButton);
        panel.add(storagePathArea);
        panel.add(storeButton);
        panel.add(keyField);
        panel.add(label1);
        panel.add(label2);
        panel.add(label3);
        panel.add(label4);
        panel.add(encryptionButton);
        panel.add(decryptionButton);
        panel.add(confirmButton);
        panel.add(displayPane);

        return panel;
    }

    private static void showFileOpenDialog(Component parent, JTextArea msgTextArea) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("DES(*.DES)", "DES"));

        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            msgTextArea.setText(file.getAbsolutePath());
            System.out.println(file.getAbsolutePath());
        }
    }

    private static void shoeFileSaveDialog(Component parent, JTextArea msgTextArea, JTextArea filePath) {
        JFileChooser fileChooser = new JFileChooser();
        String originPath = filePath.getText();
        String[] arr = originPath.split("\\\\");
        String fileName = arr[arr.length-1];
        if (!"DES".equals(fileName.split("\\.")[1])) {
            fileChooser.setSelectedFile(new File(fileName.split("\\.")[0] + ".DES"));
            int result = fileChooser.showSaveDialog(parent);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                msgTextArea.setText(file.getAbsolutePath());
            }
        } else {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showSaveDialog(parent);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                msgTextArea.setText(file.getAbsolutePath());
            }
        }
    }
}