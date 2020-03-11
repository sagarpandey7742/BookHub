package com.example.bookhub.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookhub.R
import com.example.bookhub.database.BookEntity

import com.squareup.picasso.Picasso


class FavouriteRecyclerAdapter(val context: Context, val bookList: List<BookEntity>): RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_favourite_single_row, parent,false)

        return FavouriteViewHolder(view)

    }

    override fun getItemCount(): Int {

        return bookList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {

        val book = bookList[position]

        holder.txtBookName.text = book.bookName
        holder.txtAuthorDescription.text = book.bookAuthor
        holder.txtBookPrice.text = book.bookPrice
        holder.txtStartRating.text = book.bookRating
        Picasso.get().load(book.bookImg).error(R.drawable.default_book_cover).into(holder.imgBook)
    }

    class FavouriteViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtBookName: TextView = view.findViewById(R.id.txtBookName)
        val txtAuthorDescription:TextView = view.findViewById(R.id.txtAuthorDescription)
        val txtBookPrice:TextView = view.findViewById(R.id.txtBookPrice)
        val imgBook:ImageView = view.findViewById(R.id.imgBook)
        val txtStartRating:TextView = view.findViewById(R.id.txtStartRating)
    }
}