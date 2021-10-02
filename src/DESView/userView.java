package DESView;

import java.io.*;
import java.util.Arrays;

public class userView {

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\Link\\Desktop\\Sys&Ver\\Assignment 1 PDF.pdf");
        FileInputStream fis = new FileInputStream(file);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len;
        while ((len = fis.read(b)) != -1) {
            bos.write(b, 0, len);
        }

        byte[] fileByte = bos.toByteArray();
        System.out.println(new String(fileByte));
        System.out.println(Arrays.toString(fileByte));

        BufferedOutputStream bos1 = null;
        FileOutputStream fos = null;
        File file1 = null;
        try {
            file1 = new File("C:\\Users\\Link\\Desktop\\test.pdf");
            if (!file1.getParentFile().exists()) {
                file1.getParentFile().mkdirs();
            }

            fos = new FileOutputStream(file1);
            bos1 = new BufferedOutputStream(fos);
            bos1.write(fileByte);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos1 != null) {
                try {
                    bos1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
