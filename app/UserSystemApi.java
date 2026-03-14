
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Api(tags = "APP")
@RequestMapping("/app/v1/system")
public interface UserSystemApi {
    @ApiOperation(value = "Regex list")
    @GetMapping("/regex")
    Map<String, String> regex();

}
