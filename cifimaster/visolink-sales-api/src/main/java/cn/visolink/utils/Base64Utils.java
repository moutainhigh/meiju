package cn.visolink.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64Utils {

	public static String base64Encode(String data,String charset){
		BASE64Encoder b64enc = new BASE64Encoder();
	    try {
			return b64enc.encode(data.getBytes(charset));
		} catch (Exception e) {
			return null;
		}
	}

	public static String base64Decode(String data,String charset){
		BASE64Decoder b64dec = new BASE64Decoder();
		try {
			return new String(b64dec.decodeBuffer(data),charset);
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] base64Decode(String data){
		BASE64Decoder b64dec = new BASE64Decoder();
		try {
			return b64dec.decodeBuffer(data);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param encoded
	 * @return
	 */
	public static String base64Encode(byte[] encoded) {
		// TODO Auto-generated method stub
		BASE64Encoder b64enc = new BASE64Encoder();
	    try {
			return b64enc.encode(encoded);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
