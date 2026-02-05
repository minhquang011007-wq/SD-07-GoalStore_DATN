package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "user-profile";
    }

    @GetMapping("/edit")
    public String editForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user-edit";
    }

    // 💾 Xử lý lưu thông tin sau khi chỉnh sửa
    @PostMapping("/edit")
    public String updateUser(@ModelAttribute User updatedUser, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        User storedUser = userService.findByUsername(currentUser.getUsername());
        if (storedUser != null) {
            storedUser.setFullName(updatedUser.getFullName());
            storedUser.setEmail(updatedUser.getEmail());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
                storedUser.setPassword(updatedUser.getPassword());
            }
            userService.update(storedUser);
            session.setAttribute("user", storedUser);
        }

        return "redirect:/user/profile";
    }


    // 🗑️ Xóa tài khoản của chính mình
    @PostMapping("/delete")
    public String deleteUser(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        userService.remove(currentUser.getUsername());
        session.invalidate(); // Xóa session
        return "redirect:/login";
    }
}
