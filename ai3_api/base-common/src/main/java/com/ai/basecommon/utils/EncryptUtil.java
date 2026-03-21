package com.ai.basecommon.utils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public class EncryptUtil {


    private static final String CHARSET = "UTF-8";
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16;

    private static final String DEFAULT_AES_KEY = "up868aItQvsDFZs32mFmbL7T336p8pnq";


    //HMAC值
    public static String hmac(String data, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(CHARSET), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        byte[] result = mac.doFinal(data.getBytes(CHARSET));
        return toHex(result);
    }


    private static String toHex(byte input[]) {
        if (input == null) return null;
        StringBuilder output = new StringBuilder(input.length * 2);
        for (byte b : input) {
            output.append(String.format("%02x", b));
        }
        return output.toString();
    }


    public static String hmac(String[] args, String key) throws Exception {
        if (args == null || args.length == 0) {
            return (null);
        }
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            str.append(args[i]);
        }
        return (hmac(str.toString(), key));
    }

    //SHA-1算法
    public static String sha1(String aValue) {
        aValue = aValue.trim();
        byte value[];
        try {
            value = aValue.getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            value = aValue.getBytes();
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return toHex(md.digest(value));

    }

    //SHA-256算法
    public static String sha256(String aValue) {
        aValue = aValue.trim();
        byte value[];
        try {
            value = aValue.getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            value = aValue.getBytes();
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return toHex(md.digest(value));

    }

    //MD5算法
    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 calculation failed", e);
        }
    }

    //AES加密
    public static String aesEncrypt(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKey = getSecretKey(key);

        // 随机生成 IV
        byte[] ivBytes = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        // 加密
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes(CHARSET));

        // 将 IV 和密文一起 Base64 编码输出
        byte[] encryptedWithIv = new byte[IV_LENGTH + encrypted.length];
        System.arraycopy(ivBytes, 0, encryptedWithIv, 0, IV_LENGTH);
        System.arraycopy(encrypted, 0, encryptedWithIv, IV_LENGTH, encrypted.length);

        return java.util.Base64.getEncoder().encodeToString(encryptedWithIv);
    }


    //AES加密
    public static String aesEncrypt(String data) throws Exception {
        return aesEncrypt(data,DEFAULT_AES_KEY);
    }


    private static SecretKeySpec getSecretKey(String key) throws Exception {
        byte[] keyBytes = new byte[16];
        byte[] inputKeyBytes = key.getBytes(CHARSET);
        System.arraycopy(inputKeyBytes, 0, keyBytes, 0, Math.min(inputKeyBytes.length, keyBytes.length));
        return new SecretKeySpec(keyBytes, "AES");
    }

    //AES解密
    public static String aesDecrypt(String data, String key) throws Exception{
        byte[] encryptedIvBytes = Base64.getDecoder().decode(data);

        // 提取 IV 和密文
        byte[] ivBytes = new byte[IV_LENGTH];
        byte[] encryptedBytes = new byte[encryptedIvBytes.length - IV_LENGTH];
        System.arraycopy(encryptedIvBytes, 0, ivBytes, 0, IV_LENGTH);
        System.arraycopy(encryptedIvBytes, IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        SecretKeySpec secretKey = getSecretKey(key);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decrypted = cipher.doFinal(encryptedBytes);
        return new String(decrypted, CHARSET);
    }

    //AES解密
    public static String aesDecrypt(String data) throws Exception {
        return aesDecrypt(data,DEFAULT_AES_KEY);
    }


}
