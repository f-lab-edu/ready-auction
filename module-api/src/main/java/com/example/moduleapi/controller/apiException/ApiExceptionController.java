package com.example.moduleapi.controller.apiException;

import com.example.moduleapi.controller.apiException.error.ErrorResponse;
import com.example.moduleapi.exception.auction.BiddingFailException;
import com.example.moduleapi.exception.auction.RedisLockAcquisitionException;
import com.example.moduleapi.exception.auction.RedisLockInterruptedException;
import com.example.moduleapi.exception.file.CreateDirectoryFailException;
import com.example.moduleapi.exception.file.DeleteImageFailException;
import com.example.moduleapi.exception.file.ImageFileUploadFailException;
import com.example.moduleapi.exception.point.PointDeductionFailedException;
import com.example.moduleapi.exception.product.NotFoundProductException;
import com.example.moduleapi.exception.product.ProductNotPendingException;
import com.example.moduleapi.exception.product.UnauthorizedEnrollException;
import com.example.moduleapi.exception.product.UnauthorizedProductAccessException;
import com.example.moduleapi.exception.user.DuplicatedUserIdException;
import com.example.moduleapi.exception.user.LoginFailException;
import com.example.moduleapi.exception.user.NotFoundUserException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionController {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NotFoundUserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundUser(NotFoundUserException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DuplicatedUserIdException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse duplicatedUser(DuplicatedUserIdException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(LoginFailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse loginFail(LoginFailException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NotFoundProductException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundProduct(NotFoundProductException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(UnauthorizedEnrollException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse productEnroll(UnauthorizedEnrollException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(UnauthorizedProductAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse productNotAccess(UnauthorizedProductAccessException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ProductNotPendingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse productStatusNotPending(ProductNotPendingException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(CreateDirectoryFailException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse createDirectoryFail(CreateDirectoryFailException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DeleteImageFailException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse deleteImageFail(DeleteImageFailException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ImageFileUploadFailException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse imageFilUploadFail(ImageFileUploadFailException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(BiddingFailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse biddingFailException(BiddingFailException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(RedisLockAcquisitionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse redisLockAcquisitionException(RedisLockAcquisitionException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(RedisLockInterruptedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse redisLockOperationException(RedisLockInterruptedException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(PointDeductionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse pointDeductionFailedException(PointDeductionFailedException e) {
        return new ErrorResponse(e.getMessage());
    }
}
