package com.zlz.oauthcenter.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.zlz.oauthcenter.util.ConfigurationUtil;
import com.zlz.oauthcenter.util.SpringContextUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.SavedCookie;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.spring5.context.SpringContextUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 重写请求缓存（可优化）
 * 当一个请求被拦截并重定向到登陆界面时，该请求会被缓存，登陆成功时重新跳转回该请求
 * gateway代理登陆时，无法获取到真实的地址，所以利用修改缓存的方法实现登陆成功后跳转回请求gateway的地址
 * 问题描述：A请求gateway代理的oauth时，gateway获取oauth真实地址并请求，此时oauth发现该请求未被认证，缓存该请求，
 * 并且重定向到登陆界面，此时缓存的地址是gateway请求的地址，并不是A请求gateway代理的地址。
 * @author zhulinzhong
 * @version 1.0 CreateTime:2020-06-11 14:39
 * @description
 */
public class MyDefaultSavedRequest implements SavedRequest {
// ~ Static fields/initializers
    // =====================================================================================
    protected static final Log logger = LogFactory.getLog(DefaultSavedRequest.class);

    private static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    private static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";

    // ~ Instance fields
    // ================================================================================================

    private final ArrayList<SavedCookie> cookies = new ArrayList<>();
    private final ArrayList<Locale> locales = new ArrayList<>();
    private final Map<String, List<String>> headers = new TreeMap<>(
            String.CASE_INSENSITIVE_ORDER);
    private final Map<String, String[]> parameters = new TreeMap<>();
    private final String contextPath;
    private final String method;
    private final String pathInfo;
    private final String queryString;
    private final String requestURI;
    private final String requestURL;
    private final String scheme;
    private final String serverName;
    private final String servletPath;
    private final int serverPort;

    // ~ Constructors
    // ===================================================================================================

    @SuppressWarnings("unchecked")
    public MyDefaultSavedRequest(HttpServletRequest request, PortResolver portResolver) {
        Assert.notNull(request, "Request required");
        Assert.notNull(portResolver, "PortResolver required");

        //获取configurationUtil
        ConfigurationUtil configurationUtil = (ConfigurationUtil) SpringContextUtil.getBean("configurationUtil");
        
        // Cookies
        addCookies(request.getCookies());
        // Headers
        Enumeration<String> names = request.getHeaderNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            // Skip If-Modified-Since and If-None-Match header. SEC-1412, SEC-1624.
            if (HEADER_IF_MODIFIED_SINCE.equalsIgnoreCase(name)
                    || HEADER_IF_NONE_MATCH.equalsIgnoreCase(name)) {
                continue;
            }
            Enumeration<String> values = request.getHeaders(name);

            while (values.hasMoreElements()) {
                this.addHeader(name, values.nextElement());
            }
        }

        // Locales
        addLocales(request.getLocales());

        // Parameters
        addParameters(request.getParameterMap());

        // Primitives
        this.method = request.getMethod();
        this.pathInfo = request.getPathInfo();
        this.queryString = request.getQueryString();
        this.requestURI = request.getRequestURI();

