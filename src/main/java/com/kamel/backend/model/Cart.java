package com.kamel.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false, unique = true)
    private MyUser buyer;


    /**
     * @OneToMany: Declares a one-to-many relationship: one cart has many cart items. <br>
     * mappedBy = "cart" :
     * <pre>
     * • Tells JPA that the CartItem entity owns the relationship, i.e., it has a field called cart annotated with @ManyToOne.
     * • Prevents the creation of a join table (which would happen if both sides had their own mappings).
     * • You don’t want a join table here — the FK will be in the cart_item table.
     * </pre>
     * cascade = CascadeType.ALL:
     * <pre>
     * • Any operation on the Cart (persist, merge, remove) will cascade to its CartItems.
     * • Example: deleting a Cart deletes all its items automatically. <
     * </pre>
     * orphanRemoval = true: <br>
     *          • If a CartItem is removed from the items list in the Cart, it will be deleted from the database. <br>
     *          • Without this, removing it from the list just breaks the association — the row would still sit in cart_item. <br>
     * new ArrayList<>(): Initializes the list to prevent NullPointerException when adding items.<br>
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> items = new ArrayList<>();

    private LocalDateTime lastUpdated;

    @PreUpdate
    @PrePersist
    public void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }
}
