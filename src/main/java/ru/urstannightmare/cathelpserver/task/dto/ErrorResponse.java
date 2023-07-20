package ru.urstannightmare.cathelpserver.task.dto;

import java.util.Objects;

public class ErrorResponse {
    private int code;
    private String details;

    public ErrorResponse() {
    }

    public ErrorResponse(int code, String details) {
        this.code = code;
        this.details = details;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return code == that.code && Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, details);
    }

    @Override
    public String toString() {
        return "ErrorResponseDto{" +
                "code=" + code +
                ", details='" + details + '\'' +
                '}';
    }
}
