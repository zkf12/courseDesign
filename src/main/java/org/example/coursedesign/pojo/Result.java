package org.example.coursedesign.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<T>(0, "", data);
    }
    public static <T> Result<T> success(String msg) {
        return new Result<T>(0, msg, null);
    }

    public static <T> Result<T> success() {
        return new Result<T>(0, "", null);
    }

    public static <T> Result<T> success(Integer code, String msg, T data) {
        return new Result<T>(code, msg, data);
    }

    public static <T> Result<T> success(Integer code, String msg) {
        return new Result<T>(code, msg, null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<T>(code, msg, null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<T>(1, msg, null);
    }
}
