package com.blogapplication.blogapplication.ServiceImpl;

import com.blogapplication.blogapplication.entity.Comment;
import com.blogapplication.blogapplication.entity.Post;
import com.blogapplication.blogapplication.exception.BlogAPIException;
import com.blogapplication.blogapplication.payload.CommentDTO;
import com.blogapplication.blogapplication.repository.CommentRepository;
import com.blogapplication.blogapplication.repository.PostRepository;
import com.blogapplication.blogapplication.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock private PostRepository postRepository;
    @InjectMocks
    private CommentServiceImpl commentService;

    private Post post1, post2;
    private Comment comment;
    private CommentDTO commentDTO;
    @Mock
    private ModelMapper modelMapper;
    @BeforeEach
    void setUp() {
        post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Post 1");

        post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Post 2");

        comment = new Comment();
        comment.setId(10L);
        comment.setName("John");
        comment.setEmail("john@example.com");
        comment.setBody("Original");
        comment.setPost(post1);

        commentDTO = new CommentDTO();
        commentDTO.setId(10L);
        commentDTO.setName("John");
        commentDTO.setEmail("john@example.com");
        commentDTO.setBody("Original");
    }
    @Test
    @DisplayName("1. createComment – success")
    void createComment_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        when(commentRepository.save(any())).thenReturn(comment);
        Comment result = commentService.createComment(1L, comment);
        assertThat(result).isNotNull();
    }
    @Test
    @DisplayName("4. getCommentById – valid")
    void getCommentById_Valid() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        Comment result = commentService.getCommentById(1L, 10L);
        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("5. getCommentById – comment not belonging to post")
    void getCommentById_NotBelong() {
        comment.setPost(post2); // belongs to post2
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        BlogAPIException ex = assertThrows(BlogAPIException.class, () -> commentService.getCommentById(1L, 10L));
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("6. updateCommentById – partial update")
    void updateCommentById_Partial() {
        CommentDTO update = new CommentDTO();
        update.setBody("New Body");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        comment.setBody(update.getBody());
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        Comment result = commentService.updateCommentById(1L, 10L, update);

        assertThat(result.getBody()).isEqualTo("New Body");
    }

    @Test
    @DisplayName("7. deleteCommentById – success")
    void deleteCommentById_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        commentService.deleteCommentById(1L, 10L);
        verify(commentRepository).deleteById(10L);
    }

    @Test
    @DisplayName("8. deleteCommentById – not belonging")
    void deleteCommentById_NotBelong() {
        comment.setPost(post2);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        assertThrows(BlogAPIException.class, () -> commentService.deleteCommentById(1L, 10L));
    }

}
