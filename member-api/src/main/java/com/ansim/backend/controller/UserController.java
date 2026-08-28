package com.ansim.backend.controller;

import com.ansim.backend.entity.Usr;
import com.ansim.backend.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search/id")
    public Usr searchByLoginId(@RequestParam String loginId) {
        return userService.searchByLoginId(loginId);
    }

    @GetMapping("/search/name")
    public List<Usr> searchByName(@RequestParam String name) {
        return userService.searchByName(name);
    }
}
