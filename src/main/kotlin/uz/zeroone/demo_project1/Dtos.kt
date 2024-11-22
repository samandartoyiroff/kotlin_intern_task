package uz.zeroone.demo_project1

import java.security.Timestamp
import java.util.*

data class UserCreateDto(
    val username: String,
    val password: String,
    val fullname: String,
    val email: String,
    val address: String
){
    fun toEntity(): User{
        return User(username, password, fullname, email, address)
    }
}

data class UserUpdateDto(
    val username: String,
    val password: String,
    val fullname: String,
    val email: String,
    val address: String
)

data class UserResponseDto(
    val id: Long?,
    val username: String,
    val password: String,
    val fullname: String,
    val email: String,
    val address: String
) {
    companion object {
        fun toDto(user: User): UserResponseDto {
            user.run {
                return UserResponseDto(id, username, password, fullname, email, address)
            }
        }
    }
}

data class CategoryCreateDto(
    val name: String,
    val description: String,
) {
    fun toEntity(): Category {
        return Category(name, description)
    }
}

data class CategoryUpdateDto(
    val name: String,
    val description: String,
)
data class CategoryResponseDto(
    val id: Long?,
    val name: String,
    val description: String,
) {
    companion object {
        fun toResponse(category: Category): CategoryResponseDto {
             category.run {
                return CategoryResponseDto(id, name, description)
            }
        }
    }
}

data class ProductCreateDto(
    val name: String,
    val description: String,
    val price: Double,
    val stockAmount: Int,
    val categoryId: Long
) {
    fun toEntity(category: Category): Product {
        return Product(name, description, price, stockAmount,category)
    }
}

data class ProductUpdateDto(
    val name: String,
    val description: String,
    val price: Double,
    val categoryId: Long
)

data class ProductResponseDto(
    val id: Long?,
    val name: String,
    val description: String,
    val price: Double,
    val categoryName: String
) {
    companion object {
        fun toDto(product: Product): ProductResponseDto {
            product.run {
                return ProductResponseDto(id, name, description, price, category.name)
            }
        }
    }
}

data class CreateOrderDto(
    val userId: Long,

    val payType: PaymentMethod,
    val orderItems: List<OrderItemDto>

)

data class OrderItemDto(
    val productId: Long,
    val amount: Int,
)

data class OrderResponseDto(
    val orderId: Long?,
    val totalSum: Double,
    val status: OrderStatus,
    val userId: Long?,
    val user: String,
    val createdDate: String
) {
    companion object {
        fun toDto(order: Order): OrderResponseDto {
            order.run {
                return OrderResponseDto(id, totalAmount, status, user.id, user.fullname, createdDate.toString())
            }
        }
    }
}

data class PaymentResponseDto(
    val id: Long?,
    val totalSum: Double,
    val userId: Long?,
    val user: String,
    val createdDate: String
){
    companion object {
        fun toDto(payment: Payment): PaymentResponseDto {
            payment.run {
                return PaymentResponseDto( id, amount, user.id, user.fullname, createdDate.toString())
            }
        }
    }
}

data class TimeIntervalDto(
    val startDate: Date,
    val endDate: Date,
)


















