package com.yemi.core.api.app;

import com.yemi.core.consts.UserInfo;
import com.yemi.core.model.dto.UserDeviceDto;
import com.yemi.core.model.dto.UserDeviceOpLogDto;
import com.yemi.core.service.IUserDeviceService;
import com.yemi.utils.consts.CommonResult;
import com.yemi.web.context.ThreadContext;
import com.yemi.web.result.LocalizationTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @version 1.0
 * @description: TODO
 * @author: hanxiaojun
 * @date 2023/10/10 10:45
 */
@RestController
@LocalizationTag
public class AppUserDeviceController implements AppUserDeviceApi {
    @Autowired
    private IUserDeviceService userDeviceService;

    @Override
    public String addDevice(UserDeviceDto userDeviceDto) {
        String userIp = ThreadContext.getUser().getUserIp();
        userDeviceDto.setUserIp(userIp);
        userDeviceService.addDevice(userDeviceDto);
        return CommonResult.SUCCESS;
    }

    @Override
    public String addDeviceLog(UserDeviceOpLogDto userDeviceOpLogDto) {
        // 注意：此处userId可能为空，用户未登录的情况下操作app，是没有userId的
        Long userId = ThreadContext.getUser().getUserId();
        userDeviceService.addOperationLog(Objects.isNull(userId) ? UserInfo.UN_LOGGED_IN_USER_ID : userId, userDeviceOpLogDto);
        return CommonResult.SUCCESS;
    }
}
