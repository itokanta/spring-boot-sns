package com.example.sns.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sns.entity.Post;
import com.example.sns.repository.PostRepository;

/**
 * 投稿サービスクラス
 * 
 * Qiita記事でControllerに直接記述されていたビジネスロジックを
 * Serviceクラスに分離して実装（Spring Boot のベストプラクティス）
 * 
 * Rails の Service Object パターンに相当
 */
@Service
@Transactional
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    /**
     * 全ての投稿を取得（新しい順）
     * 
     * Qiita記事のControllerで実行されていた処理：
     * String sql = "SELECT * FROM test ORDER BY id DESC";
     * List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
     * 
     * @return 投稿リスト
     */
    public List<Post> getAllPosts() {
        return postRepository.findAllOrderByCreatedAtDesc();
    }
    
    /**
     * 新しい投稿を作成
     * 
     * Qiita記事では投稿機能は実装されていませんが、
     * SNSアプリケーションに必要な機能として追加
     * 
     * @param username 投稿者名
     * @param content 投稿内容
     * @return 保存された投稿
     */
    public Post createPost(String username, String content) {
        // バリデーション（Railsのvalidatesに相当）
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("ユーザー名は必須です");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("投稿内容は必須です");
        }
        if (content.length() > 500) {
            throw new IllegalArgumentException("投稿内容は500文字以内で入力してください");
        }
        
        // 新しい投稿を作成
        Post post = new Post(username.trim(), content.trim());
        return postRepository.save(post);
    }
    
    /**
     * 投稿をIDで取得
     * 
     * @param id 投稿ID
     * @return 投稿（見つからない場合は空のOptional）
     */
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }
    
    /**
     * 指定したユーザーの投稿を取得
     * 
     * @param username ユーザー名
     * @return 該当ユーザーの投稿リスト
     */
    public List<Post> getPostsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("ユーザー名を指定してください");
        }
        return postRepository.findByUsername(username.trim());
    }
    
    /**
     * 投稿を削除
     * 
     * @param id 削除する投稿のID
     * @return 削除が成功した場合true
     */
    public boolean deletePost(Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * 投稿数を取得
     * 
     * @return 総投稿数
     */
    public long getPostCount() {
        return postRepository.count();
    }
}