package com.bagastudio.encryption;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class AeSimpleSHA1
{
    /**
     * AES 加解密
     *
     * @author magiclen
     * @see Base64
     */
    public static class MagicCrypt
    {

        /**
         * 加密演算法使用AES
         */
        private static final String ALGORITHM = "AES";
        // -----類別常數-----
        /**
         * 預設的Initialization Vector，為16 Bits的0
         */
        private static final IvParameterSpec DEFAULT_IV = new IvParameterSpec(new byte[]
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        /**
         * AES使用CBC模式與PKCS5Padding
         */
        private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

        /**
         * 取得資料的雜湊值
         *
         * @param algorithm
         *            傳入雜驟演算法
         * @param data
         *            傳入要雜湊的資料
         * @return 傳回雜湊後資料內容
         */
        private static byte[] getHash(final String algorithm, final byte[] data)
        {
            try
            {
                final MessageDigest digest = MessageDigest.getInstance(algorithm);
                digest.update(data);
                return digest.digest();
            }
            catch (final Exception ex)
            {
                throw new RuntimeException(ex.getMessage());
            }
        }

        // -----物件方法-----
        /**
         * 取得字串的雜湊值
         *
         * @param algorithm
         *            傳入雜驟演算法
         * @param text
         *            傳入要雜湊的字串
         * @return 傳回雜湊後資料內容
         */
        public static byte[] getHash(final String algorithm, final String text)
        {
            try
            {
                return MagicCrypt.getHash(algorithm, text.getBytes("UTF-8"));
            }
            catch (final Exception ex)
            {
                throw new RuntimeException(ex.getMessage());
            }
        }

        /**
         * Cipher 物件
         */
        private Cipher cipher;

        /**
         * AES CBC模式使用的Initialization Vector
         */
        private IvParameterSpec iv;

        // -----物件變數-----
        /**
         * 取得AES加解密的密鑰
         */
        private Key key;

        // -----建構子-----
        /**
         * 建構子，使用128 Bits的AES密鑰(計算任意長度密鑰的MD5)和預設IV
         *
         * @param key
         *            傳入任意長度的AES密鑰
         */
        public MagicCrypt(final String key)
        {
            this(key, 128);
        }

        /**
         * 建構子，使用128 Bits或是256 Bits的AES密鑰(計算任意長度密鑰的MD5或是SHA256)和預設IV
         *
         * @param key
         *            傳入任意長度的AES密鑰
         * @param bit
         *            傳入AES密鑰長度，數值可以是128、256 (Bits)
         */
        public MagicCrypt(final String key, final int bit)
        {
            this(key, bit, null);
        }

        /**
         * 建構子，使用128 Bits或是256 Bits的AES密鑰(計算任意長度密鑰的MD5或是SHA256)，用MD5計算IV值
         *
         * @param key
         *            傳入任意長度的AES密鑰
         * @param bit
         *            傳入AES密鑰長度，數值可以是128、256 (Bits)
         * @param iv
         *            傳入任意長度的IV字串
         */
        public MagicCrypt(final String key, final int bit, final String iv)
        {
            if (bit == 256)
            {
                this.key = new SecretKeySpec(MagicCrypt.getHash("SHA-256", key), MagicCrypt.ALGORITHM);
            }
            else
            {
                this.key = new SecretKeySpec(MagicCrypt.getHash("MD5", key), MagicCrypt.ALGORITHM);
            }
            if (iv != null)
            {
                this.iv = new IvParameterSpec(MagicCrypt.getHash("MD5", iv));
            }
            else
            {
                this.iv = MagicCrypt.DEFAULT_IV;
            }

            this.init();
        }

        /**
         * 解密文字
         *
         * @param data
         *            傳入要解密的資料
         * @return 傳回解密後的文字
         */
        public String decrypt(final byte[] data)
        {
            try
            {
                this.cipher.init(Cipher.DECRYPT_MODE, this.key, this.iv);
                final byte[] decryptData = this.cipher.doFinal(data);
                return new String(decryptData, "UTF-8");
            }
            catch (final Exception ex)
            {
                throw new RuntimeException(ex.getMessage());
            }
        }

        /**
         * 解密文字
         *
         * @param str
         *            傳入要解密的文字
         * @return 傳回解密後的文字
         */
        public String decrypt(final String str)
        {
            try
            {
                return this.decrypt(Base64.decode(str, Base64.DEFAULT));
            }
            catch (final Exception ex)
            {
                return "";
            }
        }

        /**
         * 加密資料
         *
         * @param data
         *            傳入要加密的資料
         * @return 傳回加密後的資料
         */
        public String encrypt(final byte[] data)
        {
            try
            {
                this.cipher.init(Cipher.ENCRYPT_MODE, this.key, this.iv);
                final byte[] encryptData = this.cipher.doFinal(data);
                return new String(Base64.encode(encryptData, Base64.DEFAULT), "UTF-8");
            }
            catch (final Exception ex)
            {
                throw new RuntimeException(ex.getMessage());
            }
        }

        /**
         * 加密文字
         *
         * @param str
         *            傳入要加密的文字
         * @return 傳回加密後的文字
         */
        public String encrypt(final String str)
        {
            try
            {
                return this.encrypt(str.getBytes("UTF-8"));
            }
            catch (final Exception ex)
            {
                throw new RuntimeException(ex.getMessage());
            }
        }

        /**
         * 初始化
         */
        private void init()
        {
            try
            {
                this.cipher = Cipher.getInstance(MagicCrypt.TRANSFORMATION);
            }
            catch (final Exception ex)
            {
                throw new RuntimeException(ex.getMessage());
            }
        }

    }

    private static String ConvertToHex(final byte[] data)
    {
        final StringBuilder buf = new StringBuilder();
        for (final byte b : data)
        {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do
            {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(final String text) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        final MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        final byte[] sha1hash = md.digest();
        return AeSimpleSHA1.ConvertToHex(sha1hash);
    }
}
