package com.example.readyauction.controller.user;

import com.example.readyauction.controller.request.user.UserSaveRequest;
import com.example.readyauction.controller.response.user.UserSaveResponse;
import com.example.readyauction.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserApiController {

  private final UserService userService;

  public UserApiController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/v1/users") // RestDocs
  public UserSaveResponse join(@Valid @RequestBody UserSaveRequest userSaveRequest){
    // 디스패터 서블릿, response resolver
    UserSaveResponse userSaveResponse = userService.join(userSaveRequest);
    return userSaveResponse;
  }


}
