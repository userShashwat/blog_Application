package com.blogapplication.blogapplication.controller;

import com.blogapplication.blogapplication.entity.Post;
import com.blogapplication.blogapplication.payload.PostDTO;
import com.blogapplication.blogapplication.payload.PostResponse;
import com.blogapplication.blogapplication.security.JwtTokenProvider;
import com.blogapplication.blogapplication.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostService postService;
    @MockBean  // ✅ Add this – mocks JwtTokenProvider
    private JwtTokenProvider jwtTokenProvider;

    @Autowired private ObjectMapper objectMapper;
    private PostDTO postDTO;
    private Post post;
    @BeforeEach
    void setUp() {
        postDTO = new PostDTO();
        postDTO.setTitle("Test Post");
        postDTO.setDescription("Test Description");
        postDTO.setContent("Test Content");
        postDTO.setCategoryId(1L);

        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setDescription("Test Description");
        post.setContent("Test Content");
    }
    @WithMockUser(roles = "ADMIN")
    @Test
    void createPost_RoleAdmin() throws Exception{
      when(postService.createPost(any(PostDTO.class))).thenReturn(postDTO);
      mockMvc.perform(post("/api/posts")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(postDTO))
              .with(csrf()))
              .andExpect(status().isOk());
    }
    @Test
    @DisplayName("2. createPost – USER role returns 403")
    @WithMockUser(roles = "USER")
    void createPost_WithoutAdmin() throws Exception {
        mockMvc.perform(post("/api/posts").content("{}"))
                .andExpect(status().isForbidden());
    }
    @Test
    @DisplayName("3. createPost – no auth returns 401")
    void createPost_NoAuth() throws Exception {
       mockMvc.perform(post("/api/posts")
               .content("{}")
               .with(csrf()))
               .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("4. getAllPost – ADMIN returns paginated")
    @WithMockUser(roles = "ADMIN")
    void getAllPost_Admin() throws Exception {
        PostResponse resp = new PostResponse();
        resp.setContent(List.of(postDTO));

        when(postService.getAllPost(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(resp);
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("6. updatePost – ADMIN returns 200")
    @WithMockUser(roles = "ADMIN")
    void updatePost_Admin() throws Exception {
        when(postService.updatePost(any(),any())).thenReturn(postDTO);
        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("7. updatePost – USER forbidden")
    @WithMockUser(roles = "USER")
    void updatePost_UserForbidden() throws Exception {
        mockMvc.perform(put("/api/posts/1").content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("8. deletePost – ADMIN deletes")
    @WithMockUser(roles = "ADMIN")
    void deletePost_Admin() throws Exception {
        when(postService.deletePost(1L)).thenReturn("Deleted");
        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("9. deletePost – USER forbidden")
    @WithMockUser(roles = "USER")
    void deletePost_UserForbidden() throws Exception {
        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("10. findByCategoryId – public returns list")
    void findByCategoryId() throws Exception {
        when(postService.findByCategoryId(1L)).thenReturn(List.of(postDTO));
        mockMvc.perform(get("/api/posts/category/1"))
                .andExpect(status().isOk());
    }
}



