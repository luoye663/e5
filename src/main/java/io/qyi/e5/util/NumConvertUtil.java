package io.qyi.e5.util;

import java.text.DecimalFormat;

/**
 * 数据类型转换工具类
 * @author cyf
 *
 */
public class NumConvertUtil{


    /**
     * bytes 转16进制字符串
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }




    /**
     * 16进制字符串转bytes
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {

        int len = 0;
        int num=0;

        //判断字符串的长度是否是两位
        if(hex.length()>=2){

            //判断字符喜欢是否是偶数
            len=(hex.length() / 2);
            num = (hex.length() % 2);

            if (num == 1) {
                hex = "0" + hex;
                len=len+1;
            }


        }else{

            hex = "0" + hex;
            len=1;


        }


        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;



    }


    private static int toByte(char c) {


        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;


    }


    /**
     * 16进制字符串转十进制int
     * @param HexString
     * @return
     */
    public static int HexStringToInt(String HexString) {

        int inJTFingerLockAddress = Integer.valueOf(HexString, 16);

        return inJTFingerLockAddress;
    }




    /**
     * 十进制int转16进制字符串
     * @param HexString
     * @return
     */
    public static String IntToHexString(int num) {

        String hexString = Integer.toHexString(num);

        return hexString;
    }


    /**
     * 16进制String转BCD
     * @param asc
     * @return
     */
    public static byte[] strToBcd(String asc) {
        int len = asc.length();
        int mod = len % 2;

        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }

        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length()/2; p++) {
            if ( (abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ( (abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ( (abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ( (abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            }else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }


    /**
     * String 类型数字转化为Double  保留置顶小数位(用于显示金额等。)
     * @param money
     * @param type  保留小数点位数  #.00保留两位  #.0保留一位  #保留整数
     * @return
     */
    public static String strToDouble(String money,String type){

        String toDouble= new DecimalFormat(type).format(Double.parseDouble(money));

        return toDouble;

    }





}