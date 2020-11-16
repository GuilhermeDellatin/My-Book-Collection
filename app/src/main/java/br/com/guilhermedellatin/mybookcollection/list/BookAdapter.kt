package br.com.guilhermedellatin.mybookcollection.list

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import br.com.guilhermedellatin.mybookcollection.R
import br.com.guilhermedellatin.mybookcollection.model.Book

class BookAdapter(context: Context, books: List<Book>): ArrayAdapter<Book>(context, 0, books) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val book = getItem(position)
        val viewHolder = if (convertView == null) {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_book, parent, false)
            val holder = ViewHolder(view)
            view.tag = holder
            holder
        } else {
            convertView.tag as ViewHolder
        }
        viewHolder.imageView.setImageBitmap(BitmapFactory.decodeFile(book?.path))
        viewHolder.txtName.text = book?.name
        viewHolder.rtbRating.rating = book?.rating ?: 0f
        return viewHolder.view
    }

    class ViewHolder(val view: View) {
        val imageView: ImageView = view.imgView
        val txtName: TextView = view.txtName
        val rtbRating: RatingBar = view.rtbRating
    }
}