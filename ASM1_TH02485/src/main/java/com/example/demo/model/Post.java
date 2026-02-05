package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    private String author;
    private int views = 0;
    private boolean featured = false;
    private LocalDateTime createdAt = LocalDateTime.now();

    public void increaseViews() {
        this.views++;
    }

    public String getTagsAsString() {
        if (tags == null || tags.isEmpty()) return "";
        return String.join(", ", tags);
    }
}
