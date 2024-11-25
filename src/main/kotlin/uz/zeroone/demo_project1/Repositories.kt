package uz.zeroone.demo_project1

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@NoRepositoryBean
interface BaseRepository<T, BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun findByIdAndDeletedFalse(id: Long): T?
    fun trash(id: Long): T?
    fun trashList(ids: List<Long>): List<T>?
    fun findAllNotDeleted(): List<T>
    fun findAllNotDeleted(pageable: Pageable): List<T>
    fun findAllNotDeletedForPageable(pageable: Pageable): Page<T>
    fun saveAndRefresh(t: T): T
}

class BaseRepositoryImpl<T : BaseEntity>(
    entityFormation: JpaEntityInformation<T, Long>,
    private val entityManager: EntityManager
) : SimpleJpaRepository<T, Long>(entityFormation, entityManager), BaseRepository<T, BaseEntity> {

    val isNotDeletedSpecification = Specification<T> { root, _, cb ->
        cb.equal(root.get<Boolean>("deleted"), false)
    }

    override fun findByIdAndDeletedFalse(id: Long): T? =
        findByIdOrNull(id)?.takeIf { !it.deleted }

    @Transactional
    override fun trash(id: Long): T? =
        findByIdOrNull(id)?.apply {
            deleted = true
            save(this)
        }

    @Transactional
    override fun trashList(ids: List<Long>): List<T>? =
        ids.mapNotNull { trash(it) }

    override fun findAllNotDeleted(): List<T> =
        findAll(isNotDeletedSpecification)

    override fun findAllNotDeleted(pageable: Pageable): List<T> =
        findAll(isNotDeletedSpecification, pageable).content

    override fun findAllNotDeletedForPageable(pageable: Pageable): Page<T> =
        findAll(isNotDeletedSpecification, pageable)


    override fun saveAndRefresh(t: T): T =
        save(t).apply { entityManager.refresh(this) }
}



@Repository
interface UserRepository : BaseRepository<User, Long> {
    @Query(nativeQuery = true, value = """
        select * from users u where u.username=:username and u.deleted=false
    """)
    fun findByUsernameAndDeletedFalse(username: String): User?

    @Query(nativeQuery = true, value = """
        select * from users u where u.id =:id and u.deleted=false
    """)
    fun findByIdDeletedFalse(id: Long): User?


}

@Repository
interface CategoryRepository : BaseRepository<Category, Long> {
    fun findByName(name: String): Category?
}

@Repository
interface ProductRepository : BaseRepository<Product, Long> {
    fun findByName(name: String) : Product?


    @Query(nativeQuery = true, value = """
        select
    count(distinct u.*) as user_count,
    p.name as product_name,
    p.id as product_id

    from product p
join order_item ot on p.id = ot.product_id
join orders o on o.id=ot.order_id
join users u on u.id=o.user_id

where p.id=:productId
group by p.name, p.id
    """)
    fun getUserCountOrder(productId: Long) : List<ProductOrderUserCountProjection>
    @Query(nativeQuery = true, value = """
        SELECT
    p.name AS product_name,
    SUM(oi.quantity) AS product_count,
    COUNT(o.*) AS order_count,
    SUM(p.price * oi.quantity) AS total_sum
FROM users u
         LEFT JOIN orders o ON o.user_id = u.id
         LEFT JOIN order_item oi ON o.id = oi.order_id
         LEFT JOIN product p ON p.id = oi.product_id
WHERE
    o.user_id =:userId
  AND o.order_date BETWEEN :startTime AND :endTime
GROUP BY p.name, p.id;
    """)
        fun getProductUserData(
        userId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<UserProductDataProjection>

}

@Repository
interface OrderRepository : BaseRepository<Order, Long> {
    @Query(nativeQuery = true, value = """
        select *from orders o where o.user_id=:userId and o.deleted=false
    """)
    fun findAllByUserIdAndDeletedFalse(userId: Long): List<Order>

}

@Repository
interface OrderItemRepository : BaseRepository<OrderItem, Long> {

}

@Repository
interface PaymentRepository : BaseRepository<Payment, Long> {

    @Query(nativeQuery = true, value = """
        
        select * from payment p where p.user_id=:userId and p.deleted=false
        
    """)
    fun getUserPaymentsAndDeletedFalse(userId: Long): List<Payment>

    @Query(nativeQuery = true, value = """
        select
             count(*) as order_count,
             sum(p.amount) as total_sum
        from payment p
              join users u on p.user_id=u.id
              where u.id=:userId and EXTRACT(MONTH FROM p.created_date)=:month
    """)
    fun getUserMonthlyOrder(userId: Long, month: Int): List<UserOrderProjection>

}







