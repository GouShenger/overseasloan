package com.yemi.core.api.app;

import com.yemi.core.config.NacosConfig;
import com.yemi.core.service.ISystemService;
import com.yemi.web.context.ThreadContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @description:
 * @author: hanxiaojun
 * @date 2023/11/20 16:53
 */
@RestController
public class UserSystemController implements UserSystemApi {
    @Autowired
    private NacosConfig nacosConfig;
    @Autowired
    private ISystemService systemService;

    @Override
    public Map<String, String> regex() {
        String userCountry = ThreadContext.getUser().getUserCountry();
        Map<String, Map<String, String>> regex = nacosConfig.getSystem().getRegex();
        Map<String, String> result = new HashMap<>();
        regex.forEach((key, value1) -> {
            String configRegex = value1.get(userCountry);
            String value = StringUtils.isBlank(configRegex) ? Strings.EMPTY : configRegex;
            result.put(key, value);
        });
        return result;
    }

}
