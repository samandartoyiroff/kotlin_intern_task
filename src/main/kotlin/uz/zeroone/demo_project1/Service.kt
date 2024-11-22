package uz.zeroone.demo_project1

import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

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
            if (user != null) throw RuntimeException("User already exists")
            val toEntity = toEntity()
            userRepository.save(toEntity)
            return ResponseEntity.ok().body("User created successfully")
        }
    }
    @Transactional
    override fun updateUser(userUpdateDto: UserUpdateDto, userId: Long): ResponseEntity<*> {
        val optUser = userRepository.findById(userId)?:throw RuntimeException("User not found")
        val user = optUser.get()
        userUpdateDto.run {
            username?.let {
                val user1 = userRepository.findByUsernameAndDeletedFalse(username)
                if (user1 != null) {
                    if (user.id != user1.id)
                    throw RuntimeException("User already exists")
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
        userRepository.trash(id)?: throw RuntimeException("User not found")
    }

    override fun getAllUsersNotDeleted(): ResponseEntity<*> {
        val users = userRepository.findAllNotDeleted()
        val toList = users.map {
            UserResponseDto.toDto(it)
        }.toList()
        return ResponseEntity.ok().body(toList)
    }

    override fun getUserById(id: Long): ResponseEntity<*> {
        val user = userRepository.findByIdDeletedFalse(id) ?: throw RuntimeException("User not found")
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
            if (category != null) throw RuntimeException("Category already exists")
            categoryRepository.save(toEntity())
            return ResponseEntity.ok("Category created successfully")
        }
    }
    @Transactional
    override fun deleteCategory(id: Long) {
        categoryRepository.trash(id)?: throw RuntimeException("Category not found")
    }

    override fun getAllCategoriesNotDeleted(): ResponseEntity<*> {
        var categoriesOpt = categoryRepository.findAllNotDeleted()
        var categories = categoriesOpt.map {
            CategoryResponseDto.toResponse(it)
        }.toList()
        return ResponseEntity.ok(categories)

    }

    override fun getCategoryById(id: Long): ResponseEntity<*> {
        var category = categoryRepository.findById(id)?: throw RuntimeException("Category not found")
        var toResponse = CategoryResponseDto.toResponse(category.get())
        return ResponseEntity.ok(toResponse)
    }
    @Transactional
    override fun updateCategory(id: Long, categoryUpdateDto: CategoryUpdateDto): ResponseEntity<*> {
        var optionalCategory = categoryRepository.findById(id)
        var category = optionalCategory.get()?:throw RuntimeException("Category not found")
        categoryUpdateDto.run {
            name?.let {
                var findByNameCategory = categoryRepository.findByName(it)
                if (findByNameCategory!=null){
                    if (category.id!=findByNameCategory.id) throw RuntimeException("Category already exists")
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
            if (product != null) throw RuntimeException("Product already exists")
            val optCategory = categoryRepository.findById(categoryId)?:throw RuntimeException("Category not found")
            val category = optCategory.get()
            val product1 = toEntity(category)
            productRepository.save(product1)
            return ResponseEntity.ok("Product created successfully")
        }
    }

    @Transactional
    override fun deleteProduct(id: Long) {
        productRepository.trash(id)?: throw RuntimeException("Product not found")
    }

    override fun getAllProductsNotDeleted(): ResponseEntity<*> {
        val productsNotDeleted = productRepository.findAllNotDeleted()
        val toList = productsNotDeleted.map {
            ProductResponseDto.toDto(it)
        }.toList()
        return ResponseEntity.ok().body(toList)
    }

    override fun getProductById(id: Long): ResponseEntity<*> {
        var product = productRepository.findByIdAndDeletedFalse(id)?:throw RuntimeException("Product not found")
        return ResponseEntity.ok().body(product)
    }

    @Transactional
    override fun updateProduct(id: Long, productUpdateDto: ProductUpdateDto): ResponseEntity<*> {
        var optProduct = productRepository.findByIdAndDeletedFalse(id)?:throw RuntimeException("Product not found")
        productUpdateDto.run {

            name?.let {
                val product1 = productRepository.findByName(it)
                if (product1!=null){
                    if (product1.id != optProduct.id) throw RuntimeException("Product already exists")
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
                val optCategory = categoryRepository.findById(it)?:throw RuntimeException("Category not found")
                val category = optCategory.get()?:throw RuntimeException("Category not found")
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

        val optUser = userRepository.findById(createOrderDto.userId)?: throw RuntimeException("User not found")
        val user = optUser.get()?:throw RuntimeException("User not found")
        createOrderDto.run {
            var order = Order(user,OrderStatus.PENDING)
            var totalPayment: Double = 0.0;
            orderRepository.save(order)
            for (orderItem in orderItems) {
                val productOpt = productRepository.findById(orderItem.productId)?: throw RuntimeException("Product not found")
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
        var order = orderRepository.findById(orderId)?: throw RuntimeException("Order not found")
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
        val optOrder = orderRepository.findById(orderId)?: throw RuntimeException("Order not found")
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
        val productUserData = productRepository.getProductUserData(userId, timeInterval.startDate, timeInterval.endDate)
        return ResponseEntity.ok().body(productUserData)
    }

    override fun getProductUserCount(productId: Long): ResponseEntity<*> {
        val userCountOrder = productRepository.getUserCountOrder(productId)
        return ResponseEntity.ok().body(userCountOrder)
    }

}
