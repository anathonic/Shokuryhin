package anath.mumulemouton.shokuryhin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    private val sharedPreferencesFile = "Background"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val showButton = findViewById<Button>(R.id.showButton)
        val optionsButton = findViewById<Button>(R.id.optionsButton)
        val addButton = findViewById<Button>(R.id.addButton)
        val background = findViewById<RelativeLayout>(R.id.background)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPreferencesFile,
            Context.MODE_PRIVATE)
        val mode = sharedPreferences.getInt("darkMode",0)
        if(mode == 0){
            background.setBackgroundResource(R.color.gray_scale_1)
        } else{
            background.setBackgroundResource(R.color.gray_scale_4)
        }
        showButton.setOnClickListener{
            val intent0 = Intent(this, ProductListActivity::class.java)
            startActivity(intent0)
        }
        optionsButton.setOnClickListener {
            val intent1 = Intent( this, OptionsActivity::class.java)
            startActivity(intent1)
        }
        addButton.setOnClickListener {
            val intent2 = Intent( this, AddActivity::class.java)
            startActivity(intent2)

        }
    }

}