package Utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.zip.CRC32;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import com.example.hotelbooking.entity.Customer;

import io.micrometer.common.util.StringUtils;

public class StringUtil {
	
	public static boolean areNotBlank(String... strings) {
		for(String str: strings) {
			if(StringUtils.isBlank(str))return false;
		}
		return true;
	}
	
	
	private static String[] shortChars = new String[]{"a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z"};
	
	public static String shortUUID() {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(shortChars[x % 0x3E]);
		}
		return shortBuffer.toString();
	}

	public static String shortUUIDWithDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		Date now = new Date();

		return shortUUID() + sdf.format(now);
	}
	
	public static String encryptPassword(String rawPassword, String salt) {
		MessageDigest digest;
		String sha256hex = rawPassword;
		
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(rawPassword.concat(salt).getBytes(StandardCharsets.UTF_8));
			sha256hex = new String(Hex.encode(encodedhash));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return sha256hex;
	}
	
	public static boolean isAuthenticate(String password, Customer customer) {
		if(StringUtils.isBlank(password) || customer == null) return false;
		
		String cusPassword = customer.getPassword();
		String salt = customer.getSalt();
		
		String encryptPassword = encryptPassword(password, salt);
		return encryptPassword.equals(cusPassword);
	}
	
	public static boolean sessionVerified(String sessionToken, Customer customer) {
		return sessionToken.equals(customer.getSession_token());
	}
	
	
}
