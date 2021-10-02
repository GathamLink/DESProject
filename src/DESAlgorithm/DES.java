package DESAlgorithm;

import sun.misc.BASE64Encoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static DESAlgorithm.DESMetric.*;

public class DES {

    private static final int MAX_TIME = 16;
    private String[] Keys = new String[MAX_TIME];
    private String[] P_Content;
    private String[] C_Content;
    private int Origin_Length;

    private int[][] Sub_Keys = new int[16][48];
    private String Content;
    private int P_Origin_Length;

    public DES(String key, String content) {
        this.Content = content;
        P_Origin_Length = content.getBytes().length;
        generateKeys(key);
    }


    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("输入需要加密的文本:");
        String origin = bufferedReader.readLine();
        System.out.println("原文：\n"+origin);
        DES des = new DES("12345678", origin);
        byte[] c = des.deal(origin.getBytes(), 1);
        BASE64Encoder encoder = new BASE64Encoder();
        System.out.println("密文：\n"+encoder.encode(c));
        byte[] test = origin.getBytes();
        byte[] p = des.deal(c, 0);
//        byte[] p_d = new byte[origin.getBytes().length];
        byte[] p_d = new byte[p.length];
        System.arraycopy(p, 0, p_d, 0, origin.getBytes().length);
        System.out.println("明文：\n"+new String(p));
    }


    public byte[] deal(byte[] p, int flag) {
        Origin_Length = p.length;
        int g_Num;
        int r_Num;
        g_Num = Origin_Length / 8;
        r_Num = 8 - (Origin_Length - g_Num * 8);
        byte[] p_Padding;

        if (r_Num < 8) {
            p_Padding = new byte[Origin_Length + r_Num];
            System.arraycopy(p, 0, p_Padding, 0, Origin_Length);
            for (int i = 0; i < r_Num; i++) {
                p_Padding[Origin_Length + i] = (byte) r_Num;
            }
        } else {
            p_Padding = p;
        }

        g_Num = p_Padding.length / 8;
        byte[] f_p = new byte[8];
        byte[] result_data = new byte[p_Padding.length];
        for (int i = 0; i < g_Num; i++) {
            System.arraycopy(p_Padding, i * 8, f_p, 0, 8);
            System.arraycopy(encrytionUnit(f_p, Sub_Keys, flag), 0, result_data, i * 8, 8);
        }
        if (flag == 0) {
            byte[] p_result_data = new byte[P_Origin_Length];
            System.arraycopy(result_data, 0, p_result_data, 0, P_Origin_Length);
            return p_result_data;
        }

        return result_data;
//        return null;
    }


    public byte[] encrytionUnit(byte[] p, int k[][], int flag) {
        int[] p_bit = new int[64];
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            String p_b = Integer.toBinaryString(p[i]&0xff);
            while (p_b.length() % 8 != 0) {
                p_b = "0" + p_b;
            }
            stringBuilder.append(p_b);
        }
        String p_str = stringBuilder.toString();
        for (int i = 0; i < 64; i++) {
            int p_t = (int) p_str.charAt(i);
            if (p_t == 48) {
                p_t = 0;
            } else if (p_t == 49) {
                p_t = 1;
            }else{
                System.out.println("Convert to bit error!!!");
            }
            p_bit[i] = p_t;
        }

        /**
         *  IP convert part
         */
        int[] p_IP = new int[64];
        for (int i = 0; i < 64; i++) {
            p_IP[i] = p_bit[IP[i] - 1];
        }
        if (flag == 1) {
            for (int i = 0; i < 16; i++) {
                L(p_IP, i, flag, k[i]);
            }
        } else if (flag == 0) {
            for (int i = 15; i > -1; i--) {
                L(p_IP, i, flag, k[i]);
            }
        }
        int[] c = new int[64];
        for (int i = 0; i < IP_1.length; i++) {
            c[i] = p_IP[IP_1[i] - 1];
        }
        byte[] c_byte = new byte[8];
        for (int i = 0; i < 8; i++) {
            c_byte[i] = (byte) ((c[8 * i] << 7) + (c[8 * i + 1] << 6) + (c[8 * i + 2] << 5) + (c[8 * i + 3] << 4)+(c[8 * i + 4] << 3)+(c[8 * i + 5] << 2)+(c[8 * i + 6] << 1)+(c[8 * i + 7]));
        }
        return c_byte;

//        return null;
    }


    public void L(int[] M, int times, int flag, int[] keyarray) {
        int[] L0 = new int[32];
        int[] R0 = new int[32];
        int[] L1 = new int[32];
        int[] R1 = new int[32];
        int[] f = new int[32];
        System.arraycopy(M, 0, L0, 0, 32);
        System.arraycopy(M, 32, R0, 0, 32);
        L1 = R0;
        f = f_Function(R0, keyarray);
        for (int j = 0; j < 32; j++) {
            R1[j] = L0[j]^f[j];
            if (((flag == 0) && (times == 0)) || ((flag == 1) && (times == 15))) {
                M[j] = R1[j];
                M[j + 32] = L1[j];
            } else {
                M[j] = L1[j];
                M[j + 32] = R1[j];
            }
        }
    }


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


    public void generateKeys(String key) {
        while (key.length() < 8) {
            key = key + key;
        }

        key = key.substring(0, 8);
        byte[] keys = key.getBytes();
        int[] k_bit = new int[64];

        for (int i = 0; i < 8; i++) {
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
