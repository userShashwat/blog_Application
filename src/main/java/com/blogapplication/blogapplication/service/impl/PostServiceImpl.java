package com.blogapplication.blogapplication.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.blogapplication.blogapplication.entity.Category;
import com.blogapplication.blogapplication.entity.Post;
import com.blogapplication.blogapplication.exception.BlogAPIException;
import com.blogapplication.blogapplication.exception.ResourceNotFoundException;
import com.blogapplication.blogapplication.payload.PostDTO;
import com.blogapplication.blogapplication.payload.PostResponse;
import com.blogapplication.blogapplication.repository.CategoryRepository;
import com.blogapplication.blogapplication.repository.PostRepository;
import com.blogapplication.blogapplication.service.PostService;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public PostDTO createPost(PostDTO postDto) {
        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        Post post = modelMapper.map(postDto, Post.class);
        post.setCategory(category);
        postRepository.save(post);

        return modelMapper.map(post, PostDTO.class);
    }

    @Override
    public PostResponse getAllPost(int pageNo, int pageSize, String sortBy, String sortDir) {

        // FIXED: sortBy and sortDir were previously ignored — now applied correctly
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Post> paginatedPosts = postRepository.findAll(pageable);
        List<Post> posts = paginatedPosts.getContent();

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(posts);
        postResponse.setPageNo(paginatedPosts.getNumber());
        postResponse.setPageSize(paginatedPosts.getSize());
        postResponse.setTotalElements(paginatedPosts.getTotalElements());
        postResponse.setTotalPages(paginatedPosts.getTotalPages());
        postResponse.setLast(paginatedPosts.isLast());

        return postResponse;
    }

    @Override
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
    }

    @Override
    public PostDTO updatePost(Long blogId, PostDTO postDto) {

        // FIXED: replaced unsafe .get() with orElseThrow
        Post postGet = postRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", blogId));

        if (Objects.nonNull(postDto.getTitle()) && !postDto.getTitle().isEmpty()) {
            postGet.setTitle(postDto.getTitle());
        }

        if (Objects.nonNull(postDto.getDescription()) && !postDto.getDescription().isEmpty()) {
            postGet.setDescription(postDto.getDescription());
        }

        // FIXED: was checking postDto.getDescription() and calling setDescription() — should be content
        if (Objects.nonNull(postDto.getContent()) && !postDto.getContent().isEmpty()) {
            postGet.setContent(postDto.getContent());
        }

        if (Objects.nonNull(postDto.getCategoryId())) {
            Category category = categoryRepository.findById(postDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));
            postGet.setCategory(category);
        }

        postRepository.save(postGet);
        return modelMapper.map(postGet, PostDTO.class);
    }

    @Override
    public String deletePost(Long postId) {
        // Verify it exists before deleting
        postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        postRepository.deleteById(postId);
        return "Post Deleted Successfully";
    }

    @Override
    public List<PostDTO> findByCategoryId(Long categoryId) {
        List<Post> posts = postRepository.findByCategoryId(categoryId);
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .collect(Collectors.toList());
    }
}
