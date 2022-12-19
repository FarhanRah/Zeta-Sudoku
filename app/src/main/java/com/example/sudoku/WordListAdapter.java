package com.example.sudoku;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordListHolder> {
    private List<String> names = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public WordListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_list_item, parent, false);
        return new WordListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordListHolder holder, int position) {
        String currentItem = names.get(position);
        holder.listItem.setText(currentItem);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public void setNames(List<String> names) {
        this.names = names;
        notifyDataSetChanged();
    }

    public String getNameAt(int position) {
        return names.get(position);
    }

    class WordListHolder extends RecyclerView.ViewHolder{
        private TextView listItem;

        public WordListHolder(@NonNull View itemView) {
            super(itemView);
            listItem = itemView.findViewById(R.id.list_item_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position + 1);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
