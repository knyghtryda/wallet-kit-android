package bitcoin.wallet.kit.models

import bitcoin.wallet.kit.network.NetworkParameters
import bitcoin.walllet.kit.io.BitcoinInput
import bitcoin.walllet.kit.io.BitcoinOutput
import bitcoin.walllet.kit.utils.NetworkUtils
import java.io.IOException
import java.net.InetAddress

/**
 * Network Address
 *
 *   Size       Field       Description
 *   ====       =====       ===========
 *   4 bytes    Time        Timestamp in seconds since the epoch
 *   8 bytes    Services    Services provided by the node
 *  16 bytes    Address     IPv6 address (IPv4 addresses are encoded as IPv6 addresses)
 *   2 bytes    Port        Port (network byte order)
 */
class NetworkAddress {

    // Uint32, the Time (version >= 31402). Not present in version message.
    var time: Long = 0

    // Uint64, same service(s) listed in version
    var services: Long = 0

    // 16 bytes IPv6 address. Network byte order. The IPv4 address is 12 bytes
    // 00 00 00 00 00 00 00 00 00 00 FF FF, followed by the 4 bytes of the IPv4 address
    lateinit var address: ByteArray

    // Uint16, port number
    var port: Int = 0

    constructor()

    @Throws(IOException::class)
    constructor(input: BitcoinInput, excludeTime: Boolean) {
        if (!excludeTime) {
            time = input.readUnsignedInt()
        }
        services = input.readLong()
        address = input.readBytes(16)
        port = input.readUnsignedShort()
    }

    constructor(addr: InetAddress, networkParameters: NetworkParameters) {
        time = System.currentTimeMillis() / 1000
        services = 1
        address = NetworkUtils.getIPv6(addr)
        port = networkParameters.port
    }

    fun toByteArray(excludeTime: Boolean): ByteArray {
        val output = BitcoinOutput()
        if (!excludeTime) {
            output.writeUnsignedInt(time)  // time
        }
        output.writeLong(services)         // service
                .write(address)            // address
                .writeUnsignedShort(port)  // port
        return output.toByteArray()
    }

    companion object {

        @Throws(IOException::class)
        fun parse(input: BitcoinInput, excludeTime: Boolean): NetworkAddress {
            val addr = NetworkAddress()
            if (!excludeTime) {
                addr.time = input.readUnsignedInt()
            }
            addr.services = input.readLong()
            addr.address = input.readBytes(16)
            addr.port = input.readUnsignedShort()

            return addr
        }
    }
}
