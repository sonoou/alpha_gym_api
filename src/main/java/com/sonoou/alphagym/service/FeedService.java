package com.sonoou.alphagym.service;

import com.sonoou.alphagym.dto.CommentRequest;
import com.sonoou.alphagym.dto.CommentResponse;
import com.sonoou.alphagym.dto.CreatePostRequest;
import com.sonoou.alphagym.dto.PostResponse;
import com.sonoou.alphagym.entity.CommunityPostEntity;
import com.sonoou.alphagym.entity.PostCommentEntity;
import com.sonoou.alphagym.entity.PostLikeEntity;
import com.sonoou.alphagym.entity.UserEntity;
import com.sonoou.alphagym.repository.CommunityPostRepository;
import com.sonoou.alphagym.repository.PostCommentRepository;
import com.sonoou.alphagym.repository.PostLikeRepository;
import com.sonoou.alphagym.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedService {

    private final CommunityPostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final PostLikeRepository likeRepository;
    private final UserRepository userRepository;

    public FeedService(CommunityPostRepository postRepository,
                       PostCommentRepository commentRepository,
                       PostLikeRepository likeRepository,
                       UserRepository userRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
    }

    public List<PostResponse> getFeed(String currentEmail) {
        UserEntity currentUser = userRepository.findByEmail(currentEmail).orElse(null);
        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        List<CommunityPostEntity> posts = postRepository.findAllByOrderByCreatedAtDesc();

        return posts.stream().map(post -> {
            PostResponse response = new PostResponse();
            response.setId(post.getId());
            response.setUserId(post.getUser().getId());
            response.setUserName(post.getUser().getName());
            response.setUserPhotoUrl(post.getUser().getProfilePhotoUrl());
            response.setCaption(post.getCaption());
            response.setImageUrl(post.getImageUrl());
            response.setLikesCount(post.getLikesCount());
            response.setCreatedAt(post.getCreatedAt());

            boolean isLiked = currentUserId != null && likeRepository.existsByPostIdAndUserId(post.getId(), currentUserId);
            response.setIsLikedByCurrentUser(isLiked);

            int commentsCount = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId()).size();
            response.setCommentsCount(commentsCount);

            return response;
        }).collect(Collectors.toList());
    }

    public PostResponse createPost(String email, CreatePostRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CommunityPostEntity post = new CommunityPostEntity();
        post.setUser(user);
        post.setCaption(request.getCaption());
        post.setImageUrl(request.getImageUrl());
        post.setLikesCount(0);

        CommunityPostEntity saved = postRepository.save(post);

        PostResponse response = new PostResponse();
        response.setId(saved.getId());
        response.setUserId(user.getId());
        response.setUserName(user.getName());
        response.setUserPhotoUrl(user.getProfilePhotoUrl());
        response.setCaption(saved.getCaption());
        response.setImageUrl(saved.getImageUrl());
        response.setLikesCount(0);
        response.setIsLikedByCurrentUser(false);
        response.setCommentsCount(0);
        response.setCreatedAt(saved.getCreatedAt());
        return response;
    }

    @Transactional
    public PostResponse toggleLike(String email, Long postId) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        CommunityPostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Optional<PostLikeEntity> existingLike = likeRepository.findByPostIdAndUserId(postId, user.getId());
        boolean isNowLiked;
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
            isNowLiked = false;
        } else {
            likeRepository.save(new PostLikeEntity(postId, user.getId()));
            post.setLikesCount(post.getLikesCount() + 1);
            isNowLiked = true;
        }

        CommunityPostEntity updatedPost = postRepository.save(post);

        PostResponse response = new PostResponse();
        response.setId(updatedPost.getId());
        response.setUserId(updatedPost.getUser().getId());
        response.setUserName(updatedPost.getUser().getName());
        response.setUserPhotoUrl(updatedPost.getUser().getProfilePhotoUrl());
        response.setCaption(updatedPost.getCaption());
        response.setImageUrl(updatedPost.getImageUrl());
        response.setLikesCount(updatedPost.getLikesCount());
        response.setIsLikedByCurrentUser(isNowLiked);
        response.setCommentsCount(commentRepository.findByPostIdOrderByCreatedAtAsc(postId).size());
        response.setCreatedAt(updatedPost.getCreatedAt());
        return response;
    }

    public List<CommentResponse> getComments(Long postId) {
        List<PostCommentEntity> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        return comments.stream().map(comment -> {
            CommentResponse res = new CommentResponse();
            res.setId(comment.getId());
            res.setPostId(postId);
            res.setUserId(comment.getUser().getId());
            res.setUserName(comment.getUser().getName());
            res.setUserPhotoUrl(comment.getUser().getProfilePhotoUrl());
            res.setText(comment.getText());
            res.setCreatedAt(comment.getCreatedAt());
            return res;
        }).collect(Collectors.toList());
    }

    public CommentResponse addComment(String email, Long postId, CommentRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        CommunityPostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        PostCommentEntity comment = new PostCommentEntity();
        comment.setPost(post);
        comment.setUser(user);
        comment.setText(request.getText());

        PostCommentEntity saved = commentRepository.save(comment);

        CommentResponse res = new CommentResponse();
        res.setId(saved.getId());
        res.setPostId(postId);
        res.setUserId(user.getId());
        res.setUserName(user.getName());
        res.setUserPhotoUrl(user.getProfilePhotoUrl());
        res.setText(saved.getText());
        res.setCreatedAt(saved.getCreatedAt());
        return res;
    }
}
