package com.example.datingapp2021.ui;

public class Result<T> {
    private Result() {}

    public static final class Success<T> extends Result<T> {
        public T data;
        public Success(T data) {
            this.data = data;
        }
    }

    public static final class SuccessNULL<T> extends Result<T> {
        public String body;
        public SuccessNULL(String body) {
            this.body = body;
        }
    }

    public static final class Error<T> extends Result<T> {
        public Exception exception;
        public Error(Exception e) {
            this.exception = e;
        }
    }
}
