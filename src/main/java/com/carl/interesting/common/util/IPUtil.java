package com.carl.interesting.common.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * IP address utility class
 * 
 * @author Carl Liu
 * @version [version, Sep 21, 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class IPUtil {
    private static final String IP_PATTERN = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}"
            + "(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
    
    /**
     * IP address String to long
     * 
     * @param ipAddress
     * @return [explain parameter]
     * @return long [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static long ipStr2Long(String ipAddress) {
        long ip = 0L;
        if (isValid(ipAddress)) {
            try {
                String[] ips = ipAddress.split("\\.");
                for (int i = 0; i < 4; ++i) {
                    //
                    // IPv4为32bit，所以计算方法为一个数字乘以256加上第二个数字后，乘以256加上第三个数后，乘以256加上第四个数
                    ip = ip << 8 | Integer.parseInt(ips[i]);
                }
            }
            catch (NumberFormatException e) {
            }
        }
        return ip;
    }
    
    /**
     * IP address long to String
     * 
     * @param ip
     * @return [explain parameter]
     * @return String [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static String ipLong2Str(long ip) {
        return (ip >>> 24 & 255L) + "." + (ip >>> 16 & 255L) + "."
                + (ip >>> 8 & 255L) + "." + (ip >>> 0 & 255L);
    }
    
    /**
     * Validate the input of ip. At 2015-3-19 10:02,jiaqi yang create for
     * validating the ip.
     * 
     * @param ip
     * @return [explain parameter]
     * @return boolean [explain return type] if is valid return true,or false.
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static boolean isValid(String ip) {
        boolean isValid = true;
        try {
            isValid = Pattern.matches(IP_PATTERN, ip);
        }
        catch (Exception e) {
            isValid = false;
        }
        return isValid;
    }
    
    /**
     * compare IP
     * 
     * @param startIP
     * @param endIP
     * @return boolean true is startIP >endIP,otherwise false
     * @see [class,class#method,class#member]
     */
    public static boolean compare(String startIP, String endIP) {
        return (ipStr2Long(startIP) > ipStr2Long(endIP));
    }
    
    /**
     * IP address into ascending order
     * 
     * @param ips
     * @return [explain parameter]
     * @return String[] [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static String[] sortIPASC(String[] ips) {
        Long[] data = new Long[ips.length];
        String[] sortRusult = new String[ips.length];
        for (int i = 0; i < ips.length; i++) {
            data[i] = ipStr2Long(ips[i]);
        }
        Arrays.sort(data);
        for (int k = 0; k < data.length; k++) {
            sortRusult[k] = ipLong2Str(data[k]);
        }
        return sortRusult;
    }
    
    /**
     * IP address in range
     * 
     * @param ip IP address,example:10.10.10.2
     * @param cidr IP network,example:10.10.10.0/24
     * @return boolean true is IP in range,otherwise false.
     * @see [class,class#method,class#member]
     */
    public static boolean isInRange(String ip, String cidr) {
        boolean result = false;
        try {
            if (isValid(ip)) {
                long i = ipStr2Long(ip);
                long netmask = 0xFFFFFFFF << (32
                        - Integer.parseInt(cidr.replaceAll(".*/", "")));
                String ip2 = cidr.replaceAll("/.*", "");
                if (isValid(ip2)) {
                    long j = ipStr2Long(ip2);
                    result = (i & netmask) == (j & netmask);
                }
            }
        }
        catch (Exception e) {
            result = false;
        }
        return result;
    }
    
    /**
     * validate IP is the same network.
     * 
     * @param ip1 IP,example:192.18.22.2
     * @param ip2 IP,example:192.18.22.2
     * @param netmask Netmask,example:255.255.255.0
     * @return boolean true is the same,false is different or netmask not exist.
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static boolean isTheSameNetwork(String ip1, String ip2,
            String netmask) {
        boolean result = false;
        if (0 != netmaskStr2Long(netmask)) {
            result = ((ipStr2Long(ip1)
                    & netmaskStr2Long(netmask)) == (ipStr2Long(ip2)
                            & netmaskStr2Long(netmask)));
        }
        return result;
    }
    
    /**
     * get netmask by mask bit
     * 
     * @param bit mask bit
     * @return String netmask ,example:255.255.255.0
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static String getNetmaskByMaskBit(int bit) {
        return ipLong2Str(getNetmaskNumByMaskBit(bit));
    }
    
    /**
     * Netmask String convert Long
     * 
     * @param netmask
     * @return long netmask's long ,0 is netmask not exist.
     * @see [class,class#method,class#member]
     */
    public static long netmaskStr2Long(String netmask) {
        long i = 0;
        if (null != getMaskBitByNetmask(netmask)) {
            i = getNetmaskNumByMaskBit(
                    Integer.parseInt(getMaskBitByNetmask(netmask)));
        }
        return i;
    }
    
    /**
     * get netmask number by mask bit
     * 
     * @param bit mask bit
     * @return String netmask ,example:255.255.255.0
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static long getNetmaskNumByMaskBit(int bit) {
        long result = 0;
        if (1 <= bit && bit <= 32) {
            result = 0xFFFFFFFF << (32 - bit);
        }
        return result;
    }
    
    /**
     * get IP of network by IP and netmask bit
     * 
     * @param ip IP address
     * @param bit netmask bit
     * @return [explain parameter]
     * @return String [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static String getStartIp(String ip, int bit) {
        return ipLong2Str(ipStr2Long(ip) & getNetmaskNumByMaskBit(bit));
    }
    
    /**
     * get network by IP and netmask bit
     * 
     * @param ip address
     * @param bit netmask bit
     * @return [explain parameter]
     * @return String network,example:192.168.9.176/24
     * @see [class,class#method,class#member]
     */
    public static String getNetwok(String ip, int bit) {
        return getStartIp(ip, bit) + "/" + String.valueOf(bit);
    }
    
    /**
     * get network by IP and netmask
     * 
     * @param ip address
     * @param netmask netmask
     * @return [explain parameter]
     * @return String network,example:192.168.9.176/24
     * @see [class,class#method,class#member]
     */
    public static String getNetwok(String ip, String netmask) {
        String str = "";
        if (null != getMaskBitByNetmask(netmask)) {
            str = getStartIp(ip, Integer.parseInt(getMaskBitByNetmask(netmask)))
                    + "/" + getMaskBitByNetmask(netmask);
        }
        return str;
    }
    
    /**
     * get netmask bit by netmask
     * 
     * @param netmask
     * @return [explain parameter]
     * @return String [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static String getMaskBitByNetmask(String netmask) {
        return netmaskMap().get(netmask);
    }
    
    /**
     * initial Netmask map,key is netmask,value is netmask bit.
     * 
     * @return [explain parameter]
     * @return Map<String,String> [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    private static Map<String, String> netmaskMap() {
        Map<String, String> netmask = new HashMap<String, String>();
        netmask.put("128.0.0.0", "1");
        netmask.put("192.0.0.0", "2");
        netmask.put("224.0.0.0", "3");
        netmask.put("240.0.0.0", "4");
        netmask.put("248.0.0.0", "5");
        netmask.put("252.0.0.0", "6");
        netmask.put("254.0.0.0", "7");
        netmask.put("255.0.0.0", "8");
        netmask.put("255.128.0.0", "9");
        netmask.put("255.192.0.0", "10");
        netmask.put("255.224.0.0", "11");
        netmask.put("255.240.0.0", "12");
        netmask.put("255.248.0.0", "13");
        netmask.put("255.252.0.0", "14");
        netmask.put("255.254.0.0", "15");
        netmask.put("255.255.0.0", "16");
        netmask.put("255.255.128.0", "17");
        netmask.put("255.255.192.0", "18");
        netmask.put("255.255.224.0", "19");
        netmask.put("255.255.240.0", "20");
        netmask.put("255.255.248.0", "21");
        netmask.put("255.255.252.0", "22");
        netmask.put("255.255.254.0", "23");
        netmask.put("255.255.255.0", "24");
        netmask.put("255.255.255.128", "25");
        netmask.put("255.255.255.192", "26");
        netmask.put("255.255.255.224", "27");
        netmask.put("255.255.255.240", "28");
        netmask.put("255.255.255.248", "29");
        netmask.put("255.255.255.252", "30");
        netmask.put("255.255.255.254", "31");
        netmask.put("255.255.255.255", "32");
        return netmask;
    }
}
