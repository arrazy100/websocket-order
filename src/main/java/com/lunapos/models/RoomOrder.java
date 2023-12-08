package com.lunapos.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "room_order")
public class RoomOrder extends PanacheEntityBase implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    public UUID id;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now(ZoneOffset.UTC);

    @Column(name = "expired_in_seconds", nullable = false)
    public Integer expiredInSeconds;

    @OneToOne(mappedBy = "roomOrder", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    public OrderCart orderCart;

    @OneToMany(mappedBy = "roomOrder", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    public List<UserClient> userClients;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getExpiredInSeconds() {
        return expiredInSeconds;
    }

    public void setExpiredInSeconds(Integer expiredInSeconds) {
        this.expiredInSeconds = expiredInSeconds;
    }

    public OrderCart getOrderCart() {
        return orderCart;
    }

    public void setOrderCart(OrderCart orderCart) {
        this.orderCart = orderCart;
    }

    public List<UserClient> getUserclients() {
        return userClients;
    }

    public void setUserClients(List<UserClient> userClients) {
        this.userClients = userClients;
    }
}