        //缓存时将对我的请求,设置为对gateway的请求
        if(portResolver.getServerPort(request) == Integer.valueOf(configurationUtil.getMyPort())){
            this.serverPort = configurationUtil.getGatewayPort();
        }else {
            this.serverPort = portResolver.getServerPort(request);
        }
        //仅修改客户端请求权限时的路由地址
        String def1 = "http://" + configurationUtil.getMyHost() + ":" + configurationUtil.getMyPort() + "/oauth-server/oauth/authorize";
        if(def1.equals(request.getRequestURL().toString())){
            this.scheme = "https";
            this.requestURL = configurationUtil.getGatewayUrl()+"oauth-server/oauth/authorize";
        }else {
            this.requestURL = request.getRequestURL().toString();
            this.scheme = request.getScheme();
        }
        this.serverName = request.getServerName();
        this.contextPath = request.getContextPath();
        this.servletPath = request.getServletPath();
    }

    /**
     * Private constructor invoked through Builder
     */
    private MyDefaultSavedRequest(MyDefaultSavedRequest.Builder builder) {
        this.contextPath = builder.contextPath;
        this.method = builder.method;
        this.pathInfo = builder.pathInfo;
        this.queryString = builder.queryString;
        this.requestURI = builder.requestURI;
        this.requestURL = builder.requestURL;
        this.scheme = builder.scheme;
        this.serverName = builder.serverName;
        this.servletPath = builder.servletPath;
        this.serverPort = builder.serverPort;
    }

    // ~ Methods
    // ========================================================================================================

    /**
     * @since 4.2
     */
    private void addCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                this.addCookie(cookie);
            }
        }
    }

    private void addCookie(Cookie cookie) {
        cookies.add(new SavedCookie(cookie));
    }

    private void addHeader(String name, String value) {
        List<String> values = headers.get(name);

        if (values == null) {
            values = new ArrayList<>();
            headers.put(name, values);
        }

        values.add(value);
    }

    /**
     * @since 4.2
     */
    private void addLocales(Enumeration<Locale> locales) {
        while (locales.hasMoreElements()) {
            Locale locale = locales.nextElement();
            this.addLocale(locale);
        }
    }

    private void addLocale(Locale locale) {
        locales.add(locale);
    }

    /**
     * @since 4.2
     */
    private void addParameters(Map<String, String[]> parameters) {
        if (!ObjectUtils.isEmpty(parameters)) {
            for (String paramName : parameters.keySet()) {
                Object paramValues = parameters.get(paramName);
                if (paramValues instanceof String[]) {
                    this.addParameter(paramName, (String[]) paramValues);
                } else {
                    if (logger.isWarnEnabled()) {
                        logger.warn("ServletRequest.getParameterMap() returned non-String array");
                    }
                }
            }
        }
    }

    private void addParameter(String name, String[] values) {
        parameters.put(name, values);
    }

    /**
     * Determines if the current request matches the <code>DefaultSavedRequest</code>.
     * <p>
     * All URL arguments are considered but not cookies, locales, headers or parameters.
     *
     * @param request      the actual request to be matched against this one
     * @param portResolver used to obtain the server port of the request
     * @return true if the request is deemed to match this one.
     */
    public boolean doesRequestMatch(HttpServletRequest request, PortResolver portResolver) {

        if (!propertyEquals("pathInfo", this.pathInfo, request.getPathInfo())) {
            return false;
        }

        if (!propertyEquals("queryString", this.queryString, request.getQueryString())) {
            return false;
        }

        if (!propertyEquals("requestURI", this.requestURI, request.getRequestURI())) {
            return false;
        }

        if (!"GET".equals(request.getMethod()) && "GET".equals(method)) {
            // A save GET should not match an incoming non-GET method
            return false;
        }

        if (!propertyEquals("serverPort", Integer.valueOf(this.serverPort),
                Integer.valueOf(portResolver.getServerPort(request)))) {
            return false;
        }

        if (!propertyEquals("requestURL", this.requestURL, request.getRequestURL()
                .toString())) {
            return false;
        }

        if (!propertyEquals("scheme", this.scheme, request.getScheme())) {
            return false;
        }

        if (!propertyEquals("serverName", this.serverName, request.getServerName())) {
            return false;
        }

        if (!propertyEquals("contextPath", this.contextPath, request.getContextPath())) {
            return false;
        }

        return propertyEquals("servletPath", this.servletPath, request.getServletPath());

    }

    public String getContextPath() {
        return contextPath;
    }

    @Override
    public List<Cookie> getCookies() {
        List<Cookie> cookieList = new ArrayList<Cookie>(cookies.size());

        for (SavedCookie savedCookie : cookies) {
            cookieList.add(savedCookie.getCookie());
        }

        return cookieList;
    }

    /**
     * Indicates the URL that the user agent used for this request.
     *
     * @return the full URL of this request
     */
    @Override
    public String getRedirectUrl() {
        return UrlUtils.buildFullRequestUrl(scheme, serverName, serverPort, requestURI,
                queryString);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public List<String> getHeaderValues(String name) {
        List<String> values = headers.get(name);

        if (values == null) {
            return Collections.emptyList();
        }

        return values;
    }

    @Override
    public List<Locale> getLocales() {
        return locales;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    public Collection<String> getParameterNames() {
        return parameters.keySet();
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getQueryString() {
        return (this.queryString);
    }

    public String getRequestURI() {
        return (this.requestURI);
    }

    public String getRequestURL() {
        return requestURL;
    }

    public String getScheme() {
        return scheme;
    }

    public String getServerName() {
        return serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getServletPath() {
        return servletPath;
    }

    private boolean propertyEquals(String log, Object arg1, Object arg2) {
        if ((arg1 == null) && (arg2 == null)) {
            if (logger.isDebugEnabled()) {
                logger.debug(log + ": both null (property equals)");
            }

            return true;
        }

        if (arg1 == null || arg2 == null) {
            if (logger.isDebugEnabled()) {
                logger.debug(log + ": arg1=" + arg1 + "; arg2=" + arg2
                        + " (property not equals)");
            }

            return false;
        }

        if (arg1.equals(arg2)) {
            if (logger.isDebugEnabled()) {
                logger.debug(log + ": arg1=" + arg1 + "; arg2=" + arg2
                        + " (property equals)");
            }

            return true;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(log + ": arg1=" + arg1 + "; arg2=" + arg2
                        + " (property not equals)");
            }

            return false;
        }
    }

    @Override
    public String toString() {
        return "DefaultSavedRequest[" + getRedirectUrl() + "]";
    }

    /**
     * @since 4.2
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {

        private List<SavedCookie> cookies = null;
        private List<Locale> locales = null;
        private Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        private Map<String, String[]> parameters = new TreeMap<>();
        private String contextPath;
        private String method;
        private String pathInfo;
        private String queryString;
        private String requestURI;
        private String requestURL;
        private String scheme;
        private String serverName;
        private String servletPath;
        private int serverPort = 80;

        public MyDefaultSavedRequest.Builder setCookies(List<SavedCookie> cookies) {
            this.cookies = cookies;
            return this;
        }

        public MyDefaultSavedRequest.Builder setLocales(List<Locale> locales) {
            this.locales = locales;
            return this;
        }

        public MyDefaultSavedRequest.Builder setHeaders(Map<String, List<String>> header) {
            this.headers.putAll(header);
            return this;
        }

        public MyDefaultSavedRequest.Builder setParameters(Map<String, String[]> parameters) {
            this.parameters = parameters;
            return this;
        }

        public MyDefaultSavedRequest.Builder setContextPath(String contextPath) {
            this.contextPath = contextPath;
            return this;
        }

        public MyDefaultSavedRequest.Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        public MyDefaultSavedRequest.Builder setPathInfo(String pathInfo) {
            this.pathInfo = pathInfo;
            return this;
        }

        public MyDefaultSavedRequest.Builder setQueryString(String queryString) {
            this.queryString = queryString;
            return this;
        }

        public MyDefaultSavedRequest.Builder setRequestURI(String requestURI) {
            this.requestURI = requestURI;
            return this;
        }

        public MyDefaultSavedRequest.Builder setRequestURL(String requestURL) {
            this.requestURL = requestURL;
            return this;
        }

        public MyDefaultSavedRequest.Builder setScheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        public MyDefaultSavedRequest.Builder setServerName(String serverName) {
            this.serverName = serverName;
            return this;
        }

        public MyDefaultSavedRequest.Builder setServletPath(String servletPath) {
            this.servletPath = servletPath;
            return this;
        }

        public MyDefaultSavedRequest.Builder setServerPort(int serverPort) {
            this.serverPort = serverPort;
            return this;
        }

        public MyDefaultSavedRequest build() {
            MyDefaultSavedRequest savedRequest = new MyDefaultSavedRequest(this);
            if (!ObjectUtils.isEmpty(this.cookies)) {
                for (SavedCookie cookie : this.cookies) {
                    savedRequest.addCookie(cookie.getCookie());
                }
            }
            if (!ObjectUtils.isEmpty(this.locales))
                savedRequest.locales.addAll(this.locales);
            savedRequest.addParameters(this.parameters);

            this.headers.remove(HEADER_IF_MODIFIED_SINCE);
            this.headers.remove(HEADER_IF_NONE_MATCH);
            for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                String headerName = entry.getKey();
                List<String> headerValues = entry.getValue();
                for (String headerValue : headerValues) {
                    savedRequest.addHeader(headerName, headerValue);
                }
            }
            return savedRequest;
        }
    }

}
