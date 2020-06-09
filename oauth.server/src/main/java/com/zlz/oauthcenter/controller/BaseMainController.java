package com.zlz.oauthcenter.controller;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhulinzhong
 * @version 1.0 CreateTime:2019/10/24 18:04
 */
@Controller
@SessionAttributes({ "authorizationRequest" })
public class BaseMainController {

    @GetMapping("/auth/login")
    public String loginPage(Model model, HttpServletRequest request){
        if(request.getQueryString() == null){
            model.addAttribute("error", "null");
        }else{
            model.addAttribute("error", "error");
        }
        return "index";
    }

    @RequestMapping({ "/oauth/approvale/confirm" })
    public String getAccessConfirmation(Map<String, Object> map,
                                        HttpServletRequest request, Model model) throws Exception {
        String scope = (String) (map.containsKey("scope") ?
                map.get("scope") : request.getAttribute("scope"));
        String client = (String) (map.containsKey("client_id") ?
                map.get("client_id") : request.getAttribute("client_id"));
        List<String> scopeList = new ArrayList<String>();
        scopeList.add(scope);
        map.put("scopeList", scopeList);
        model.addAttribute("scope",scope).addAttribute("client", client);
        System.out.println("授权进行中，client："+client);
        return "authorization";
    }
    @RequestMapping({ "/oauth/approvale/error" })
    public String handleError(Map<String, Object> model, HttpServletRequest request) {
        Object error = request.getAttribute("error");
        String errorSummary;
        if (error instanceof OAuth2Exception) {
            OAuth2Exception oauthError = (OAuth2Exception) error;
            errorSummary = HtmlUtils.htmlEscape(oauthError.getSummary());
        } else {
            errorSummary = "Unknown error";
        }
        model.put("errorSummary", errorSummary);
        return "oauth_error";
    }

}
