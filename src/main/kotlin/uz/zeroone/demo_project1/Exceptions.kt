package uz.zeroone.demo_project1

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource

sealed class DemoExceptionHandler(): RuntimeException() {
    abstract fun errorCode(): ErrorCode
    open fun getArguments(): Array<Any?>?=null

    fun getErrorMessage(resourceBundle: ResourceBundleMessageSource):BaseMessage{
        val message1 = try {
            resourceBundle.getMessage(
                errorCode().name, getArguments(), LocaleContextHolder.getLocale()
            )
        }
        catch (e: Exception){
            e.message
        }

        val baseMessage = BaseMessage(errorCode().code, message1)
        return baseMessage
    }
}

class UserAlreadyExistsException: DemoExceptionHandler(){
    override fun errorCode(): ErrorCode {
        return ErrorCode.USER_ALREADY_EXIST
    }
}

class UserNotFoundException: DemoExceptionHandler(){
    override fun errorCode(): ErrorCode {
        return ErrorCode.USER_NOT_FOUND
    }
}

class CategoryNotFoundException: DemoExceptionHandler(){
    override fun errorCode(): ErrorCode {
        return ErrorCode.CATEGORY_NOT_FOUND
    }
}

class CategoryAlreadyExistsException: DemoExceptionHandler(){
    override fun errorCode(): ErrorCode {
        return ErrorCode.CATEGORY_ALREADY_EXIST
    }
}

class ProductNotFoundException: DemoExceptionHandler(){
    override fun errorCode(): ErrorCode {
        return ErrorCode.PRODUCT_NOT_FOUND
    }
}

class ProductAlreadyExistsException: DemoExceptionHandler(){
    override fun errorCode(): ErrorCode {
        return ErrorCode.PRODUCT_ALREADY_EXIST
    }
}

class OrderNotFoundException: DemoExceptionHandler(){
    override fun errorCode(): ErrorCode {
        return ErrorCode.ORDER_NOT_FOUND
    }
}

class PatternNotMatchException: DemoExceptionHandler(){
    override fun errorCode(): ErrorCode {
        return ErrorCode.PATTERN_NOT_MATCHED
    }
}


