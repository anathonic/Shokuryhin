package anath.mumulemouton.shokuryhin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProductListActivity : AppCompatActivity() {
    private lateinit var sqlliteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter: ProductAdapter? = null
    private var std: ProductModel? = null
    private val sharedPreferencesFile = "Background"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_list_activity)
        sqlliteHelper = SQLiteHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        val background = findViewById<RelativeLayout>(R.id.background)
        var header = findViewById<RelativeLayout>(R.id.header)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPreferencesFile,
            Context.MODE_PRIVATE)
        val mode = sharedPreferences.getInt("darkMode",0)
        if(mode == 0){
            header.setBackgroundResource(R.color.gray_scale_1)
            background.setBackgroundResource(R.color.gray_scale_1)
        } else{
            header.setBackgroundResource(R.color.gray_scale_4)
            background.setBackgroundResource(R.color.gray_scale_4)
        }
        initRecyclerView()
        getProduct()
        val backButton = findViewById<Button>(R.id.backButton)
        val total = findViewById<TextView>(R.id.tvTotal)
        val checkedTotal = findViewById<TextView>(R.id.tvCheckedTotal)
        val rows = findViewById<TextView>(R.id.tvRows)
        checkedTotal.text = "Checked total: " + returnCheckedTotalPrice().toString() + "$"
        rows.text = "List products: " + returnRows().toString()
        total.text = "Total price: " + returnTotalPrice().toString() + "$"
        backButton.setOnClickListener{
            val intent0 = Intent(this, MainActivity::class.java)
            startActivity(intent0)
        }
        adapter?.setOnClickUpdateItem {
            Toast.makeText(this,it.name, Toast.LENGTH_SHORT).show()
            std = it
            val intent = Intent(this, UpdateActivity::class.java).apply {
                putExtra("id", it.id.toString())
                putExtra("name", it.name)
                putExtra("price", it.price.toString())
                putExtra("quantity", it.quantity.toString())
                putExtra("status", it.status.toString())
            }
            startActivity(intent)
        }

        adapter?.setOnClickItem {
            Toast.makeText(this,it.name, Toast.LENGTH_SHORT).show()

       }
        adapter?.setOnClickDeleteItem {
                deleteProduct(it.id, it.name, it.price, it.quantity, it.status)
        }
        adapter?.setOnCheckItem {
            std = it
            if (it.status == true ){
                it.status = false
            } else {
                it.status = true
            }
            updateProduct(it.id,it.name,it.price,it.quantity,it.status)
            val intent2 = Intent(this, ProductListActivity::class.java)
            startActivity(intent2)
        }
    }

    private fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter()
        recyclerView.adapter = adapter

    }
    private fun getProduct(){
        val stdList = sqlliteHelper.getAllProduct()
        Log.e("ppp", "${stdList.size}")
        adapter?.addItems(stdList)
    }
    private fun returnRows(): Int {
        val stdList = sqlliteHelper.getAllProduct()
        var nullableCount = adapter?.getItemCount()
        var nonullableCount: Int = nullableCount!!
        return nonullableCount
    }
    private fun returnTotalPrice(): Double {
        val stdList = sqlliteHelper.getAllProduct()
        var nullableCount = adapter?.getTotalPrice(stdList)
        var nonullableCount: Double = nullableCount!!
        return nonullableCount
    }
    private fun returnCheckedTotalPrice(): Double{
        val stdList = sqlliteHelper.getAllProduct()
        var nullableCount = adapter?.getCheckedTotalPrice(stdList)
        var nonullableCount: Double = nullableCount!!
        return nonullableCount
    }

    private fun deleteProduct(id:Int,name:String,price:Double,quantity:Int,status:Boolean){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure?")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes"){dialog, _ ->
            sqlliteHelper.deleteProduct(id)
            dialog.dismiss()
            updateProduct(id,name,price,quantity,status)
            val intent1 = Intent(this, ProductListActivity::class.java)
            startActivity(intent1)
        }
        builder.setNegativeButton("No"){dialog, _ ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }
    private fun updateProduct(id:Int,name:String,price: Double,quantity: Int,status: Boolean){
        if(std == null) return
        val std = ProductModel(id = id, name = name, price = price, quantity = quantity, status = status)
        Log.d("ppp", "$name")
        val stat = sqlliteHelper.updateProduct(std)
        if (stat > -1 ){
            getProduct()
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
        }

    }




}