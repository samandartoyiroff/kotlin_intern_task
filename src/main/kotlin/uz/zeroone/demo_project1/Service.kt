package uz.zeroone.demo_project1

import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

interface UserService {
    fun createUser(userCreateDto: UserCreateDto): ResponseEntity<*>
    fun updateUser(userUpdateDto: UserUpdateDto, userId: Long): ResponseEntity<*>
    fun deleteUser(id: Long)
    fun getAllUsersNotDeleted(): ResponseEntity<*>
    fun getUserById(id: Long): ResponseEntity<*>
}

interface CategoryService {
    fun createCategory(categoryCreateDto: CategoryCreateDto): ResponseEntity<*>
    fun deleteCategory(id: Long)
    fun getAllCategoriesNotDeleted(): ResponseEntity<*>
    fun getCategoryById(id: Long): ResponseEntity<*>
    fun updateCategory(id: Long, categoryUpdateDto: CategoryUpdateDto): ResponseEntity<*>
}

interface ProductService {
    fun createProduct(productCreateDto: ProductCreateDto): ResponseEntity<*>
    fun deleteProduct(id: Long)
    fun getAllProductsNotDeleted(): ResponseEntity<*>
    fun getProductById(id: Long): ResponseEntity<*>
    fun updateProduct(id: Long, productUpdateDto: ProductUpdateDto): ResponseEntity<*>
}

interface OrderService{
    fun makeOrder(createOrderDto: CreateOrderDto): ResponseEntity<*>
    fun showOrder(): ResponseEntity<*>
    fun declineOrder(orderId: Long) : ResponseEntity<*>
    fun changeStatus(orderId: Long, status: OrderStatus): ResponseEntity<*>
    fun geTUserOrders(userId: Long): ResponseEntity<*>
}


interface PaymentService{
    fun getUserPayments(userId: Long): ResponseEntity<*>
    fun getMonthlyData(userId: Long, month: Int): ResponseEntity<*>
    fun getProductData(userId: Long, timeInterval: TimeIntervalDto): ResponseEntity<*>
    fun getProductUserCount(productId: Long): ResponseEntity<*>
}
@Service
class UserServiceImpl(
    private val userRepository: UserRepository,

    ) : UserService {
    @Transactional
    override fun createUser(userCreateDto: UserCreateDto): ResponseEntity<*> {
        userCreateDto.run {
            val user = userRepository.findByUsernameAndDeletedFalse(username)
            if (user != null) throw UserAlreadyExistsException()
            val toEntity = toEntity()
            userRepository.save(toEntity)
            return ResponseEntity.ok().body("User created successfully")
        }
    }
    @Transactional
    override fun updateUser(userUpdateDto: UserUpdateDto, userId: Long): ResponseEntity<*> {
        val optUser = userRepository.findById(userId)?:throw UserNotFoundException()
        val user = optUser.get()
        userUpdateDto.run {
            username?.let {
                val user1 = userRepository.findByUsernameAndDeletedFalse(username)
                if (user1 != null) {
                    if (user.id != user1.id)
                    throw UserAlreadyExistsException()
                }
                user.username=it
            }
            fullname?.let {
                user.fullname=it
            }
            address?.let {
                user.address=it
            }
            email?.let {
                user.email=it
            }
            userRepository.save(user)

        }
        return ResponseEntity.ok().body("User updated successfully")
    }
    @Transactional
    override fun deleteUser(id: Long) {
        userRepository.trash(id)?: throw UserNotFoundException()
    }

    override fun getAllUsersNotDeleted(): ResponseEntity<*> {
        val users = userRepository.findAllNotDeleted()
        val toList = users.map {
            UserResponseDto.toDto(it)
        }.toList()
        return ResponseEntity.ok().body(toList)
    }

    override fun getUserById(id: Long): ResponseEntity<*> {
        val user = userRepository.findByIdDeletedFalse(id) ?: throw UserNotFoundException()
        user.run {
            return ResponseEntity.ok().body(UserResponseDto.toDto(this))
        }
    }

}

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository
): CategoryService {
    @Transactional
    override fun createCategory(categoryCreateDto: CategoryCreateDto): ResponseEntity<*> {
        categoryCreateDto.run {
            val category = categoryRepository.findByName(name)
            if (category != null) throw CategoryAlreadyExistsException()
            categoryRepository.save(toEntity())
            return ResponseEntity.ok("Category created successfully")
        }
    }
    @Transactional
    override fun deleteCategory(id: Long) {
        categoryRepository.trash(id)?: throw CategoryNotFoundException()
    }

    override fun getAllCategoriesNotDeleted(): ResponseEntity<*> {
        var categoriesOpt = categoryRepository.findAllNotDeleted()
        var categories = categoriesOpt.map {
            CategoryResponseDto.toResponse(it)
        }.toList()
        return ResponseEntity.ok(categories)

    }

    override fun getCategoryById(id: Long): ResponseEntity<*> {
        var category = categoryRepository.findById(id)?: throw CategoryNotFoundException()
        var toResponse = CategoryResponseDto.toResponse(category.get())
        return ResponseEntity.ok(toResponse)
    }
    @Transactional
    override fun updateCategory(id: Long, categoryUpdateDto: CategoryUpdateDto): ResponseEntity<*> {
        var optionalCategory = categoryRepository.findById(id)
        var category = optionalCategory.get()?:throw CategoryNotFoundException()
        categoryUpdateDto.run {
            name?.let {
                var findByNameCategory = categoryRepository.findByName(it)
                if (findByNameCategory!=null){
                    if (category.id!=findByNameCategory.id) throw CategoryAlreadyExistsException()
                }
                category.name=it
            }
            description?.let {
                category.description=it
            }
            categoryRepository.save(category)
            return ResponseEntity.ok("Category updated successfully")
        }
    }

}


