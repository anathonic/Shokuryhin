package anath.mumulemouton.shokuryhin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UpdateActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var sqlliteHelper: SQLiteHelper
    private val sharedPreferencesFile = "Background"
    private val productCollectionRef = Firebase.firestore.collection("product")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_activity)
        val backButton = findViewById<Button>(R.id.backButton)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val background = findViewById<RelativeLayout>(R.id.background)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            sharedPreferencesFile,
            Context.MODE_PRIVATE
        )
        val mode = sharedPreferences.getInt("darkMode", 0)
        if (mode == 0) {
            background.setBackgroundResource(R.color.gray_scale_1)
        } else {
            background.setBackgroundResource(R.color.gray_scale_4)
        }
        init()
        sqlliteHelper = SQLiteHelper(this)
        updateButton.setOnClickListener {
            //clickUpdate()
            updateProductInFirebase()
        }
        backButton.setOnClickListener {
            val intent0 = Intent(this, ProductListActivity::class.java)
            startActivity(intent0)
        }

    }

    private fun Intent.getData(key: String): String {
        return extras?.getString(key) ?: "intent is null"
    }

    private fun getProduct() {
        val stdList = sqlliteHelper.getAllProduct()
        Log.e("ppp", "${stdList.size}")
    }

    private fun init() {
        nameEditText = findViewById(R.id.nameText)
        priceEditText = findViewById(R.id.priceText)
        quantityEditText = findViewById(R.id.quantityText)
        nameEditText.setText(intent.getData("name"))
        priceEditText.setText(intent.getData("price"))
        quantityEditText.setText(intent.getData("quantity"))

    }

    private fun updateProductInFirebase() = CoroutineScope(Dispatchers.IO).launch {
        val name = nameEditText.text.toString()
        val price = (priceEditText.text.toString()).toDouble()
        val quantity = Integer.parseInt(quantityEditText.text.toString())
        val id = Integer.parseInt(intent.getData("id"))
        val status = intent.getData("status").toBoolean()
        val productQuery = productCollectionRef
            .whereEqualTo("id", id)
            .get()
            .await()
        if(productQuery.documents.isNotEmpty()) {
            for(document in productQuery) {
                try {
                    productCollectionRef.document(document.id).update("name", name).await()
                    productCollectionRef.document(document.id).update("price", price).await()
                    productCollectionRef.document(document.id).update("quantity", quantity).await()
                    productCollectionRef.document(document.id).update("status", status).await()

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@UpdateActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UpdateActivity, "No persons matched the query.", Toast.LENGTH_LONG).show()
            }
        }
        goToProductList()
    }
    private fun goToProductList(){
        val intent1 = Intent(this, ProductListActivity::class.java)
        startActivity(intent1)
    }
    private fun clickUpdate() {
        val name = nameEditText.text.toString()
        val price = (priceEditText.text.toString()).toDouble()
        val quantity = Integer.parseInt(quantityEditText.text.toString())
        val id = Integer.parseInt(intent.getData("id"))
        val status = intent.getData("status").toBoolean()
        val std =
            ProductModel(id = id, name = name, price = price, quantity = quantity, status = status)
        val stat = sqlliteHelper.updateProduct(std)
        if (stat > -1) {
            getProduct()
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
        }
        val intent1 = Intent(this, ProductListActivity::class.java)
        startActivity(intent1)
    }
}
