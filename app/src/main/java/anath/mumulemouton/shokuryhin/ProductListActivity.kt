package anath.mumulemouton.shokuryhin

import android.content.ContentValues.TAG
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ProductListActivity : AppCompatActivity() {
    private lateinit var sqlliteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter: ProductAdapter? = null
    private var std: ProductModel? = null
    private val sharedPreferencesFile = "Background"
    private lateinit var dbref: DatabaseReference
    private lateinit var  productArrayList: ArrayList<ProductModel>
    private lateinit var db: FirebaseFirestore
    private val productCollectionRef = Firebase.firestore.collection("product")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_list_activity)
        sqlliteHelper = SQLiteHelper(this)
        recyclerView = findViewById(R.id.RecyclerView)
        productArrayList = arrayListOf<ProductModel>()
        initRecyclerView()
        EventChangeListerner()
        returnFirebaseRows()
        val background = findViewById<RelativeLayout>(R.id.background)
        var header = findViewById<RelativeLayout>(R.id.header)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            sharedPreferencesFile,
            Context.MODE_PRIVATE
        )
        val mode = sharedPreferences.getInt("darkMode", 0)
        if (mode == 0) {
            header.setBackgroundResource(R.color.gray_scale_1)
            background.setBackgroundResource(R.color.gray_scale_1)
        } else {
            header.setBackgroundResource(R.color.gray_scale_4)
            background.setBackgroundResource(R.color.gray_scale_4)
        }
        val backButton = findViewById<Button>(R.id.backButton)
        //checkedTotal.text = "Checked total: " + returnCheckedTotalPrice().toString() + "$"
        //rows.text = "List products: " + returnFirebaseRows().toString()
        //total.text = "Total price: " + returnTotalPrice().toString() + "$"
        backButton.setOnClickListener {
            val intent0 = Intent(this, MainActivity::class.java)
            startActivity(intent0)
        }


        adapter?.setOnClickUpdateItem {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()

        }
        adapter?.setOnClickDeleteItem {
           // deleteProduct(it.id, it.name, it.price, it.quantity, it.status)
            deleteProductInFirebase(it.id)
        }
        adapter?.setOnCheckItem {
            std = it
            if (it.status == true) {
                it.status = false
            } else {
                it.status = true
            }
            updateProductInFirebase(it.id, it.status)
            //updateProduct(it.id, it.name, it.price, it.quantity, it.status)
            //val intent2 = Intent(this, ProductListActivity::class.java)
            //startActivity(intent2)
        }
    }

    private fun updateProductInFirebase(id: Int, status: Boolean) = CoroutineScope(Dispatchers.IO).launch {
        val productQuery = productCollectionRef
            .whereEqualTo("id", id)
            .get()
            .await()
        if(productQuery.documents.isNotEmpty()) {
            for(document in productQuery) {
                try {
                    productCollectionRef.document(document.id).update("status", status).await()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ProductListActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ProductListActivity, "No persons matched the query.", Toast.LENGTH_LONG).show()
            }
        }
        updateList()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter()
        recyclerView.adapter = adapter

    }

    private fun EventChangeListerner(){
        db = FirebaseFirestore.getInstance()
        db.collection("product").
                addSnapshotListener(object : EventListener<QuerySnapshot>{
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if(error != null) {
                            Log.e("Firestore Error", error.message.toString())
                            return
                        }
                        for (dc: DocumentChange in value?.documentChanges!!){
                            if(dc.type == DocumentChange.Type.ADDED){
                                productArrayList.add(dc.document.toObject(ProductModel::class.java))
                            }
                        }
                        adapter?.addItems(productArrayList)
                        val total = findViewById<TextView>(R.id.tvTotal)
                        val checkedTotal = findViewById<TextView>(R.id.tvCheckedTotal)
                        total.text = "Total price: " + returnTotalFirebasePrice(productArrayList).toString() + "$"
                        checkedTotal.text ="Checked total: " + returnCheckedTotalFirebasePrice(productArrayList).toString() + "$"

                    }
                })
    }


    private fun getProduct() {
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

    private fun returnFirebaseRows() {
        val collection = db.collection("product")
        val countQuery = collection.count()
        var rows = findViewById<TextView>(R.id.tvRows)
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                Log.d(TAG, "Count: ${snapshot.count}")
                rows.text = "List products: " + snapshot.count.toString()

            } else {
                Log.d(TAG, "Count failed: ", task.getException())
            }

        }
    }

    private fun returnTotalFirebasePrice(a: ArrayList<ProductModel>): Double {
        var nullableCount = adapter?.getTotalPrice(a)
        var nonullableCount: Double = nullableCount!!
        return nonullableCount
    }

    private fun returnTotalPrice(): Double {
        val stdList = sqlliteHelper.getAllProduct()
        var nullableCount = adapter?.getTotalPrice(stdList)
        var nonullableCount: Double = nullableCount!!
        return nonullableCount
    }

    private fun returnCheckedTotalPrice(): Double {
        val stdList = sqlliteHelper.getAllProduct()
        var nullableCount = adapter?.getCheckedTotalPrice(stdList)
        var nonullableCount: Double = nullableCount!!
        return nonullableCount
    }

    private fun returnCheckedTotalFirebasePrice(a: ArrayList<ProductModel>): Double {
        var nullableCount = adapter?.getCheckedTotalPrice(a)
        var nonullableCount: Double = nullableCount!!
        return nonullableCount
    }

    private fun deleteProductInFirebase(id: Int) = CoroutineScope(Dispatchers.IO).launch {
        val personQuery = productCollectionRef
            .whereEqualTo("id", id)
            .get()
            .await()
        if(personQuery.documents.isNotEmpty()) {
            for(document in personQuery) {
                try {
                    productCollectionRef.document(document.id).delete().await()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ProductListActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ProductListActivity, "No persons matched the query.", Toast.LENGTH_LONG).show()
            }
        }
        updateList()

    }
    private fun updateList(){
        val intent1 = Intent(this, ProductListActivity::class.java)
        startActivity(intent1)
    }

    private fun deleteProduct(
        id: Int,
        name: String,
        price: Double,
        quantity: Int,
        status: Boolean
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure?")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes") { dialog, _ ->
            sqlliteHelper.deleteProduct(id)
            dialog.dismiss()
            updateProduct(id, name, price, quantity, status)
            val intent1 = Intent(this, ProductListActivity::class.java)
            startActivity(intent1)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun updateProduct(
        id: Int,
        name: String,
        price: Double,
        quantity: Int,
        status: Boolean
    ) {
        if (std == null) return
        val std =
            ProductModel(id = id, name = name, price = price, quantity = quantity, status = status)
        Log.d("ppp", "$name")
        val stat = sqlliteHelper.updateProduct(std)
        if (stat > -1) {
            getProduct()
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
        }

    }


}