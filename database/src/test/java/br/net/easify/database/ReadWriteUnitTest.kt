package br.net.easify.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.net.easify.database.dao.TokenDao
import br.net.easify.database.dao.UserDao
import br.net.easify.database.model.SqliteToken
import br.net.easify.database.model.SqliteUser
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ReadWriteUnitTest {

    @get:Rule
    var instantTask = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var tokenDao: TokenDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room
            .inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userDao = database.userDao()
        tokenDao = database.tokenDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    private fun insertUser() {
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

        userDao.insert(user)
    }

    private fun insertToken() {
        val token: SqliteToken = SqliteToken("1234", "5679")
        tokenDao.insert(token)
    }

    @Test
    fun `USER - test User Insert`() {
        insertUser()
        val insertedUser = userDao.getLoggedUser()
        Assert.assertNotNull(insertedUser)
        userDao.delete()
    }

    @Test
    fun `USER - test User Delete`() {
        insertUser()
        var insertedUser = userDao.getLoggedUser()
        Assert.assertNotNull(insertedUser)
        userDao.delete()
        insertedUser = userDao.getLoggedUser()
        Assert.assertNull(insertedUser)
    }

    @Test
    fun `USER - test User Update`() {
        insertUser()
        var insertedUser = userDao.getLoggedUser()
        val newUserName = "New User Name"
        if (insertedUser != null) {
            insertedUser.user_name = newUserName
        }
        userDao.update(insertedUser)
        insertedUser = userDao.getLoggedUser()
        Assert.assertEquals(insertedUser!!.user_name, newUserName)
    }

    @Test
    fun `TOKEN - test Token Insert`() {
        insertToken()
        val token = tokenDao.get()
        Assert.assertNotNull(token)
        tokenDao.delete()
    }

    @Test
    fun `TOKEN - test Token Delete`() {
        insertToken()
        var token = tokenDao.get()
        Assert.assertNotNull(token)
        tokenDao.delete()
        token = tokenDao.get()
        Assert.assertNull(token)
    }
}