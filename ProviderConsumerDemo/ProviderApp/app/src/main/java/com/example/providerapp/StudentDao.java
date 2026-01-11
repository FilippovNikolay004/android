package com.example.providerapp;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

@Dao
public interface StudentDao {
    @Insert
    void insert(Student student);

    @Query("SELECT * FROM students ORDER BY lastName")
    LiveData<List<Student>> getAllStudents();

    @Query("DELETE FROM students")
    void deleteAll();

    @Query("SELECT * FROM students ORDER BY lastName")
    Cursor getAllStudentsBlocking();

    @Query("SELECT * FROM students WHERE _id = :id")
    Cursor getStudentByIdBlocking(long id);
}
