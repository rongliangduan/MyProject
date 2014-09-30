package cn.op.common.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 3DES加密工具类
 */
public class DesUtils {
	// 密钥
	private final static String secretKey = "jujiaruanjianshunlu666123456";
	// 向量
	private final static String iv = "01234567";
	// 加解密统一使用的编码方式
	private final static String encoding = "utf-8";

	public static void main(String[] args) throws Exception {
		String data = "我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双"
				+ "方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导"
				+ "书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是"
				+ "双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发"
				+ "生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方"
				+ "商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方"
				+ "商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双"
				+ "方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双"
				+ "方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定"
				+ "发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是"
				+ "双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们"
				+ "是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们"
				+ "是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是"
				+ "双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书我们是双方商定发生的辅导书 ";
		System.err.println(encrypt(data));
		System.err.println(decrypt(encrypt(data)));

	}

	/**
	 * 3DES加密
	 * 
	 * @param plainText
	 *            普通文本
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String plainText) {
		String encrptStr = null;
		if (plainText == null || plainText.trim().equals("")
				|| plainText.equals("null")) {
			return null;
		}
		try {
			DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
			SecretKeyFactory keyfactory = SecretKeyFactory
					.getInstance("desede");
			Key deskey = keyfactory.generateSecret(spec);
			Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
			IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
			byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
			encrptStr = Base64.encode(encryptData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encrptStr;
	}

	/**
	 * 3DES解密
	 * 
	 * @param encryptText
	 *            加密文本
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String encryptText) {
		String decryptStr = null;
		if (encryptText == null || encryptText.trim().equals("")
				|| encryptText.equals("null")) {
			return null;
		}
		try {
			DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
			SecretKeyFactory keyfactory = SecretKeyFactory
					.getInstance("desede");
			Key deskey = keyfactory.generateSecret(spec);
			Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
			IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
			byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));
			decryptStr = new String(decryptData, encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decryptStr;
	}
}