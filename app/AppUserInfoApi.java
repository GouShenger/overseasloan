package com.yemi.core.api.app;

import com.yemi.core.consts.UserAction;
import com.yemi.core.consts.UserActionEndpoint;
import com.yemi.core.model.OcrData;
import com.yemi.core.model.app.AppOcrVo;
import com.yemi.core.model.dto.FileInfoDto;
import com.yemi.core.model.dto.UserContactsDto;
import com.yemi.core.model.dto.UserInfoDataSubmissionDto;
import com.yemi.core.model.dto.UserLivenessDto;
import com.yemi.core.model.vo.UserInfoDataVo;
import com.yemi.core.model.vo.UserInfoFieldVo;
import com.yemi.core.model.vo.UserInfoFulfillmentStatusVo;
import com.yemi.core.model.vo.UserInfoTypeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Api(tags = "APP-用户信息")
@RequestMapping("/app/v1/user/info")
public interface AppUserInfoApi {

    @ApiOperation(value = "获取用户信息类型")
    @GetMapping("/types")
    List<UserInfoTypeVo> getAllUserInfoType();

    @ApiOperation(value = "根据类型ID获取用户信息字段列表")
    @GetMapping("/types/fields/{infoTypeId}")
    List<UserInfoFieldVo> getUserInfoFields(@PathVariable Long infoTypeId);

    @ApiOperation(value = "提交用户信息")
    @PostMapping
    String submitUserInfo(@RequestBody @Validated UserInfoDataSubmissionDto submissionDto);

    @ApiOperation(value = "获取用户信息填写状态")
    @GetMapping("/status")
    UserInfoFulfillmentStatusVo getUserFulfillmentStatus();

    @ApiOperation(value = "获取用户已填写数据")
    @GetMapping("/data")
    List<UserInfoDataVo> getUserSubmitData();

    @ApiOperation(value = "根据当前用户获取省市县级联数据-供APP使用")
    @GetMapping("/province/app")
    Map<String, List<Map<String, List<String>>>> getProvinceData();

    @ApiOperation(value = "ocr身份证识别")
    @PostMapping("/ocr/identification")
    OcrData ocrIdentify(FileInfoDto dto, @RequestPart("file") MultipartFile file);

    @ApiOperation(value = "ocr身份证识别-结果保存")
    @PostMapping("/ocr")
    String saveOcrInfo(@RequestBody @Validated OcrData ocrData);

    @ApiOperation(value = "保存联系人")
    @PostMapping("/contacts")
    String saveUserContacts(@RequestBody @Validated UserContactsDto contactsDto);

    @ApiOperation(value = "活体识别")
    @PostMapping("/liveness")
    String saveFaceIdentifyResult(@RequestBody @Validated UserLivenessDto userLivenessDto);

    @ApiOperation(value = "ocr身份证识别结果获取")
    @GetMapping("/ocr")
    AppOcrVo getOcrInfo();

    @ApiOperation(value = "获取旷视活体识别token")
    @PostMapping("/liveness/token")
    String getLivenessToken(@RequestBody @Validated UserLivenessDto userLivenessDto);

    @ApiOperation(value = "活体识别-旷视")
    @PostMapping("/liveness/megv")
    String saveLivenessResult(@RequestParam String token, @RequestPart("file") MultipartFile file);
}
