package com.loja99.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lojas")
public class Loja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, length = 160, unique = true)
    private String slug;

    @Column(nullable = false, length = 120)
    private String ownerName;

    @Column(nullable = false, length = 120, unique = true)
    private String ownerEmail;

    @Column(nullable = false, length = 64)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String whatsapp;

    @Column(nullable = false, length = 14, unique = true)
    private String cnpj;

    @Column(nullable = false, length = 120)
    private String pixKey;

    @Column(nullable = false, length = 20)
    private String status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cep", column = @Column(name = "zip_code", nullable = false, length = 9)),
            @AttributeOverride(name = "logradouro", column = @Column(name = "street", nullable = false, length = 150)),
            @AttributeOverride(name = "numero", column = @Column(name = "street_number", nullable = false, length = 20)),
            @AttributeOverride(name = "complemento", column = @Column(name = "complement", length = 100)),
            @AttributeOverride(name = "bairro", column = @Column(name = "district", nullable = false, length = 100)),
            @AttributeOverride(name = "cidade", column = @Column(name = "city", nullable = false, length = 100)),
            @AttributeOverride(name = "estado", column = @Column(name = "state", nullable = false, length = 2))
    })
    private Endereco endereco;
}
