package com.flower.spirit.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

public class AESUtils {

    public static String decrypt(String uuid, String encrypted, String password) throws Exception {
        String rawInput = uuid + "-" + password;
        String md5Hex = md5(rawInput);
        String realPassphrase = md5Hex.substring(0, 16); // 16 字节 = 128 位
        byte[] encryptedBytes = java.util.Base64.getDecoder().decode(encrypted);
        byte[] salt = Arrays.copyOfRange(encryptedBytes, 8, 16);
        byte[] cipherText = Arrays.copyOfRange(encryptedBytes, 16, encryptedBytes.length);
        byte[] passBytes = realPassphrase.getBytes(StandardCharsets.UTF_8);
        byte[] keyAndIv = bytesToKey(passBytes, salt, 48);
        byte[] key = Arrays.copyOfRange(keyAndIv, 0, 32);
        byte[] iv = Arrays.copyOfRange(keyAndIv, 32, 48);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] decryptedBytes = cipher.doFinal(cipherText);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    private static byte[] bytesToKey(byte[] password, byte[] salt, int outputLen) {
        byte[] data = concat(password, salt);
        byte[] key = md5(data);
        byte[] finalKey = key;
        while (finalKey.length < outputLen) {
            byte[] nextInput = concat(key, data);
            key = md5(nextInput);
            finalKey = concat(finalKey, key);
        }

        return Arrays.copyOf(finalKey, outputLen);
    }
    private static byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    private static byte[] md5(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(input);
        } catch (Exception e) {
            throw new RuntimeException("MD5 error", e);
        }
    }
    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 error", e);
        }
    }
}