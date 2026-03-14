
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Api(tags = "APP")
@RequestMapping("/app/v1/user/info")
public interface AppUserInfoApi {

    @ApiOperation(value = "Get user info types")
    @GetMapping("/types")
    List<UserInfoTypeVo> getAllUserInfoType();

    @ApiOperation(value = "Get user info fields by type id")
    @GetMapping("/types/fields/{infoTypeId}")
    List<UserInfoFieldVo> getUserInfoFields(@PathVariable Long infoTypeId);

    @ApiOperation(value = "Submit user info")
    @PostMapping
    String submitUserInfo(@RequestBody @Validated UserInfoDataSubmissionDto submissionDto);

    @ApiOperation(value = "Get user info fill status")
    @GetMapping("/status")
    UserInfoFulfillmentStatusVo getUserFulfillmentStatus();

    @ApiOperation(value = "Get user filled data")
    @GetMapping("/data")
    List<UserInfoDataVo> getUserSubmitData();

    @ApiOperation(value = "Get province city county data by current user-for APP use")
    @GetMapping("/province/app")
    Map<String, List<Map<String, List<String>>>> getProvinceData();

    @ApiOperation(value = "OCR identity recognition")
    @PostMapping("/ocr/identification")
    OcrData ocrIdentify(FileInfoDto dto, @RequestPart("file") MultipartFile file);

    @ApiOperation(value = "OCR identity recognition-result save")
    @PostMapping("/ocr")
    String saveOcrInfo(@RequestBody @Validated OcrData ocrData);

    @ApiOperation(value = "Save contacts")
    @PostMapping("/contacts")
    String saveUserContacts(@RequestBody @Validated UserContactsDto contactsDto);

    @ApiOperation(value = "Face recognition")
    @PostMapping("/liveness")
    String saveFaceIdentifyResult(@RequestBody @Validated UserLivenessDto userLivenessDto);

    @ApiOperation(value = "OCR identity recognition result get")
    @GetMapping("/ocr")
    AppOcrVo getOcrInfo();

    @ApiOperation(value = "Get Megvii face recognition token")
    @PostMapping("/liveness/token")
    String getLivenessToken(@RequestBody @Validated UserLivenessDto userLivenessDto);

    @ApiOperation(value = "Face recognition-Megvii")
    @PostMapping("/liveness/megv")
    String saveLivenessResult(@RequestParam String token, @RequestPart("file") MultipartFile file);
}
