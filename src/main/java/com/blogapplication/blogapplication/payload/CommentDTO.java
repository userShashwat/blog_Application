package com.blogapplication.blogapplication.payload;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String name;
    private String email;
    private String body;
    // Notice: NO post field! Client doesn't need it
}