@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
): ProductService{
    @Transactional
    override fun createProduct(productCreateDto: ProductCreateDto): ResponseEntity<*> {
        productCreateDto.run {
            val product = productRepository.findByName(name)
            if (product != null) throw ProductAlreadyExistsException()
            val optCategory = categoryRepository.findById(categoryId)?:throw CategoryNotFoundException()
            val category = optCategory.get()
            val product1 = toEntity(category)
            productRepository.save(product1)
            return ResponseEntity.ok("Product created successfully")
        }
    }

    @Transactional
    override fun deleteProduct(id: Long) {
        productRepository.trash(id)?: throw ProductNotFoundException()
    }

    override fun getAllProductsNotDeleted(): ResponseEntity<*> {
        val productsNotDeleted = productRepository.findAllNotDeleted()
        val toList = productsNotDeleted.map {
            ProductResponseDto.toDto(it)
        }.toList()
        return ResponseEntity.ok().body(toList)
    }

    override fun getProductById(id: Long): ResponseEntity<*> {
        var product = productRepository.findByIdAndDeletedFalse(id)?:throw ProductNotFoundException()
        return ResponseEntity.ok().body(product)
    }

    @Transactional
    override fun updateProduct(id: Long, productUpdateDto: ProductUpdateDto): ResponseEntity<*> {
        var optProduct = productRepository.findByIdAndDeletedFalse(id)?:throw ProductNotFoundException()
        productUpdateDto.run {

            name?.let {
                val product1 = productRepository.findByName(it)
                if (product1!=null){
                    if (product1.id != optProduct.id) throw ProductAlreadyExistsException()
                }
                optProduct.name=it
            }
            description?.let {
                optProduct.description=it
            }
            price?.let {
                optProduct.price=it
            }
            categoryId?.let {
                val optCategory = categoryRepository.findById(it)?:throw CategoryNotFoundException()
                val category = optCategory.get()?:throw CategoryNotFoundException()
                optProduct.category=category
            }
        }
        productRepository.save(optProduct)
        return ResponseEntity.ok("Product updated successfully")
    }

}

