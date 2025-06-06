package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 팔로워 또는 팔로잉 목록을 클라이언트에 반환하는 DTO입니다.
 * 각 목록은 UserResponse 객체의 리스트로 구성됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponse {
    private List<UserResponse> users; // 팔로워 또는 팔로잉 사용자 목록
    private int count; // 목록의 총 개수
}
