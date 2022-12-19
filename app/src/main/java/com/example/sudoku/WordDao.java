package com.example.sudoku;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WordDao {

    @Insert
    void insert(Word word);

    @Update
    void update(Word word);

    @Delete
    void delete(Word word);

    @Query("DELETE FROM word_table")
    void deleteAllWords();

    @Query("SELECT * FROM word_table ORDER BY id ASC")
    LiveData<List<Word>> getAllWords();

    @Query("SELECT * FROM word_table WHERE name = :listName ORDER BY id ASC")
    LiveData<List<Word>> getListWords(String listName);

    @Query("SELECT DISTINCT name FROM word_table WHERE name != 'RANDOMLY_GENERATED'")
    LiveData<List<String>> getListNames();

    @Query("SELECT * FROM word_table WHERE id IN (SELECT MIN(id) FROM word_table GROUP BY english) AND incorrectCount >= 1 ORDER BY incorrectCount DESC")
    LiveData<List<Word>> getIncorrectCountList();

    @Query("UPDATE word_table SET incorrectCount = incorrectCount + 1 WHERE english = :englishWord")
    void incrementIncorrectCount(String englishWord);

    @Query("DELETE FROM word_table WHERE name = :listName")
    void deleteList(String listName);

    @Query("SELECT * FROM word_table WHERE name = :listName LIMIT 1")
    Word isNameInDb(String listName);
}
