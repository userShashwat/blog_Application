package com.blogapplication.blogapplication.ServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;

import com.blogapplication.blogapplication.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import com.blogapplication.blogapplication.entity.Category;
import com.blogapplication.blogapplication.entity.Post;
import com.blogapplication.blogapplication.exception.ResourceNotFoundException;
import com.blogapplication.blogapplication.payload.PostDTO;
import com.blogapplication.blogapplication.payload.PostResponse;
import com.blogapplication.blogapplication.repository.CategoryRepository;
import com.blogapplication.blogapplication.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Post Service Implementation Tests")
class PostServiceImplTest {

    @Mock private PostRepository postRepository;
    @Mock private CategoryRepository categoryRepository;
    @Spy private ModelMapper modelMapper = new ModelMapper();
    @InjectMocks private PostServiceImpl postService;

    // Shared test objects – initialised with common defaults
    private Post post;
    private Category category;
    private PostDTO postDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        post = new Post();
        post.setId(1L);
        post.setTitle("Original Title");
        post.setDescription("Original Desc");
        post.setContent("Original Content");
        post.setCategory(category);

        postDTO = new PostDTO();
        postDTO.setTitle("New Title");
        postDTO.setDescription("New Desc");
        postDTO.setContent("New Content");
        postDTO.setCategoryId(1L);
    }

    @Test
    @DisplayName("1. createPost – success")
    void createPost_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(modelMapper.map(postDTO, Post.class)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(modelMapper.map(post, PostDTO.class)).thenReturn(postDTO);

        PostDTO result = postService.createPost(postDTO);
        assertThat(result).isNotNull();
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("2. createPost – category not found throws ResourceNotFoundException")
    void createPost_CategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.createPost(postDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id : 1");

        verify(postRepository, never()).save(any());
    }
    /*
    @ParameterizedTest(name = "{0} -> page {0}, size {1}, sort {2} {3}")
    @CsvSource({"0,10,title,asc", "1,5,description,desc", "2,20,id,asc"})
    @DisplayName("3. getAllPost – pagination and sort respected")
    void getAllPost_WithSort(int pageNo, int pageSize, String sortBy, String sortDir) {
        // Create a fresh copy of post for this iteration
        Post freshPost = new Post();
        freshPost.setId(1L);
        freshPost.setTitle("Original Title");
        freshPost.setDescription("Original Desc");
        freshPost.setContent("Original Content");
        freshPost.setCategory(category);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Post> page = new PageImpl<>(List.of(freshPost, new Post()), pageable, 20);

        when(postRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(Post.class), eq(PostDTO.class))).thenReturn(postDTO);

        PostResponse resp = postService.getAllPost(pageNo, pageSize, sortBy, sortDir);

        assertThat(resp.getContent()).hasSize(2);
        assertThat(resp.getPageNo()).isEqualTo(pageNo);
        assertThat(resp.getTotalElements()).isEqualTo(20);
    }
    */

    @Test
    @DisplayName("4. getPostById – found")
    void getPostById_Found() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        Post result = postService.getPostById(1L);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("5. getPostById – not found")
    void getPostById_NotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy( () -> postService.getPostById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found with id : 1");
        verify(postRepository).findById(1L);
    }

    @Test
    @DisplayName("updatePost - update all fields successfully")
    void updatePost_UpdateAllFields_Success() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(modelMapper.map(post, PostDTO.class)).thenReturn(postDTO);

        // When
        PostDTO result = postService.updatePost(1L, postDTO);

        // Then
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertThat(savedPost.getTitle()).isEqualTo("New Title");
        assertThat(savedPost.getDescription()).isEqualTo("New Desc");
        assertThat(savedPost.getContent()).isEqualTo("New Content");
        assertThat(savedPost.getCategory()).isEqualTo(category);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("updatePost - update only title")
    void updatePost_UpdateOnlyTitle_Success() {
        // Given
        //creating updated title only object
        PostDTO titleOnlyDTO = new PostDTO();
        titleOnlyDTO.setTitle("Updated Title Only");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(modelMapper.map(post, PostDTO.class)).thenReturn(titleOnlyDTO);

        // When
        postService.updatePost(1L, titleOnlyDTO);

        // Then
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertThat(savedPost.getTitle()).isEqualTo("Updated Title Only");
        assertThat(savedPost.getDescription()).isEqualTo("Original Desc");
        assertThat(savedPost.getContent()).isEqualTo("Original Content");
        assertThat(savedPost.getCategory()).isEqualTo(category);

        verify(categoryRepository, never()).findById(any());
    }

    @Test
    @DisplayName("updatePost - update only description")
    void updatePost_UpdateOnlyDescription_Success() {
        // Given
        PostDTO descOnlyDTO = new PostDTO();
        descOnlyDTO.setDescription("Updated Description Only");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(modelMapper.map(post, PostDTO.class)).thenReturn(descOnlyDTO);

        // When
        postService.updatePost(1L, descOnlyDTO);

        // Then
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertThat(savedPost.getTitle()).isEqualTo("Original Title");
        assertThat(savedPost.getDescription()).isEqualTo("Updated Description Only");
        assertThat(savedPost.getContent()).isEqualTo("Original Content");
        assertThat(savedPost.getCategory()).isEqualTo(category);
    }


    @Test
    @DisplayName("8. deletePost – success")
    void deletePost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        String msg = postService.deletePost(1L);
        assertThat(msg).isEqualTo("Post Deleted Successfully");
        verify(postRepository).deleteById(1L);
    }

    @Test
    @DisplayName("9. deletePost – not found")
    void deletePost_NotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy( () -> postService.deletePost(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found with id : 1");
    }

    @Test
    @DisplayName("10. findByCategoryId – returns list of DTOs")
    void findByCategoryId_Success() {
        when(postRepository.findByCategoryId(1L)).thenReturn(List.of(post));
        when(modelMapper.map(post, PostDTO.class)).thenReturn(postDTO);
        List<PostDTO> list = postService.findByCategoryId(1L);
        assertThat(list).hasSize(1);
    }
}