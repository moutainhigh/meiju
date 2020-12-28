package cn.visolink.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

public class AESUtils {

	/**密钥算法*/
	private static final String KEY_ALGORITHM = "AES";

	/**加解密算法/工作模式/填充方式*/
	private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	/**SecretKeySpec类是KeySpec接口的实现类,用于构建秘密密钥规范 */
	private SecretKeySpec key;

	private static AESUtils instance;

	public AESUtils(String hexKey) {
		key = new SecretKeySpec(hexKey.getBytes(), KEY_ALGORITHM);
	}

	/**
	 * 随机生成秘钥
	 */
	public static String generateDesKey(int length) {
		try {
			KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
			kg.init(length);
			//要生成多少位，只需要修改这里即可128, 192或256
			SecretKey sk = kg.generateKey();
			byte[] b = sk.getEncoded();
			String s = byteToHexString(b);
			System.out.println(s);
			return s;
		}catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * byte数组转化为16进制字符串
	 *
	 * @param bytes
	 * @return
	 */
	public static String byteToHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String strHex = Integer.toHexString(bytes[i]);
			if (strHex.length() > 3) {
				sb.append(strHex.substring(6));
			} else {
				if (strHex.length() < 2) {
					sb.append("0" + strHex);
				} else {
					sb.append(strHex);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * AES加密
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String data) throws Exception {
		Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM); // 创建密码器
		cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
		return new BASE64Encoder().encode(cipher.doFinal(data.getBytes()));
	}

	/**
	 * AES解密
	 * @param base64Data
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String base64Data) throws Exception{
		Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(base64Data)));
	}

	public static AESUtils getInstance(String msgKey) {
		if(instance == null)
			instance = new AESUtils(msgKey);
		return instance;
	}

	public static void main(String[] args) throws Exception {
		String key = AESUtils.generateDesKey(128);
		AESUtils util = AESUtils.getInstance(key); // 密钥
		System.out.println("encrypt:"+util.encrypt("1234")); // 加密
		System.out.println("decrypt:"+util.decrypt(util.encrypt("1234"))); // 解密
		generateDesKey(256);
	}


}
