package com.example.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.bookhub.R
import com.example.bookhub.activity.ActivityDescription
import com.example.bookhub.model.book
import com.squareup.picasso.Picasso
import java.util.ArrayList


class DashboardRecyclerAdapter(val context : Context, val itemList : ArrayList<book> ): RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_item, parent, false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = itemList[position]
        holder.txtAuthorName.text = book.bookAuthor
        holder.txtBookName.text = book.bookName
        holder.txtBookPrice.text = book.bookPrice
        holder.txtBookRating.text = book.bookRating
        Picasso.get().load(book.bookImg).error(R.drawable.default_book_cover)
            .into(holder.imgBookImage)
        holder.llContext.setOnClickListener {
            val intent = Intent(context, ActivityDescription::class.java)
            intent.putExtra("book_id", book.bookid)
            context.startActivity(intent)
        }
    }

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val txtBookName : TextView = view.findViewById(R.id.txtRecyclerItem)
        val txtAuthorName : TextView = view.findViewById(R.id.txtAuthorDescription)
        val txtBookPrice : TextView = view.findViewById(R.id.txtBookPrice)
        val txtBookRating : TextView = view.findViewById(R.id.txtStartRating)
        val imgBookImage : ImageView = view.findViewById(R.id.imgBook)
        val llContext : LinearLayout = view.findViewById(R.id.llContent)
    }


}