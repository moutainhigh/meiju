package cn.visolink.utils;

import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
/**
 * @author sjl
 * @Created date 2020/6/30 11:43 上午
 * OA参数加密，解密工具类
 */
@Component
@Data
public class OAEncryptionUtil {

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_PSWD = "Djk@%&opN!$$*";

    public static String encrypt(String content) {
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            // 初始化为加密模式的密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(DEFAULT_PSWD));
            // 加密
            byte[] result = cipher.doFinal(byteContent);
            //通过Base64转码返回
            return org.apache.commons.codec.binary.Base64.encodeBase64String(result);
        }catch (Exception e){
            e.printStackTrace();
            return org.apache.commons.codec.binary.Base64.encodeBase64String(null);
        }

    }
    //返回生成指定算法密钥生成器的 KeyGenerator 对象
    private static SecretKeySpec getSecretKey(final String password) throws NoSuchAlgorithmException {
        KeyGenerator kg = null;
        kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
        secureRandom.setSeed(password.getBytes());
        //AES 要求密钥长度为 128
        kg.init(128, secureRandom);
        //生成一个密钥
        SecretKey secretKey = kg.generateKey();
        // 转换为AES专用密钥
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }

    /**
     * 解密
     * @param content
     * @return
     * @throws Exception
     */
    public static String decrypt(String content) throws Exception {
        //实例化
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        // 使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(DEFAULT_PSWD));
        //执行操作
        byte[] result = cipher.doFinal(Base64.decodeBase64(content));
        return new String(result, "utf-8");
    }
}
