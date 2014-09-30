package cn.op.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

public class FileUtils {

	private static final int IO_BUFFER_SIZE = 8 * 1024;
	private static String TAG = Log.makeLogTag(FileUtils.class);

	/**
	 * 读取文件返回字符串
	 * 
	 * @param file
	 * @return
	 */
	public static String readTextFile(File file) {
		String line; // 用来保存每行读取的内容
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			line = reader.readLine(); // 读取第一行
			while (line != null) { // 如果 line 为空说明读完了
				sb.append(line); // 将读到的内容添加到 buffer 中
				line = reader.readLine(); // 读取下一行
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException", e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
			e.printStackTrace();
		} finally {
			IoCloseUtil.close(reader);
		}
		return sb.toString();
	}

	/**
	 * 写字符串到文件中
	 * 
	 * @param file
	 * @param data
	 * @throws IOException
	 */
	public static void writeTextFile(File file, String data) throws IOException {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			String s = data;
			fw.write(s, 0, s.length());
			fw.flush();
		} finally {
			IoCloseUtil.close(fw);
		}
	}

	/**
	 * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
	 * 
	 * @param context
	 * @param msg
	 */
	public static void write(Context context, String fileName, String content) {
		if (content == null)
			content = "";

		try {
			FileOutputStream fos = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			fos.write(content.getBytes());

			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取文本文件
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String read(Context context, String fileName) {
		try {
			FileInputStream in = context.openFileInput(fileName);
			return readInStream(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String readInStream(FileInputStream inStream) {
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}

			outStream.close();
			inStream.close();
			return outStream.toString();
		} catch (IOException e) {
			Log.i("FileTest", e.getMessage());
		}
		return null;
	}

	public static File createFile(String folderPath, String fileName) {
		File destDir = new File(folderPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return new File(folderPath, fileName + fileName);
	}

	/**
	 * 向手机写图片
	 * 
	 * @param buffer
	 * @param folder
	 * @param fileName
	 * @return
	 */
	public static boolean writeFile(byte[] buffer, String folder,
			String fileName) {
		boolean writeSucc = false;

		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);

		String folderPath = "";
		if (sdCardExist) {
			folderPath = Environment.getExternalStorageDirectory()
					+ File.separator + folder + File.separator;
		} else {
			writeSucc = false;
		}

		File fileDir = new File(folderPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		File file = new File(folderPath + fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(buffer);
			writeSucc = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return writeSucc;
	}

	/**
	 * 写文件
	 * 
	 * @param in
	 * @param outFile
	 */
	public static boolean writeFile(InputStream inputStream, File outFile) {
		boolean writeSucc = false;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);

		InputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(inputStream, IO_BUFFER_SIZE);
			out = new BufferedOutputStream(new FileOutputStream(outFile),
					IO_BUFFER_SIZE);

			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}

			writeSucc = true;
		} catch (IOException e) {
			Log.e(TAG, "Error in writeFile - " + e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (final IOException e) {
					Log.e(TAG, "Error in writeFile - " + e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (final IOException e) {
					Log.e(TAG, "Error in writeFile - " + e);
				}
			}
		}
		return sdCardExist;
	}

	/**
	 * 根据文件绝对路径获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (StringUtils.isEmpty(filePath))
			return "";
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 根据文件的绝对路径获取文件名但不包含扩展名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileNameNoFormat(String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			return "";
		}
		int point = filePath.lastIndexOf('.');
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
				point);
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat(String fileName) {
		if (StringUtils.isEmpty(fileName))
			return "";

		int point = fileName.lastIndexOf('.');
		return fileName.substring(point + 1);
	}

	/**
	 * 获取文件大小
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		long size = 0;

		File file = new File(filePath);
		if (file != null && file.exists()) {
			size = file.length();
		}
		return size;
	}

	/**
	 * 获取文件大小
	 * 
	 * @param size
	 *            字节
	 * @return
	 */
	public static String getFileSize(long size) {
		if (size <= 0)
			return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float) size / 1024;
		if (temp >= 1024) {
			return df.format(temp / 1024) + "M";
		} else {
			return df.format(temp) + "K";
		}
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获取目录文件大小
	 * 
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

	/**
	 * 获取目录文件个数
	 * 
	 * @param f
	 * @return
	 */
	public long getFileList(File dir) {
		long count = 0;
		File[] files = dir.listFiles();
		count = files.length;
		for (File file : files) {
			if (file.isDirectory()) {
				count = count + getFileList(file);// 递归
				count--;
			}
		}
		return count;
	}

	public static byte[] toBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		byte buffer[] = out.toByteArray();
		out.close();
		return buffer;
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkFileExists(String name) {
		boolean status;
		if (!name.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + name);
			status = newPath.exists();
		} else {
			status = false;
		}
		return status;

	}

	/**
	 * 计算SD卡的剩余空间
	 * 
	 * @return 返回-1，说明没有安装sd卡
	 */
	public static long getFreeDiskSpace() {
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * 新建目录
	 * 
	 * @param directoryName
	 * @return
	 */
	public static boolean createDirectory(String directoryName) {
		boolean status;
		if (!directoryName.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + directoryName);
			status = newPath.mkdir();
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * 检查是否安装SD卡
	 * 
	 * @return
	 */
	public static boolean checkSaveLocationExists() {
		String sDCardStatus = Environment.getExternalStorageState();
		boolean status;
		if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除目录(包括：目录里的所有文件)
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteDirectory(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isDirectory()) {
				String[] listfile = newPath.list();
				// delete all files within the specified directory and then
				// delete the directory
				try {
					for (int i = 0; i < listfile.length; i++) {
						File deletedFile = new File(newPath.toString() + "/"
								+ listfile[i].toString());
						deletedFile.delete();
					}
					newPath.delete();
					Log.i("DirectoryManager deleteDirectory", fileName);
					status = true;
				} catch (Exception e) {
					e.printStackTrace();
					status = false;
				}

			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isFile()) {
				try {
					Log.i("DirectoryManager deleteFile", fileName);
					newPath.delete();
					status = true;
				} catch (SecurityException se) {
					se.printStackTrace();
					status = false;
				}
			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * 获取外部sd卡路径根路径,
	 * 
	 * @return /mnt/external_sd, or /mnt/extsd, or ……
	 */
	private static String getExternalStorage2Directory() {
		// TODO 只对部分设备有效，有的设备返回的map中没有包含外部存储卡的信息
		Map<String, String> map = System.getenv();

		String extStore2 = map.get("SECOND_VOLUME_STORAGE");

		if (StringUtils.isEmpty(extStore2)) {
			File file = new File("/mnt");
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				File dir = files[i];
				if (dir.isDirectory()) {
					if (dir.getName().toLowerCase().startsWith("ext")) {
						extStore2 = dir.getAbsolutePath();
					}
				}
			}
		}
		Log.d(TAG, "getExternalStorage2Directory===" + extStore2);

		return extStore2;
	}

	/**
	 * 外部sd卡是否已挂载
	 * 
	 * @return true 已挂在
	 */
	public static boolean isExtStore2Mount() {
		String directory = getExternalStorage2Directory();

		File file = new File(directory + "/Download");

		if (file.exists()) {
			return true;
		} else {
			return file.mkdirs();
		}
	}

	/**
	 * 获取外部sd卡上的输出流
	 * 
	 * @param fileName
	 *            文件名，不能包含路径分隔符
	 * @return 输出流，对应文件 /mnt/外部sd卡/Download/cache/fileName
	 * @throws FileNotFoundException
	 */
	public static FileOutputStream openFileOutputOnExtStore2(String fileName)
			throws FileNotFoundException {
		File dir = getCacheDirOnExtStore2();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, fileName);
		return new FileOutputStream(file);
	}

	/**
	 * 获取内部sd卡上的输出流
	 * 
	 * @param fileName
	 *            文件名，不能包含路径分隔符
	 * @return 输出流，对应文件 /mnt/外部sd卡/Download/cache/fileName
	 * @throws FileNotFoundException
	 */
	public static FileOutputStream openFileOutputOnExtStore(String fileName)
			throws FileNotFoundException {
		File dir = getCacheDirOnExtStore();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, fileName);
		return new FileOutputStream(file);
	}

	/**
	 * @param fileName
	 *            文件名，不能包含路径分隔符
	 * @return 输入流，对应文件 /mnt/外部sd卡/Download/cache/fileName
	 * @throws FileNotFoundException
	 */
	public static FileInputStream openFileInputOnExtStore2(String fileName)
			throws FileNotFoundException {
		File dir = getCacheDirOnExtStore2();

		File file = new File(dir, fileName);
		return new FileInputStream(file);
	}

	/**
	 * 获取外部sd卡上的数据缓存目录
	 * 
	 * @return
	 */
	public static File getCacheDirOnExtStore2() {
		File dir = getDirOnExtStore2("/Download/cache");
		return dir;
	}

	/**
	 * 获取内部sd卡上的数据缓存目录
	 * 
	 * @return
	 */
	public static File getCacheDirOnExtStore() {
		File dir = getDirOnExtStore("/Download/cache");
		return dir;
	}

	/**
	 * 获取外部sd卡上的指定目录,不存在则创建
	 * 
	 * @param dirs
	 *            指定目录，例如 "/Download/cache"
	 * @return /mnt/外部sd卡目录/Download/cache
	 */
	public static File getDirOnExtStore2(String dirs) {
		File dir = new File(getExternalStorage2Directory() + dirs);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 获取内部sd卡上的指定目录,不存在则创建
	 * 
	 * @param dirs
	 *            指定目录，例如 "/Download/cache"
	 * @return /mnt/sdcard/Download/cache
	 */
	public static File getDirOnExtStore(String dirs) {
		File dir = Environment.getExternalStoragePublicDirectory(dirs);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 优先获取外部sd卡上的指定目录
	 * 
	 * @param dirs
	 *            指定目录，例如 "/Download/cache"
	 * @return /mnt/外部sd卡目录/Download/cache
	 */
	public static File getDirExtStore2Priority(String dirs) {
		File dir;
		if (FileUtils.isExtStore2Mount()) {
			dir = FileUtils.getDirOnExtStore2(dirs);
		} else {
			dir = FileUtils.getDirOnExtStore(dirs);
		}
		return dir;
	}

	/**
	 * 获得使用 {@link #openFileOutputOnExtStore2} 创建的一个file {@link #openFileOutput}
	 * 
	 * @param cachefile
	 * @return
	 */
	public static File getFileStreamPathOnExtStore2(String cachefile) {
		File file = new File(getCacheDirOnExtStore2(), cachefile);
		return file;
	}

	/**
	 * 搜索文件
	 * 
	 * @param dir
	 *            要搜索的目录，不搜索root目录、最多搜索10级目录
	 * @param fileNamePart
	 *            要搜索的文件名包含的关键字
	 * @return
	 */
	public static List<File> findFiles(File dir, String fileNamePart) {
		List<File> result;

		// 目录存在、不搜索root目录、最多搜索10级目录
		if (dir.exists() && dir.isDirectory()
				&& !dir.getAbsolutePath().contains("root")
				&& dir.getAbsolutePath().split("/").length < 10) {
			Log.i("fileSearch", "search in directory " + dir.getAbsolutePath());
			File[] files = dir.listFiles();

			if (files != null)
				result = new ArrayList<File>(Arrays.asList(files));
			else
				result = new ArrayList<File>();
		}

		else
			result = Collections.emptyList();

		/* Filter for interesting files. */
		List<File> filteredResult = new ArrayList<File>();
		for (File file : result) {
			// 文件路径包含搜索词
			// if (file.getAbsolutePath().toLowerCase()
			// .contains(fileNamePart.toLowerCase())) {

			// 文件名包含搜索词
			if (file.isFile()
					&& file.getName().toLowerCase()
							.contains(fileNamePart.toLowerCase())) {
				filteredResult.add(file);
			}
			// no else.
		}

		/* Search recursively. */
		List<File> furtherResults = new ArrayList<File>();
		for (File file : result) {
			if (file.isDirectory())
				furtherResults.addAll(findFiles(file, fileNamePart));
			// no else.
		}
		// end for.

		filteredResult.addAll(furtherResults);

		return filteredResult;
	}

}
