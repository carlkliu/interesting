package com.carl.interesting;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.carl.interesting.common.constants.KeyConstant;
import com.carl.interesting.common.util.LogUtil;

/**
 * Entry for stopping ING
 * 
 * @author xupingzheng
 * @version [version, 24 Mar 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class StopINGServer {
    private static final Log LOG = LogFactory.getLog(StopINGServer.class);
    
    /**
     * Main method.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) {
        LOG.info("Stop ING server");
        FileChannel fileChannel = null;
        try {
            File file = new File(KeyConstant.MICROSERVICE_PROCESS_RUN_LOG);
            boolean isExist = file.exists();
            Path path = Paths.get(KeyConstant.MICROSERVICE_PROCESS_RUN_LOG);
            fileChannel = FileChannel.open(path,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.READ);
            // if the size of mapped file <4 bytes, we alloc 4 bytes.
            if (!isExist || file.length() < 4) {
                ByteBuffer buffer = ByteBuffer.allocate(4);
                fileChannel.write(buffer);
            }
            MappedByteBuffer byteBuffer = fileChannel.map(MapMode.READ_WRITE,
                    0,
                    4);
            byteBuffer.putInt(MessageTask.MESSAGE_STOP);
            byteBuffer.flip();
        }
        catch (Exception e) {
            LogUtil.logError(LOG, e);
        }
        finally {
            if (null != fileChannel) {
                try {
                    fileChannel.close();
                }
                catch (IOException e) {
                    LogUtil.logError(LOG, e);
                }
            }
        }
    }
}
