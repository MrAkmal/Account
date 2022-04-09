package com.example.account.payment;

import com.example.account.user.User;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;


    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String description;// why this payment for

    private Double price;

}
