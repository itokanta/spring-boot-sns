package com.example.sns.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.sns.entity.Post;

/**
 * 投稿リポジトリインターフェース
 * 
 * Qiita記事の JdbcTemplate アプローチを JPA Data Repository で実装
 * Spring Data JPA を使用することで、基本的なCRUD操作が自動提供される
 * 
 * Rails の ActiveRecord::Base に相当する機能を提供
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    /**
     * 全ての投稿を作成日時の降順で取得
     * 
     * Qiita記事のSQL: 
     * SELECT * FROM test ORDER BY id DESC
     * に相当
     * 
     * @return 投稿リスト（新しい順）
     */
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllOrderByCreatedAtDesc();
    
    /**
     * 指定したユーザー名の投稿を取得
     * 
     * @param username ユーザー名
     * @return 該当ユーザーの投稿リスト
     */
    List<Post> findByUsername(String username);
}
