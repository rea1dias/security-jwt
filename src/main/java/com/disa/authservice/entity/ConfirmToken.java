package com.disa.authservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "confirm_tokens")
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    private LocalDateTime createdTime;

    private LocalDateTime expiredTime;

    private LocalDateTime confirmedTime;
}
