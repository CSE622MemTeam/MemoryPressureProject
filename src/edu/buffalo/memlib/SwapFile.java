//package edu.buffalo.memlib;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import static java.nio.channels.FileChannel.MapMode.*;

class SwapFile {
  /** Global swap file. */
  private static volatile SwapFile instance;

  /** The default max swap file size in MB. */
  public static int SWAP_SIZE = 256;

  /** The file used as swap space. */
  private MappedByteBuffer swapSpace;

  /** The file lock acquired when the swap file is created. */
  private FileLock lock;

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
  }

  public static synchronized SwapFile instance() {
    if (instance == null) try {
      return instance = new SwapFile();
    } catch (Exception e) {
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
  public synchronized long put(Serializable object) throws IOException {
    long token = swapSpace.position();
    OutputStream os = new ByteBufferOutputStream(swapSpace);

    ObjectOutputStream oos = new ObjectOutputStream(os);
    oos.writeObject(object);
    oos.close();

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

/** An {@code OutputStream} wrapping a {@code ByteBuffer}. */
class ByteBufferOutputStream extends OutputStream {
  private ByteBuffer buffer;

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
  private ByteBuffer buffer;

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
