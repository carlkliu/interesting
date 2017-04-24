package com.carl.interesting.common.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility for network
 * 
 * @author xupingzheng
 * @version [version, Sep 25, 2014]
 * @see liutianbao 20151021 start<br>
 * delete not use function:isIpMacMatch(String ip, String mac)<br>
 * delete function isConnect(long deviceId) ,isConnect(Device device)<br>
 * liutianbao 20151021 end<br>
 * @since [product/module version]
 */
public class NetworkUtil {
    private static final Log LOG = LogFactory.getLog(NetworkUtil.class);
    
    /**
     * Check if the service with ip is ping.
     * 
     * @param ip
     * @return boolean true or false
     * @throws IOException
     * @throws UnknownHostException
     */
    public static boolean isConnect(String ip)
            throws UnknownHostException, IOException {
        return InetAddress.getByName(ip).isReachable(700);
    }
    
    /***
     * true:already in using false:not using
     * 
     * @param host
     * @param port
     * @throws UnknownHostException
     */
    public static boolean isPortUsing(String host, int port)
            throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        Socket socket = null;
        try {
            socket = new Socket(theAddress, port);
            flag = true;
        }
        catch (IOException e) {
            // LOG.error("Failed to create connection.Host:" + host + ",port:"
            // + port);
        }
        finally {
            if (null != socket) {
                try {
                    socket.close();
                }
                catch (IOException e) {
                    LOG.error("Failed to close connection.Host:" + host
                            + ",port:" + port);
                }
            }
        }
        return flag;
    }
}
