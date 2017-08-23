package com.shopping.core.zip;


import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;

public class CompressedStream extends ServletOutputStream
{
  private ServletOutputStream out;
  private GZIPOutputStream gzip;

  public CompressedStream(ServletOutputStream out)
    throws IOException
  {
    this.out = out;
    reset();
  }

  public void close() throws IOException
  {
    this.gzip.close();
  }

  public void flush() throws IOException
  {
    this.gzip.flush();
  }

  public void write(byte[] b) throws IOException
  {
    write(b, 0, b.length);
  }

  public void write(byte[] b, int off, int len) throws IOException
  {
    this.gzip.write(b, off, len);
  }

  public void write(int b) throws IOException
  {
    this.gzip.write(b);
  }

  public void reset() throws IOException {
    this.gzip = new GZIPOutputStream(this.out);
  }
}