package utils.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class My500Exception extends RuntimeException {
    public My500Exception(String message) {
        super(message);
    }
}