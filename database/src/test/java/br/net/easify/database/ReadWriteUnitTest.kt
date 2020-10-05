package br.net.easify.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.net.easify.database.dao.UserDao
import br.net.easify.database.model.SqliteUser
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ReadWriteUnitTest {

    @get:Rule
    var instantTask = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
          database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testUserInsert() {
        val user: SqliteUser = SqliteUser(
            1,
            "Test User",
            "test@test.com",
            "pass",
            null,
            1,
            "2020-10-05 08:00:00",
            100,
            null,
            null
        )

        userDao.insert(user);

        val insertedUser = userDao.getLoggedUser()

        Assert.assertNotNull(insertedUser)
    }
}