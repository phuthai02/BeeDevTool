package bee.dev.tool.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

@Slf4j
public class Logger {

    public static void logController(Object controller, Method method, Object data) {
        String className = controller.getClass().getSimpleName();
        String endpoint = getEndpoint(method);
        String httpMethod = getHttpMethod(method);
        log.info("Class: " + className + ", Endpoint: " + endpoint + ", HTTP Method: " + httpMethod);
        log.info("Data: " + data);
    }

    private static String getEndpoint(Method method) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            return requestMapping.value()[0];
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            return postMapping.value()[0];
        } else if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            return getMapping.value()[0];
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            PutMapping putMapping = method.getAnnotation(PutMapping.class);
            return putMapping.value()[0];
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
            return deleteMapping.value()[0];
        }
        return "Unknown Endpoint";
    }

    private static String getHttpMethod(Method method) {
        if (method.isAnnotationPresent(PostMapping.class)) {
            return "POST";
        } else if (method.isAnnotationPresent(GetMapping.class)) {
            return "GET";
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            return "PUT";
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            return "DELETE";
        } else if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            RequestMethod[] methods = requestMapping.method();
            if (methods.length > 0) {
                return methods[0].name();
            }
        }
        return "Unknown HTTP Method";
    }
}

