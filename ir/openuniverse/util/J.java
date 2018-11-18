package ir.openuniverse.util;

import kotlin.jvm.functions.Function2;

/**
 * Created by <a href="https://mirismaili.github.io">S. Mahdi Mir-Ismaili</a> on 1397/8/27 (18/11/2018).
 */
public class J {
	public static <R, A extends AutoCloseable, B extends AutoCloseable> R
	jUsing(A a, B b, Function2<A, B, R> block) throws Exception {
		try (a; b) {
			return block.invoke(a, b);
		}
	}
}
