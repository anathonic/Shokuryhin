package anath.mumulemouton.shokuryhin

import java.util.*

data class ProductModel(
    var id: Int = getAutoId(),
    var name: String = "",
    var price: Double = 0.0,
    var quantity: Int = 0,
    var status: Boolean = false
) {
    companion object {
        fun getAutoId(): Int {
            val random = Random()
            return random.nextInt(100)
        }
    }
}