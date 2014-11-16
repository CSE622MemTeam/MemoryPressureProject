package edu.buffalo.memlib.swap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.os.Environment;

public final class SwapUtil {
	public static final String FILE_PREPEND = "edu.buffalo.swap.";
	
	/**
	 * Deletes a swap file on internal storage.
	 * 
	 * @param id - file id
	 */
	public static void deleteFile(long id) {
		deleteFile(id, true);
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
			context.deleteFile(FILE_PREPEND + id);
		}
		else {
			File file = new File(context.getExternalFilesDir(null), FILE_PREPEND + id);
			file.delete();
		}
	}
	
	/**
	 * Acquires the swap file output stream on internal storage.
	 * 
	 * @param id - file id
	 * @return the swap file output stream on internal storage
	 * @throws FileNotFoundException
	 */
	public static FileOutputStream getFileOutputStream(long id) throws FileNotFoundException {
		return getFileOutputStream(id, true);
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
	 * Acquires the swap file input stream on internal storage.
	 * 
	 * @param id - file id
	 * @return the swap file input stream on internal storage
	 * @throws FileNotFoundException
	 */
	public static FileInputStream getFileInputStream(long id) throws FileNotFoundException {
		return getFileInputStream(id, true);
	}

	/**
	 * Acquires the swap file input stream on selected medium.
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
	 * Acquires the swap file object output stream on internal storage.
	 * 
	 * @param id - file id
	 * @return the swap file object output stream on internal storage
	 * @throws FileNotFoundException
	 */
	public static ObjectOutputStream getObjectOutputStream(long id) throws IOException {
		return getObjectOutputStream(id, true);
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
	 * Acquires the swap file object input stream on internal storage.
	 * 
	 * @param id - file id
	 * @return the swap file object input stream on internal storage
	 * @throws FileNotFoundException
	 */
	public static ObjectInputStream getObjectInputStream(long id) throws IOException {
		return getObjectInputStream(id, true);
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
