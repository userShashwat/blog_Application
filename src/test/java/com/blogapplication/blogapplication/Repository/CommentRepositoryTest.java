package com.blogapplication.blogapplication.Repository;

import com.blogapplication.blogapplication.entity.Comment;
import com.blogapplication.blogapplication.entity.Post;
import com.blogapplication.blogapplication.repository.CommentRepository;
import com.blogapplication.blogapplication.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired private PostRepository postRepository;
    private Post savedPost;
    private Comment comment1, comment2;
    @BeforeEach
    void setUp() {
        // Create valid post
        Post post = new Post();
        post.setTitle("Spring Boot Tutorial");
        post.setDescription("A comprehensive guide to Spring Boot framework with practical examples and best practices.");
        post.setContent("This is the full content of the blog post...");
        savedPost = postRepository.save(post);

        // Create comments
        comment1 = new Comment();
        comment1.setName("John Doe");
        comment1.setEmail("john@example.com");
        comment1.setBody("Excellent tutorial!");
        comment1.setPost(savedPost);

        comment2 = new Comment();
        comment2.setName("Jane Smith");
        comment2.setEmail("jane@example.com");
        comment2.setBody("Very helpful, thanks!");
        comment2.setPost(savedPost);
    }
    @Test
    void saveComment() {
        Comment saved = commentRepository.save(comment1);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPost().getId()).isEqualTo(savedPost.getId());
    }
    @Test
    void findById_Existing() {
        Comment saved = commentRepository.save(comment1);
        Optional<Comment> found = commentRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }

    @Test
    void findById_NonExisting() {
        Optional<Comment> found = commentRepository.findById(999L);
        assertThat(found).isEmpty();
    }
    @Test

    void findByPostId_WithComments() {
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        List<Comment> comments = commentRepository.findByPostId(savedPost.getId());
        assertThat(comments).hasSize(2);
    }
    @Test
    void findByPostId_Empty(){
        List<Comment> comments = commentRepository.findByPostId(savedPost.getId());
        assertThat(comments).isEmpty();
    }
    @Test
    void deleteById_Existing(){
        Comment saveComment = commentRepository.save(comment2);
        commentRepository.deleteById(saveComment.getId());
        assertThat(commentRepository.findById(saveComment.getId())).isEmpty();
    }
    @Test

    void updateComment() {
        // Given - Save original comment
        Comment saved = commentRepository.save(comment1);
        // When - Update the comment
        saved.setBody("Updated Body");
        saved.setName("Updated Name");
        commentRepository.save(saved);

        // Then - Verify update was persisted
        Comment reloaded = commentRepository.findById(saved.getId()).orElseThrow();

        assertThat(reloaded.getBody()).isEqualTo("Updated Body");
        assertThat(reloaded.getName()).isEqualTo("Updated Name");
        // Original fields remain unchanged
        assertThat(reloaded.getEmail()).isEqualTo(comment1.getEmail());
    }

}
