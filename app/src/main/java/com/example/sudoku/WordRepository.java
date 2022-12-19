package com.example.sudoku;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class WordRepository {
    private WordDao wordDao;
    private LiveData<List<Word>> allWords;
    private LiveData<List<String>> listNames;
    private LiveData<List<Word>> incorrectCount;

    public WordRepository(Application application) {
        WordDatabase database = WordDatabase.getInstance(application);
        wordDao = database.wordDao();
        allWords = wordDao.getAllWords();
        listNames = wordDao.getListNames();
        incorrectCount = wordDao.getIncorrectCountList();
    }

    public void insert(Word word) {
        new InsertWordAsyncTask(wordDao).execute(word);
    }

    public void update(Word word) {
        new UpdateWordAsyncTask(wordDao).execute(word);
    }

    public void delete(Word word) {
        new DeleteWordAsyncTask(wordDao).execute(word);
    }

    public void deleteAllWords() {
        new DeleteAllWordsAsyncTask(wordDao).execute();
    }

    public LiveData<List<Word>> getAllWords() {
        return allWords;
    }

    public LiveData<List<Word>> getListWords(String listName) {
        LiveData<List<Word>> wordList = wordDao.getListWords(listName);
        return wordList;
    }

    public LiveData<List<String>> getListNames() {
        return listNames;
    }

    public void deleteList(String listName) {
        wordDao.deleteList(listName);
    }

    public Word isNameInDb(String listName) {
        Word ret = wordDao.isNameInDb(listName);
        return ret;
    }

    public LiveData<List<Word>> getIncorrectCountList() {
        return incorrectCount;
    }

    public void incrementIncorrectCount(String englishWord) {
        new incrementIncorrectCountAsyncTask(wordDao).execute(englishWord);
    }

    private static class InsertWordAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        private InsertWordAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insert(words[0]);
            return null;
        }
    }

    private static class UpdateWordAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        private UpdateWordAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.update(words[0]);
            return null;
        }
    }

    private static class DeleteWordAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        private DeleteWordAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.delete(words[0]);
            return null;
        }
    }

    private static class DeleteAllWordsAsyncTask extends AsyncTask<Void, Void, Void> {
        private WordDao wordDao;

        private DeleteAllWordsAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.deleteAllWords();
            return null;
        }
    }

    private static class incrementIncorrectCountAsyncTask extends AsyncTask<String, Void, Void> {
        private WordDao wordDao;

        private incrementIncorrectCountAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }
        @Override
        protected Void doInBackground(String... strings) {
            wordDao.incrementIncorrectCount(strings[0]);
            return null;
        }
    }
}
