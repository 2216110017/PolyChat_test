package com.example.polychat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class PostAdapter(context: Context, posts: List<Post>) : ArrayAdapter<Post>(context, 0, posts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val post = getItem(position)
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_post, parent, false)
        }

        val titleTextView: TextView = itemView!!.findViewById(R.id.titleTextView)
        val authorTextView: TextView = itemView.findViewById(R.id.authorTextView)

        titleTextView.text = post?.title
        authorTextView.text = post?.author

        return itemView
    }
}
