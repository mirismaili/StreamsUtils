package ir.openuniverse.util

import java.io.*

/**
 * Created by [S. Mahdi Mir-Ismaili](https://mirismaili.github.io) on 1397/8/26 (17/11/2018).
 */
object StreamsUtils {
	/**
	 * NOTE: This method automatically closes both streams correctly using [using] function. But if you want to do
	 * some stuffs about [outputStream] at the end, you can use [finalStep].
	 *
	 * @param   totalSize   You can set it if you know total (reminded) size of the source stream.
	 * Any negative value means you don't know it.
	 * @param   finalStep   Is optional. But is easier to use overloaded version (without this parameter)
	 * if you don't want to set it.
	 */
	@Throws(IOException::class)
	fun <T> InputStream.streamTo(
			outputStream: OutputStream,
			totalSize: Long = -1,
			finalStep: (OutputStream) -> T? = { null }): T? {
		@Suppress("NAME_SHADOWING")
		var totalSize = totalSize
		if (totalSize < 0L) totalSize = Long.MAX_VALUE

		val bufLen = Math.min(totalSize, 4 * 0x400).toInt() // 4 * 0x400 = 4KB
		val buf = ByteArray(bufLen)
		val forceFlushInterval = 200
		var readLen = 0
		var totalBytes = 0L
		var nextFlushTime = 0L

		return using(this, outputStream) { i, o ->
			while (i.read(buf, 0, buf.size).also { readLen = it } != -1) { //log("\nl  =" + buf.length + "\nreadLen=" + readLen);
				if (readLen == 0) continue

				o.write(buf, 0, readLen)  // Possible IOException (1)
				totalBytes += readLen.toLong()
				if (totalBytes > totalSize) {
					log("InputStream Overflowed! Source data probably has been changed.")
					break
				}

				if (System.currentTimeMillis() < nextFlushTime) continue
				//-------------------------------------------------------

				//if (!flushOrStopOrBoth(o, totalBytes)) break;

				nextFlushTime = System.currentTimeMillis() + forceFlushInterval
			}
			log("TotalBytes: $totalBytes")

			finalStep(o)
		}
	}

	@Throws(IOException::class)
	inline fun InputStream.streamTo(outputStream: OutputStream, totalSize: Long = -1) {
		streamTo<Unit>(outputStream, totalSize)
	}

	@Throws(IOException::class)
	fun InputStream.readAllBytes(): ByteArray {
		return this.streamTo(
				outputStream = ByteArrayOutputStream())
		{ (it as ByteArrayOutputStream).toByteArray() }!!
	}
}
