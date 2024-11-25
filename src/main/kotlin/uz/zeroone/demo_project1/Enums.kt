package uz.zeroone.demo_project1

enum class OrderStatus {
    PENDING,
    DELIVERED,
    FINESHED,
    CANCELLED
}

enum class PaymentMethod {
    UZCARD,
    HUMO,
    PAYME,
    CASH
}

enum class ErrorCode(val code: Int){
    USER_ALREADY_EXIST(100),
    USER_NOT_FOUND(101),

    CATEGORY_NOT_FOUND(200),
    CATEGORY_ALREADY_EXIST(201),

    PRODUCT_NOT_FOUND(300),
    PRODUCT_ALREADY_EXIST(301),

    ORDER_NOT_FOUND(400),

    PATTERN_NOT_MATCHED(500)


}