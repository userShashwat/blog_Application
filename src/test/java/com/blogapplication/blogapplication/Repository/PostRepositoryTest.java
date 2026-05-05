package com.blogapplication.blogapplication.Repository;

import com.blogapplication.blogapplication.entity.Category;
import com.blogapplication.blogapplication.entity.Post;
import com.blogapplication.blogapplication.repository.CategoryRepository;
import com.blogapplication.blogapplication.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private Category savedCategory;
    private Post post1, post2;
    @BeforeEach
    void setUp(){
        savedCategory = new Category();
        savedCategory.setName("Java");
        savedCategory.setDescription("Java related posts");
        savedCategory = categoryRepository.save(savedCategory);

        post1 = new Post();
        post1.setTitle("First Post");
        post1.setDescription("Description of first post");
        post1.setContent("Content of first post");
        post1.setCategory(savedCategory);

        post2 = new Post();
        post2.setTitle("Second Post");
        post2.setDescription("Description of second post");
        post2.setContent("Content of second post");
        post2.setCategory(savedCategory);
    }
    @Test
    void savePost_ShouldPersist() {
        Post saved = postRepository.save(post1);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("First Post");
    }
    @Test
    void findById_ExistingId() {
        Post saved = postRepository.save(post1);
        Optional<Post> found = postRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("First Post");
    }
    @ParameterizedTest
    @ValueSource(longs = {9999L, 10000L, 0L})

    void findById_NonExistingId(Long id) {
        Optional<Post> found = postRepository.findById(id);
        assertThat(found).isEmpty();
    }
    @Test

    void findByCategoryId_ExistingCategory() {
        postRepository.save(post1);
        postRepository.save(post2);
        List<Post> posts = postRepository.findByCategoryId(savedCategory.getId());
        assertThat(posts).hasSize(2);
        assertThat(posts).extracting(Post::getTitle)
                .containsExactlyInAnyOrder("First Post", "Second Post");
    }
    @Test

    void findByCategoryId_NoPosts() {
        Category emptyCat = new Category();
        emptyCat.setName("Empty");
        emptyCat = categoryRepository.save(emptyCat);
        List<Post> posts = postRepository.findByCategoryId(emptyCat.getId());
        assertThat(posts).isEmpty();
    }

    @Test

    void deleteById_ExistingId() {
        Post saved = postRepository.save(post1);
        postRepository.deleteById(saved.getId());
        Optional<Post> found = postRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
    @Test

    void saveMultiple_Posts() {
        postRepository.save(post1);
        postRepository.save(post2);
        long count = postRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test

    void updatePost_ReflectsChanges() {
        Post saved = postRepository.save(post1);
        saved.setTitle("Updated Title");
        postRepository.save(saved);
        Post reloaded = postRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getTitle()).isEqualTo("Updated Title");
    }

}
