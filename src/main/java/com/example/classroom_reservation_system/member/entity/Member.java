package com.example.classroom_reservation_system.member.entity;

import com.example.classroom_reservation_system.notification.entity.Notification;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "role")
@Table(name = "member", indexes = {
        @Index(name = "idx_member_uuid", columnList = "member_uuid", unique = true),
        @Index(name = "idx_member_email", columnList = "email", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@ToString(exclude = "notifications")    // 모든 연관 필드 제외
public abstract class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_uuid", updatable = false, nullable = false, unique = true, length = 36)
    private String memberUuid;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, insertable = false, updatable = false)
    private Role role;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    /**
     * UUID 생성 메서드
     */
    @PrePersist
    public void generateUUID() {
        if (memberUuid == null) {
            memberUuid = UUID.randomUUID().toString();
        }
    }

    /**
     * 비밀번호 변경 메서드
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 연관관계 편의 메서드 - 알림 추가, 제거
     */
    public void addNotifications(Notification notification){
        this.notifications.add(notification);
        notification.assignToMember(this);
    }

    public void removeNotifications(Notification notification){
        this.notifications.remove(notification);
        notification.unassignFromMember();
    }

    public abstract String getId();
}
