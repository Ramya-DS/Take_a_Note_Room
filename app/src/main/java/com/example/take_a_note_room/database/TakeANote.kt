package com.example.take_a_note_room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.take_a_note_room.login.model.LoginDao
import com.example.take_a_note_room.login.model.LoginEntity
import kotlinx.coroutines.CoroutineScope

@Database(entities = [NoteClass::class, LoginEntity::class], version = 4, exportSchema = false)
abstract class TakeANote : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun loginDao(): LoginDao

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
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance
                return instance
            }
        }
    }
}