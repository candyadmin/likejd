 package com.shopping.pay.bill.util;
 
 public class MD5
 {
   static final int S11 = 7;
   static final int S12 = 12;
   static final int S13 = 17;
   static final int S14 = 22;
   static final int S21 = 5;
   static final int S22 = 9;
   static final int S23 = 14;
   static final int S24 = 20;
   static final int S31 = 4;
   static final int S32 = 11;
   static final int S33 = 16;
   static final int S34 = 23;
   static final int S41 = 6;
   static final int S42 = 10;
   static final int S43 = 15;
   static final int S44 = 21;
   static final byte[] PADDING = { 
     -128 };
 
   private long[] state = new long[4];
   private long[] count = new long[2];
   private byte[] buffer = new byte[64];
   public String digestHexStr;
   private byte[] digest = new byte[16];
 
   public String getMD5ofStr(String inbuf)
   {
     md5Init();
     md5Update(inbuf.getBytes(), inbuf.length());
     md5Final();
     this.digestHexStr = "";
     for (int i = 0; i < 16; i++) {
       this.digestHexStr += byteHEX(this.digest[i]);
     }
     return this.digestHexStr;
   }
 
   public MD5()
   {
     md5Init();
   }
 
   private void md5Init()
   {
     this.count[0] = 0L;
     this.count[1] = 0L;
 
     this.state[0] = 1732584193L;
     this.state[1] = 4023233417L;
     this.state[2] = 2562383102L;
     this.state[3] = 271733878L;
   }
 
   private long F(long x, long y, long z)
   {
     return x & y | (x ^ 0xFFFFFFFF) & z;
   }
 
   private long G(long x, long y, long z)
   {
     return x & z | y & (z ^ 0xFFFFFFFF);
   }
 
   private long H(long x, long y, long z)
   {
     return x ^ y ^ z;
   }
 
   private long I(long x, long y, long z) {
     return y ^ (x | z ^ 0xFFFFFFFF);
   }
 
   private long FF(long a, long b, long c, long d, long x, long s, long ac)
   {
     a += F(b, c, d) + x + ac;
     a = (int)a << (int)s | (int)a >>> (int)(32L - s);
     a += b;
     return a;
   }
 
   private long GG(long a, long b, long c, long d, long x, long s, long ac)
   {
     a += G(b, c, d) + x + ac;
     a = (int)a << (int)s | (int)a >>> (int)(32L - s);
     a += b;
     return a;
   }
 
   private long HH(long a, long b, long c, long d, long x, long s, long ac)
   {
     a += H(b, c, d) + x + ac;
     a = (int)a << (int)s | (int)a >>> (int)(32L - s);
     a += b;
     return a;
   }
 
   private long II(long a, long b, long c, long d, long x, long s, long ac)
   {
     a += I(b, c, d) + x + ac;
     a = (int)a << (int)s | (int)a >>> (int)(32L - s);
     a += b;
     return a;
   }
 
   private void md5Update(byte[] inbuf, int inputLen)
   {
     byte[] block = new byte[64];
     int index = (int)(this.count[0] >>> 3) & 0x3F;
 		this.count[0] += (inputLen << 3);
     if (this.count[0]  < inputLen << 3)
       this.count[1] += 1L;
     this.count[1] += (inputLen >>> 29);
 
     int partLen = 64 - index;
     int i = 0;
     if (inputLen >= partLen) {
       md5Memcpy(this.buffer, inbuf, index, 0, partLen);
       md5Transform(this.buffer);
 
       for (int j = partLen; j + 63 < inputLen; j += 64)
       {
         md5Memcpy(block, inbuf, 0, j, 64);
         md5Transform(block);
       }
       index = 0;
     }
     else
     {
       i = 0;
     }
 
     md5Memcpy(this.buffer, inbuf, index, i, inputLen - i);
   }
 
   private void md5Final()
   {
     byte[] bits = new byte[8];
 
     Encode(bits, this.count, 8);
 
     int index = (int)(this.count[0] >>> 3) & 0x3F;
     int padLen = index < 56 ? 56 - index : 120 - index;
     md5Update(PADDING, padLen);
 
     md5Update(bits, 8);
 
     Encode(this.digest, this.state, 16);
   }
 
   private void md5Memcpy(byte[] output, byte[] input, int outpos, int inpos, int len)
   {
     for (int i = 0; i < len; i++)
       output[(outpos + i)] = input[(inpos + i)];
   }
 
   private void md5Transform(byte[] block)
   {
     long a = this.state[0]; long b = this.state[1]; long c = this.state[2]; long d = this.state[3];
     long[] x = new long[16];
 
     Decode(x, block, 64);
 
     a = FF(a, b, c, d, x[0], 7L, 3614090360L);
     d = FF(d, a, b, c, x[1], 12L, 3905402710L);
     c = FF(c, d, a, b, x[2], 17L, 606105819L);
     b = FF(b, c, d, a, x[3], 22L, 3250441966L);
     a = FF(a, b, c, d, x[4], 7L, 4118548399L);
     d = FF(d, a, b, c, x[5], 12L, 1200080426L);
     c = FF(c, d, a, b, x[6], 17L, 2821735955L);
     b = FF(b, c, d, a, x[7], 22L, 4249261313L);
     a = FF(a, b, c, d, x[8], 7L, 1770035416L);
     d = FF(d, a, b, c, x[9], 12L, 2336552879L);
     c = FF(c, d, a, b, x[10], 17L, 4294925233L);
     b = FF(b, c, d, a, x[11], 22L, 2304563134L);
     a = FF(a, b, c, d, x[12], 7L, 1804603682L);
     d = FF(d, a, b, c, x[13], 12L, 4254626195L);
     c = FF(c, d, a, b, x[14], 17L, 2792965006L);
     b = FF(b, c, d, a, x[15], 22L, 1236535329L);
 
     a = GG(a, b, c, d, x[1], 5L, 4129170786L);
     d = GG(d, a, b, c, x[6], 9L, 3225465664L);
     c = GG(c, d, a, b, x[11], 14L, 643717713L);
     b = GG(b, c, d, a, x[0], 20L, 3921069994L);
     a = GG(a, b, c, d, x[5], 5L, 3593408605L);
     d = GG(d, a, b, c, x[10], 9L, 38016083L);
     c = GG(c, d, a, b, x[15], 14L, 3634488961L);
     b = GG(b, c, d, a, x[4], 20L, 3889429448L);
     a = GG(a, b, c, d, x[9], 5L, 568446438L);
     d = GG(d, a, b, c, x[14], 9L, 3275163606L);
     c = GG(c, d, a, b, x[3], 14L, 4107603335L);
     b = GG(b, c, d, a, x[8], 20L, 72720877L);
     a = GG(a, b, c, d, x[13], 5L, 2850285829L);
     d = GG(d, a, b, c, x[2], 9L, 4243563512L);
     c = GG(c, d, a, b, x[7], 14L, 1735328473L);
     b = GG(b, c, d, a, x[12], 20L, 2368359562L);
 
     a = HH(a, b, c, d, x[5], 4L, 4294588738L);
     d = HH(d, a, b, c, x[8], 11L, 2272392833L);
     c = HH(c, d, a, b, x[11], 16L, 1839030562L);
     b = HH(b, c, d, a, x[14], 23L, 4259657740L);
     a = HH(a, b, c, d, x[1], 4L, 2763975236L);
     d = HH(d, a, b, c, x[4], 11L, 1272893353L);
     c = HH(c, d, a, b, x[7], 16L, 4139469664L);
     b = HH(b, c, d, a, x[10], 23L, 3200236656L);
     a = HH(a, b, c, d, x[13], 4L, 681279174L);
     d = HH(d, a, b, c, x[0], 11L, 3936430074L);
     c = HH(c, d, a, b, x[3], 16L, 3572445317L);
     b = HH(b, c, d, a, x[6], 23L, 76029189L);
     a = HH(a, b, c, d, x[9], 4L, 3654602809L);
     d = HH(d, a, b, c, x[12], 11L, 3873151461L);
     c = HH(c, d, a, b, x[15], 16L, 530742520L);
     b = HH(b, c, d, a, x[2], 23L, 3299628645L);
 
     a = II(a, b, c, d, x[0], 6L, 4096336452L);
     d = II(d, a, b, c, x[7], 10L, 1126891415L);
     c = II(c, d, a, b, x[14], 15L, 2878612391L);
     b = II(b, c, d, a, x[5], 21L, 4237533241L);
     a = II(a, b, c, d, x[12], 6L, 1700485571L);
     d = II(d, a, b, c, x[3], 10L, 2399980690L);
     c = II(c, d, a, b, x[10], 15L, 4293915773L);
     b = II(b, c, d, a, x[1], 21L, 2240044497L);
     a = II(a, b, c, d, x[8], 6L, 1873313359L);
     d = II(d, a, b, c, x[15], 10L, 4264355552L);
     c = II(c, d, a, b, x[6], 15L, 2734768916L);
     b = II(b, c, d, a, x[13], 21L, 1309151649L);
     a = II(a, b, c, d, x[4], 6L, 4149444226L);
     d = II(d, a, b, c, x[11], 10L, 3174756917L);
     c = II(c, d, a, b, x[2], 15L, 718787259L);
     b = II(b, c, d, a, x[9], 21L, 3951481745L);
 
     this.state[0] += a;
     this.state[1] += b;
     this.state[2] += c;
     this.state[3] += d;
   }
 
   private void Encode(byte[] output, long[] input, int len)
   {
     int i = 0; for (int j = 0; j < len; j += 4) {
       output[j] = (byte)(int)(input[i] & 0xFF);
       output[(j + 1)] = (byte)(int)(input[i] >>> 8 & 0xFF);
       output[(j + 2)] = (byte)(int)(input[i] >>> 16 & 0xFF);
       output[(j + 3)] = (byte)(int)(input[i] >>> 24 & 0xFF);
 
       i++;
     }
   }
 
   private void Decode(long[] output, byte[] input, int len)
   {
     int i = 0; for (int j = 0; j < len; j += 4) {
       output[i] = 
         (b2iu(input[j]) | 
         b2iu(input[(j + 1)]) << 8 | 
         b2iu(input[(j + 2)]) << 16 | 
         b2iu(input[(j + 3)]) << 24);
 
       i++;
     }
   }
 
   public static long b2iu(byte b)
   {
     return b < 0 ? b & 0xFF : b;
   }
 
   public static String byteHEX(byte ib)
   {
     char[] Digit = { 
       '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
       'A', 'B', 'C', 'D', 'E', 'F' };
     char[] ob = new char[2];
     ob[0] = Digit[(ib >>> 4 & 0xF)];
     ob[1] = Digit[(ib & 0xF)];
     String s = new String(ob);
     return s;
   }
 }


 
 
 