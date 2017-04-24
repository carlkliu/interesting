/*
 * file name: MyBinder.java copyright: Unis Cloud Information Technology Co.,
 * Ltd. Copyright 2016, All rights reserved description: <description> mofidy
 * staff: Jiaqi Yang mofidy time: 2016年9月1日
 */
package com.carl.interesting;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.carl.interesting.common.ResponseDataFormat;
import com.carl.interesting.common.constants.Constant;
import com.carl.interesting.common.constants.LanguageKeys;
import com.carl.interesting.common.constants.ResponseFrontEndErrorCode;
import com.carl.interesting.common.i18n.I18nManager;
import com.carl.interesting.common.i18n.entity.InterestingI18n;
import com.carl.interesting.common.session.CustomSessionManagement;
import com.carl.interesting.common.util.LogUtil;

/**
 * HTTP response filter
 * 
 * @author Jiaqi Yang
 * @version [version, 1 Jan. 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
@Provider
public class ResponseFilter implements ContainerResponseFilter {
    private Log LOG = LogFactory.getLog(ResponseFilter.class);
    
    /**
     * @param requestContext
     * @param responseContext
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) throws IOException {
        UriInfo uriInfo = requestContext.getUriInfo();
        // login default true
        if (!uriInfo.getPath().contains("login")) {
            String valid = requestContext.getHeaders()
                    .getFirst(Constant.HTTP_SESSION_VALID);
            if (null != valid && !"0".equals(valid)) {
                try {
                    String clientIP = CustomSessionManagement.getInstance()
                            .getClientIP(requestContext)
                            .get(0);
                    InterestingI18n i18n = I18nManager.INSTANCE
                            .getEntity(clientIP);
                    // set utf-8 encoding
                    if (null != responseContext.getMediaType()) {
                        MediaType contentType = responseContext.getMediaType();
                        responseContext.getHeaders().putSingle("Content-Type",
                                contentType.toString() + ";charset=UTF-8");
                    }
                    else {
                        responseContext.getHeaders().putSingle("Content-Type",
                                "text/html;charset=UTF-8");
                    }
                    if (String.valueOf(ResponseFrontEndErrorCode.USER_LOGINED)
                            .equals(valid)) {
                        responseContext.setEntity(ResponseDataFormat
                                .error(ResponseFrontEndErrorCode.USER_LOGINED,
                                        i18n.getString(
                                                LanguageKeys.COMMON_COMMON_FUNC_USER_LOGGED))
                                .toString());
                    }
                    else {
                        responseContext.setEntity(ResponseDataFormat
                                .error(ResponseFrontEndErrorCode.SESSION_INVALID,
                                        i18n.getSessionInvalidKey())
                                .toString());
                    }
                }
                catch (Exception e) {
                    LogUtil.logError(LOG,
                            "Failed to get language environment.responseContext:"
                                    + responseContext.toString()
                                    + ",requestContext:" + requestContext);
                }
            }
        }
        // NOTE: we need unbind task from current thread when http thread exit,
        // because thread pool reuse thread in order to avoid assign the old
        // previous task,we need clean the current thread data cache.
        // TaskService.INSTANCE.unbindCurrent();
    }
}
