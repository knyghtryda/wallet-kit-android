package bitcoin.wallet.sample

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import bitcoin.wallet.kit.WalletKit
import bitcoin.wallet.kit.hdwallet.Mnemonic
import bitcoin.wallet.kit.models.TransactionInfo
import org.jetbrains.anko.*

class MainViewModel : ViewModel(), WalletKit.Listener, AnkoLogger {

    enum class State {
        STARTED, STOPPED
    }

    val transactions = MutableLiveData<List<TransactionInfo>>()
    val balance = MutableLiveData<Long>()
    val lastBlockHeight = MutableLiveData<Int>()
    val status = MutableLiveData<State>()

    private var started = false
        set(value) {
            field = value
            status.value = (if (value) State.STARTED else State.STOPPED)
        }

    private lateinit var walletKit: WalletKit
    private var words: List<String>
    private var mnemonic = Mnemonic()

    init {
        val wordString = "ecology leader suspect expand company between baby lab ship giggle state visit uniform medal they decide surface name glow slight wonder sing bleak panic"
        words = wordString.split(" ").map{ it.trim()}
        //val words = listOf("used", "ugly", "meat", "glad", "balance", "divorce", "inner", "artwork", "hire", "invest", "already", "piano")
        generateWallet()
    }

    fun start() {
        if (started) return
        started = true

        walletKit.start()
    }

    fun receiveAddress(): String {
        return walletKit.receiveAddress()
    }

    fun send(address: String, amount: Int) {
        walletKit.send(address, amount)
    }

    override fun transactionsUpdated(walletKit: WalletKit, inserted: List<TransactionInfo>, updated: List<TransactionInfo>, deleted: List<Int>) {
        transactions.value = walletKit.transactions.asReversed()
    }

    override fun balanceUpdated(walletKit: WalletKit, balance: Long) {
        this.balance.value = balance
    }

    override fun lastBlockHeightUpdated(walletKit: WalletKit, lastBlockHeight: Int) {
        this.lastBlockHeight.value = lastBlockHeight
    }

    override fun progressUpdated(walletKit: WalletKit, progress: Double) {
        TODO("not implemented")
    }

    fun generateNewMnemonic() {
        words = mnemonic.generate(Mnemonic.Strength.VeryHigh)
        generateWallet()
    }

    private fun generateWallet() {
        walletKit = WalletKit(words, WalletKit.NetworkType.TestNet)
        walletKit.listener = this
        info(walletKit.receiveAddress())
        info(walletKit.toString())

        transactions.value = walletKit.transactions.asReversed()
        balance.value = walletKit.balance
        lastBlockHeight.value = walletKit.lastBlockHeight

        started = false
    }

    fun getWords() : List<String> {
        return words
    }
}
