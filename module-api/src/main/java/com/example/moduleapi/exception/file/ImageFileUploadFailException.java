package com.example.moduleapi.exception.file;

public class ImageFileUploadFailException extends RuntimeException {
    public ImageFileUploadFailException(Throwable cause) {
        super(cause + " : 파일을 업로드하는데 실패하였습니다.");
    }
}
