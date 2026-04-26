package com.blogapplication.blogapplication.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.blogapplication.blogapplication.entity.Comment;
import com.blogapplication.blogapplication.entity.Post;
import com.blogapplication.blogapplication.exception.BlogAPIException;
import com.blogapplication.blogapplication.exception.ResourceNotFoundException;
import com.blogapplication.blogapplication.repository.CommentRepository;
import com.blogapplication.blogapplication.repository.PostRepository;
import com.blogapplication.blogapplication.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    @Override
    public Comment createComment(Long postId, Comment comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        comment.setPost(post);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getAllCommentsByPost(Long postId) {
        // FIXED: was commentRepository.findAll() — ignored postId entirely
        return commentRepository.findByPostId(postId);
    }

    @Override
    public Comment getCommentById(Long postId, Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to the post");
        }
        return comment;
    }

    @Override
    public Comment updateCommentById(Long postId, Long commentId, Comment comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Comment getComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!getComment.getPost().getId().equals(post.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to the post");
        }

        if (Objects.nonNull(comment.getName()) && !comment.getName().isEmpty()) {
            getComment.setName(comment.getName());
        }
        if (Objects.nonNull(comment.getEmail()) && !comment.getEmail().isEmpty()) {
            getComment.setEmail(comment.getEmail());
        }
        if (Objects.nonNull(comment.getBody()) && !comment.getBody().isEmpty()) {
            getComment.setBody(comment.getBody());
        }

        return commentRepository.save(getComment);
    }

    @Override
    public Comment deleteCommentById(Long postId, Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Comment getComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!getComment.getPost().getId().equals(post.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to the post");
        }

        commentRepository.deleteById(commentId);
        return getComment;
    }
}
