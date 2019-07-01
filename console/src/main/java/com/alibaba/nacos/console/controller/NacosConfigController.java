package com.alibaba.nacos.console.controller;

import com.alibaba.nacos.config.server.model.RestResult;
import com.alibaba.nacos.core.utils.SystemUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
@RestController("consoleNacosConfig")
@RequestMapping("/v1/console/config")
public class NacosConfigController {

    @ResponseBody
    @RequestMapping(value = "cluster", method = RequestMethod.GET)
    public RestResult<List<String>> getClusterConfig(HttpServletRequest request, HttpServletResponse response) {
        RestResult<List<String>> restResult = new RestResult<List<String>>();
        List<String> clusterIps = new ArrayList<>();
        try {
            clusterIps = SystemUtils.readClusterConf();
        } catch (IOException e) {
            restResult.setCode(500);
            restResult.setMessage("IO operation exception : " + e.getMessage());
        }
        restResult.setCode(200);
        restResult.setData(clusterIps);
        return restResult;
    }

    @ResponseBody
    @RequestMapping(value = "cluster", method = RequestMethod.PUT)
    public RestResult<List<String>> updateClusterConfig(HttpServletRequest request, HttpServletResponse response,
                                                        @RequestParam("clusterHosts") List<String> clusterHosts) {
        RestResult<List<String>> restResult = new RestResult<List<String>>();
        try {
            String content = generateText(clusterHosts);
            if(content.isEmpty()){
                restResult.setCode(500);
                restResult.setData(null);
                restResult.setMessage("At least one ip!");
                return restResult;
            }
            SystemUtils.writeClusterConf(content);
            restResult.setCode(200);
            restResult.setData(SystemUtils.readClusterConf());
            restResult.setMessage("update cluster.config success.");
        }catch (IOException e){
            restResult.setCode(500);
            restResult.setData(null);
            restResult.setMessage("IO operation exception : " + e.getMessage());
        }
        return restResult;
    }

    private String generateText(List<String> clusterHosts) {
        String lineSeparator = System.getProperty("line.separator");
        StringBuilder content = new StringBuilder();
        for(String host : clusterHosts){
            content.append(host)
                .append(lineSeparator);
        }
        return content.toString();
    }

}
