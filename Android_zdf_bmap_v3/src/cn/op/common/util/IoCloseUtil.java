package cn.op.common.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class IoCloseUtil {

	public static void close(Reader reader) {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void close(Writer writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}