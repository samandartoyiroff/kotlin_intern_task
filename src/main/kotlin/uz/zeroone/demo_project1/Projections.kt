package uz.zeroone.demo_project1

interface UserOrderProjection {
    val orderCount: Int
    val totalSum: Double
}

interface UserProductDataProjection {
    val productName: String
    val productCount: Int
    val orderCount: Int
    val totalSum: Double
}

interface ProductOrderUserCountProjection {
    val userCount: Int
    val productName: String
    val productId: Long
}