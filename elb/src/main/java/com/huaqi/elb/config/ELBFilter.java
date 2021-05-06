package com.huaqi.elb.config;

import com.alibaba.fastjson.JSON;
import com.huaqi.common.msg.AppEngine;
import com.huaqi.common.util.MessageUtil;
import com.huaqi.elb.ElbApplication;
import com.huaqi.elb.lb.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Objects;

@Slf4j
@Component
public class ELBFilter implements Filter {
    private static RestTemplate restTemplate = new RestTemplate();

    @Resource(name = "randomLB")
    private LoadBalance randomLB;

    @Resource(name = "roundRobin")
    private LoadBalance RoundRobin;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("ELBFilter: doFilter");
        if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
            return;
        }
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse rsp = (HttpServletResponse) servletResponse;

        Integer mode = ElbApplication.getMode();
        LoadBalance loadBalance = randomLB;
        if (mode == 1){
            loadBalance = randomLB;
        } else if (mode == 2){
            loadBalance = RoundRobin;
        }
        AppEngine appEngine = loadBalance.getAppEngineServer();
        log.info("ELBFilter: doFilter  load Balance rule is : "+ mode+ " , App Engine Server  info is:  "+ MessageUtil.getGson().toJson(appEngine));
        String host = null;
        String port = null;
        if (Objects.nonNull(appEngine)){
            host = appEngine.getIp();
            port = appEngine.getPort();
        } else {
             host = "127.0.0.1";
             port = "8083";
        }

        switch (req.getMethod()) {
            case "GET": {
                sendRequest(req, rsp, HttpMethod.GET, appEngine);
                break;
            }
            case "POST": {
                sendRequest(req, rsp, HttpMethod.POST, appEngine);
                break;
            }
            case "PUT": {
                sendRequest(req, rsp, HttpMethod.PUT, appEngine);
                break;
            }
            case "PATCH": {
                sendRequest(req, rsp, HttpMethod.PATCH, appEngine);
                break;
            }
            case "DELETE": {
                sendRequest(req, rsp, HttpMethod.DELETE, appEngine);
                break;
            }
        }
    }

    private void sendRequest(HttpServletRequest req, HttpServletResponse rsp, HttpMethod method, AppEngine appEngine) throws IOException {
        rsp.setCharacterEncoding("UTF-8");
        String requestBody = IOUtils.toString(req.getInputStream(), "UTF-8");
        Object body = null;
        if (StringUtils.hasText(requestBody)) {
            body = JSON.parse(requestBody);
        }
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.add(name, req.getHeader(name));
        }
        String url;
        String serverAddr = new StringBuilder(appEngine.getIp()).append(":").append(appEngine.getPort()).toString();
        if (StringUtils.hasText(req.getQueryString())) {
            url = String.format(
                    "http://%s%s?%s",
                    serverAddr,
                    req.getRequestURI().substring(req.getContextPath().length()),
                    req.getQueryString()
            );
        } else {
            url = String.format(
                    "http://%s%s",
                    serverAddr,
                    req.getRequestURI().substring(req.getContextPath().length())
            );
        }
        HttpEntity<Object> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> exchange = null;
        try {
            exchange = restTemplate.exchange(
                    url,
                    method,
                    httpEntity,
                    String.class
            );
        } catch (Exception e) {
            log.error("hit exception " + e);
        }
        if (exchange != null) {
            rsp.setStatus(exchange.getStatusCodeValue());
            exchange.getHeaders().entrySet().stream().forEach(entry -> {
                String value = entry.getValue().toString();
                rsp.addHeader(entry.getKey(), value.substring(1, value.length() - 1));
            });
            try (PrintWriter out = rsp.getWriter()) {
                String s = exchange.getBody();
                String result = s.replace("server1" ,appEngine.getInstanceID());
                out.write(result);
                out.flush();
                out.close();
            } catch (Exception e) {
            }
        } else {
            try (PrintWriter out = rsp.getWriter()) {
                out.write("not found!!");
            } catch (Exception e1) {
            }
        }

    }

    @Override
    public void destroy() {

    }
}
