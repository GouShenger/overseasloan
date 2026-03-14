
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @version 1.0
 */
@Slf4j
@RestController
@LocalizationTag
public class AppUserInfoController implements AppUserInfoApi {
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private UserGeoParser userGeoParser;
    @Autowired
    private IOcrService ocrService;
    @Autowired
    private RateLimiter rateLimiter;
    @Autowired
    private IUserGeoService userGeoService;
    @Autowired
    private LivenessCheckHelper livenessCheckHelper;
    @Autowired
    private FacebookAdsReporter facebookAdsReporter;

    @Override
    public List<UserInfoTypeVo> getAllUserInfoType() {
        return userInfoService.getAllTypes(ThreadContext.getUser().getRequestLocale());
    }

    @Override
    public List<UserInfoFieldVo> getUserInfoFields(Long infoTypeId) {
        return userInfoService.getFieldsByInfoType(infoTypeId, ThreadContext.getUser().getRequestLocale());
    }

    @UserActionEndpoint(UserAction.USER_INFO_SUBMIT)
    @Override
    public String submitUserInfo(UserInfoDataSubmissionDto submissionDto) {
        Long userId = ThreadContext.getUser().getUserId();

        // update user info data
        userInfoService.submitUserInfoData(userId, submissionDto);
        // report data after submission
        try {
            UserAuthType authType = UserAuthType.getByOrder(Integer.parseInt(submissionDto.getInfoTypeId().toString()));
            if (authType != null) {
                String eventName = switch (authType) {
                    case ID_CARD -> "User-Info-Page";
                    case LIVENESS -> "User-Company-Page";
                    case DYNAMIC_INFO -> "User-Concat-Page";
                };

                facebookAdsReporter.reportWithRequestContext(eventName, userId, null);
            }
        } catch (Exception e) {
            log.error("Report Facebook error", e);
        }
        // record user location information
        Optional<PositionDetail> positionDetail = PositionDetail.of(submissionDto.getPosition());
        positionDetail.ifPresent(pd -> userGeoParser.asyncParseAndSave(userId, UserAction.USER_INFO_SUBMIT, String.valueOf(userId), pd.getLatitude(), pd.getLongitude(), submissionDto.getPosition()));
        // check user contacts
        if (Objects.equals(submissionDto.getInfoTypeId(), (long) UserAuthType.DYNAMIC_INFO.getOrder())) {
            userInfoService.checkUserContacts(userId);
        }
        return CommonResult.SUCCESS;
    }

    @Override
    public UserInfoFulfillmentStatusVo getUserFulfillmentStatus() {
        return userInfoService.getUserFulfillmentStatus(ThreadContext.getUser().getUserId());
    }

    @Override
    public List<UserInfoDataVo> getUserSubmitData() {
        Long userId = ThreadContext.getUser().getUserId();
        return userInfoService.getUserInfoSubmitData(userId, ThreadContext.getUser().getRequestLocale());
    }

    @Override
    public Map<String, List<Map<String, List<String>>>> getProvinceData() {
        return userInfoService.getProvinceData(ThreadContext.getUser().getRequestLocale());
    }

    @FacebookAdsReport(eventName = "OCR-Identity", errorEventName = "OCR-Identity-Error", successEventName = "OCR-Identity-Success")
    @Override
    public OcrData ocrIdentify(FileInfoDto dto, MultipartFile file) {
        Long userId = ThreadContext.getUser().getUserId();
        RateLimitRule rateLimitRule = RateLimitRule.builder().key("OCR_IDENTIFY_" + userId)
                .seconds(60).limit(5).build();
        rateLimiter.rateLimit(rateLimitRule);
        return ocrService.identify(userId, file, dto);
    }

    @UserActionEndpoint(UserAction.OCR_CHECK)
    @Override
    public String saveOcrInfo(OcrData ocrData) {
        Long userId = ThreadContext.getUser().getUserId();
        RateLimitRule rateLimitRule = RateLimitRule.builder().key("OCR_CONFIRM_" + userId)
                .seconds(60).limit(5).build();
        rateLimiter.rateLimit(rateLimitRule);
        ocrService.saveOcrInfo(userId, ocrData);
        // record user location information
        Optional<PositionDetail> positionDetail = PositionDetail.of(ocrData.getPosition());
        positionDetail.ifPresent(pd -> userGeoParser.asyncParseAndSave(userId, UserAction.OCR_CHECK, String.valueOf(userId), pd.getLatitude(), pd.getLongitude(), ocrData.getPosition()));
        return CommonResult.SUCCESS;
    }

    @UserActionEndpoint(UserAction.SAVE_CONTACT)
    @Override
    public String saveUserContacts(UserContactsDto contactsDto) {
        Long userId = ThreadContext.getUser().getUserId();
        userInfoService.saveUserContacts(userId, contactsDto);
        return CommonResult.SUCCESS;
    }

    @UserActionEndpoint(UserAction.LIVENESS_CHECK)
    @Override
    public String saveFaceIdentifyResult(UserLivenessDto userLivenessDto) {
        Long userId = ThreadContext.getUser().getUserId();
        RateLimitRule rateLimitRule = RateLimitRule.builder().key("LIVENESS_CHECK" + userId)
                .seconds(60).limit(5).build();
        rateLimiter.rateLimit(rateLimitRule);
        userInfoService.livenessCheck(userId, userLivenessDto);
        // record user location information
        Optional<PositionDetail> positionDetail = PositionDetail.of(userLivenessDto.getPosition());
        positionDetail.ifPresent(pd -> userGeoParser.asyncParseAndSave(userId, UserAction.LIVENESS_CHECK, String.valueOf(userId), pd.getLatitude(), pd.getLongitude(), userLivenessDto.getPosition()));
        return CommonResult.SUCCESS;
    }

    @Override
    public AppOcrVo getOcrInfo() {
        Long userId = ThreadContext.getUser().getUserId();
        OcrData userOcrInfo = ocrService.getUserOcrInfo(userId);
        return AppOcrVo.of(userOcrInfo);
    }

    @Override
    public String getLivenessToken(UserLivenessDto userLivenessDto) {
        Long userId = ThreadContext.getUser().getUserId();
        RateLimitRule rateLimitRule = RateLimitRule.builder().key("LIVENESS_CHECK" + userId) .seconds(60).limit(5).build();
        rateLimiter.rateLimit(rateLimitRule);
        // record user location information
        Optional<PositionDetail> positionDetail = PositionDetail.of(userLivenessDto.getPosition());
        positionDetail.ifPresent(pd -> userGeoParser.asyncParseAndSave(userId, UserAction.LIVENESS_CHECK, String.valueOf(userId), pd.getLatitude(), pd.getLongitude(), userLivenessDto.getPosition()));
        return livenessCheckHelper.getLivenessToken(userId, userLivenessDto);
    }

    @FacebookAdsReport(eventName = "Liveness-Check", errorEventName = "Liveness-Check-Fail", successEventName = "Liveness-Check-Pass")
    @Override
    public String saveLivenessResult(String token, MultipartFile file) {
        Long userId = ThreadContext.getUser().getUserId();
        livenessCheckHelper.livenessCheck(userId, token, file);
        return CommonResult.SUCCESS;
    }
}
