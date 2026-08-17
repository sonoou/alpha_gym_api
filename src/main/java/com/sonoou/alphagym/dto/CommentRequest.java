package com.sonoou.alphagym.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentRequest {

    @NotBlank
    private String text;

    public CommentRequest() {}

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
