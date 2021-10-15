package DESAlgorithm;

import static DESAlgorithm.DESMetric.*;

/**
 * This class the main class of DES algorithms.
 * The encryption or decryption function is method -- operation()
 */

public class DES {
    // This is metric for key
    private final int[][] Sub_Keys = new int[16][48];

    /**
     * Constructor of this class
     * @param key   the key used for encryption or decryption
     */
    public DES(String key) {
        generateKeys(key);
    }

    /**
     * The main decryption or encryption method
     * @param content the bytes of entered content
     * @param flag  the choose of encryption or decryption
     * @return  the bytes after encryption or decryption operations
     */
    public byte[] operate(byte[] content, int flag) {
        int origin_Length = content.length;
        int g_Num;
        int r_Num;
        g_Num = origin_Length / 8;
        r_Num = 8 - (origin_Length - g_Num * 8);
        byte[] content_Padding;

        // If the length of bytes is not octuple, need to add enough rest numbers to chang it to a octuple length
        if (r_Num < 8) {
            content_Padding = new byte[origin_Length + r_Num];
            System.arraycopy(content, 0, content_Padding, 0, origin_Length);
            for (int i = 0; i < r_Num; i++) {
                content_Padding[origin_Length + i] = (byte) r_Num;
            }
        } else {
            content_Padding = content;
        }

        // separate the bytes array into several 8 length arrays and operate them respectively
        // Then combine them together and return
        g_Num = content_Padding.length / 8;
        byte[] content_segment = new byte[8];
        byte[] result_data = new byte[content_Padding.length];
        for (int i = 0; i < g_Num; i++) {
            System.arraycopy(content_Padding, i * 8, content_segment, 0, 8);
            System.arraycopy(EnOrDecrytionUnit(content_segment, Sub_Keys, flag), 0, result_data, i * 8, 8);
        }

        return result_data;
    }

    /**
     * Encryption or Decryption method
     * @param content the content need to be operate
     * @param key key
     * @param flag  chosen mode
     * @return  content after encryption or decryption
     */
    public byte[] EnOrDecrytionUnit(byte[] content, int key[][], int flag) {
        int[] content_bit = new int[64];
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            // use &0xff to Protect the stack overflow from transmitting negative number to eight length bits
            // like -35 will be transferred to "11111111111111111...101010" that is much bigger than eight-bits length and break the structure of content metric, which must be 64 length
            String content_b = Integer.toBinaryString(content[i]&0xff);
            while (content_b.length() % 8 != 0) {
                content_b = "0" + content_b;
            }
            stringBuilder.append(content_b);
        }
        String content_str = stringBuilder.toString();
        for (int i = 0; i < 64; i++) {
            int p_t = (int) content_str.charAt(i);
            if (p_t == 48) {
                p_t = 0;
            } else if (p_t == 49) {
                p_t = 1;
            }else{
                System.out.println("Convert to bit error!!!");
            }
            content_bit[i] = p_t;
        }

        /**
         *  IP convert part
         */
        int[] content_IP = new int[64];
        for (int i = 0; i < 64; i++) {
            content_IP[i] = content_bit[IP[i] - 1];
        }
        if (flag == 1) {
            for (int i = 0; i < 16; i++) {
                separationFunction(content_IP, i, flag, key[i]);
            }
        } else if (flag == 0) {
            for (int i = 15; i > -1; i--) {
                separationFunction(content_IP, i, flag, key[i]);
            }
        }

        /**
         * IP_1 convert after encryption or decryption process
         */
        int[] convert_result = new int[64];
        for (int i = 0; i < IP_1.length; i++) {
            convert_result[i] = content_IP[IP_1[i] - 1];
        }

