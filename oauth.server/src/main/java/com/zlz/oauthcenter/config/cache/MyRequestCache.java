package com.zlz.oauthcenter.config.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 缓存的请求
 * @author zhulinzhong
 * @version 1.0 CreateTime:2020-06-11 14:21
 * @description
 */
public class MyRequestCache implements RequestCache {
    static final String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";
    protected final Log logger = LogFactory.getLog(this.getClass());

    private PortResolver portResolver = new PortResolverImpl();
    private boolean createSessionAllowed = true;
    private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;
    private String sessionAttrName = SAVED_REQUEST;

    /**
     * Stores the current request, provided the configuration properties allow it.
     */
    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        if (requestMatcher.matches(request)) {
            String requestURI = request.getRequestURI();
            StringBuffer requestURL = request.getRequestURL();
            MyDefaultSavedRequest savedRequest = new MyDefaultSavedRequest(request,
                    portResolver);

            if (createSessionAllowed || request.getSession(false) != null) {
                // Store the HTTP request itself. Used by
                // AbstractAuthenticationProcessingFilter
                // for redirection after successful authentication (SEC-29)
                request.getSession().setAttribute(this.sessionAttrName, savedRequest);
                logger.debug("DefaultSavedRequest added to Session: " + savedRequest);
            }
        }
        else {
            logger.debug("Request not saved as configured RequestMatcher did not match");
        }
    }

    @Override
    public SavedRequest getRequest(HttpServletRequest currentRequest,
                                   HttpServletResponse response) {
        HttpSession session = currentRequest.getSession(false);

        if (session != null) {
            return (SavedRequest) session.getAttribute(this.sessionAttrName);
        }

        return null;
    }
    @Override
    public void removeRequest(HttpServletRequest currentRequest,
                              HttpServletResponse response) {
        HttpSession session = currentRequest.getSession(false);

        if (session != null) {
            logger.debug("Removing DefaultSavedRequest from session if present");
            session.removeAttribute(this.sessionAttrName);
        }
    }
    @Override
    public HttpServletRequest getMatchingRequest(HttpServletRequest request,
                                                 HttpServletResponse response) {
        SavedRequest saved = getRequest(request, response);

        if (!matchesSavedRequest(request, saved)) {
            logger.debug("saved request doesn't match");
            return null;
        }

        removeRequest(request, response);

        return new SavedRequestAwareWrapper(saved, request);
    }

    private boolean matchesSavedRequest(HttpServletRequest request, SavedRequest savedRequest) {
        if (savedRequest == null) {
            return false;
        }

        if (savedRequest instanceof DefaultSavedRequest) {
            DefaultSavedRequest defaultSavedRequest = (DefaultSavedRequest) savedRequest;
            return defaultSavedRequest.doesRequestMatch(request, this.portResolver);
        }

        String currentUrl = UrlUtils.buildFullRequestUrl(request);
        return savedRequest.getRedirectUrl().equals(currentUrl);
    }

    /**
     * Allows selective use of saved requests for a subset of requests. By default any
     * request will be cached by the {@code saveRequest} method.
     * <p>
     * If set, only matching requests will be cached.
     *
     * @param requestMatcher a request matching strategy which defines which requests
     * should be cached.
     */
    public void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    /**
     * If <code>true</code>, indicates that it is permitted to store the target URL and
     * exception information in a new <code>HttpSession</code> (the default). In
     * situations where you do not wish to unnecessarily create <code>HttpSession</code>s
     * - because the user agent will know the failed URL, such as with BASIC or Digest
     * authentication - you may wish to set this property to <code>false</code>.
     */
    public void setCreateSessionAllowed(boolean createSessionAllowed) {
        this.createSessionAllowed = createSessionAllowed;
    }

    public void setPortResolver(PortResolver portResolver) {
        this.portResolver = portResolver;
    }

    /**
     * If the {@code sessionAttrName} property is set, the request is stored in
     * the session using this attribute name. Default is
     * "SPRING_SECURITY_SAVED_REQUEST".
     *
     * @param sessionAttrName a new session attribute name.
     * @since 4.2.1
     */
    public void setSessionAttrName(String sessionAttrName) {
        this.sessionAttrName = sessionAttrName;
    }
}
