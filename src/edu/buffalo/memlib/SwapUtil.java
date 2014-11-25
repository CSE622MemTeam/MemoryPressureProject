package edu.buffalo.memlib.swap;

import java.io.*;

import android.content.Context;
import android.os.Environment;

/**
 * Various file system-related tasks used in swapping.
 */
final class SwapUtil {
	public static final String FILE_PREPEND = "edu.buffalo.swap.";
	
	/**
	 * Deletes a swap file on globally set medium.
	 * 
	 * @param id - file id
	 */
	public static void deleteFile(long id) {
		deleteFile(id, SwapActivity.isInternal());
	}

	/**
	 * Deletes a swap file on selected medium.
	 * 
	 * @param id - file id
	 * @param internal - true for internal storage, otherwise external
	 */
	public static void deleteFile(long id, boolean internal) {
		Context context = SwapActivity.getSwapContext();
		if (internal) {
			if (fileExists(id, internal))
				context.deleteFile(FILE_PREPEND + id);
		}
		else {
			if (fileExists(id, internal)) {
				File file = new File(context.getExternalFilesDir(null), FILE_PREPEND + id);
				file.delete();
			}
		}
	}

	/**
	 * Checks if a file exists on globally set medium.
	 * 
	 * @param id - file id
	 * @return true if file exists, otherwise false
	 */
	public static boolean fileExists(long id) {
		return fileExists(id, SwapActivity.isInternal());
	}

	/**
	 * Checks if a file exists on selected medium
	 * 
	 * @param id - file id
	 * @param internal - true for internal storage, otherwise external
	 * @return true if file exists, otherwise false
	 */
	public static boolean fileExists(long id, boolean internal) {
		Context context = SwapActivity.getSwapContext();
		if (internal)
			return context.getFileStreamPath(FILE_PREPEND + id).exists();
		else
			return new File(context.getExternalFilesDir(null), FILE_PREPEND + id).exists();
	}

	/**
	 * Acquires the swap file output stream on globally set medium.
	 * 
	 * @param id - file id
	 * @return the swap file output stream on globally set medium
	 * @throws FileNotFoundException
	 */
	public static FileOutputStream getFileOutputStream(long id) throws FileNotFoundException {
		return getFileOutputStream(id, SwapActivity.isInternal());
	}

	/**
	 * Acquires the swap file output stream on selected medium.
	 * 
	 * @param id - file id
	 * @param internal - true for internal storage, otherwise external
	 * @return the swap file output stream on selected medium
	 * @throws FileNotFoundException
	 */
	public static FileOutputStream getFileOutputStream(long id, boolean internal) throws FileNotFoundException {
		Context context = SwapActivity.getSwapContext();
		if (internal) {
			return context.openFileOutput(FILE_PREPEND + id, Context.MODE_PRIVATE);
		}
		else {
			if (isExternalStorageWritable()) {
				File file = new File(context.getExternalFilesDir(null), FILE_PREPEND + id);
				return new FileOutputStream(file);
			}
			else {
				return null;
			}
		}
	}

	/**
	 * Acquires the swap file input stream on globally set medium.
	 * 
	 * @param id - file id
	 * @return the swap file input stream on globally set medium
	 * @throws FileNotFoundException
	 */
	public static FileInputStream getFileInputStream(long id) throws FileNotFoundException {
		return getFileInputStream(id, SwapActivity.isInternal());
	}

	/**
	 * Acquires the swap file input stream on globally set medium.
	 * 
	 * @param id - file id
	 * @param internal - true for internal storage, otherwise external
	 * @return the swap file input stream on selected medium
	 * @throws FileNotFoundException
	 */
	public static FileInputStream getFileInputStream(long id, boolean internal) throws FileNotFoundException {
		Context context = SwapActivity.getSwapContext();
		if (internal) {
			return context.openFileInput(FILE_PREPEND + id);
		}
		else {
			if (isExternalStorageWritable()) {
				File file = new File(context.getExternalFilesDir(null), FILE_PREPEND + id);
				return new FileInputStream(file);
			}
			else {
				return null;
			}
		}
	}

	/**
	 * Acquires the swap file object output stream on globally set medium.
	 * 
	 * @param id - file id
	 * @return the swap file object output stream on globally set medium
	 * @throws FileNotFoundException
	 */
	public static ObjectOutputStream getObjectOutputStream(long id) throws IOException {
		return getObjectOutputStream(id, SwapActivity.isInternal());
	}

	/**
	 * Acquires the swap file object output stream on selected medium.
	 * 
	 * @param id - file id
	 * @param internal - true for internal storage, otherwise external
	 * @return the swap file object output stream on selected medium
	 * @throws FileNotFoundException
	 */
	public static ObjectOutputStream getObjectOutputStream(long id, boolean internal) throws IOException {
		FileOutputStream fos = getFileOutputStream(id, internal);
		return new ObjectOutputStream(fos);
	}

	/**
	 * Acquires the swap file object input stream on globally set medium.
	 * 
	 * @param id - file id
	 * @return the swap file object input stream on globally set medium
	 * @throws FileNotFoundException
	 */
	public static ObjectInputStream getObjectInputStream(long id) throws IOException {
		return getObjectInputStream(id, SwapActivity.isInternal());
	}

	/**
	 * Acquires the swap file object input stream on selected medium.
	 * 
	 * @param id - file id
	 * @param internal - true for internal storage, otherwise external
	 * @return the swap file object input stream on selected medium
	 * @throws FileNotFoundException
	 */
	public static ObjectInputStream getObjectInputStream(long id, boolean internal) throws IOException {
		FileInputStream fis = getFileInputStream(id, internal);
		return new ObjectInputStream(fis);
	}


	/**
	 * Checks whether external storage is available.
	 * 
	 * @return true if external storage is available, otherwise false
	 */
	public static boolean isExternalStorageWritable() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
			return true;
		else
			return false;
	}
}
