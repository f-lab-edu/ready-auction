package com.example.moduleapi.exception.file;

public class DeleteImageFailException extends RuntimeException {
    public DeleteImageFailException(Throwable e) {
        super(e + ": 이미지 삭제를 실패했습니다.");
    }
}
