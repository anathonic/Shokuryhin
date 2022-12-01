package anath.mumulemouton.shokuryhin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
//BroadcastReceiver TEST
class SenderReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {

        var resultCode = resultCode
        var resultData = resultData
        val resultExtras = getResultExtras(true)
        var name = resultExtras.getString("name")

        resultCode++

        val toast = "SenderReceiver: "+
                "Name: "+ name


        Toast.makeText(context,toast, Toast.LENGTH_LONG).show()
        resultData = "SenderReceiver"
        resultExtras.putString("name",name)
        setResult(resultCode,resultData,resultExtras)
    }
}