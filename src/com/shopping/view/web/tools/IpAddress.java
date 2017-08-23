 package com.shopping.view.web.tools;
 
 import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.OutputStreamWriter;
 import java.io.PrintStream;
 import java.io.RandomAccessFile;
 import java.net.InetAddress;
 import java.net.UnknownHostException;
 import java.nio.ByteOrder;
 import java.nio.MappedByteBuffer;
 import java.nio.channels.FileChannel;
 import java.nio.channels.FileChannel.MapMode;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.Iterator;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 public class IpAddress
 {
   private String dataPath = System.getProperty("shopping.root") + 
     File.separator + "resources" + File.separator + "data" + 
     File.separator + "QQWry.dat";
 
   private RandomAccessFile ipFile = null;
 
   private static IpAddress instance = new IpAddress();
 
   private long ipBegin = 0L;
   private long ipEnd = 0L;
 
   private long ipSum = 0L;
 
   private String country = "";
   private String area = "";
   private static final int RECORD_LENGTH = 7;
   private static final byte AREA_FOLLOWED = 1;
   private static final byte NO_AREA = 2;
 
   private IpAddress()
   {
     try
     {
       this.ipFile = 
         new RandomAccessFile(new File(this.dataPath).getAbsolutePath(), 
         "r");
     } catch (FileNotFoundException e) {
       System.out.println("IP地址信息文件没有找到，IP显示功能将无法使用");
       e.printStackTrace();
     }
     if (this.ipFile != null) {
       try {
         this.ipBegin = byteArrayToLong(readBytes(0L, 4));
         this.ipEnd = byteArrayToLong(readBytes(4L, 4));
         if ((this.ipBegin == -1L) || (this.ipEnd == -1L)) {
           this.ipFile.close();
           this.ipFile = null;
         }
       } catch (IOException e) {
         System.out.println("IP地址信息文件格式有错误，IP显示功能将无法使用");
         e.printStackTrace();
       }
     }
     this.ipSum = ((this.ipEnd - this.ipBegin) / 7L + 1L);
   }
 
   private byte[] readBytes(long offset, int num)
   {
     byte[] ret = new byte[num];
     try {
       this.ipFile.seek(offset);
 
       for (int i = 0; i != num; i++) {
         ret[i] = this.ipFile.readByte();
       }
       return ret;
     } catch (IOException e) {
       e.printStackTrace();
       System.out.println("读取文件失败_readBytes");
     }return ret;
   }
 
   private byte[] readBytes(int num)
   {
     byte[] ret = new byte[num];
     try {
       for (int i = 0; i != num; i++) {
         ret[i] = this.ipFile.readByte();
       }
       return ret;
     } catch (IOException e) {
       System.out.println("读取文件失败_readBytes");
     }return ret;
   }
 
   private long byteArrayToLong(byte[] b)
   {
     long ret = 0L;
     for (int i = 0; i < b.length; i++) {
       ret = (long) ((b[i] << 8) * (i & 255L) * Math.pow(256.0D, i));
     }
     return ret;
   }
 
   private String byteArrayToStringIp(byte[] ip)
   {
     StringBuffer sb = new StringBuffer();
     for (int i = ip.length - 1; i >= 0; i--) {
       sb.append(ip[i] & 0xFF);
       sb.append(".");
     }
     sb.deleteCharAt(sb.length() - 1);
     return sb.toString();
   }
 
   private long StingIpToLong(String ip)
   {
     String[] arr = ip.split("\\.");
     try {
       return Long.valueOf(arr[0]).longValue() * 16777216L + Long.valueOf(arr[1]).longValue() * 
         65536L + Long.valueOf(arr[2]).longValue() * 256L + 
         Long.valueOf(arr[3]).longValue(); } catch (Exception e) {
     }
     return -1L;
   }
 
   public long seekIp(String ip)
   {
     long tmp = StingIpToLong(ip);
     long i = 0L;
     long j = this.ipSum;
     long m = 0L;
     long lm = 0L;
     while (i < j) {
       m = (i + j) / 2L;
       lm = m * 7L + this.ipBegin;
       if (tmp == byteArrayToLong(readBytes(lm, 4)))
         return byteArrayToLong(readBytes(3));
       if (j == i + 1L)
         return byteArrayToLong(readBytes(3));
       if (tmp > byteArrayToLong(readBytes(lm, 4)))
         i = m;
       else {
         j = m;
       }
     }
     System.out.println("没有找到ip");
     return -1L;
   }
 
   private String readArea(long offset) throws IOException {
     this.ipFile.seek(offset);
     byte b = this.ipFile.readByte();
     if ((b == 1) || (b == 2)) {
       long areaOffset = byteArrayToLong(readBytes(offset + 1L, 3));
 
       return readString(areaOffset);
     }
     return readString(offset);
   }
 
   private String seekCountryArea(long offset)
   {
     try
     {
       this.ipFile.seek(offset + 4L);
       byte b = this.ipFile.readByte();
       if (b == 1) {
         long countryOffset = byteArrayToLong(readBytes(3));
         this.ipFile.seek(countryOffset);
         b = this.ipFile.readByte();
         if (b == 2) {
           this.country = readString(byteArrayToLong(readBytes(3)));
           this.ipFile.seek(countryOffset + 4L);
         } else {
           this.country = readString(countryOffset);
         }
       } else if (b == 2) {
         this.country = readString(byteArrayToLong(readBytes(3)));
       }
       else {
         this.country = readString(this.ipFile.getFilePointer() - 1L);
       }
 
       if ((this.country.indexOf("省") > 0) && (this.country.indexOf("市") > 0)) {
         return readText(this.country, "省(.+?)市");
       }
       if ((this.country.indexOf("省") < 0) && (this.country.indexOf("市") > 0)) {
         return readText(this.country, "(.+?)市");
       }
       if ((this.country.indexOf("省") > 0) && (this.country.indexOf("市") < 0)) {
         return readText(this.country, "(.+?)省");
       }
       return this.country; } catch (IOException e) {
     }
     return null;
   }
 
   public static String readText(String result, String identifier)
   {
     Pattern shopNumberPattern = Pattern.compile(identifier);
     Matcher shopNamMatcher = shopNumberPattern.matcher(result);
     if (shopNamMatcher.find())
       return shopNamMatcher.group(1);
     return "";
   }
 
   private String readString(long offset)
   {
     try
     {
       this.ipFile.seek(offset);
       byte[] b = new byte[''];
 
       for (int i = 0; (b.length != i) && ((b[i] = this.ipFile.readByte()) != 0); i++);
       String ret = new String(b, 0, AREA_FOLLOWED);
       ret = ret.trim();
       return (ret.equals("")) || (ret.indexOf("CZ88.NET") != -1) ? "未知" : 
         ret;
     } catch (IOException e) {
       System.out.println("读取文件失败_readString");
     }
     return "";
   }
 
   public ArrayList<IpRecord> stringToIp(String addr)
   {
     ArrayList ret = new ArrayList();
     try {
       FileChannel fc = this.ipFile.getChannel();
       MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0L, 
         this.ipFile.length());
       mbb.order(ByteOrder.LITTLE_ENDIAN);
 
       for (long i = this.ipBegin + 4L; i != this.ipEnd + 4L; i += 7L) {
         String sca = seekCountryArea(byteArrayToLong(readBytes(i, 3)));
         if (sca.indexOf(addr) != -1) {
           IpRecord rec = new IpRecord();
           rec.address = sca;
           rec.beginIp = byteArrayToStringIp(readBytes(i - 4L, 4));
           rec.endIp = byteArrayToStringIp(readBytes(i + 3L, 4));
           ret.add(rec);
         }
       }
     } catch (IOException e) {
       System.out.println(e.getMessage());
     }
     return ret;
   }
 
   public static IpAddress getInstance()
   {
     return instance;
   }
 
   public String IpStringToAddress(String ip)
   {
     long ipOffset = seekIp(ip);
     String ret = seekCountryArea(ipOffset);
     return ret;
   }
 
   public long getIpSum()
   {
     return this.ipSum;
   }
 
   public static void main(String[] args) throws UnknownHostException {
     
	   IpAddress ipAddr = getInstance();
 
     long l = ipAddr.getIpSum();
     System.out.println(l);
 
     String str = ipAddr.IpStringToAddress("255.255.255.0");
     System.out.println(str);
 
     str = ipAddr.IpStringToAddress("222.88.59.214");
     System.out.println(str);
     str = ipAddr.IpStringToAddress("222.248.70.78");
     System.out.println(str);
     str = ipAddr.IpStringToAddress("188.1.255.255");
     System.out.println(str);
     str = ipAddr.IpStringToAddress("220.168.59.166");
     System.out.println(str);
     str = ipAddr.IpStringToAddress("221.10.61.90");
     System.out.println(str);
     str = ipAddr.IpStringToAddress("119.119.91.147");
     System.out.println(str);
     InetAddress inet = InetAddress.getLocalHost();
     System.out.println("本机的ip=" + inet.getHostAddress());
 
     InetAddress addr = null;
     try {
       addr = InetAddress.getLocalHost();
     } catch (UnknownHostException e) {
       e.printStackTrace();
     }
     String ip = addr.getHostAddress().toString();
     System.out.print(ip);
     String address = addr.getHostName().toString();
     System.out.print(address);
     str = ipAddr.IpStringToAddress(ip);
     System.out.println(str);
 
     ArrayList al = ipAddr.stringToIp("网吧");
     Iterator it = al.iterator();
 
     File f = new File("ipdata.txt");
     try {
       if (!f.exists()) {
         f.createNewFile();
       }
       BufferedWriter out = new BufferedWriter(
         new OutputStreamWriter(new FileOutputStream(f, true)));
       int i = 0;
       while (it.hasNext()) {
         out.write(it.next().toString());
         out.newLine();
         i++;
       }
       out.write(new Date().toString());
       out.write("总共搜索到 " + i);
       out.close();
     } catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   private class IpRecord
   {
     public String beginIp;
     public String endIp;
     public String address;
 
     public IpRecord()
     {
       this.beginIp = (this.endIp = this.address = "");
     }
 
     public String toString() {
       return this.beginIp + " - " + this.endIp + " " + this.address;
     }
   }
   
 }


 
 
 