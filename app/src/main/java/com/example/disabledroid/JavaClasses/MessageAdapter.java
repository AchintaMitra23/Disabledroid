package com.example.disabledroid.JavaClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disabledroid.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.CustomViewHolder> {

    private List<ResponseMessage> responseMessageList;

    public MessageAdapter(List<ResponseMessage> responseMessageList) {
        this.responseMessageList = responseMessageList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.textView.setText(responseMessageList.get(position).getText());
    }

    @Override
    public int getItemViewType(int position) {
        if (responseMessageList.get(position).isMe())   return R.layout.me_bubble;
        else                                            return R.layout.bot_bubble;
    }

    @Override
    public int getItemCount() {
        return responseMessageList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.textMessage);
        }
    }

}
