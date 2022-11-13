package anath.mumulemouton.shokuryhin

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class SQLiteHelper(context: Context) :
SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "product.db"
        private const val TABLE_PRODUCT = "table_product"
        private const val ID = "id"
        private const val NAME = "name"
        private const val PRICE = "price"
        private const val QUANTITY = "quantity"
        private const val STATUS = "status"
    }
    override fun onCreate(db: SQLiteDatabase?){
        val createProduct = ("CREATE TABLE " + TABLE_PRODUCT + "(" + ID + " INTEGER PRIMARY KEY,"
                + NAME + "TEXT," + PRICE + " DOUBLE," + QUANTITY + " INTEGER," + STATUS + " BOOLEAN" + ")" )
        db?.execSQL(createProduct)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCT")
    }

    fun insertProduct(std: ProductModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, std.id)
        contentValues.put(NAME, std.name)
        contentValues.put(PRICE, std.price)
        contentValues.put(QUANTITY, std.quantity)
        contentValues.put(STATUS, std.status)
        val success  = db.insert(TABLE_PRODUCT, null, contentValues)
        db.close()
        return success
    }

    fun getAllProduct(): ArrayList<ProductModel>{
        val stdList: ArrayList<ProductModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_PRODUCT"
        val db = this.readableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery,null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var name: String
        var price: Double
        var quantity: Int
        var status: Boolean

        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
                status = (cursor.getInt(cursor.getColumnIndexOrThrow("status")) == 1)
                val std = ProductModel(id = id, name = name, price = price, quantity = quantity, status = status)
                stdList.add(std)
            } while (cursor.moveToNext())
        }
        return stdList
    }
    fun deleteProduct(id: Int): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, id)
        val success = db.delete(TABLE_PRODUCT, "id=$id", null)
        db.close()
        return success
    }
    fun updateProduct(std: ProductModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, std.id)
        contentValues.put(NAME, std.name)
        contentValues.put(PRICE, std.price)
        contentValues.put(QUANTITY, std.quantity)
        contentValues.put(STATUS, std.status)
        val success = db.update(TABLE_PRODUCT, contentValues, "id=" + std.id, null)
        db.close()
        return success
    }
}