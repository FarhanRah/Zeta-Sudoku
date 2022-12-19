package com.example.sudoku;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DifficultWordsAdapter extends RecyclerView.Adapter<DifficultWordsAdapter.WordListHolder> {
    private List<Word> words = new ArrayList<>();

    @NonNull
    @Override
    public WordListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.difficult_words_item, parent, false);
        return new WordListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordListHolder holder, int position) {
        Word currentItem = words.get(position);
        holder.englishWord.setText(currentItem.getEnglish());
        holder.frenchWord.setText(currentItem.getFrench());
        holder.count.setText(String.valueOf(currentItem.getIncorrectCount()));

        // change width of bar
        int layoutWidth = holder.frenchWord.getLayoutParams().width;
        int sum = 0;
        for (int i = 0; i < getItemCount(); i++) {
            sum += words.get(i).getIncorrectCount();
        }

        ViewGroup.LayoutParams layoutParams = holder.bar.getLayoutParams();
        float newWidth = (float)(currentItem.getIncorrectCount() * currentItem.getIncorrectCount()) / (float) sum;
        newWidth = (newWidth > 1) ? layoutWidth : (layoutWidth * newWidth);
        layoutParams.width = (int) newWidth;
        holder.bar.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        int limit = 9;

        if (words.size() > limit) {
            return limit;
        } else {
            return words.size();
        }
    }

    public void setWords(List<Word> words) {
        this.words = words;
        notifyDataSetChanged();
    }

    public Word getNameAt(int position) {
        return words.get(position);
    }

    class WordListHolder extends RecyclerView.ViewHolder{
        private TextView englishWord;
        private TextView frenchWord;
        private TextView count;
        private TextView bar;

        public WordListHolder(@NonNull View itemView) {
            super(itemView);
            englishWord = itemView.findViewById(R.id.text_view_english);
            frenchWord = itemView.findViewById(R.id.text_view_french);
            count = itemView.findViewById(R.id.text_view_count);
            bar = itemView.findViewById(R.id.text_view_bar);
        }
    }
}
