/*
 * file name: MyBinder.java copyright: Unis Cloud Information Technology Co.,
 * Ltd. Copyright 2016, All rights reserved description: <description> mofidy
 * staff: Jiaqi Yang mofidy time: 2016年9月1日
 */
package com.carl.interesting;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.carl.interesting.common.ResponseDataFormat;
import com.carl.interesting.common.constants.Constant;
import com.carl.interesting.common.i18n.I18nManager;
import com.carl.interesting.common.i18n.entity.InterestingI18n;
import com.carl.interesting.common.session.CustomSessionManagement;

/**
 * Filter http request and valid session
 * 
 * @author Jiaqi Yang
 * @version [version, 1 Sep 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
@Provider
public class RequestFilter implements ContainerRequestFilter {
    private Log LOG = LogFactory.getLog(RequestFilter.class);
    
    private static final String URL_FILTER_LOGIN = "login";
    
    private static final String URL_FILTER_HOSTAGENT = "hostAgentRest";
    
    /**
     * @param arg0
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        try {
            List<String> ips = CustomSessionManagement.getInstance()
                    .getClientIP(request);
            if (null != ips && ips.size() > 0) {
                String ip = ips.get(0);
                InterestingI18n i18n = I18nManager.INSTANCE.getEntity(ip);
                ResponseDataFormat.map.put(Thread.currentThread().getId(),
                        i18n);
                UriInfo uriInfo = request.getUriInfo();
                String path = uriInfo.getPath();
                // login default true
                if (!path.contains(URL_FILTER_LOGIN)
                        && !path.contains(URL_FILTER_HOSTAGENT)) {
                    String scheduler = uriInfo.getQueryParameters()
                            .getFirst(Constant.HTTP_SCHEDULER);
                    int valid = CustomSessionManagement.getInstance()
                            .filterReq(request, scheduler);
                    if (0 != valid) {
                        request.getHeaders().putSingle(
                                Constant.HTTP_SESSION_VALID,
                                String.valueOf(valid));
                        request.abortWith(
                                Response.status(Response.Status.FORBIDDEN)
                                        .entity(null)
                                        .build());
                    }
                }
            }
        }
        catch (Exception e) {
            LOG.error(request.getHeaders().toString() + " error message:"
                    + e.getMessage());
        }
    }
}
