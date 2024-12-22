package br.com.mateus.payflow.domain.charge.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.enums.charge.ChargeStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "charges")
public class ChargeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "payee_id", nullable = false)
    private UserEntity payee;

    @ManyToOne
    @JoinColumn(name = "payer_id", nullable = false)
    private UserEntity payer;

    @Column(nullable = false)
    private Double amount;

    private String description;

    @Enumerated(EnumType.STRING)
    private ChargeStatus status = ChargeStatus.PENDING;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @CreationTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

}
