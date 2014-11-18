package edu.buffalo.memlib.swap;

import java.lang.ref.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import edu.buffalo.memlib.util.Bundler;

import static java.nio.channels.FileChannel.MapMode.*;

/**
 * A class representing a handle on an individual swap file.
 */
class SwapFile {
  /** Global swap file. */
  private static volatile SwapFile instance;

  /** The default max swap file size in MB. */
  public static int SWAP_SIZE = 1;

  /** The file used as swap space. */
  private MappedByteBuffer swapSpace;

  /** The file lock acquired when the swap file is created. */
  private FileLock lock;

  /** A reusable {@code ObjectOutputStream} wrapper. */
  private SwapObjectOutputStream soos;

  /** Create a {@code SwapFile} using the default name. */
  public SwapFile() throws IOException {
    this("swap");
  }

  /** Create a {@code SwapFile} using the given name. */
  public SwapFile(String name) throws IOException {
    this(new RandomAccessFile(name, "rw").getChannel());
  }

  /** Create a {@code SwapFile} using the given channel. */
  public SwapFile(FileChannel ch) throws IOException {
    ch.truncate(0);
    lock = ch.tryLock();
    if (lock == null)
      throw new IOException("Could not lock swap file.");
    swapSpace = ch.map(READ_WRITE, 0, ((long) SWAP_SIZE) << 20);
    soos = new SwapObjectOutputStream(swapSpace);
  }

  public static synchronized SwapFile instance() {
    if (instance == null) try {
      return instance = new SwapFile();
    } catch (Exception e) {
      e.printStackTrace();
      return instance = null;  // FIXME
    } else {
      return instance;
    }
  }

  /** Create a slice of the swap space at the given offset. */
  public ByteBuffer slice(long offset) {
    ByteBuffer buffer = swapSpace.duplicate();
    buffer.position((int)offset);
    return buffer.slice();
  }

  /**
   * Swap an object out and return its swap token.
   */
  public synchronized long put(Object object) throws IOException {
    if (object instanceof Serializable)
      return put((Serializable) object);
    return put(Bundler.bundle(object));
  }

  /**
   * Swap a serializable object out and return its swap token.
   */
  public synchronized long put(Serializable object) throws IOException {
    long token = swapSpace.position();

    soos.setBuffer(swapSpace).write(object);

    System.out.println("New token: "+token);
    return token;
  }

  /**
   * Swap the object with the given swap token back in.
   */
  public synchronized Object get(long token)
  throws IOException, ClassNotFoundException {
    if (token < 0)
      return null;

    InputStream is = new ByteBufferInputStream(slice(token));

    ObjectInputStream ois = new ObjectInputStream(is);
    Object obj = ois.readObject();
    ois.close();

    return obj;
  }

  /**
   * Free space previously allocated by a call to {@link #put(Serializable)}.
   */
  public synchronized void free(long token) {
    if (token < 0)
      return;
    // TODO
  }

  /**
   * Close the swap file.
   */
  public void close() {
    try {
      lock.release();
    } catch (Exception e) {
      // Oh well...
    }
  }
}

/**
 * A wrapper around {@code ObjectOutputStream} which can be dynamically pointed
 * to a new {@code ByteBuffer}. This allows the {@code ObjectOutputStream} to
 * be reused.
 */
class SwapObjectOutputStream {
  private ByteBufferOutputStream bbos;
  private ObjectOutputStream oos;

  public SwapObjectOutputStream(ByteBuffer buffer) throws IOException {
    bbos = new ByteBufferOutputStream(buffer);
    oos = new ObjectOutputStream(bbos);
  }

  public SwapObjectOutputStream setBuffer(ByteBuffer buffer) {
    bbos.buffer = buffer;
    return this;
  }

  public void write(Serializable object) throws IOException {
    oos.writeObject(object);
    oos.flush();
  }
}

/** An {@code OutputStream} wrapping a {@code ByteBuffer}. */
class ByteBufferOutputStream extends OutputStream {
  ByteBuffer buffer;

  public ByteBufferOutputStream(ByteBuffer buffer) {
    this.buffer = buffer;
  }

  public void write(int b) {
    buffer.put((byte) (b & 0xFF));
  }

  public void write(byte[] b, int off, int len) {
    buffer.put(b, off, len);
  }
}

/** An {@code InputStream} wrapping a {@code ByteBuffer}. */
class ByteBufferInputStream extends InputStream {
  ByteBuffer buffer;

  public ByteBufferInputStream(ByteBuffer buffer) {
    this.buffer = buffer;
  }

  public int available() {
    return buffer.remaining();
  }

  public void mark() {
    buffer.mark();
  }

  public boolean markSupported() {
    return true;
  }

  public int read() {
    if (!buffer.hasRemaining())
      return -1;
    return buffer.get() & 0xFF;
  }

  public int read(byte[] b, int off, int len) {
    if (!buffer.hasRemaining())
      return -1;
    if (len > buffer.remaining())
      len = buffer.remaining();
    buffer.get(b, off, len);
    return len;
  }

  public void reset() {
    buffer.reset();
  }
}
