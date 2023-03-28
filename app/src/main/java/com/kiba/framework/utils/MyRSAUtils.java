package com.kiba.framework.utils;


import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class MyRSAUtils {

    /**
     * \* 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * \* 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * \* 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";


    /**
     * \* <p>
     * \* 生成密钥对(公钥和私钥)
     * \* </p>
     * <p>
     * \* @return
     * \* @throws Exception
     */

    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return base64Encode(key.getEncoded());
    }

    public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return base64Encode(key.getEncoded());
    }

    /**
     * \* 用base64编码     \* @param bytes     \* @return
     */
    private static String base64Encode(byte[] bytes) throws UnsupportedEncodingException {
        String str = new String(Base64.encodeBase64(bytes));
        //byte[] decoded = Base64.decodeBase64(str.getBytes("UTF-8"));
        return str;
    }

    /**
     * RSA私钥解密
     *
     * @param str        解密字符串
     * @param privateKey 私钥
     * @return 明文
     */
    public static String decrypt(String str, String privateKey) {
        //64位解码加密后的字符串
        byte[] inputByte;
        String outStr = "";
        try {
            inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
            //base64编码的私钥
            byte[] decoded = Base64.decodeBase64(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            //RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            outStr = new String(cipher.doFinal(inputByte));
        } catch (UnsupportedEncodingException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return outStr;
    }

    /**
     * RSA公钥加密
     *
     * @param str       需要加密的字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {

        try {
            //base64编码的公钥
            byte[] decoded = Base64.decodeBase64(publicKey.getBytes("UTF-8"));
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            //RSA加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] info = cipher.doFinal(str.getBytes("UTF-8"));
            String outStr = new String(Base64.encodeBase64(info));
            //String outStr = Base64.encodeBase64String(info);
            return outStr;
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * @param publicKeyStr 经过Base64编码过的公钥字符串
     * @param content
     * @return {@link String}
     * @throws
     * @Description: TODO(使用公钥加密)
     * @author
     * @date 2020/9/27 23:12
     */
    public static String publicKeyEncrypt(String content, String publicKeyStr)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, IOException {
//        传递进来的公钥是经过Base64编码过的字符串，要返回成字节数组形式，需要解码
        byte[] decodePublicKeyByte = Base64.decodeBase64(publicKeyStr.getBytes("UTF-8"));
        //调用X509EncodedKeySpec对象,转换格式
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodePublicKeyByte);
        //调用密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        //调用Java加密的Cipher对象，
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);//第一个参数表示这是加密模式，第二个参数表示密钥

        //分段加密
        byte[] bytesContent = content.getBytes();
        int inputLen = bytesContent.length;
        int offLen = 0;//偏移量
        int i = 0;
        ByteArrayOutputStream bops = new ByteArrayOutputStream();
        while (inputLen - offLen > 0) {
            byte[] cache;
            if (inputLen - offLen > 256) {
                cache = cipher.doFinal(bytesContent, offLen, 256);
            } else {
                cache = cipher.doFinal(bytesContent, offLen, inputLen - offLen);
            }
            bops.write(cache);
            i++;
            offLen = 256 * i;
        }
        bops.close();
        byte[] encryptedData = bops.toByteArray();


        //使用Base64对加密结果进行编码
        //String encode = Base64.getEncoder().encodeToString(encryptedData);
        String encode = Base64.encodeBase64String(encryptedData);
        return new String(encode);
    }

    /**
     * @param content       秘文，base64编码过的字符串
     * @param privateKeyStr 私钥，base64编码过的字符串
     * @return {@link String}
     * @throws
     * @Description: TODO(私钥解密)
     * @author
     * @date 2020/9/27 23:15
     */
    public static String privateKeyDecrypt(String content, String privateKeyStr)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, IOException {
        //传递进来的私钥是经过Base64编码过的字符串，要返回成字节数组形式，需要解码
        byte[] decodePrivateKeyByte = Base64.decodeBase64(privateKeyStr);
        //调用PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodePrivateKeyByte);
        //调用密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        //调用Java加密的Cipher对象，
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);//第一个参数表示这是加密模式，第二个参数表示密钥


        byte[] bytesContent = Base64.decodeBase64(content.getBytes());
        int inputLen = bytesContent.length;
        int offLen = 0;
        int i = 0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (inputLen - offLen > 0) {
            byte[] cache;
            if (inputLen - offLen > 256) {
                cache = cipher.doFinal(bytesContent, offLen, 256);
            } else {
                cache = cipher.doFinal(bytesContent, offLen, inputLen - offLen);
            }
            byteArrayOutputStream.write(cache);
            i++;
            offLen = 256 * i;

        }
        byteArrayOutputStream.close();
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        //使用Base64对加密结果进行编码
        return new String(byteArray);
    }

    public static void Excute() throws Exception {
        Map<String, Object> pairs = MyRSAUtils.genKeyPair();
        System.out.println("公钥:" + MyRSAUtils.getPublicKey(pairs));
        System.out.println("私钥:" + MyRSAUtils.getPrivateKey(pairs));
    }


}
