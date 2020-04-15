package com.example.take_a_note_room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.take_a_note_room.NoteClass
import kotlinx.coroutines.CoroutineScope

@Database(entities = [NoteClass::class], version = 1, exportSchema = false)
abstract class TakeANote : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: TakeANote? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TakeANote {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TakeANote::class.java,
                    "Note_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}