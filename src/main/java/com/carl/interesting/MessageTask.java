package com.carl.interesting;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.carl.interesting.common.util.LogUtil;

/**
 * Thread for communicating with external.
 * 
 * @author xupingzheng
 * @version [version, 13 Apr 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class MessageTask extends Thread {
    /**
     * Memory map file for communicating with interesting
     */
    public static final String MEMORY_MAP_FILE = "/tmp/interesting_communication";
    
    /**
     * Receive the stop message
     */
    public static final int MESSAGE_STOP = 1;
    
    private static final Log LOG = LogFactory.getLog(MessageTask.class);
    
    @Override
    public void run() {
        try {
            File mapFile = new File(MEMORY_MAP_FILE);
            if (!mapFile.exists()) {
                mapFile.createNewFile();
            }
            final FileInputStream inputStream = new FileInputStream(
                    MEMORY_MAP_FILE);
            Path path = Paths.get(MEMORY_MAP_FILE);
            FileChannel fileChannel = FileChannel.open(path,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.READ);
            final MappedByteBuffer byteBuffer = fileChannel
                    .map(MapMode.READ_WRITE, 0, 4);
            while (true) {
                byteBuffer.position(0);
                Thread.sleep(1000);
                int code = byteBuffer.getInt();
                if (MESSAGE_STOP == code) {
                    LOG.info(
                            "Receive stop command,now Microservice is going to shutdown");
                    byteBuffer.position(0);
                    byteBuffer.putInt(0);
                    byteBuffer.flip();
                    inputStream.close();
                    System.exit(0);
                }
            }
        }
        catch (Exception e) {
            LogUtil.logError(LOG, e);
        }
    }
}
