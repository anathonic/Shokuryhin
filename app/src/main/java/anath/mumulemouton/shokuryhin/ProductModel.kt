package anath.mumulemouton.shokuryhin

import java.util.*
data class ProductModel (
    var id: Int = getAutoId(),
    var name: String,
    var price: Double,
    var quantity: Int,
    var status: Boolean
){
    companion object {
    fun getAutoId():Int{
        val random = Random()
        return random.nextInt(100)
    }
}
}