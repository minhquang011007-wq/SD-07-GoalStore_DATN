package com.example.demo.scheduler;

import com.example.demo.model.Post;
import com.example.demo.service.PostService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TopPostViewScheduler {

    private final PostService postService;

    public TopPostViewScheduler(PostService postService) {
        this.postService = postService;
    }

    @Scheduled(fixedRate = 300000)
    public void updateTopPostViews() {
        List<Post> topPosts = postService.getTopPosts(5);
        for (Post post : topPosts) {
            post.setViews(post.getViews() + 1);
            postService.save(post);
        }
        System.out.println("Đã cập nhật view cho " + topPosts.size() + " top posts.");
    }
}
