package cn.visolink.utils;

import cn.hutool.log.Log;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Logger;

/**
 * 加密
 * @author WCL
 * @date 2018-11-23
 */
public class EncryptUtils {

    private static String strKey = "Passw0rd", strParam = "Passw0rd";

    // 加密时采用的编码方式;
    private final static String encoding = "UTF-8";

    private final static Base64 base64 = new Base64();

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String DEFAULT_PSWD = "Djk@%&opN!$$*";
    private static final String KEY_ALGORITHM = "AES";

    /**
     * 对称加密
     * @param source
     * @return
     * @throws Exception
     */
    public static String desEncrypt(String source) throws Exception {
        if (source == null || source.length() == 0){
            return null;
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(strKey.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("AES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(strParam.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return byte2hex(
                cipher.doFinal(source.getBytes("UTF-8"))).toUpperCase();
    }

    public static String byte2hex(byte[] inStr) {
        String stmp;
        StringBuffer out = new StringBuffer(inStr.length * 2);
        for (int n = 0; n < inStr.length; n++) {
            stmp = Integer.toHexString(inStr[n] & 0xFF);
            if (stmp.length() == 1) {
                // 如果是0至F的单位字符串，则添加0
                out.append("0" + stmp);
            } else {
                out.append(stmp);
            }
        }
        return out.toString();
    }


    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0){
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    /**
     * 对称解密
     * @param source
     * @return
     * @throws Exception
     */
    public static String desDecrypt(String source) throws Exception {
        if (source == null || source.length() == 0){
            return null;
        }
        byte[] src = hex2byte(source.getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(strKey.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("AES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(strParam.getBytes("UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] retByte = cipher.doFinal(src);
        return new String(retByte);
    }

    /**
     * 密码加密
     * @param password
     * @return
     */
    public static String encryptPassword(String password){
        return  DigestUtils.md5DigestAsHex(password.getBytes());
    }


    /**
     * 用base64编码后返回
     *
     * @param str  需要加密的字符串
     * @param sKey 加密密钥
     * @return 经过加密的字符串
     */
    public static String encrypt(String str, String sKey) {
        // 声明加密后的结果字符串变量
        String result = str;
        if (str != null && str.length() > 0 && sKey != null && sKey.length() >= 8) {
            try {
                // 调用DES 加密数组的 encrypt方法，返回加密后的byte数组;
                byte[] encodeByte = encryptBasedDes(str.getBytes(encoding), sKey);
                // 调用base64的编码方法，返回加密后的字符串;
                result = base64.encodeToString(encodeByte).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("加密密钥不能为空且不能小于8位。");
        }
        return result;
    }

    /**
     * 先用DES算法对byte[]数组加密
     *
     * @param byteSource 需要加密的数据
     * @param sKey       加密密钥
     * @return 经过加密的数据
     * @throws Exception
     */
    private static byte[] encryptBasedDes(byte[] byteSource, String sKey) throws Exception {
        try {
            // 声明加密模式;
            int mode = Cipher.ENCRYPT_MODE;
            // 创建密码工厂对象;
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("AES");
            // 把字符串格式的密钥转成字节数组;
            byte[] keyData = sKey.getBytes();
            // 以密钥数组为参数，创建密码规则
            DESKeySpec keySpec = new DESKeySpec(keyData);
            // 以密码规则为参数，用密码工厂生成密码
            Key key = keyFactory.generateSecret(keySpec);
            // 创建密码对象
            Cipher cipher = Cipher.getInstance("AES");
            // 以加密模式和密码为参数对密码对象 进行初始化
            cipher.init(mode, key);
            // 完成最终加密
            byte[] result = cipher.doFinal(byteSource);
            // 返回加密后的数组
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @author wyg
     * @param password
     * @return
     */
    public static String password(String password,String sky) {
//        return encrypt(password,sky);
        return encrypt(password,sky);
    }


    /**
     * @author wyg
     * base64加密
     * */
    public static String encrypt2(String content)throws Exception{
        if (content == null || content.length() == 0){
            return null;
        }
        Cipher cipher=Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);//创建密码器
        byte[] byteContent=content.getBytes("utf-8");cipher.init(Cipher.ENCRYPT_MODE,
                getSecretKey(DEFAULT_PSWD));//初始化为加密模式的密码器
        byte[] result=cipher.doFinal(byteContent);//加密
        return
                org.apache.commons.codec.binary.Base64.encodeBase64String(result);//通过Base64转码返回
    }

    private static SecretKeySpec getSecretKey(final String password)throws NoSuchAlgorithmException {
        KeyGenerator kg=null;
        kg=KeyGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom=SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(password.getBytes());
        //AES要求密钥长度为128
        kg.init(128,secureRandom);
        SecretKey secretKey=kg.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(),KEY_ALGORITHM);//转换为AES专用密钥
    }

    /**
     * base64加密方法（OA同事提供的方法）
     * */
        /**
         * 将字符串转化为base64编码
         *
         * @author wyg
         * @param
         * @return
         * @throws
         */
        public static String getBase64(String s) throws UnsupportedEncodingException {
            if(StringUtil.isEmpty(s)){
                s = "";
            }
            byte[] bytes = org.apache.commons.codec.binary.Base64.encodeBase64(s.getBytes("utf-8"));
            return new String(bytes, "utf-8");
        }

    /**
     * 将 BASE64 编码的字符串 s 进行解码
     *
     * @author wyg
     * @param
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getFromBase64(String s)
            throws UnsupportedEncodingException {
        if(s!=null){
            byte[] bytes = s.getBytes("utf-8");
            byte[] convertBytes = org.apache.commons.codec.binary.Base64
                    .decodeBase64(bytes);
            return new String(convertBytes, "utf-8");
        }
        return  "";
    }





}
