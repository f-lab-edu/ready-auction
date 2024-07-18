package com.example.readyauction.controller.user;

import com.example.readyauction.controller.request.user.UserSaveRequest;
import com.example.readyauction.controller.request.user.PasswordUpdateRequest;
import com.example.readyauction.controller.response.user.UserSaveResponse;
import com.example.readyauction.controller.response.user.PasswordUpdateResponse;
import com.example.readyauction.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserApiController {

  private final UserService userService;

  public UserApiController(UserService userService) {
    this.userService = userService;
  }
  // 회원가입
  @PostMapping("/v1/users") // RestDocs
  public UserSaveResponse join(@Valid @RequestBody UserSaveRequest userSaveRequest){
    // 디스패터 서블릿, response resolver
    UserSaveResponse userSaveResponse = userService.join(userSaveRequest);
    return userSaveResponse;
  }

  // 회원정보 비밀번호 수정
  @PutMapping("/v1/users/{userId}/changePassword")
  public PasswordUpdateResponse update(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest,
                                       @PathVariable String userId){
    PasswordUpdateResponse passwordUpdateResponse = userService.updatePassword(passwordUpdateRequest, userId);
    return passwordUpdateResponse;
  }


}
