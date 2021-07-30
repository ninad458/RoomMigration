package com.enigma.roommigration.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class], version = 8, exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = MigrationFrom2To3::class),
        AutoMigration(from = 3, to = 4, spec = MigrationFrom3To4::class),
        AutoMigration(from = 4, to = 5, spec = MigrationFrom4To5::class),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7, spec = MigrationFrom6To7::class),
    ])
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    companion object {
        @Synchronized
        fun getInstance(context: Context): AppDatabase = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "migration-db"
        ).addMigrations(MigrationFrom7To8).build()
    }
}

@RenameColumn(tableName = "User", fromColumnName = "uid", toColumnName = "id")
class MigrationFrom2To3 : AutoMigrationSpec

@RenameTable(fromTableName = "User", toTableName = "students")
class MigrationFrom3To4 : AutoMigrationSpec

@RenameColumn(tableName = "users", fromColumnName = "id", toColumnName = "uid")
@RenameTable(fromTableName = "users", toTableName = "usersss")
class MigrationFrom4To5 : AutoMigrationSpec

@DeleteTable(tableName = "teachers")
class MigrationFrom6To7 : AutoMigrationSpec {
    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        super.onPostMigrate(db)

    }
}

class MigrationFrom1To2 : AutoMigrationSpec {
    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        super.onPostMigrate(db)

    }
}

object MigrationFrom7To8 : Migration(7, 8) {

    private data class UserPre(val id: Int, val name: String?, val photo: String)
    private data class UserPost(
        val id: Int,
        val firstName: String?,
        val lastName: String?,
        val photo: String,
    )

    @ExperimentalStdlibApi
    override fun migrate(db: SupportSQLiteDatabase) {
        val users = db.query("SELECT * FROM usersss").use {
            buildList {
                if (it.count == 0) return@buildList
                it.moveToFirst()
                do {
                    add(UserPre(it.getInt(it.getColumnIndex("uid")),
                        it.getString(it.getColumnIndex("name")),
                        it.getString(it.getColumnIndex("photo")).orEmpty()))
                } while (it.moveToNext())
            }
        }

        db.execSQL("CREATE TABLE IF NOT EXISTS `user_temp` (`uid` INTEGER NOT NULL, `first_name` TEXT, `last_name` TEXT, `photo` TEXT NOT NULL DEFAULT 'https://bit.ly/3laimzH', PRIMARY KEY(`uid`))")

        for ((id, name, photo) in users) {
            db.execSQL("INSERT INTO users_temp (`uid`, `first_name`, `last_name`, `photo`) VALUES ($id, '${
                name?.split(" ")?.getOrNull(0)
            }', ${name?.split(" ")?.drop(1)?.joinToString(" ")}, $photo)")
        }

        db.execSQL("DROP TABLE usersss")
        db.execSQL("ALTER TABLE user_temp RENAME TO users")
    }
}