package edu.buffalo.memlib.swap;

import java.io.*;

/**
 * All swap-related operations and configurations are performed through this
 * class.
 */
public final class Swap {
  /** The default base name of the swap directory. */
  // TODO: Make this configurable.
  static String swapDirName = ".swap";

  /** Swap an object out and return its swap token. */
  public static synchronized <T> Token<T> swapOut(T t)
  throws IOException {
    Token<T> token = new Token<T>();
    SwapDirectory.open(token, true).swapOut(t);
    return token;
  }

  /** Swap the object with the given swap token back in. */
  public static synchronized <T> T swapIn(Token<T> token)
  throws IOException, ClassNotFoundException {
    T t = SwapDirectory.open(token, false).swapIn();
    SwapDirectory.free(token);
    return t;
  }

  /** Free a swapped out object, given its token. */
  public static synchronized void free(Token<?> token)
  throws IOException {
    SwapDirectory.free(token);
  }

  /** Configure the location for the swap directory. */
  public static synchronized void setRoot(String path) {
    setRoot(new File(path));
  }

  /** Configure the location for the swap directory. */
  public static synchronized void setRoot(File dir) {
    dir = new File(dir, swapDirName);
    SwapDirectory.directory(dir);
  }

  /** A token which can be used to recover a swapped object. */
  static class Token<T> {
    private static volatile long nextTokenId = 0;
    final long value = nextTokenId();

    private static synchronized long nextTokenId() {
      return nextTokenId++;
    }

    public String toString() {
      return Long.toHexString(value);
    }
  }

  private Swap() { /* Don't make me. */ }
}

/**
 * A file system location where swapped objects go. For now, the members of
 * this class are static for simplicity's sake. In the future, it should be
 * possible to manage multiple swap directories with multiple SwapDirectory
 * instances.
 */
final class SwapDirectory {
  /** The current swap location. Lazily initialized. */
  private static File directory;  // XXX: Use carefully.

  /** Open a new swap file for token. */
  static <T> SwapFile<T> open(Swap.Token<T> token, boolean create)
  throws IOException {
    File file = fileFor(token);
    return new SwapFile<T>(file, create);
  }

  /** Delete the swap file for the given token. */
  static void free(Swap.Token<?> token) {
    fileFor(token).delete();
  }

  /** Clear all the files in the swap directory. */
  static void clear() { /* TODO */ }

  /** Get the file associated with the token. */
  static private File fileFor(Swap.Token<?> token) {
    return new File(directory(), token.toString());
  }

  /** Get the swap directory. If none is set, a default will be chosen. */
  static synchronized File directory() {
    return directory(directory);
  }

  /** Set the swap directory. If dir is null, a default will be chosen. */
  static synchronized File directory(File dir) {
    return directory = (dir != null) ? dir : defaultDirectory();
  }

  /** Create and return a default swap directory location. */
  private static synchronized File defaultDirectory() {
    // TODO: Improve how we decide where the default swap directory should go.
    // This could involve finding the running app's private application storage
    // directory, finding a suitable location in internal or external storage,
    // or some other method.  For now, just create and return a new temp
    // directory.
    try {
      File tmp = File.createTempFile(Swap.swapDirName, null);
      tmp.delete();
      tmp.mkdirs();  // FIXME: Possible race condition...
      return tmp;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

/** A handle on an individual swap file. */
final class SwapFile<T> {
  private final File file;

  /** Create and open file with mode. */
  public SwapFile(File file, boolean create) throws IOException {
    if (create && !file.createNewFile())
      throw new RuntimeException("Could not create file.");
    if (!create && !file.exists())
      throw new RuntimeException("File does not exist");
    this.file = file;
  }

  /** Get the object stored in the swap file. */
  public synchronized T swapIn() throws IOException, ClassNotFoundException {
    ObjectInputStream ois =
      new ObjectInputStream(new FileInputStream(file));
    T t = (T) ois.readObject();
    ois.close();
    return t;
  }

  /** Put an object into the swap file. */
  public synchronized void swapOut(T t) throws IOException {
    ObjectOutputStream oos =
      new ObjectOutputStream(new FileOutputStream(file));
    oos.writeObject(t);
    oos.close();
  }
}
