package bee.dev.tool.utils;

import bee.dev.tool.model.Response;
import bee.dev.tool.model.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utils {
    public static Response createResponse(final ResponseCode responseCode) {
        final Response response = new Response();
        response.setCode(responseCode.getCode());
        response.setMessage(responseCode.getMessage());
        return response;
    }

    public static Response createResponse(final ResponseCode responseCode, Object data) {
        final Response response = new Response();
        response.setCode(responseCode.getCode());
        response.setMessage(responseCode.getMessage());
        response.setData(data);
        return response;
    }
}
