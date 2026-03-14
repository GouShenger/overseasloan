package com.yemi.core.api.app;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Api(tags = "APP-系统服务")
@RequestMapping("/app/v1/system")
public interface UserSystemApi {
    @ApiOperation(value = "正则表达式列表")
    @GetMapping("/regex")
    Map<String, String> regex();

}
