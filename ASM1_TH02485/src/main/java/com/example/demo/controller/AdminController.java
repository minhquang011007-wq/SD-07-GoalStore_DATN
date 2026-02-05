package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
 
    private final PostService postService;
    private final UserService userService; // ✅ thêm dòng này

    // ✅ Constructor injection cả 2 service
    public AdminController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/posts")
    public String allPosts(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("posts", postService.getAll());
        return "admin-posts";
    }

    @GetMapping("/users")
    public String manageUsers(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("users", userService.getAll()); // ✅ gọi userService.getAll()
        return "admin-users";
    }
}
