package uz.zeroone.demo_project1

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(
    basePackages = ["uz.zeroone.demo_project1"],
    repositoryBaseClass = BaseRepositoryImpl::class // Custom implementatsiyani bog‘lash
)
class DemoProject1Application

fun main(args: Array<String>) {
    runApplication<DemoProject1Application>(*args)
}
