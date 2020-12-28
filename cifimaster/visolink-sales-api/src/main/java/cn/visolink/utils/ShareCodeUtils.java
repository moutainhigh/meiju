package cn.visolink.utils;

import java.util.Random;

/**
 * @author share
 */
public class ShareCodeUtils {

    /**
     * 自定义进制(0,1没有加入,容易与o,l混淆)
     */
    private static final char[] CHARSARR = new char[]{'q', 'w', 'e', '8', 'a', 's', '2', 'd', 'z', 'x', '9', 'c', '7', 'p', '5', 'i', 'k', '3', 'm', 'j', 'u', 'f', 'r', '4', 'v', 'y', 'l', 't', 'n', '6', 'b', 'g', 'h'};

    /**
     * (不能与自定义进制有重复)
     */
    private static final char REPALCECHAR = 'o';

    /**
     * 进制长度
     */
    private static final int BINLEN = CHARSARR.length;

    /**
     * 序列最小长度
     */
    private static final int S = 8;

    /**
     * 根据ID生成六位随机码
     *
     * @param id ID
     * @return 随机码
     */
    public static String toSerialCode(long id) {
        char[] buf = new char[32];
        int charPos = 32;

        while ((id / BINLEN) > 0) {
            int ind = (int) (id % BINLEN);
            buf[--charPos] = CHARSARR[ind];
            id /= BINLEN;
        }
        buf[--charPos] = CHARSARR[(int) (id % BINLEN)];
        String str = new String(buf, charPos, (32 - charPos));
        // 不够长度的自动随机补全
        if (str.length() < S) {
            StringBuilder sb = new StringBuilder();
            sb.append(REPALCECHAR);
            Random rnd = new Random();
            for (int i = 1; i < S - str.length(); i++) {
                sb.append(CHARSARR[rnd.nextInt(BINLEN)]);
            }
            str += sb.toString();
        }
        return str;
    }

    public static long codeToId(String code) {
        char chs[] = code.toCharArray();
        long res = 0L;
        for (int i = 0; i < chs.length; i++) {
            int ind = 0;
            for (int j = 0; j < BINLEN; j++) {
                if (chs[i] == CHARSARR[j]) {
                    ind = j;
                    break;
                }
            }
            if (chs[i] == REPALCECHAR) {
                break;
            }
            if (i > 0) {
                res = res * BINLEN + ind;
            } else {
                res = ind;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        System.out.println(toSerialCode(0L));
    }
}