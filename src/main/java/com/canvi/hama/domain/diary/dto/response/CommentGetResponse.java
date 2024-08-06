package com.canvi.hama.domain.diary.dto.response;

import com.canvi.hama.domain.diary.entity.Comment;

public record CommentGetResponse(Long id, String comment) {
    public CommentGetResponse(Comment comment) {
        this(comment != null ? comment.getId() : null,
                comment != null ? comment.getComment() : null);
    }}