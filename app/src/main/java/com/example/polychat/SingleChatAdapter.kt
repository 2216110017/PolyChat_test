package com.example.polychat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SingleChatAdapter(
    context: Context,
    private val messages: ArrayList<SingleChatActivity.Message>,
    private val loggedInUserID: Int
) : ArrayAdapter<SingleChatActivity.Message>(context, 0, messages) {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val message = getItem(position)
        val view: View

        // 현재 사용자가 보낸 메시지인 경우 오른쪽에 표시
        if (message?.senderID == loggedInUserID) {
            view = inflater.inflate(R.layout.list_item_sent_message, parent, false)
            view.findViewById<TextView>(R.id.textMessage).text = message.messageText
        } else { // 받은 메시지인 경우 왼쪽에 표시
            view = inflater.inflate(R.layout.list_item_received_message, parent, false)
            view.findViewById<TextView>(R.id.textMessage).text = message?.messageText
        }

        return view
    }
}
