package anath.mumulemouton.shokuryhin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class OptionsActivity : AppCompatActivity() {
    private val sharedPreferencesFile = "Background"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.options_activity)
        val backButton = findViewById<Button>(R.id.backButton)
        val darkModeButton = findViewById<Button>(R.id.darkModeButton)
        val lightModeButton = findViewById<Button>(R.id.lightModeButton)
        val background = findViewById<RelativeLayout>(R.id.background)
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE)
        val mode = sharedPreferences.getInt("darkMode", 0)
        if (mode == 0) {
            background.setBackgroundResource(R.color.gray_scale_1)
        } else {
            background.setBackgroundResource(R.color.gray_scale_4)

        }
        backButton.setOnClickListener {
            val intent0 = Intent(this, MainActivity::class.java)
            startActivity(intent0)
        }
        darkModeButton.setOnClickListener() {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putInt("darkMode", 1)
            editor.apply()
            editor.commit()
            val mode = sharedPreferences.getInt("darkMode", 0)
            if (mode == 0) {
                background.setBackgroundResource(R.color.gray_scale_1)
                Toast.makeText(this, "darkMode off", Toast.LENGTH_SHORT).show()
            } else {
                background.setBackgroundResource(R.color.gray_scale_4)
                Toast.makeText(this, "darkMode on", Toast.LENGTH_SHORT).show()

            }

        }

        lightModeButton.setOnClickListener {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putInt("darkMode", 0)
            editor.apply()
            editor.commit()
            val mode = sharedPreferences.getInt("darkMode", 0)
            if (mode == 0) {
                background.setBackgroundResource(R.color.gray_scale_1)
                Toast.makeText(this, "darkMode off", Toast.LENGTH_SHORT).show()
            } else {
                background.setBackgroundResource(R.color.gray_scale_4)
                Toast.makeText(this, "darkMode on", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@OptionsActivity, msg, Toast.LENGTH_SHORT).show()
    }
}