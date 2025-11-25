package com.foro_cine.backend.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCategory(String category);
    List<Post> findByMovieTitle(String movieTitle);
}
