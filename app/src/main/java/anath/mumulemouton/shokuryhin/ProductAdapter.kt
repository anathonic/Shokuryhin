package anath.mumulemouton.shokuryhin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var stdList: ArrayList<ProductModel> = ArrayList()
    private var onClickItem: ((ProductModel) -> Unit)? = null
    private var onCheckItem: ((ProductModel)-> Unit)? = null
    private var onClickDeleteItem: ((ProductModel) -> Unit)? = null
    private var onClickUpdateItem: ((ProductModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.product_card,parent,false)
    )
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val std = stdList[position]
        holder.bindView(std)
        holder.itemView.setOnClickListener { onClickItem?.invoke(std)}
        holder.deleteButton.setOnClickListener { onClickDeleteItem?.invoke(std) }
        holder.status.setOnClickListener { onCheckItem?.invoke(std) }
        holder.updateButton.setOnClickListener { onClickUpdateItem?.invoke(std)}
    }
    override fun getItemCount(): Int {
        return stdList.size
    }
    fun addItems(items: ArrayList<ProductModel>){
        this.stdList = items
        notifyDataSetChanged()
    }
    fun getTotalPrice(items: ArrayList<ProductModel>): Double {
        var total = 0.0
        for (i in items.indices) {
            total = total + items.get(i).price * items.get(i).quantity
        }
        return total
    }
    fun getCheckedTotalPrice(items: ArrayList<ProductModel>): Double {
        var total = 0.0
        for (i in items.indices) {
            var status = items.get(i).status
            if (status == true) {
                total = total + items.get(i).price * items.get(i).quantity
            } else {
                total = total
            }
        }
            return total
        }

    fun setOnClickItem(callback: (ProductModel) -> Unit){
        this.onClickItem = callback
    }
    fun setOnClickDeleteItem(callabck: (ProductModel)->Unit){
        this.onClickDeleteItem = callabck
    }
    fun setOnCheckItem(callabck: (ProductModel) -> Unit){
        this.onCheckItem = callabck
    }
    fun setOnClickUpdateItem(callabck: (ProductModel) -> Unit){
        this.onClickUpdateItem = callabck
    }
    class ProductViewHolder(view: View): RecyclerView.ViewHolder(view){
        private var name = view.findViewById<TextView>(R.id.tvName)
        private var price = view.findViewById<TextView>(R.id.tvPrice)
        private var quality = view.findViewById<TextView>(R.id.tvQuantity)
        var status = view.findViewById<CheckBox>(R.id.status)
        var deleteButton = view.findViewById<Button>(R.id.deleteButton)
        var updateButton = view.findViewById<Button>(R.id.updateButton)
        fun bindView(std:ProductModel){
            name.text = std.name
            price.text = std.price.toString()
            quality.text = std.quantity.toString()
            status.isChecked = std.status




        }
    }
}