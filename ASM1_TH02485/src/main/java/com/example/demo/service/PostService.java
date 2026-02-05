package com.example.demo.service;

import com.example.demo.model.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository repo;

    public PostService(PostRepository repo) {
        this.repo = repo;
    }

    // Sắp xếp thật sự theo createdAt (dùng repo để lấy đã sắp xếp)
    public Page<Post> getFilteredPosts(int page, int size, String sortDir,
                                       String tag, String author,
                                       Integer minViews, Integer maxViews) {

        // Lấy danh sách đã sắp xếp theo createdAt từ repository
        List<Post> allPosts = "asc".equalsIgnoreCase(sortDir)
                ? repo.findAllByOrderByCreatedAtAsc()
                : repo.findAllByOrderByCreatedAtDesc();

        // Lọc điều kiện
        List<Post> filtered = allPosts.stream()
                .filter(p -> author == null || author.isEmpty() ||
                        (p.getAuthor() != null && p.getAuthor().toLowerCase().contains(author.toLowerCase())))
                .filter(p -> tag == null || tag.isEmpty() ||
                        (p.getTags() != null && p.getTags().stream()
                                .anyMatch(t -> t.equalsIgnoreCase(tag))))
                .filter(p -> minViews == null || p.getViews() >= minViews)
                .filter(p -> maxViews == null || p.getViews() <= maxViews)
                .collect(Collectors.toList());

        // Phân trang thủ công
        Pageable pageable = PageRequest.of(page, size);
        int start = Math.min((int) pageable.getOffset(), filtered.size());
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<Post> pageContent = filtered.subList(start, end);

        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    public List<Post> getAll() {
        return repo.findAllByOrderByCreatedAtDesc();
    }

    public Post getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<Post> getByAuthor(String author) {
        return repo.findByAuthorContainingIgnoreCase(author);
    }

    public List<Post> getTopPosts(int n) {
        return repo.findAllByOrderByViewsDesc().stream()
                .limit(n).collect(Collectors.toList());
    }

    public List<Post> getFeaturedPosts() {
            return repo.findByFeaturedTrueOrderByViewsDesc();
    }

    @Transactional
    public Post save(Post post) {
        // đảm bảo tags mutable trước khi save
        if (post.getTags() == null) {
            post.setTags(new java.util.ArrayList<>());
        } else if (!(post.getTags() instanceof java.util.ArrayList)) {
            // nếu người dùng truyền immutable list, convert sang mutable
            post.setTags(new java.util.ArrayList<>(post.getTags()));
        }
        return repo.save(post);
    }

    @Transactional
    public void delete(Post post) {
        repo.delete(post);
    }

    @Transactional
    public void increaseViews(Post post) {
        post.setViews(post.getViews() + 1);
        repo.save(post);
    }
}
