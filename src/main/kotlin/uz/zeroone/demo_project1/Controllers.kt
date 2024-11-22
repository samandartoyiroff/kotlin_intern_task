package uz.zeroone.demo_project1

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/user")
class UserController(
     val userService: UserService
){

    @PostMapping
    fun createUser(@RequestBody userCreateDto: UserCreateDto): ResponseEntity<*> {
        return userService.createUser(userCreateDto)
    }

    @GetMapping
    fun getUsers(): ResponseEntity<*> {
        return userService.getAllUsersNotDeleted()
    }
    @PutMapping("/{userId}")
    fun updateUser(@RequestBody userUpdateDto: UserUpdateDto, @PathVariable userId: Long): ResponseEntity<*> {
        println("AAAAAAAAA")
        return userService.updateUser(userUpdateDto, userId)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(id: Long) {
        return userService.deleteUser(id)
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: Long): ResponseEntity<*> {
        println("AAAAAAAAA")
        return userService.getUserById(userId)
    }

}



//-------------------------------------------------------------------------------------------------------------------------------


@RequestMapping("/api/v1/category")
@RestController
class CategoryController(
    val categoryService: CategoryService
){

    @PostMapping
    fun createCategory(@RequestBody categoryCreateDto: CategoryCreateDto): ResponseEntity<*> {
        return categoryService.createCategory(categoryCreateDto)
    }
    @PutMapping("/{categoryId}")
    fun updateCategory(@RequestBody categoryUpdateDto: CategoryUpdateDto, @PathVariable categoryId: Long): ResponseEntity<*> {
        return categoryService.updateCategory(categoryId, categoryUpdateDto)
    }

    @GetMapping
    fun getCategories(): ResponseEntity<*> {
        return categoryService.getAllCategoriesNotDeleted()
    }

    @DeleteMapping("/{categoryId}")
    fun deleteCategories(@PathVariable categoryId: Long){
         categoryService.deleteCategory(categoryId)
    }

    @GetMapping("/{categoryId}")
    fun getCategory(@PathVariable categoryId: Long): ResponseEntity<*> {
        return categoryService.getCategoryById(categoryId)
    }
}


//-------------------------------------------------------------------------------------------------------------------------------


@RestController
@RequestMapping("/api/v1/order")
class OrderController(
    val orderService: OrderService
){

    @PostMapping
    fun createOrder(@RequestBody orderDto: CreateOrderDto): ResponseEntity<*> {
        return orderService.makeOrder(orderDto)
    }

    @PatchMapping("/{orderId}")
    fun declineOrder(@PathVariable orderId: Long): ResponseEntity<*> {
        return orderService.declineOrder(orderId)
    }
    @GetMapping
    fun getOrders(): ResponseEntity<*> {
        return orderService.showOrder()
    }

    @PutMapping("/{orderId}")
    fun changeStatus(@RequestParam orderStatus: OrderStatus, @PathVariable orderId: Long): ResponseEntity<*> {
        return orderService.changeStatus(orderId, orderStatus)
    }

    @GetMapping("/user/{userId}")
    fun getUserOrders(@PathVariable userId: Long): ResponseEntity<*> {
        return orderService.geTUserOrders(userId)
    }


}

//-------------------------------------------------------------------------------------------------------------------------------


@RequestMapping("/api/v1/product")
@RestController
class ProductController(private val productService: ProductService) {

    @PostMapping
    fun createProduct(@RequestBody productCreateDto: ProductCreateDto): ResponseEntity<*> {
        return productService.createProduct(productCreateDto)
    }

    @DeleteMapping("/{productId}")
    fun deleteProduct(@PathVariable productId: Long) {
        return productService.deleteProduct(productId)
    }
    @PutMapping("/{productId}")
    fun updateProduct(@PathVariable productId: Long, @RequestBody productUpdateDto: ProductUpdateDto): ResponseEntity<*> {
        return productService.updateProduct(productId, productUpdateDto)
    }

    @GetMapping
    fun getProducts(): ResponseEntity<*> {
        return productService.getAllProductsNotDeleted()
    }


}

//-------------------------------------------------------------------------------------------------------------------------------


@RestController
@RequestMapping("/api/v1/payment")
class PaymentController(
    private val paymentService: PaymentService
){

    @GetMapping("/user/{userId}")
    fun getUser(@PathVariable userId: Long): ResponseEntity<*> {
        return paymentService.getUserPayments(userId)
    }

    @GetMapping("/month/user/{userId}")
    fun getUserMonthlyData(@PathVariable userId: Long, @RequestParam month: Int): ResponseEntity<*> {
        return paymentService.getMonthlyData(userId, month)
    }

    @GetMapping("/product/user/{userId}")
    fun getUserProduct(@PathVariable userId: Long, @RequestBody timeInterval: TimeIntervalDto ): ResponseEntity<*> {
        return paymentService.getProductData(userId, timeInterval)
    }

    @GetMapping("/user/count-order/{productId}")
    fun getUserCount(@PathVariable productId: Long): ResponseEntity<*> {
        return paymentService.getProductUserCount(productId)
    }

}



