package com.example.polychat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class UserListAdapter(context: Context, private val userList: ArrayList<LoginActivity.LoginData>) :
    ArrayAdapter<LoginActivity.LoginData>(context, 0, userList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_user, parent, false)
        }

        val user = userList[position]

        val tvUserName = itemView!!.findViewById<TextView>(R.id.tvUserName)
        val tvStuNum = itemView.findViewById<TextView>(R.id.tvStuNum)

        tvUserName.text = user.stuName
        tvStuNum.text = user.stuNum

        return itemView
    }
}
