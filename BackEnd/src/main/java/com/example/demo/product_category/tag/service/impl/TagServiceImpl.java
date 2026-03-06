package com.example.demo.product_category.tag.service.impl;

import com.example.demo.product_category.common.exception.BadRequestException;
import com.example.demo.product_category.common.exception.ResourceNotFoundException;
import com.example.demo.product_category.common.mapper.ProductMapper;
import com.example.demo.product_category.tag.dto.TagRequest;
import com.example.demo.product_category.tag.dto.TagResponse;
import com.example.demo.product_category.tag.entity.Tag;
import com.example.demo.product_category.tag.repository.TagRepository;
import com.example.demo.product_category.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public TagResponse create(TagRequest request) {
        tagRepository.findByNameIgnoreCase(request.getName().trim())
                .ifPresent(t -> { throw new BadRequestException("Tên tag đã tồn tại"); });

        Tag tag = Tag.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .build();
        return ProductMapper.toTagResponse(tagRepository.save(tag));
    }

    @Override
    public TagResponse update(Integer id, TagRequest request) {
        Tag tag = getEntity(id);
        if (tagRepository.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), id)) {
            throw new BadRequestException("Tên tag đã tồn tại");
        }
        tag.setName(request.getName().trim());
        tag.setDescription(request.getDescription());
        return ProductMapper.toTagResponse(tagRepository.save(tag));
    }

    @Override
    public void delete(Integer id) {
        tagRepository.delete(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> findAll() {
        return tagRepository.findAll().stream().map(ProductMapper::toTagResponse).toList();
    }

    private Tag getEntity(Integer id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tag id = " + id));
    }
}
