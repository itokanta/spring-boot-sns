package com.example.sns.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 投稿エンティティクラス
 * Rails の app/models/post.rb に相当
 * 
 * データベースの posts テーブルと対応
 */
@Entity // JPA エンティティとして認識させる（Rails の ApplicationRecord 継承に相当）
@Table(name = "posts") // テーブル名を明示的に指定
@Data // Lombok: getter/setter/toString/equals/hashCode を自動生成
@NoArgsConstructor // Lombok: パラメータなしコンストラクタを自動生成
@AllArgsConstructor // Lombok: 全フィールドを引数に取るコンストラクタを自動生成
public class Post {
    
    /**
     * 投稿ID (主キー)
     * Rails の id フィールドに相当
     */
    @Id // 主キーとして指定
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT (Rails の id 自動採番に相当)
    private Long id;
    
    /**
     * 投稿者名
     * Rails では validates :username, presence: true に相当するバリデーションは
     * 後で Controller や Form クラスで実装
     */
    @Column(name = "username", nullable = false, length = 50)
    private String username;
    
    /**
     * 投稿内容
     * Rails では validates :content, presence: true に相当
     */
    @Column(name = "content", nullable = false, length = 500)
    private String content;
    
    /**
     * 投稿日時
     * Rails の created_at に相当
     * @CreationTimestamp で自動的に作成日時が設定される
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 表示用のコンストラクタ
     * Rails では new Post(username: "太郎", content: "こんにちは") のような書き方に相当
     */
    public Post(String username, String content) {
        this.username = username;
        this.content = content;
    }
}
