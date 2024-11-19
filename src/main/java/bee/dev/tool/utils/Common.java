package bee.dev.tool.utils;

import bee.dev.tool.model.Response;
import bee.dev.tool.model.ResponseCode;

public class Common {
    public static Response createResponse(final ResponseCode responseCode) {
        final Response response = new Response();
        response.setCode(responseCode.getCode());
        response.setMessage(responseCode.getMessage());
        return response;
    }


}
