
package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable; // 직렬화를 위해 필요합니다.

/**
 * '좋아요' 엔티티의 복합 기본 키를 나타내는 클래스입니다.
 * 이 클래스는 @Embeddable로 지정되어 다른 엔티티에 내장될 수 있습니다.
 */
@Embeddable // 이 클래스가 다른 엔티티에 내장될 수 있는 임베더블 타입임을 나타냅니다.
@Getter // Lombok: getter 메서드를 자동으로 생성합니다.
@Setter // Lombok: setter 메서드를 자동으로 생성합니다.
@NoArgsConstructor // Lombok: 기본 생성자를 자동으로 생성합니다.
@AllArgsConstructor // Lombok: 모든 필드를 인자로 받는 생성자를 자동으로 생성합니다.
@EqualsAndHashCode // Lombok: equals()와 hashCode() 메서드를 자동으로 생성합니다. 복합 키에서는 필수적입니다.
public class LikeId implements Serializable {

    @Column(name = "user_number") // 'likes' 테이블의 'user_number' 컬럼과 매핑됩니다.
    private Integer userNumber;

    @Column(name = "post_id") // 'likes' 테이블의 'post_id' 컬럼과 매핑됩니다. (리뷰 ID)
    private Integer postId;
}
