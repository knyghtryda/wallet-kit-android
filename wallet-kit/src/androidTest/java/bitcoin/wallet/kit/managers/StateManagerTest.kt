package bitcoin.wallet.kit.managers

import bitcoin.wallet.kit.RealmFactoryMock
import io.realm.Realm
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StateManagerTest {

    private val factory = RealmFactoryMock()
    private lateinit var realm: Realm

    private val stateManager = StateManager(factory.realmFactory)

    @Before
    fun setUp() {
        realm = factory.realmFactory.realm

        realm.executeTransaction { it.deleteAll() }
    }

    @Test
    fun apiSynced_SetTrue() {
        stateManager.apiSynced = true

        assertTrue(stateManager.apiSynced)
    }

    @Test
    fun apiSynced_NotSet() {
        assertFalse(stateManager.apiSynced)
    }

    @Test
    fun apiSynced_Update() {
        stateManager.apiSynced = true
        assertTrue(stateManager.apiSynced)

        stateManager.apiSynced = false
        assertFalse(stateManager.apiSynced)
    }

}
