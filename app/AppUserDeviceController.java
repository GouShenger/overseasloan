
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @version 1.0
 * @description: TODO
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
        // Note: userId may be null, the user is not logged in and the app is not operated without userId
        Long userId = ThreadContext.getUser().getUserId();
        userDeviceService.addOperationLog(Objects.isNull(userId) ? UserInfo.UN_LOGGED_IN_USER_ID : userId, userDeviceOpLogDto);
        return CommonResult.SUCCESS;
    }
}
