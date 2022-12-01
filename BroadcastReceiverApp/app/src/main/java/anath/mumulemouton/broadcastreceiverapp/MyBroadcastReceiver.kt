package anath.mumulemouton.broadcastreceiverapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var resultCode = resultCode
        var resultData = resultData
        val resultExtras = getResultExtras(true)
        Log.i(this::class.java.name,
    " \n" + " id: " + "${getResultExtras(true).getString("id")}"+ "\n name: " + "${getResultExtras(true).getString("name")}" + "\n" +
            " price: " + "${getResultExtras(true).getString("price")}" + "\n" +
            " quantity: " + "${getResultExtras(true).getString("quantity")}"
)
        val broadcastIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("id", "${getResultExtras(true).getString("id")}")
            putExtra("name", "${getResultExtras(true).getString("name")}")
            putExtra("price", "${getResultExtras(true).getString("price")}")
            putExtra("quantity","${getResultExtras(true).getString("quantity")}")
        }
        context.startActivity(broadcastIntent)
    }
}