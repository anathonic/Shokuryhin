package anath.mumulemouton.shokuryhin
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AddActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var sqlliteHelper: SQLiteHelper
    private val sharedPreferencesFile = "Background"
    private val productCollectionRef = Firebase.firestore.collection("product")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_activity)
        val backButton = findViewById<Button>(R.id.backButton)
        val addButton = findViewById<Button>(R.id.addButton)
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
        addButton.setOnClickListener { clickAdd() }
        backButton.setOnClickListener {
            val intent0 = Intent(this, MainActivity::class.java)
            startActivity(intent0)
        }
    }


    private fun init() {
        nameEditText = findViewById(R.id.nameText)
        priceEditText = findViewById(R.id.priceText)
        quantityEditText = findViewById(R.id.quantityText)
    }

    private fun saveProductToFirebase(product: ProductModel) = CoroutineScope(Dispatchers.IO).launch {
        try {
            productCollectionRef.add(product).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddActivity, "Successfully saved data", Toast.LENGTH_LONG).show()
            }
        } catch(e: Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun clickAdd() {
        val name = nameEditText.text.toString()
        val price = (priceEditText.text.toString()).toDouble()
        val quantity = Integer.parseInt(quantityEditText.text.toString())
        val status = false

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter required field ", Toast.LENGTH_SHORT).show()
        } else {
            val std = ProductModel(name = name, price = price, quantity = quantity, status = status)
            val stat = sqlliteHelper.insertProduct(std)
            val intent = Intent()
            //intent.setPackage("com.example.receiver")
            intent.setAction("anath.mumulemouton.MyBroadcastMessage")
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            val extras = Bundle()
            extras.putString("id", std.id.toString())
            extras.putString("name", std.name)
            extras.putString("price", std.price.toString())
            extras.putString("quantity", std.quantity.toString())
            sendOrderedBroadcast(
                intent, Manifest.permission.WAKE_LOCK, SenderReceiver(),
                null, 0, "start", extras
            )
            saveProductToFirebase(std)
        }


        //BroadcastSend()
        val intent1 = Intent(this, ProductListActivity::class.java)
        startActivity(intent1)
    }

}