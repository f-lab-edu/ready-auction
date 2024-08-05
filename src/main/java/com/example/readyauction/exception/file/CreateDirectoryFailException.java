package com.example.readyauction.exception.file;

public class CreateDirectoryFailException extends RuntimeException {
	public CreateDirectoryFailException(Throwable cause) {
		super(cause + " : 이미지 디렉토리를 생성하는데 실패하였습니다.");

	}
}
