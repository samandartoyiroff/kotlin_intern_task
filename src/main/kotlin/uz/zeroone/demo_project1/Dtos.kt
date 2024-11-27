package uz.zeroone.demo_project1

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.security.Timestamp
import java.util.*

data class UserUpdateDto(
    val username: String,
    val password: String,
    val fullname: String,
    val email: String,
    val roleName: RoleName,
    val address: String
){

}

data class UserCreateDto(
    @field:NotBlank(message = "cannot be blank") val username: String,
    @field:NotBlank(message = "cannot be blank") val password: String,
    @field:NotBlank(message = "cannot be blank") val fullname: String,
    @field:NotBlank(message = "cannot be blank") val email: String,
    @field:NotNull(message = "cannot be null") val roleName: RoleName,
    @field:NotBlank(message = "cannot be blank") val address: String

){
    fun toEntity(): User{
        return User(username, password, fullname, email, address, roleName)
    }
}

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
    @field:NotBlank(message = "cannot be blank")val name: String,
    @field:NotBlank(message = "cannot be blank")val description: String,
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
    @field:NotBlank(message = "cannot be blank")val name: String,
    @field:NotBlank(message = "cannot be blank")val description: String,
    @field:NotNull(message = "cannot be null")val price: Double,
    @field:NotBlank(message = "cannot be null")val stockAmount: Int,
    @field:NotBlank(message = "cannot be null")val categoryId: Long
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
    @field:NotNull(message = "cannot be null")val userId: Long,

    @field:NotNull(message = "cannot be null")val payType: PaymentMethod,
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
    val startDate: String, //pattern yyyy-MM-dd
    val endDate: String,
)

data class BaseMessage(
    val code: Int,
    val message: String?
)


















