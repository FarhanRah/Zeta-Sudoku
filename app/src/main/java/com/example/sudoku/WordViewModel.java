package com.example.sudoku;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class WordViewModel extends AndroidViewModel {
    private WordRepository repository;
    private LiveData<List<Word>> allWords;
    private LiveData<List<String>> listNames;
    private LiveData<List<Word>> incorrectCount;

    public WordViewModel(@NonNull Application application) {
        super(application);
        repository = new WordRepository(application);
        allWords = repository.getAllWords();
        listNames = repository.getListNames();
        incorrectCount =repository.getIncorrectCountList();
    }

    public void insert(Word word) {
        repository.insert(word);
    }

    public void update(Word word) {
        repository.update(word);
    }

    public void delete(Word word) {
        repository.delete(word);
    }

    public void deleteAllWords() {
        repository.deleteAllWords();
    }

    public LiveData<List<Word>> getAllWords() {
        return allWords;
    }

    public LiveData<List<Word>> getListWords(String listName) {
        LiveData<List<Word>> wordList = repository.getListWords(listName);
        return wordList;
    }

    public void incrementIncorrectCount(String englishWord) {
        repository.incrementIncorrectCount(englishWord);
    }

    public LiveData<List<String>> getListNames() {
        return listNames;
    }

    public LiveData<List<Word>> getIncorrectCountList() {
        return incorrectCount;
    }

    public void deleteList(String listName) {
        repository.deleteList(listName);
    }

    public Word isNameInDb(String listName) {
        Word ret = repository.isNameInDb(listName);
        return ret;
    }
}
