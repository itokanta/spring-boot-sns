package com.example.sns.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sns.entity.Post;
import com.example.sns.service.PostService;

/**
 * 投稿コントローラークラス
 * 
 * Qiita記事のControllerコードを参考に実装
 * https://qiita.com/t-yama-3/items/969825d5c1bc4a16866d
 * 
 * Rails の app/controllers/posts_controller.rb に相当
 */
@Controller
public class PostController {
    
    @Autowired
    private PostService postService;
    
    /**
     * 投稿一覧表示
     * 
     * Qiita記事の該当コード：
     * @GetMapping("/index")
     * public String index(Model model) {
     *     String sql = "SELECT * FROM test ORDER BY id DESC";
     *     List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
     *     model.addAttribute("testList", list);
     *     return "index";
     * }
     * 
     * Rails では: def index; @posts = Post.all.order(created_at: :desc); end
     * 
     * @param model ビューに渡すデータ
     * @return index.htmlテンプレート
     */
    @GetMapping("/")
    public String index(Model model) {
        // Serviceクラスから投稿リストを取得
        List<Post> posts = postService.getAllPosts();
        
        // Qiita記事と同様にModelに追加（変数名はpostListに変更）
        model.addAttribute("postList", posts);
        
        // 投稿数も表示用に追加
        model.addAttribute("postCount", postService.getPostCount());
        
        return "index";
    }
    
    /**
     * 新規投稿フォーム表示
     * 
     * Rails では: def new; @post = Post.new; end
     * 
     * @return new.htmlテンプレート
     */
    @GetMapping("/new")
    public String newPost() {
        return "new";
    }
    
    /**
     * 投稿作成処理
     * 
     * Rails では: def create; @post = Post.create(post_params); end
     * 
     * @param username 投稿者名
     * @param content 投稿内容
     * @param redirectAttributes リダイレクト時のメッセージ
     * @return リダイレクト先
     */
    @PostMapping("/create")
    public String createPost(
            @RequestParam("username") String username,
            @RequestParam("content") String content,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Serviceクラスで投稿作成（バリデーション含む）
            Post createdPost = postService.createPost(username, content);
            
            // 成功メッセージを設定
            redirectAttributes.addFlashAttribute("successMessage", 
                "投稿が正常に作成されました！");
            
        } catch (IllegalArgumentException e) {
            // バリデーションエラー時のメッセージ
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("content", content);
            
            // エラー時は新規投稿フォームにリダイレクト
            return "redirect:/new";
        }
        
        // 成功時は一覧画面にリダイレクト
        return "redirect:/";
    }
    
    /**
     * 特定ユーザーの投稿表示
     * 
     * Rails では: def show; @posts = Post.where(username: params[:username]); end
     * 
     * @param username ユーザー名
     * @param model ビューに渡すデータ
     * @return user.htmlテンプレート
     */
    @GetMapping("/user")
    public String userPosts(@RequestParam("username") String username, Model model) {
        try {
            List<Post> userPosts = postService.getPostsByUsername(username);
            model.addAttribute("postList", userPosts);
            model.addAttribute("username", username);
            model.addAttribute("postCount", userPosts.size());
            
            return "user";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
    }
}
