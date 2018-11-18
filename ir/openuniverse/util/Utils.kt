package ir.openuniverse.util

import java.io.Closeable

/**
 * Created by [S. Mahdi Mir-Ismaili](https://mirismaili.github.io) on 1397/8/26 (17/11/2018).
 */

inline fun <T> log(thing: T) = println(thing)

// crossinline version:
inline fun <R, A : Closeable?, B : Closeable?>
        jUsing(a: A, b: B, crossinline block: (A, B) -> R): R =
    J.jUsing(a, b) { c, d -> block(c, d) }

// noinline version:
//inline fun <R, A : Closeable?, B : Closeable?>
//        jUsing(a: A, b: B, noinline block: (A, B) -> R): R = JUtil.jUsing(a, b, block)

/**
 * Based on https://github.com/FelixEngl/KotlinUsings/blob/master/Usings.kt
 */
inline fun <R, A : Closeable, B : Closeable> using(a: A, b: B, block: (A, B) -> R): R {
    var exception: Throwable? = null

    try {
        return block(a, b)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        if (exception == null) {
            a.close()
            b.close()
        } else {
            try {
                a.close()
            } catch (closeException: Throwable) {
                exception.addSuppressed(closeException)
            }
            try {
                b.close()
            } catch (closeException: Throwable) {
                exception.addSuppressed(closeException)
            }
        }
    }
}


/**
 * Based on https://medium.com/@appmattus/effective-kotlin-item-9-prefer-try-with-resources-to-try-finally-aec8c202c30a
 */
inline fun <T : Closeable?, R> Array<T>.use(block: (Array<T>) -> R): R {
    var exception: Throwable? = null

    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        when (exception) {
            null -> forEach { it?.close() }
            else -> forEach {
                try {
                    it?.close()
                } catch (closeException: Throwable) {
                    exception.addSuppressed(closeException)
                }
            }
        }
    }
}

