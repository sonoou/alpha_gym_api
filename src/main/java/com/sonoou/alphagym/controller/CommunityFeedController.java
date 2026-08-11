package com.sonoou.alphagym.controller;

import com.sonoou.alphagym.dto.*;
import com.sonoou.alphagym.service.FeedService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class CommunityFeedController {

    private final FeedService feedService;

    public CommunityFeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getFeed(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        List<PostResponse> feed = feedService.getFeed(email);
        return ResponseEntity.ok(feed);
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(Authentication authentication,
                                                   @RequestBody CreatePostRequest request) {
        PostResponse post = feedService.createPost(authentication.getName(), request);
        return ResponseEntity.ok(post);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostResponse> toggleLike(Authentication authentication,
                                                   @PathVariable Long postId) {
        PostResponse post = feedService.toggleLike(authentication.getName(), postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        List<CommentResponse> comments = feedService.getComments(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(Authentication authentication,
                                                      @PathVariable Long postId,
                                                      @Valid @RequestBody CommentRequest request) {
        CommentResponse comment = feedService.addComment(authentication.getName(), postId, request);
        return ResponseEntity.ok(comment);
    }
}
