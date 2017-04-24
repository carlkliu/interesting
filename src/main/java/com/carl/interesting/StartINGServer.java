/*
 * file name: StartINGServer.java mofidy staff: template mofidy time: 24 Mar
 * 2016
 */
package com.carl.interesting;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.carl.interesting.common.constants.Constant;
import com.carl.interesting.common.constants.KeyConstant;
import com.carl.interesting.common.util.LogUtil;
import com.carl.interesting.common.util.NetworkUtil;

/**
 * This is the Main class for start Server.
 * 
 * @author xupingzheng
 * @version [version, 24 Mar 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class StartINGServer {
    private static final Log LOG = LogFactory.getLog(StartINGServer.class);
    
    private static String BASE = null;
    
    private static int PORT = -1;
    
    /**
     * Starts Jetty HTTP server exposing JAX-RS resources defined in this
     * 
     * @param port
     * @return [explain parameter]
     * @return Server [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static Server startServer(int port) {
        final ResourceConfig rc = new ResourceConfig()
                .packages("com.carl.interesting");
        try {
            String address = InetAddress.getLocalHost().getHostAddress();
            if (NetworkUtil.isPortUsing(address, port)) {
                return null;
            }
            BASE = "http://" + address;
        }
        catch (UnknownHostException e) {
            LogUtil.logError(LOG, e);
        }
        // create and start a new instance of Jetty http server
        // exposing the Jersey application at base and port
        return JettyHttpContainerFactory.createServer(
                URI.create(BASE + ":" + Integer.toString(port)), rc);
    }
    
    /**
     * Main method.
     * 
     * @param args [explain parameter]
     * @return void [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static void main(String[] args) {
        LOG.info("Start ING server");
        switch (args.length) {
            case 0:
                LOG.info("The default port " + PORT + " will be used.");
                break;
            case 1:
                PORT = Integer.parseInt(args[0]);
                break;
            default:
                PORT = Integer.parseInt(args[0]);
        }
        try {
            initServer();
            // start server
            if (null == startServer(PORT)) {
                LOG.error("port " + PORT + " has been used");
                System.exit(0);
                return;
            }
            LOG.info(
                    String.format(
                            "ING Server is started at: " + "%s/"
                                    + Constant.PROJECT + "/ ...",
                            BASE + ":" + Integer.toString(PORT)));
            // start clean monitor job
            // new MonitorCleanJob().start();
            // 加载数据库所有对象数据到缓存(Host/Mon/Osd/Mds/Pool/Access)
            // clusterServiceImpl.initFromDB();
            // 加载数据库信息到缓存(心跳，监控)
            // initHostAgentDB();
            // 启动定时器执行任务
            // initScheduler();
            // initLeader();
            // 启动与license通信
            // communicationScheduler.initCommunicate();
            // 启动告警的线程
            // warningDisScheduler.initWarningDistribute();
            // recoverTask();
        }
        catch (Exception e) {
            LogUtil.logError(LOG, e);
        }
    }
    
    /**
     * Mapping MEMORY_MAP_FILE into memory for communicating with external.
     * 
     * @throws IOException [explain parameter]
     * @return void [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    private static void initServer() throws IOException {
        BASE = KeyConstant.HOST;
        PORT = Integer.parseInt(KeyConstant.SERVER_PORT);
        Thread messageThread = new MessageTask();
        messageThread.setName("messageThread");
        messageThread.start();
    }
}
