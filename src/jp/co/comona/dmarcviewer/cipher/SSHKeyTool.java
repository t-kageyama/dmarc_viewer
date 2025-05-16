package jp.co.comona.dmarcviewer.cipher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

/**
 * SSH key tool.
 * @author kageyama
 * date: 2025/05/09
 */
public abstract class SSHKeyTool {

	// MARK: - Static Properties
	private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

	// MARK: - Properties
	protected Key key = null;

	// MARK: - Constructor.
	/**
	 * constructor.
	 */
	protected SSHKeyTool() {
		super();
	}

	// MARK: - Load from File
	/**
	 * load from file.
	 * @param path file path.
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 */
	public abstract void load(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException;

	// MARK: - Encrypt
	/**
	 * encrypt.
	 * @param str string to encode.
	 * @return encoded data.
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] encrypt(String str) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher encrypterWithKey = Cipher.getInstance(TRANSFORMATION);
		encrypterWithKey.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedWithKey = encrypterWithKey.doFinal(str.getBytes(StandardCharsets.UTF_8));
		return encryptedWithKey;
	}

	// MARK: - Decrypt
	/**
	 * decrypt.
	 * @param encrypted encrypted bytes.
	 * @return decrypted string.
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String decrypt(byte[] encrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher decrypterWithKey = Cipher.getInstance(TRANSFORMATION);
		decrypterWithKey.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptedWithKey = decrypterWithKey.doFinal(encrypted);
		return new String(decryptedWithKey, StandardCharsets.UTF_8);
	}

	// MARK: - Base64
	/**
	 * get base 64 string from byte array.
	 * @param bytes byte array.
	 * @return base 64 string.
	 */
	public static String base64String(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	/**
	 * get byte array from base 64 string.
	 * @param str base 64 string.
	 * @return byte array.
	 */
	public static byte[] base64Bytes(String str) {
		return Base64.decodeBase64(str);
	}
}