@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository
): OrderService{
    //private final val orderItemRepository: OrderItemRepository = TODO("initialize me")

    @Transactional
    override fun makeOrder(createOrderDto: CreateOrderDto): ResponseEntity<*> {

        val optUser = userRepository.findById(createOrderDto.userId)?: throw UserNotFoundException()
        val user = optUser.get()?:throw UserNotFoundException()
        createOrderDto.run {
            var order = Order(user,OrderStatus.PENDING)
            var totalPayment: Double = 0.0;
            orderRepository.save(order)
            for (orderItem in orderItems) {
                val productOpt = productRepository.findById(orderItem.productId)?: throw ProductNotFoundException()
                val product = productOpt.get()
                val orderItem1 =
                    OrderItem(order, product, orderItem.amount, product.price, orderItem.amount * product.price)
                orderItemRepository.save(orderItem1)
                totalPayment+=orderItem.amount * product.price
            }
            val payment = Payment(order, user, createOrderDto.payType, totalPayment)
            paymentRepository.save(payment)
        }
        return ResponseEntity.ok("Order created successfully")
    }

    override fun showOrder(): ResponseEntity<*> {
        val orders = orderRepository.findAllNotDeleted()
        val orderDtos = orders.map { OrderResponseDto.toDto(it) }.toList()
        return ResponseEntity.ok(orderDtos)
    }

    @Transactional
    override fun declineOrder(orderId: Long): ResponseEntity<*> {
        var order = orderRepository.findById(orderId)?: throw OrderNotFoundException()
        if (order.isPresent){
            val order1 = order.get()
            order1.status=OrderStatus.CANCELLED
            orderRepository.save(order1)
            return ResponseEntity.ok("Order declined")
        }
        return ResponseEntity.badRequest().body("Order not found")
    }

    @Transactional
    override fun changeStatus(orderId: Long, status: OrderStatus): ResponseEntity<*> {
        val optOrder = orderRepository.findById(orderId)?: throw OrderNotFoundException()
        if (optOrder.isPresent){
            val order = optOrder.get()
            if (getIndex(status)>getIndex(order.status)){
                order.status = status
            }
            orderRepository.save(order)
        }
        return ResponseEntity.badRequest().body("Order updated successfully")
    }

    override fun geTUserOrders(userId: Long): ResponseEntity<*> {
        val userOrders = orderRepository.findAllByUserIdAndDeletedFalse(userId)
        val toList = userOrders.map { OrderResponseDto.toDto(it) }.toList()
        return ResponseEntity.ok(toList)
    }

    private fun getIndex(status: OrderStatus): Int {

        var count: Int = 0;
        for (value in OrderStatus.entries) {
            if (status.equals(value)) return count
            count++;
        }
        return count;
    }

}


@Service
class PaymentServiceImpl(
    private val paymentRepository: PaymentRepository,
    private val productRepository: ProductRepository,
): PaymentService{
    override fun getUserPayments(userId: Long): ResponseEntity<*> {
        val payments = paymentRepository.getUserPaymentsAndDeletedFalse(userId)
        val paymentDtos = payments.map { PaymentResponseDto.toDto(it) }.toList()
        return ResponseEntity.ok().body(paymentDtos)
    }

    override fun getMonthlyData(userId: Long, month: Int): ResponseEntity<*> {
       val userOrders = paymentRepository.getUserMonthlyOrder(userId, month)
       return ResponseEntity.ok().body(userOrders)
    }

    override fun getProductData(userId: Long, timeInterval: TimeIntervalDto): ResponseEntity<*> {
        if(!isMatches(timeInterval)){
            throw PatternNotMatchException()
        }
        val split1 = timeInterval.startDate.split("-")

        val startYear = Integer.parseInt(split1[0])
        val startMonth = Integer.parseInt(split1[1])
        val startDay = Integer.parseInt(split1[2])

        val split2 = timeInterval.endDate.split("-")

        val endYear = Integer.parseInt(split2[0])
        val endMonth = Integer.parseInt(split2[1])
        val endDay = Integer.parseInt(split2[2])

        val startDateTime = LocalDateTime.of(startYear, startMonth, startDay, 0, 0, 0) // Start of the day
        val endDateTime = LocalDateTime.of(endYear, endMonth, endDay, 23, 59, 59) // End of the day
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val productUserData = productRepository.getProductUserData(userId,startDateTime, endDateTime)
        println("AASSDDFF")
        println(productUserData)
        return ResponseEntity.ok().body(productUserData)
    }

    private fun isMatches(timeInterval: TimeIntervalDto): Boolean {

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return try {
            LocalDate.parse(timeInterval.startDate, formatter)
            LocalDate.parse(timeInterval.endDate, formatter)
            true
        }
        catch (e: DateTimeParseException){
            false
        }

    }

    override fun getProductUserCount(productId: Long): ResponseEntity<*> {
        val userCountOrder = productRepository.getUserCountOrder(productId)
        return ResponseEntity.ok().body(userCountOrder)
    }

}
