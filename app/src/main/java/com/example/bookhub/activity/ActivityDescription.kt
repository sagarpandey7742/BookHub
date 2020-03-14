package com.example.bookhub.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookhub.R
import com.example.bookhub.database.BookDatabase
import com.example.bookhub.database.BookEntity
import com.squareup.picasso.Picasso
import org.json.JSONObject

class ActivityDescription : AppCompatActivity() {

    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookDescription: TextView
    lateinit var btnAddToFav: Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var toolbar: Toolbar

    var bookid: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        imgBookImage = findViewById(R.id.imgBook)
        btnAddToFav = findViewById(R.id.btnAddToFavourite)
        txtBookAuthor = findViewById(R.id.txtAuthorDescription)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookDescription = findViewById(R.id.txtAboutBook)
        txtBookRating = findViewById(R.id.txtStartRating)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"


        if(intent != null){
            bookid = intent.getStringExtra("book_id")
        }
        else{
            finish()
            Toast.makeText(this@ActivityDescription , "Some error occurred", Toast.LENGTH_SHORT).show()
        }

        if(bookid == "100"){
            finish()
            Toast.makeText(this@ActivityDescription, "Some error occurred", Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(this@ActivityDescription as Context)

        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookid)


        val jsonRequest = @SuppressLint("SetTextI18n")
        object: JsonObjectRequest(Method.POST, url, jsonParams,
            Response.Listener {

                try{

                    val success = it.getBoolean("success")
                    if(success){
                        val bookJsonObject = it.getJSONObject("book_data")
                        progressLayout.visibility = View.GONE

                        val bookImgUrl: String = bookJsonObject.getString("image")

                        Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.default_book_cover).into(imgBookImage)
                        txtBookName.text = bookJsonObject.getString("name")
                        txtBookAuthor.text = bookJsonObject.getString("author")
                        txtBookPrice.text = bookJsonObject.getString("price")
                        txtBookRating.text = bookJsonObject.getString("rating")
                        txtBookDescription.text = bookJsonObject.getString("description")

                        val bookEntity = BookEntity(
                            bookid?.toInt() as Int,
                            txtBookName.text.toString(),
                            txtBookAuthor.text.toString(),
                            txtBookPrice.text.toString(),
                            txtBookRating.text.toString(),
                            txtBookDescription.text.toString(),
                            bookImgUrl
                        )

                        val checkFav = DBAsyncTask(applicationContext, bookEntity,1).execute()
                        val isFav: Boolean = checkFav.get()

                        if(isFav){
                            btnAddToFav.text = "Remove from Favourites"
                            val favColor = ContextCompat.getColor(applicationContext, R.color.colorFav)
                            btnAddToFav.setBackgroundColor(favColor)
                        }
                        else{
                            btnAddToFav.text = "Add to Favourites"
                            val notFavColor = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                            btnAddToFav.setBackgroundColor(notFavColor)
                        }


                        btnAddToFav.setOnClickListener{
                            if(!(DBAsyncTask(applicationContext, bookEntity, 1).execute().get())){

                                val async = DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                val result = async.get()

                                if(result){
                                    Toast.makeText(this@ActivityDescription, "Book added to Favourites", Toast.LENGTH_SHORT).show()

                                    btnAddToFav.text = "Remove from Favourites"
                                    val favColor = ContextCompat.getColor(applicationContext, R.color.colorFav)
                                    btnAddToFav.setBackgroundColor(favColor)
                                }
                                else{
                                    Toast.makeText(this@ActivityDescription, "Some error Occurred", Toast.LENGTH_SHORT).show()

                                }

                            }

                            else{
                                val async = DBAsyncTask(applicationContext, bookEntity, 3).execute()
                                val result = async.get()

                                if(result){
                                    Toast.makeText(this@ActivityDescription, "Book removed from Favourites", Toast.LENGTH_SHORT).show()
                                    btnAddToFav.text = "Add to Favourites"
                                    val notFavColor = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                                    btnAddToFav.setBackgroundColor(notFavColor)

                                }
                                else{
                                    Toast.makeText(this@ActivityDescription, "Some error Occurred", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    else{
                        Toast.makeText(this@ActivityDescription, "Some error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                catch (e: Exception){
                    Toast.makeText(this@ActivityDescription, "Some error occurred", Toast.LENGTH_SHORT).show()

                }

            },
            Response.ErrorListener {
                Toast.makeText(this@ActivityDescription, "Volley error $it", Toast.LENGTH_SHORT).show()

            })

        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "219bf73d98d343"
                return headers
            }
        }
        queue.add(jsonRequest)

    }

    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int): AsyncTask<Void, Void, Boolean>(){

        val db = Room.databaseBuilder(context, BookDatabase::class.java,"books-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when(mode){
                1->{
                    //to check a specific exists in the db
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null

                }

                2->{
                    // to move book to fav
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true

                }

                3->{
                    //delete book from fav
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true

                }
            }
            return false

        }

    }
}

