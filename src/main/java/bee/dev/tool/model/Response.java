package bee.dev.tool.model;

import lombok.Data;

@Data
public class Response {
    private Integer code;
    private String message;
    private Object data;
}
