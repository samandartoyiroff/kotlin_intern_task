package uz.zeroone.demo_project1

import jakarta.persistence.*
import jdk.jfr.Timestamp
import org.springframework.context.annotation.Description
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*


@Entity
@Table(name = "users")
class User(
    @Column(nullable = false) var username: String,
    @Column(nullable = false) var fullname: String,
    @Column(nullable = false) var password: String,
    @Column(nullable = false) var email: String,
    @Column(nullable = true) var address: String,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var roleName: RoleName
):BaseEntity()

@Entity
class Category(
    @Column(nullable = false) var name: String,
    @Column(nullable = true) var description: String,
): BaseEntity()

@Entity
class Product(
    @Column(nullable = false) var name: String,
    @Column(nullable = true) var description: String,
    @Column(nullable = true) var price: Double ,
    @Column(nullable = false) var stockAmount: Int,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "category_id") var category: Category

):BaseEntity()

@Entity
@Table(name = "orders")
class Order(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id") var user: User,
    @Column(nullable = false) @CreatedDate var orderDate: Date,
    @Column(nullable = false) var totalAmount: Double,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var status: OrderStatus
): BaseEntity(){

    constructor(user: User, status: OrderStatus) : this(
        user = user,
        orderDate = Date(),
        totalAmount = 0.0,
        status = status
    )

}

@Entity
class OrderItem(
    @ManyToOne(fetch = FetchType.LAZY) var order: Order,
    @ManyToOne(fetch = FetchType.LAZY) var product: Product,
    @Column(nullable = false) var quantity: Int,
    @Column(nullable = false) var unitPrice: Double,
    @Column(nullable = false) var totalPrice: Double,
): BaseEntity()

@Entity
class Payment(
    @ManyToOne(fetch = FetchType.LAZY) var order: Order,
    @ManyToOne(fetch = FetchType.LAZY) var user: User,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var paymentMethod: PaymentMethod,
    @Column(nullable = false) var amount: Double,
    @Column(nullable = false) @CreatedDate var paymentDate: Date,
): BaseEntity(){

    constructor(order: Order, user: User, paymentMethod: PaymentMethod, amount: Double) : this(
        order = order,
        user = user,
        paymentMethod = paymentMethod,
        amount = amount,
        paymentDate = Date() // Defaulting to the current date
    )

}



@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long?=null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) val createdDate: Date?=null,
    @Column(nullable = false) var deleted: Boolean=false
)