        /**
         * convert bits into unicode bytes
         */
        byte[] convert_byte = new byte[8];
        for (int i = 0; i < 8; i++) {
            convert_byte[i] = (byte) ((convert_result[8 * i] << 7) + (convert_result[8 * i + 1] << 6) + (convert_result[8 * i + 2] << 5) + (convert_result[8 * i + 3] << 4)+(convert_result[8 * i + 4] << 3)+(convert_result[8 * i + 5] << 2)+(convert_result[8 * i + 6] << 1)+(convert_result[8 * i + 7]));
        }
        return convert_byte;
    }

    /**
     * Separate function to separate array to Left and Right two parts for further operation
     * @param origin the original 64 length array
     * @param times separating times
     * @param flag  chosen mode
     * @param keyarray  key bits array
     */
    public void separationFunction(int[] origin, int times, int flag, int[] keyarray) {
        int[] L0 = new int[32];
        int[] R0 = new int[32];
        int[] L1 = new int[32];
        int[] R1 = new int[32];
        int[] f = new int[32];
        System.arraycopy(origin, 0, L0, 0, 32);
        System.arraycopy(origin, 32, R0, 0, 32);
        L1 = R0;
        f = f_Function(R0, keyarray);
        for (int j = 0; j < 32; j++) {
            R1[j] = L0[j]^f[j];
            if (((flag == 0) && (times == 0)) || ((flag == 1) && (times == 15))) {
                origin[j] = R1[j];
                origin[j + 32] = L1[j];
            } else {
                origin[j] = L1[j];
                origin[j + 32] = R1[j];
            }
        }
    }

    /**
     * F function for box transmit and compress
     * @param r_content The right part of original array
     * @param key   key
     * @return  F array for right part operation
     */
    public int[] f_Function(int[] r_content, int[] key) {
        int[] result = new int[32];
        int[] e_k = new int[48];
        for (int i = 0; i < E.length; i++) {
            e_k[i] = e_k[i]=r_content[E[i]-1]^key[i];
        }

        /**
         *  S_Box replace: from 48 to 32
         *  Divide the e_k array first and then replace
         */
        int[][] s = new int[8][6];
        int[] s_after = new int[32];
        for (int i = 0; i < 8; i ++) {
            System.arraycopy(e_k, i * 6, s[i], 0, 6);
            int r = (s[i][0] << 1) + s[i][5];
            int c = (s[i][1] << 3) + (s[i][2] << 2) + (s[i][3] << 1) + s[i][4];
            String str = Integer.toBinaryString((S_Box[i][r][c]));
            while (str.length() < 4) {
                str = "0" + str;
            }

            for (int j = 0; j < 4; j++) {
                int p = (int) str.charAt(j);
                if (p == 48) {
                    p = 0;
                } else if (p == 49) {
                    p = 1;
                } else {
                    System.out.println("Convert to bit error!!!");
                }
                s_after[4 * i + j] = p;
            }
        }

        /**
         * P_BOX replace
         */
        for (int i = 0; i < P.length; i++) {
            result[i] = s_after[P[i] - 1];
        }
        return result;
    }

    /**
     * Generate keys into arrays for decryption or encryption
     * @param key   key
     */
    public void generateKeys(String key) {
        while (key.length() < 8) {
            key = key + key;
        }

        key = key.substring(0, 8);
        byte[] keys = key.getBytes();
        int[] k_bit = new int[64];

        for (int i = 0; i < 8; i++) {
            // the principle of using &0xff is same as the usage in encryption
            String k_str = Integer.toBinaryString(keys[i]&0xff);
            if (k_str.length() < 8) {
                int length = k_str.length();
                for (int t = 0; t < 8 - length; t++) {
                    k_str = "0" + k_str;
                }
            }

            for (int j = 0; j < 8; j ++) {
                int p = (int) k_str.charAt(j);
                if (p == 48) {
                    p = 0;
                } else if (p == 49) {
                    p = 1;
                } else {
                    System.out.println("Convert to bit process error!!!");
                }
                k_bit[i * 8 + j] = p;
            }
        }
        //  k_bit here is a 64-length key, the following part is yo replace it!
        //  k_bit<-->PC1 compress process
        int[] k_new_bit = new int[56];
        for (int i = 0; i < PC1.length; i++) {
            k_new_bit[i] = k_bit[PC1[i] - 1];
        }

        int[] c0 = new int[28];
        int[] d0 = new int[28];
        System.arraycopy(k_new_bit, 0, c0, 0, 28);
        System.arraycopy(k_new_bit, 28, d0, 0, 28);

        for (int i = 0; i < 16; i++) {
            int[] c1 = new int[28];
            int[] d1 = new int[28];

            if (LFT[i] == 1) {
                System.arraycopy(c0, 1, c1, 0, 27);
                c1[27] = c0[0];
                System.arraycopy(d0, 1, d1, 0, 27);
                d1[27] = d0[0];
            } else if (LFT[i] == 2) {
                System.arraycopy(c0, 2, c1, 0, 26);
                c1[26]=c0[0];
                c1[27]=c0[1];
                System.arraycopy(d0, 2, d1, 0, 26);
                d1[26]=d0[0];
                d1[27]=d0[1];
            } else {
                System.out.println("LFT move Error!!!");
            }

            int[] tmp = new int[56];
            System.arraycopy(c1, 0, tmp, 0, 28);
            System.arraycopy(d1, 0, tmp, 28, 28);
            for (int j = 0; j < PC2.length; j++) {
                Sub_Keys[i][j] = tmp[PC2[j] - 1];
            }
            c0=c1;
            d0=d1;
        }
    }

}
