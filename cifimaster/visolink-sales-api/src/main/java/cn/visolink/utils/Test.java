package cn.visolink.utils;



public class Test {
	public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCUQUvOrI7z+shO6ULDywzPYEhmrTD5GgE/nUEToXeSe8EM5LGQreZ/OTQ3SwLagX+0mOk3hOEe6zIWRt0yhqKGzvM0XIwRgrghwpkA4ILJGJzQCjZ5sPLoHKM8642SadnOKRK9uPTufSdGyY4XMHgsZTvpzM1POoQn9T9fp7kKgwIDAQAB";
	public static final String appPubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCVMG9GV0hQWlUqd7vjD0anaDlHosqgIp00pv+ikUjSIg0RbnWqMw+w2ZSZfam/JlwD6C/cyjtjYVxFzlniLQpqFdVCM+Kx4dOu/5pfeAF8E+4aUDW1i5Eeu5yOyI0+UyR87tDS8x9asd9HWG0+yfcwjEe7nJiU9zK1hoaKHNckxQIDAQAB";
	public static final String appPriKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJUwb0ZXSFBaVSp3u+MPRqdoOUei\n" +
			"yqAinTSm/6KRSNIiDRFudaozD7DZlJl9qb8mXAPoL9zKO2NhXEXOWeItCmoV1UIz4rHh067/ml94\n" +
			"AXwT7hpQNbWLkR67nI7IjT5TJHzu0NLzH1qx30dYbT7J9zCMR7ucmJT3MrWGhooc1yTFAgMBAAEC\n" +
			"gYA2B9eU+xFmgICto7V5M1QcVwO/rPaDbmXO5thYURO1fr2K3Z1hqaJ6IyLNQBSU9NiIVbPX26oM\n" +
			"gPtBEM2+ux804Fwl2fU0EuKCiQapxtF1l2CkPuTO5ObJkFVbzIZvnFNNDakIT9I3iIoFB+QPemQ1\n" +
			"u5S3mv8y7x7Kz7+E3Q6q1QJBAPu28mQ5pltESGVoXmcVTbuFBmaRsFLz1Pqo3vIHf5atmuVPu/Xj\n" +
			"oZ8WmAoN4s9jpJimvG5QNAZ4lL0nGb6fcasCQQCXuqYpK7bS+JF1iw2zqpaidoVZ/A5ZWkVqomHj\n" +
			"+wRkqJGhNH1Y/iovrw06wJE+q5N1+E42DPMrk74GwEGP+jNPAkEAg3KWQiCY7zBJXiuSoOJPJY3i\n" +
			"Oc369la+8eceBeZEirs+GGH7Ff05eYqi+x0lRIgUfGMWI8VeZcKyadTxbMp24QJBAIeV304DJki4\n" +
			"nyNuszvOQPXE+71BpIDsTgPQP7G+alqY2Co6AZk45vHdd/D8i854/Dj7PsjGIbbbO4BE5VGHv7kC\n" +
			"QQCQIXjDicfr73ogPdSWqmb8oRJZa2KFqN9VWv0FZgcvqy3P/3dnxYg54AraoyNSAiIH0fsX3zPU\n" +
			"b8Sroj7WTkiD";

	public static void main(String[] args) throws Exception {
		encryption();



	}


	//加密方法
	public static void  encryption() throws Exception {
		String str = "{\"name\":\"张三\",\"age\":\"18\"}";
		String key = AESUtils.generateDesKey(128);
		AESUtils util = AESUtils.getInstance(key); // 密钥
		String encryptData = util.encrypt(str);
		String encryptKey = RSAUtils.encryptByPrivate(key,appPriKey,"utf-8");
		System.out.println("使用RSA加密后的AES密钥:");
		System.out.println(encryptKey);
		System.out.println("使用AES加密后的数据:");
		System.out.println(encryptData);
	}

	//解密方法
	public static void decode(String encryptKey,String encryptData) throws Exception {
		//String key = RSAUtils.decryptByPrivateKey(encryptKey);
		//String data = AESUtils.decryptData(key,encryptData);
		System.out.println("使用RSA解密后的AES密钥:" );
		System.out.println(encryptKey);
		System.out.println("使用AES解密后的数据:");
		//System.out.println(data);
	}


}
