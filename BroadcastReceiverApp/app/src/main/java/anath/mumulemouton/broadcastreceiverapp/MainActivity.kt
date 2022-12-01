package anath.mumulemouton.broadcastreceiverapp
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"
    val NOTIF_ID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intentFilter =  IntentFilter("anath.mumulemouton.MyBroadcastMessage")
        val objR = MyBroadcastReceiver();
        registerReceiver(objR,intentFilter)
        var broadcastIntent = getIntent();
        var id = intent.getStringExtra("id")
        var name = intent.getStringExtra("name")
        var price = intent.getStringExtra("price")
        var quantity = intent.getStringExtra("quantity")

        createNotifChannel()

        //val intent= Intent(this,Test::class.java)
        val intent = Intent(Intent.ACTION_MAIN);
        intent.setComponent(ComponentName("anath.mumulemouton.shokuryhin","anath.mumulemouton.shokuryhin.UpdateActivity"))
        intent.putExtra("id", id.toString())
        intent.putExtra("name", name.toString())
        intent.putExtra("price", price)
        intent.putExtra("quantity", quantity)
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setFlags(Intent.FLAG_FROM_BACKGROUND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)

            getPendingIntent(0,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }


        val notif = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Product")
            .setContentText("id:" + id + " name: " + name + " price:" + price + " quantity: " + quantity)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()


        val notifManger = NotificationManagerCompat.from(this)

        var receiveButton = findViewById<Button>(R.id.receiveButton);
        receiveButton.setOnClickListener {
            notifManger.notify(NOTIF_ID,notif)
        }






    }
    private fun createNotifChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}