package com.example.demo.product_category.tag.service;

import com.example.demo.product_category.tag.dto.TagRequest;
import com.example.demo.product_category.tag.dto.TagResponse;

import java.util.List;

public interface TagService {
    TagResponse create(TagRequest request);
    TagResponse update(Integer id, TagRequest request);
    void delete(Integer id);
    List<TagResponse> findAll();
}
