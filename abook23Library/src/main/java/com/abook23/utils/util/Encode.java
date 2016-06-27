package com.abook23.utils.util;


import java.security.MessageDigest;

/**
 * @author abook23 2015年9月6日 11:24:07
 * @version 1.0
 */
public class Encode extends AESCipher {

    /**
     * 静态加密方法
     *
     * @param codeType 传入加密方式 MD5 || SHA
     * @param content  传入加密的内容
     * @return 返回加密结果
     */
    public static String getEncode(String codeType, String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance(codeType);// 获取一个实例，并传入加密方式
            digest.reset();// 清空一下
            digest.update(content.getBytes());// 写入内容,可以指定编码方式content.getBytes("utf-8");
            StringBuilder builder = new StringBuilder();
            for (byte b : digest.digest()) {
                builder.append(Integer.toHexString((b >> 4) & 0xf));
                builder.append(Integer.toHexString(b & 0xf));
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
