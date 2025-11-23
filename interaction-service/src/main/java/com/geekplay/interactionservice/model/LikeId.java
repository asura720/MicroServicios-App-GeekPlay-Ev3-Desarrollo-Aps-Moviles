package com.geekplay.interactionservice.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class LikeId implements Serializable {

    private Long postId;

    private String userEmail;

}
