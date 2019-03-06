package com.example.hal9000.communitywatch;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hal9000.communitywatch.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<Message>();
    Context context;
    private Gson gson;
    private FileReaderObject fro;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void add(Message message) {
        messages.add(message);

        notifyDataSetChanged(); // to render the list we need to notify
    }

    public void getStoredData()
    {
        gson = new Gson();
        fro = new FileReaderObject();
        Type listType = new TypeToken<List<Message>>() {}.getType();
        UserObject uo = new GetUserFromFile(context).getUser();
        String messagesInFile = fro.readFromFile(context,""+uo.getId()+"_Message_File");
        if(!messagesInFile.equals(""))
        {
            messages = gson.fromJson(messagesInFile, listType);

        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        if (message.isBelongsToCurrentUser()) { // this message was sent by us so let's create a basic chat bubble on the right
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());

            holder.timeOfMessage = convertView.findViewById(R.id.timeOfMessage);
            holder.timeOfMessage.setText(message.getTimeOfMessage());
            //System.out.println("CURRENT USER");
        } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
            convertView = messageInflater.inflate(R.layout.their_message, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);

            holder.name.setText(message.getName());
            holder.messageBody.setText(message.getText());
            holder.timeOfMessage = convertView.findViewById(R.id.timeOfMessage);
            holder.timeOfMessage.setText(message.getTimeOfMessage());
            //System.out.println("OTHER USER");


        }

        return convertView;
    }

}

class MessageViewHolder {
    public TextView name;
    public TextView messageBody;
    public TextView timeOfMessage;
}