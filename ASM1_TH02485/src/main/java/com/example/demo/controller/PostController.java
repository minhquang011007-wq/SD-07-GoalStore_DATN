package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping({"", "/"})
    public String rootRedirect() {
        return "redirect:/posts/feed?page=0&sortDir=desc";
        //comment o mapping
    }

    @GetMapping("/feed")
    public String feed(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       @RequestParam(defaultValue = "desc") String sortDir,
                       @RequestParam(required = false) String tag,
                       @RequestParam(required = false) String author,
                       @RequestParam(required = false) Integer minViews,
                       @RequestParam(required = false) Integer maxViews,
                       Model model) {

        Page<Post> postPage = postService.getFilteredPosts(page, size, sortDir, tag, author, minViews, maxViews);

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("tag", tag);
        model.addAttribute("author", author);
        model.addAttribute("minViews", minViews);
        model.addAttribute("maxViews", maxViews);
        model.addAttribute("topPosts", postService.getTopPosts(5));
        model.addAttribute("featured", postService.getFeaturedPosts());
        return "feed";
    }
    
    @GetMapping("/view/{id}")
    public String viewPost(@PathVariable Long id, Model model, HttpSession session) {
        Post post = postService.getById(id);
        if (post == null) return "redirect:/posts/feed";
        postService.increaseViews(post);
        model.addAttribute("post", post);
        model.addAttribute("user", session.getAttribute("user"));
        return "post-view";
    }

    @GetMapping("/manage")
    public String manage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        if ("admin".equalsIgnoreCase(user.getRole())) {
            model.addAttribute("posts", postService.getAll());
        } else {
            model.addAttribute("posts", postService.getByAuthor(user.getUsername()));
        }
        model.addAttribute("user", user);
        return "manage-posts";
    }

    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || "guest".equalsIgnoreCase(user.getRole())) return "redirect:/login";
        model.addAttribute("post", new Post());
        return "post-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Post post,
                       @RequestParam(required = false) String tags,
                       @RequestParam(required = false) Boolean featured,
                       HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        post.setAuthor(user.getUsername());

        if (tags != null && !tags.isEmpty()) {
            // ensure mutable list
            java.util.List<String> tagList = Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            post.setTags(new ArrayList<>(tagList));
        } else {
            post.setTags(new ArrayList<>());
        }

        post.setFeatured(featured != null && featured);
        postService.save(post);
        return "redirect:/posts/manage";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Post post = postService.getById(id);
        if (post == null) return "redirect:/posts/manage";

        if (!"admin".equalsIgnoreCase(user.getRole()) &&
                !post.getAuthor().equals(user.getUsername())) {
            return "redirect:/posts/manage";
        }

        model.addAttribute("post", post);
        model.addAttribute("tags", post.getTags() == null ? "" : String.join(",", post.getTags()));
        return "post-form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Post post,
                         @RequestParam(required = false) String tags,
                         HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
    
        Post existing = postService.getById(id);
        if (existing != null &&
                ("admin".equalsIgnoreCase(user.getRole()) ||
                        existing.getAuthor().equals(user.getUsername()))) {

            existing.setTitle(post.getTitle());
            existing.setContent(post.getContent());

            if (tags != null && !tags.isEmpty()) {
                // create mutable list
                java.util.List<String> tagList = Arrays.stream(tags.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                existing.setTags(new ArrayList<>(tagList));
            } else {
                existing.setTags(new ArrayList<>()); // keep mutable empty list
            }

            existing.setFeatured(post.isFeatured());
            postService.save(existing);
        }
        return "redirect:/posts/manage";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        Post post = postService.getById(id);
        if (post != null &&
                ("admin".equalsIgnoreCase(user.getRole()) ||
                        post.getAuthor().equals(user.getUsername()))) {
            postService.delete(post);
        }
        return "redirect:/posts/manage";
    }
}
