package com.shopping.core.tools;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * @author John增加
 * 创建日期 2015-10-16
 */
public class OperateImage{

    public OperateImage() {
		super();
	}

	/** 
	 * 对图片裁剪，并把裁剪新图片保存 
	 * @param srcPath 读取源图片路径
	 * @param toPath	写入图片路径
	 * @param x 剪切起始点x坐标
	 * @param y 剪切起始点y坐标
	 * @param width 剪切宽度
	 * @param height	 剪切高度
	 * @param readImageFormat  读取图片格式
	 * @param writeImageFormat 写入图片格式
	 * @throws IOException
	 */
    public void cropImage(String srcPath,String toPath,
    		int x,int y,int width,int height,
    		String readImageFormat,String writeImageFormat) throws IOException{   
        FileInputStream fis = null ;
        ImageInputStream iis =null ;
        try{   
            //读取图片文件
        	fis = new FileInputStream(srcPath); 
            Iterator it = ImageIO.getImageReadersByFormatName(readImageFormat); 
            ImageReader reader = (ImageReader) it.next(); 
            //获取图片流 
            iis = ImageIO.createImageInputStream(fis);  
            reader.setInput(iis,true) ;
            ImageReadParam param = reader.getDefaultReadParam(); 
            //定义一个矩形
            Rectangle rect = new Rectangle(x, y, width, height); 
            //提供一个 BufferedImage，将其用作解码像素数据的目标。 
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0,param);                
            //保存新图片 
            ImageIO.write(bi, writeImageFormat, new File(toPath));     
        }finally{
            if(fis!=null)
            	fis.close();       
            if(iis!=null)
               iis.close(); 
        } 
    }

    /**
     * 按倍率缩小图片
     * @param srcImagePath 读取图片路径
     * @param toImagePath 写入图片路径
     * @param widthRatio	宽度缩小比例
     * @param heightRatio	 高度缩小比例
     * @throws IOException
     */
    public void reduceImageByRatio(String srcImagePath,String toImagePath,int widthRatio,int heightRatio) throws IOException{
    	FileOutputStream out = null;
    	try{
    		//读入文件  
            File file = new File(srcImagePath);  
            // 构造Image对象  
            BufferedImage src = javax.imageio.ImageIO.read(file);  
            int width = src.getWidth();  
            int height = src.getHeight();  
            // 缩小边长 
            BufferedImage tag = new BufferedImage(width / widthRatio, height / heightRatio, BufferedImage.TYPE_INT_RGB);  
            // 绘制 缩小  后的图片 
            tag.getGraphics().drawImage(src, 0, 0, width / widthRatio, height / heightRatio, null);  
            out = new FileOutputStream(toImagePath);  
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
            encoder.encode(tag);  
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(out != null){
                out.close();  
    		}
    	}
    }

    /**
     * 长高等比例缩小图片
     * @param srcImagePath 读取图片路径
     * @param toImagePath 写入图片路径
     * @param ratio 缩小比例
     * @throws IOException
     */
    public void reduceImageEqualProportion(String srcImagePath,String toImagePath,int ratio) throws IOException{
    	FileOutputStream out = null;
    	try{
    		//读入文件  
            File file = new File(srcImagePath);  
            // 构造Image对象  
            BufferedImage src = javax.imageio.ImageIO.read(file);  
            int width = src.getWidth();  
            int height = src.getHeight();  
            // 缩小边长 
            BufferedImage tag = new BufferedImage(width / ratio, height / ratio, BufferedImage.TYPE_INT_RGB);  
            // 绘制 缩小  后的图片 
            tag.getGraphics().drawImage(src, 0, 0, width / ratio, height / ratio, null);  
            out = new FileOutputStream(toImagePath);  
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
            encoder.encode(tag);  
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(out != null){
                out.close();  
    		}
    	}
    }
    
    /**
     * 按倍率放大图片
     * @param srcImagePath 读取图形路径
     * @param toImagePath 写入入行路径
     * @param widthRatio	宽度放大比例
     * @param heightRatio 高度放大比例
     * @throws IOException
     */
    public void enlargementImageByRatio(String srcImagePath,String toImagePath,int widthRatio,int heightRatio) throws IOException{
    	FileOutputStream out = null;
    	try{
    		//读入文件  
            File file = new File(srcImagePath);  
            // 构造Image对象  
            BufferedImage src = javax.imageio.ImageIO.read(file);  
            int width = src.getWidth();  
            int height = src.getHeight();  
            // 放大边长
            BufferedImage tag = new BufferedImage(width * widthRatio, height * heightRatio, BufferedImage.TYPE_INT_RGB);  
            //绘制放大后的图片
            tag.getGraphics().drawImage(src, 0, 0, width * widthRatio, height * heightRatio, null);  
            out = new FileOutputStream(toImagePath);  
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
            encoder.encode(tag);  
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(out != null){
                out.close();  
    		}
    	}
    }
    
    
    /**
     * 长高等比例放大图片
     * @param srcImagePath 读取图形路径
     * @param toImagePath 写入入行路径
     * @param ratio	放大比例
     * @throws IOException
     */
    public void enlargementImageEqualProportion(String srcImagePath,String toImagePath,int ratio) throws IOException{
    	FileOutputStream out = null;
    	try{
    		//读入文件  
            File file = new File(srcImagePath);  
            // 构造Image对象  
            BufferedImage src = javax.imageio.ImageIO.read(file);  
            int width = src.getWidth();  
            int height = src.getHeight();  
            // 放大边长
            BufferedImage tag = new BufferedImage(width * ratio, height * ratio, BufferedImage.TYPE_INT_RGB);  
            //绘制放大后的图片
            tag.getGraphics().drawImage(src, 0, 0, width * ratio, height * ratio, null);  
            out = new FileOutputStream(toImagePath);  
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
            encoder.encode(tag);  
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(out != null){
                out.close();  
    		}
    	}
    }
    
    /**
     * 重置图形的边长大小
     * @param srcImagePath 
     * @param toImagePath
     * @param width
     * @param height
     * @throws IOException
     */
    public void resizeImage(String srcImagePath,String toImagePath,int width,int height) throws IOException{
    	FileOutputStream out = null;
    	try{
    		//读入文件  
            File file = new File(srcImagePath);  
            // 构造Image对象  
            BufferedImage src = javax.imageio.ImageIO.read(file);  
            // 放大边长
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
            //绘制放大后的图片
            tag.getGraphics().drawImage(src, 0, 0, width, height, null);  
            out = new FileOutputStream(toImagePath);  
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
            encoder.encode(tag);  
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(out != null){
                out.close();  
    		}
    	}
    }
    
    /**
     * 横向拼接图片（两张）
     * @param firstSrcImagePath 第一张图片的路径
     * @param secondSrcImagePath	第二张图片的路径
     * @param imageFormat	拼接生成图片的格式
     * @param toPath	拼接生成图片的路径
     */
    public void joinImagesHorizontal(String firstSrcImagePath, String secondSrcImagePath,String imageFormat, String toPath){  
    	try {  
    		//读取第一张图片    
    		File  fileOne  =  new  File(firstSrcImagePath);    
            BufferedImage  imageOne = ImageIO.read(fileOne);    
            int  width  =  imageOne.getWidth();//图片宽度    
            int  height  =  imageOne.getHeight();//图片高度    
            //从图片中读取RGB    
            int[]  imageArrayOne  =  new  int[width*height];    
            imageArrayOne  =  imageOne.getRGB(0,0,width,height,imageArrayOne,0,width);    
           
            //对第二张图片做相同的处理    
            File  fileTwo  =  new  File(secondSrcImagePath);    
            BufferedImage  imageTwo  =  ImageIO.read(fileTwo); 
            int width2 = imageTwo.getWidth();
            int height2 = imageTwo.getHeight();
            int[]   ImageArrayTwo  =  new  int[width2*height2];    
            ImageArrayTwo  =  imageTwo.getRGB(0,0,width,height,ImageArrayTwo,0,width);    
            //ImageArrayTwo  =  imageTwo.getRGB(0,0,width2,height2,ImageArrayTwo,0,width2); 
           
            //生成新图片
            //int height3 = (height>height2 || height==height2)?height:height2;
            BufferedImage  imageNew  =  new  BufferedImage(width*2,height,BufferedImage.TYPE_INT_RGB);    
            //BufferedImage  imageNew  =  new  BufferedImage(width+width2,height3,BufferedImage.TYPE_INT_RGB);    
            imageNew.setRGB(0,0,width,height,imageArrayOne,0,width);//设置左半部分的RGB  
            imageNew.setRGB(width,0,width,height,ImageArrayTwo,0,width);//设置右半部分的RGB 
            //imageNew.setRGB(width,0,width2,height2,ImageArrayTwo,0,width2);//设置右半部分的RGB    
           
            File  outFile  =  new  File(toPath);    
            ImageIO.write(imageNew,  imageFormat,  outFile);//写图片
        } catch (Exception e) {  
        	e.printStackTrace();  
        }  
    }
    
    /**
	 * 横向拼接一组（多张）图像
	 * @param pics  将要拼接的图像
	 * @param type 图像写入格式
	 * @param dst_pic 图像写入路径
	 * @return
	 */
    public  boolean joinImageListHorizontal(String[] pics, String type, String dst_pic) {   
    	try {  
    		int len = pics.length;  
    		if (len < 1) {  
    			System.out.println("pics len < 1");  
                return false;  
            }  
    		File[] src = new File[len];  
    		BufferedImage[] images = new BufferedImage[len];  
    		int[][] imageArrays = new int[len][];  
    		for (int i = 0; i < len; i++) {  
    			src[i] = new File(pics[i]);  
    			images[i] = ImageIO.read(src[i]);  
    			int width = images[i].getWidth();  
    			int height = images[i].getHeight();  
    			imageArrays[i] = new int[width * height];// 从图片中读取RGB    
    			imageArrays[i] = images[i].getRGB(0, 0, width, height,  imageArrays[i], 0, width);  
    		}  
    		
    		int dst_width = 0;  
    		int dst_height = images[0].getHeight();  
    		for (int i = 0; i < images.length; i++) {  
    			dst_height = dst_height > images[i].getHeight() ? dst_height : images[i].getHeight();  
    			dst_width += images[i].getWidth();
    		}  
    		//System.out.println(dst_width);  
    		//System.out.println(dst_height);  
    		if (dst_height < 1) {  
    			System.out.println("dst_height < 1");  
    			return false;  
    		} 
    		/*
    		 * 生成新图片
    		 */   
    		BufferedImage ImageNew = new BufferedImage(dst_width, dst_height,  BufferedImage.TYPE_INT_RGB);  
    		int width_i = 0;
    		for (int i = 0; i < images.length; i++) {  
    			ImageNew.setRGB(width_i, 0, images[i].getWidth(), dst_height,  imageArrays[i], 0, images[i].getWidth());  
    			width_i += images[i].getWidth();
    		}  
    		File outFile = new File(dst_pic);  
    		ImageIO.write(ImageNew, type, outFile);// 写图片   
    	} catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
        return true;  
    }
    
    /**
     * 纵向拼接图片（两张）
     * @param firstSrcImagePath 读取的第一张图片
     * @param secondSrcImagePath	读取的第二张图片
     * @param imageFormat 图片写入格式
     * @param toPath	图片写入路径
     */
    public void joinImagesVertical(String firstSrcImagePath, String secondSrcImagePath,String imageFormat, String toPath){  
        try {  
        	//读取第一张图片    
            File  fileOne  =  new  File(firstSrcImagePath);    
            BufferedImage  imageOne = ImageIO.read(fileOne);    
            int  width  =  imageOne.getWidth();//图片宽度    
            int  height  =  imageOne.getHeight();//图片高度    
            //从图片中读取RGB    
            int[]  imageArrayOne  =  new  int[width*height];    
            imageArrayOne  =  imageOne.getRGB(0,0,width,height,imageArrayOne,0,width);    
       
            //对第二张图片做相同的处理    
            File  fileTwo  =  new  File(secondSrcImagePath);    
            BufferedImage  imageTwo  =  ImageIO.read(fileTwo); 
            int width2 = imageTwo.getWidth();
            int height2 = imageTwo.getHeight();
            int[]   ImageArrayTwo  =  new  int[width2*height2];    
            ImageArrayTwo  =  imageTwo.getRGB(0,0,width,height,ImageArrayTwo,0,width);    
            //ImageArrayTwo  =  imageTwo.getRGB(0,0,width2,height2,ImageArrayTwo,0,width2); 
       
            //生成新图片
            //int width3 = (width>width2 || width==width2)?width:width2;
            BufferedImage  imageNew  =  new  BufferedImage(width,height*2,BufferedImage.TYPE_INT_RGB);    
            //BufferedImage  imageNew  =  new  BufferedImage(width3,height+height2,BufferedImage.TYPE_INT_RGB);    
            imageNew.setRGB(0,0,width,height,imageArrayOne,0,width);//设置上半部分的RGB    
            imageNew.setRGB(0,height,width,height,ImageArrayTwo,0,width);//设置下半部分的RGB
            //imageNew.setRGB(0,height,width2,height2,ImageArrayTwo,0,width2);//设置下半部分的RGB    
       
            File  outFile  =  new  File(toPath);    
            ImageIO.write(imageNew,  imageFormat,  outFile);//写图片
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    
    /**
     * 纵向拼接一组（多张）图像
     * @param pics		将要拼接的图像数组
     * @param type	写入图像类型
     * @param dst_pic	写入图像路径
     * @return
     */
	public  boolean joinImageListVertical(String[] pics, String type, String dst_pic) {   
        try {  
        	int len = pics.length;  
            if (len < 1) {  
                System.out.println("pics len < 1");  
                return false;  
            }  
        	 File[] src = new File[len];  
             BufferedImage[] images = new BufferedImage[len];  
             int[][] imageArrays = new int[len][];  
             for (int i = 0; i < len; i++) {  
            	//System.out.println(i);
	            src[i] = new File(pics[i]);  
	            images[i] = ImageIO.read(src[i]);  
	            int width = images[i].getWidth();  
	            int height = images[i].getHeight();  
	            imageArrays[i] = new int[width * height];// 从图片中读取RGB   
	            imageArrays[i] = images[i].getRGB(0, 0, width, height,  imageArrays[i], 0, width);  
	        }  
             
	        int dst_height = 0;  
	        int dst_width = images[0].getWidth();  
	        for (int i = 0; i < images.length; i++) {  
	            dst_width = dst_width > images[i].getWidth() ? dst_width : images[i].getWidth();  
	            dst_height += images[i].getHeight();  
	        }  
	        //System.out.println(dst_width);  
	        //System.out.println(dst_height);  
	        if (dst_height < 1) {  
	            System.out.println("dst_height < 1");  
	            return false;  
	        }  
	        /*
	         * 生成新图片
	         */   
            BufferedImage ImageNew = new BufferedImage(dst_width, dst_height,  BufferedImage.TYPE_INT_RGB);  
            int height_i = 0;  
            for (int i = 0; i < images.length; i++) {  
                ImageNew.setRGB(0, height_i, dst_width, images[i].getHeight(),  imageArrays[i], 0, dst_width);  
                height_i += images[i].getHeight();  
            }  
            File outFile = new File(dst_pic);  
            ImageIO.write(ImageNew, type, outFile);// 写图片   
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
        return true;  
    }  
    
    /**
     * 合并图片(按指定初始x、y坐标将附加图片贴到底图之上)
     * @param negativeImagePath 背景图片路径
     * @param additionImagePath	附加图片路径
     * @param x 附加图片的起始点x坐标
     * @param y  附加图片的起始点y坐标
     * @param toPath 图片写入路径
     * @throws IOException
     */
    public void mergeBothImage(String negativeImagePath,String additionImagePath,int x,int y,String toPath ) throws IOException{
    	InputStream is= null;
    	InputStream is2= null;
    	OutputStream os = null;
    	try{
    		is=new FileInputStream(negativeImagePath);
            is2=new FileInputStream(additionImagePath);
            BufferedImage image=ImageIO.read(is);
            BufferedImage image2=ImageIO.read(is2);
            Graphics g=image.getGraphics();
            g.drawImage(image2,x,y,null);
            os = new FileOutputStream(toPath);
            JPEGImageEncoder enc=JPEGCodec.createJPEGEncoder(os);
            enc.encode(image);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(os != null){
    			os.close();
    		}
    		if(is2 != null){
    			is2.close();
    		}
    		if(is != null){
    			is.close();
    		}
    	}
    }
    
    /** 
     * 将一组图片一次性附加合并到底图上
     * @param negativeImagePath		源图像（底图）路径
     * @param additionImageList	附加图像信息列表
     * @param imageFormat	图像写入格式
     * @param toPath	图像写入路径
     * @throws IOException
     */
    public void mergeImageList(String negativeImagePath,List additionImageList,String imageFormat, String toPath) throws IOException{
    	InputStream is= null;
    	InputStream is2= null;
    	OutputStream os = null;
    	try{
    		is=new FileInputStream(negativeImagePath);
    		BufferedImage image=ImageIO.read(is);
    		//Graphics g=image.getGraphics();
    		Graphics2D g = image.createGraphics();;
    		BufferedImage image2 = null;
    		if(additionImageList != null){
    			for(int i=0;i<additionImageList.size();i++){
    				//解析附加图片信息：x坐标、 y坐标、 additionImagePath附加图片路径
    				//图片信息存储在一个数组中
    				String[] additionImageInfo = (String[]) additionImageList.get(i);
    				int x = Integer.parseInt(additionImageInfo[0]);
    				int y = Integer.parseInt(additionImageInfo[1]);
    				String additionImagePath = additionImageInfo[2];
    				//读取文件输入流，并合并图片
    				is2 = new FileInputStream(additionImagePath);
    				//System.out.println(x+"  :  "+y+"  :  "+additionImagePath);
    				image2 = ImageIO.read(is2);
    	            g.drawImage(image2,x,y,null);
    			}
    		}
            os = new FileOutputStream(toPath);
            ImageIO.write(image,  imageFormat,  os);//写图片
            //JPEGImageEncoder enc=JPEGCodec.createJPEGEncoder(os);
            //enc.encode(image);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(os != null){
    			os.close();
    		}
    		if(is2 != null){
    			is2.close();
    		}
    		if(is != null){
    			is.close();
    		}
    	}
    }
    
    /**
     * 将附加图片合并到底图的左上角
     * @param negativeImagePath 底图路径
     * @param additionImagePath	附加图片路径
     * @param toPath	合成图片写入路径
     * @throws IOException
     */
    public void mergeBothImageTopleftcorner(String negativeImagePath,String additionImagePath,String toPath ) throws IOException{
    	InputStream is= null;
    	InputStream is2= null;
    	OutputStream os = null;
    	try{
    		is=new FileInputStream(negativeImagePath);
            is2=new FileInputStream(additionImagePath);
            BufferedImage image=ImageIO.read(is);
            BufferedImage image2=ImageIO.read(is2);
            Graphics g=image.getGraphics();
            g.drawImage(image2,0,0,null);
            os = new FileOutputStream(toPath);
            JPEGImageEncoder enc=JPEGCodec.createJPEGEncoder(os);
            enc.encode(image);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(os != null){
    			os.close();
    		}
    		if(is2 != null){
    			is2.close();
    		}
    		if(is != null){
    			is.close();
    		}
    	}
    }
    
    /**
     * 将附加图片合并到底图的右上角
     * @param negativeImagePath 底图路径
     * @param additionImagePath	附加图片路径
     * @param toPath	合成图片写入路径
     * @throws IOException
     */
    public void mergeBothImageToprightcorner(String negativeImagePath,String additionImagePath,String toPath ) throws IOException{
    	InputStream is= null;
    	InputStream is2= null;
    	OutputStream os = null;
    	try{
    		is=new FileInputStream(negativeImagePath);
            is2=new FileInputStream(additionImagePath);
            BufferedImage image=ImageIO.read(is);
            BufferedImage image2=ImageIO.read(is2);
            Graphics g=image.getGraphics();
            g.drawImage(image2,image.getWidth()-image2.getWidth(),0,null);
            os = new FileOutputStream(toPath);
            JPEGImageEncoder enc=JPEGCodec.createJPEGEncoder(os);
            enc.encode(image);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(os != null){
    			os.close();
    		}
    		if(is2 != null){
    			is2.close();
    		}
    		if(is != null){
    			is.close();
    		}
    	}
    }
    
    /**
     * 将附加图片合并到底图的左下角
     * @param negativeImagePath 底图路径
     * @param additionImagePath	附加图片路径
     * @param toPath	合成图片写入路径
     * @throws IOException
     */
    public void mergeBothImageLeftbottom(String negativeImagePath,String additionImagePath,String toPath ) throws IOException{
    	InputStream is= null;
    	InputStream is2= null;
    	OutputStream os = null;
    	try{
    		is=new FileInputStream(negativeImagePath);
            is2=new FileInputStream(additionImagePath);
            BufferedImage image=ImageIO.read(is);
            BufferedImage image2=ImageIO.read(is2);
            Graphics g=image.getGraphics();
            g.drawImage(image2,0,image.getHeight()-image2.getHeight(),null);
            os = new FileOutputStream(toPath);
            JPEGImageEncoder enc=JPEGCodec.createJPEGEncoder(os);
            enc.encode(image);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(os != null){
    			os.close();
    		}
    		if(is2 != null){
    			is2.close();
    		}
    		if(is != null){
    			is.close();
    		}
    	}
    }
    
    /**
     * 将附加图片合并到底图的左下角
     * @param negativeImagePath 底图路径
     * @param additionImagePath	附加图片路径
     * @param toPath	合成图片写入路径
     * @throws IOException
     */
    public void mergeBothImageRightbottom(String negativeImagePath,String additionImagePath,String toPath ) throws IOException{
    	InputStream is= null;
    	InputStream is2= null;
    	OutputStream os = null;
    	try{
    		is=new FileInputStream(negativeImagePath);
            is2=new FileInputStream(additionImagePath);
            BufferedImage image=ImageIO.read(is);
            BufferedImage image2=ImageIO.read(is2);
            Graphics g=image.getGraphics();
            g.drawImage(image2,image.getWidth()-image2.getWidth(),image.getHeight()-image2.getHeight(),null);
            os = new FileOutputStream(toPath);
            JPEGImageEncoder enc=JPEGCodec.createJPEGEncoder(os);
            enc.encode(image);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(os != null){
    			os.close();
    		}
    		if(is2 != null){
    			is2.close();
    		}
    		if(is != null){
    			is.close();
    		}
    	}
    }
    
    /**
     * 将附加图片合并到底图的正中央
     * @param negativeImagePath 底图路径
     * @param additionImagePath	附加图片路径
     * @param toPath	合成图片写入路径
     * @throws IOException
     */
    public void mergeBothImageCenter(String negativeImagePath,String additionImagePath,String toPath ) throws IOException{
    	InputStream is= null;
    	InputStream is2= null;
    	OutputStream os = null;
    	try{
    		is=new FileInputStream(negativeImagePath);
            is2=new FileInputStream(additionImagePath);
            BufferedImage image=ImageIO.read(is);
            BufferedImage image2=ImageIO.read(is2);
            Graphics g=image.getGraphics();
            g.drawImage(image2,image.getWidth()/2-image2.getWidth()/2,image.getHeight()/2-image2.getHeight()/2,null);
            os = new FileOutputStream(toPath);
            JPEGImageEncoder enc=JPEGCodec.createJPEGEncoder(os);
            enc.encode(image);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(os != null){
    			os.close();
    		}
    		if(is2 != null){
    			is2.close();
    		}
    		if(is != null){
    			is.close();
    		}
    	}
    }
    
    /**
     * 将附加图片合并到底图的上边中央
     * @param negativeImagePath 底图路径
     * @param additionImagePath	附加图片路径
     * @param toPath	合成图片写入路径
     * @throws IOException
     */
    public void mergeBothImageTopcenter(String negativeImagePath,String additionImagePath,String toPath ) throws IOException{
    	InputStream is= null;
    	InputStream is2= null;
    	OutputStream os = null;
    	try{
    		is=new FileInputStream(negativeImagePath);
            is2=new FileInputStream(additionImagePath);
            BufferedImage image=ImageIO.read(is);
            BufferedImage image2=ImageIO.read(is2);
            Graphics g=image.getGraphics();
            g.drawImage(image2,image.getWidth()/2-image2.getWidth()/2,0,null);
            os = new FileOutputStream(toPath);
            JPEGImageEncoder enc=JPEGCodec.createJPEGEncoder(os);
            enc.encode(image);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(os != null){
    			os.close();
    		}
    		if(is2 != null){
    			is2.close();
    		}
    		if(is != null){
    			is.close();
    		}
    	}
    }
    
    /**
     * 将附加图片合并到底图的下边中央
     * @param negativeImagePath 底图路径
     * @param additionImagePath	附加图片路径
     * @param toPath	合成图片写入路径
     * @throws IOException
     */
    public void mergeBothImageBottomcenter(String negativeImagePath,String additionImagePath,String toPath ) throws IOException{
    	InputStream is= null;
    	InputStream is2= null;
    	OutputStream os = null;
    	try{
    		is=new FileInputStream(negativeImagePath);
            is2=new FileInputStream(additionImagePath);
            BufferedImage image=ImageIO.read(is);
            BufferedImage image2=ImageIO.read(is2);
            Graphics g=image.getGraphics();
            g.drawImage(image2,image.getWidth()/2-image2.getWidth()/2,image.getHeight()-image2.getHeight(),null);
            os = new FileOutputStream(toPath);
            JPEGImageEncoder enc=JPEGCodec.createJPEGEncoder(os);
            enc.encode(image);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(os != null){
    			os.close();
    		}
    		if(is2 != null){
    			is2.close();
    		}
    		if(is != null){
    			is.close();
    		}
    	}
    }
    
    /**
     * 将附加图片合并到底图的左边中央
     * @param negativeImagePath 底图路径
     * @param additionImagePath	附加图片路径
     * @param toPath	合成图片写入路径
     * @throws IOException
     */
    public void mergeBothImageLeftcenter(String negativeImagePath,String additionImagePath,String toPath ) throws IOException{
    	InputStream is= null;
    	InputStream is2= null;
    	OutputStream os = null;
    	try{
    		is=new FileInputStream(negativeImagePath);
            is2=new FileInputStream(additionImagePath);
            BufferedImage image=ImageIO.read(is);
            BufferedImage image2=ImageIO.read(is2);
            Graphics g=image.getGraphics();
            g.drawImage(image2,0,image.getHeight()/2-image2.getHeight()/2,null);
            os = new FileOutputStream(toPath);
            JPEGImageEncoder enc=JPEGCodec.createJPEGEncoder(os);
            enc.encode(image);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(os != null){
    			os.close();
    		}
    		if(is2 != null){
    			is2.close();
    		}
    		if(is != null){
    			is.close();
    		}
    	}
    }
    
    /**
     * 将附加图片合并到底图的右边中央
     * @param negativeImagePath 底图路径
     * @param additionImagePath	附加图片路径
     * @param toPath	合成图片写入路径
     * @throws IOException
     */
    public void mergeBothImageRightcenter(String negativeImagePath,String additionImagePath,String toPath ) throws IOException{
    	InputStream is= null;
    	InputStream is2= null;
    	OutputStream os = null;
    	try{
    		is=new FileInputStream(negativeImagePath);
            is2=new FileInputStream(additionImagePath);
            BufferedImage image=ImageIO.read(is);
            BufferedImage image2=ImageIO.read(is2);
            Graphics g=image.getGraphics();
            g.drawImage(image2,image.getWidth()-image2.getWidth(),image.getHeight()/2-image2.getHeight()/2,null);
            os = new FileOutputStream(toPath);
            JPEGImageEncoder enc=JPEGCodec.createJPEGEncoder(os);
            enc.encode(image);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(os != null){
    			os.close();
    		}
    		if(is2 != null){
    			is2.close();
    		}
    		if(is != null){
    			is.close();
    		}
    	}
    }
    
    /**
     * 图片灰化操作
     * @param srcImage 读取图片路径
     * @param toPath	写入灰化后的图片路径
     * @param imageFormat 图片写入格式
     */ 
    public void grayImage(String srcImage,String toPath,String imageFormat){
    	try{
    		BufferedImage src = ImageIO.read(new File(srcImage));
        	ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        	ColorConvertOp op = new ColorConvertOp(cs, null);
        	src = op.filter(src, null);
        	ImageIO.write(src, imageFormat, new File(toPath));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    /**
     * 在源图片上设置水印文字
     * @param srcImagePath	源图片路径
     * @param alpha	透明度（0<alpha<1）
     * @param font	字体（例如：宋体）
     * @param fontStyle		字体格式(例如：普通样式--Font.PLAIN、粗体--Font.BOLD )
     * @param fontSize	字体大小
     * @param color	字体颜色(例如：黑色--Color.BLACK)
     * @param inputWords		输入显示在图片上的文字
     * @param x		文字显示起始的x坐标
     * @param y		文字显示起始的y坐标
     * @param imageFormat	写入图片格式（png/jpg等）
     * @param toPath	写入图片路径
     * @throws IOException 
     */
    public void alphaWords2Image(String srcImagePath,float alpha,
    		String font,int fontStyle,int fontSize,Color color,
    		String inputWords,int x,int y,String imageFormat,String toPath) throws IOException{
    	FileOutputStream fos=null;
		try {
			BufferedImage image = ImageIO.read(new File(srcImagePath));
			//创建java2D对象
	    	Graphics2D g2d=image.createGraphics();
	    	//用源图像填充背景
	    	g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null, null);
	    	//设置透明度
	    	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
	    	g2d.setComposite(ac);
	    	//设置文字字体名称、样式、大小
	    	g2d.setFont(new Font(font, fontStyle, fontSize));
	    	g2d.setColor(color);//设置字体颜色
	    	g2d.drawString(inputWords, x, y); //输入水印文字及其起始x、y坐标
	    	g2d.dispose();
	    	fos=new FileOutputStream(toPath);
	    	ImageIO.write(image, imageFormat, fos);
    	} catch (Exception e) {
    	   e.printStackTrace();
    	}finally{
    		if(fos!=null){
    			fos.close();
    		}
    	}
    }
    
    /**
     * 在源图像上设置图片水印  
     * 	---- 当alpha==1时文字不透明（和在图片上直接输入文字效果一样）
     * @param srcImagePath	源图片路径
     * @param appendImagePath	水印图片路径
     * @param alpha	透明度
     * @param x		水印图片的起始x坐标
     * @param y		水印图片的起始y坐标
     * @param width	水印图片的宽度
     * @param height		水印图片的高度
     * @param imageFormat	图像写入图片格式
     * @param toPath	图像写入路径
     * @throws IOException 
     */
    public void alphaImage2Image(String srcImagePath,String appendImagePath,
    		float alpha,int x,int y,int width,int height,
    		String imageFormat,String toPath) throws IOException{
    	FileOutputStream fos = null;
    	try {
			BufferedImage image = ImageIO.read(new File(srcImagePath));
			//创建java2D对象
	    	Graphics2D g2d=image.createGraphics();
	    	//用源图像填充背景
	    	g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null, null);
	    	//设置透明度
	    	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
	    	g2d.setComposite(ac);
	    	//设置水印图片的起始x/y坐标、宽度、高度
	    	BufferedImage appendImage = ImageIO.read(new File(appendImagePath));
	    	g2d.drawImage(appendImage, x, y, width, height, null, null);
	    	g2d.dispose();
	    	fos=new FileOutputStream(toPath);
	    	ImageIO.write(image, imageFormat, fos);
    	} catch (Exception e) {
    	   e.printStackTrace();
    	}finally{
    		if(fos!=null){
    			fos.close();
    		}
    	}
    }
    
    /**
     * 画单点 ---- 实际上是画一个填充颜色的圆
     * ---- 以指定点坐标为中心画一个小半径的圆形，并填充其颜色来充当点
     * @param srcImagePath	 源图片颜色
     * @param x		点的x坐标
     * @param y		点的y坐标
     * @param width	填充的宽度
     * @param height	填充的高度
     * @param ovalColor	填充颜色
     * @param imageFormat	写入图片格式
     * @param toPath	写入路径
     * @throws IOException
     */
    public void drawPoint(String srcImagePath,int x,int y,int width,int height,Color ovalColor,String imageFormat,String toPath) throws IOException{
    	FileOutputStream fos = null;
		try {
			//获取源图片
			BufferedImage image = ImageIO.read(new File(srcImagePath));
			//根据xy点坐标绘制连接线
			Graphics2D g2d = image.createGraphics();
			g2d.setColor(ovalColor);
			//填充一个椭圆形
			g2d.fillOval(x, y, width, height);
			fos = new FileOutputStream(toPath);
			ImageIO.write(image, imageFormat, fos);	
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
    			fos.close();
    		}
		}
    }
    
    /**
     * 画一组（多个）点---- 实际上是画一组（多个）填充颜色的圆
     * ---- 以指定点坐标为中心画一个小半径的圆形，并填充其颜色来充当点
     * @param srcImagePath	原图片路径
     * @param pointList	点列表
     * @param width	宽度
     * @param height		高度
     * @param ovalColor 填充颜色
     * @param imageFormat	写入图片颜色
     * @param toPath	写入路径
     * @throws IOException
     */
    public void drawPoints(String srcImagePath,List pointList,int width,int height,Color ovalColor,String imageFormat,String toPath) throws IOException{
    	FileOutputStream fos = null;
		try {
			//获取源图片
			BufferedImage image = ImageIO.read(new File(srcImagePath));
			//根据xy点坐标绘制连接线
			Graphics2D g2d = image.createGraphics();
			g2d.setColor(ovalColor);
			//填充一个椭圆形
			if(pointList != null){
				for(int i=0;i<pointList.size();i++){
					Point point = (Point)pointList.get(i);
					int x = (int) point.getX();
					int y = (int) point.getY();
					g2d.fillOval(x, y, width, height);
				}
			}
			fos = new FileOutputStream(toPath);
			ImageIO.write(image, imageFormat, fos);	
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
    			fos.close();
    		}
		}
    }
    
    /**
     * 画线段
     * @param srcImagePath	源图片路径
     * @param x1	第一个点x坐标
     * @param y1	第一个点y坐标
     * @param x2	第二个点x坐标
     * @param y2	第二个点y坐标
     * @param lineColor 线条颜色
     * @param toPath	图像写入路径
     * @param imageFormat	图像写入格式
     * @throws IOException	
     */
    public void drawLine(String srcImagePath,int x1,int y1,int x2,int y2, Color lineColor,String toPath,String imageFormat) throws IOException{
    	FileOutputStream fos = null;
		try {
			//获取源图片
			BufferedImage image = ImageIO.read(new File(srcImagePath));
			//根据xy点坐标绘制连接线
			Graphics2D g2d = image.createGraphics();
			g2d.setColor(lineColor);
			g2d.drawLine( x1, y1, x2, y2);
			fos = new FileOutputStream(toPath);
			ImageIO.write(image, imageFormat, fos);	
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
    			fos.close();
    		}
		}
    }
    
    /**
     * 画折线 / 线段
     * ---- 2个点即画线段，多个点画折线
     * @param srcImagePath	源图片路径
     * @param xPoints	x坐标数组
     * @param yPoints	y坐标数组
     * @param nPoints	点的数量
     * @param lineColor	线条颜色
     * @param toPath	图像写入路径
     * @param imageFormat	图片写入格式
     * @throws IOException	
     */
    public void drawPolyline(String srcImagePath,int[] xPoints, int[] yPoints, int nPoints,Color lineColor,String toPath,String imageFormat) throws IOException{
    	FileOutputStream fos = null;
		try {
			//获取源图片
			BufferedImage image = ImageIO.read(new File(srcImagePath));
			//根据xy点坐标绘制连接线
			Graphics2D g2d = image.createGraphics();
			//设置线条颜色
			g2d.setColor(lineColor);
			g2d.drawPolyline(xPoints, yPoints, nPoints);
			//图像写出路径
			fos = new FileOutputStream(toPath);
			ImageIO.write(image, imageFormat, fos);	
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
    			fos.close();
    		}
		}
    }
    
    /**
     * 绘制折线，并突出显示转折点
     * @param srcImagePath	源图片路径
     * @param xPoints	x坐标数组
     * @param yPoints	y坐标数组
     * @param nPoints	点的数量
     * @param lineColor	连线颜色
     * @param width	点的宽度
     * @param height		点的高度
     * @param ovalColor	点的填充颜色
     * @param toPath	图像写入路径
     * @param imageFormat	图像写入格式
     * @throws IOException
     */
    public void drawPolylineShowPoints(String srcImagePath,int[] xPoints, int[] yPoints, int nPoints,Color lineColor,int width,int height,Color ovalColor,String toPath,String imageFormat) throws IOException{
    	FileOutputStream fos = null;
		try {
			//获取源图片
			BufferedImage image = ImageIO.read(new File(srcImagePath));
			//根据xy点坐标绘制连接线
			Graphics2D g2d = image.createGraphics();
			//设置线条颜色
			g2d.setColor(lineColor);
			//画线条
			g2d.drawPolyline(xPoints, yPoints, nPoints);
			//设置圆点颜色
			g2d.setColor(ovalColor);
			//画圆点
			if(xPoints != null){
				for(int i=0;i<xPoints.length;i++){
					int x = xPoints[i];
					int y = yPoints[i];
					g2d.fillOval(x, y, width, height);
				}
			}
			//图像写出路径
			fos = new FileOutputStream(toPath);
			ImageIO.write(image, imageFormat, fos);	
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
    			fos.close();
    		}
		}
    }
    
    
    /** 
     * 绘制一个由 x 和 y 坐标数组定义的闭合多边形
     * @param srcImagePath 源图片路径
     * @param xPoints	x坐标数组
     * @param yPoints	y坐标数组
     * @param nPoints	坐标点的个数
     * @param polygonColor	线条颜色
     * @param imageFormat	图像写入格式
     * @param toPath	图像写入路径
     * @throws IOException 
     */
    public void drawPolygon(String srcImagePath,int[] xPoints,int[] yPoints,int nPoints,Color polygonColor,String imageFormat,String toPath) throws IOException {
    	FileOutputStream fos = null;
    	try {
    		//获取图片
    		BufferedImage image = ImageIO.read(new File(srcImagePath));
			//根据xy点坐标绘制闭合多边形
			Graphics2D g2d = image.createGraphics();
			g2d.setColor(polygonColor);
			g2d.drawPolygon(xPoints, yPoints, nPoints);
			fos = new FileOutputStream(toPath);
			ImageIO.write(image, imageFormat, fos);	
			g2d.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
					if(fos!=null){
						fos.close();
					} 
			}
    }
    
    /**
     * 绘制并填充多边形
     * @param srcImagePath	源图像路径
     * @param xPoints	x坐标数组
     * @param yPoints	y坐标数组
     * @param nPoints	坐标点个数
     * @param polygonColor	多边形填充颜色
     * @param alpha	多边形部分透明度
     * @param imageFormat	写入图形格式
     * @param toPath	写入图形路径
     * @throws IOException
     */
    public void drawAndAlphaPolygon(String srcImagePath,int[] xPoints,int[] yPoints,int nPoints,Color polygonColor,float alpha,String imageFormat,String toPath) throws IOException{
    	FileOutputStream fos = null;
    	try {
    		//获取图片
    		BufferedImage image = ImageIO.read(new File(srcImagePath));
			//根据xy点坐标绘制闭合多边形
			Graphics2D g2d = image.createGraphics();
			g2d.setColor(polygonColor);
			//设置透明度
	    	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
	    	g2d.setComposite(ac);
			g2d.fillPolygon(xPoints, yPoints, nPoints);
			fos = new FileOutputStream(toPath);
			ImageIO.write(image, imageFormat, fos);	
			g2d.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
					if(fos!=null){
						fos.close();
					} 
			}
    }
    
    
    public static void main(String[] args)throws Exception{
        OperateImage imageObj = new OperateImage();
        
        /*String srcPath = "D:/test/fileSource/004.jpg";
    	String toPath = "D:/test/desk/+e004.jpg";
    	int x = 200;
    	int y = 300;
    	int width = 300;
    	int height = 200 ;
    	String readImageFormat = "jpg";
    	String writeImageFormat = "jpg"*/;
        //imageObj.cropImage(srcPath, toPath, x, y, width, height,readImageFormat,writeImageFormat);//剪切图片
        //imageObj.resizeImage(srcPath, toPath, 400, 400);//按指定的长宽重置图形大小
       //imageObj.reduceImageByRatio(srcPath, toPath, 3, 3);//按指定长和宽的比例缩小图形
       //imageObj.enlargementImageByRatio(srcPath, toPath, 2, 2);//按指定长和宽的比例放大图形
       //imageObj.reduceImageEqualProportion(srcPath, toPath, 4);//长高等比例缩小
        //imageObj.enlargementImageEqualProportion(srcPath, toPath, 2);//长高等比例放大
       /* String negativeImagePath = "D:/test/fileSource/004.jpg";
        String additionImagePath = "D:/test/fileSource/005.jpg";
        int x = 200;
        int y = 200;
        String toPath = "D:/test/desk/004+005-rightcenter.jpg";*/
        //imageObj.mergeBothImage(negativeImagePath, additionImagePath, x, y, toPath); //按指定坐标合并图片
        //imageObj.mergeBothImageTopleftcorner(negativeImagePath, additionImagePath, toPath);//合并到左上角
        //imageObj.mergeBothImageToprightcorner(negativeImagePath, additionImagePath, toPath);//合并到右上角
        //imageObj.mergeBothImageLeftbottom(negativeImagePath, additionImagePath, toPath);//合并到左下角
        //imageObj.mergeBothImageRightbottom(negativeImagePath, additionImagePath, toPath);//合并到右下角
        //imageObj.mergeBothImageCenter(negativeImagePath, additionImagePath, toPath);//合并到正中央
        //imageObj.mergeBothImageTopcenter(negativeImagePath, additionImagePath, toPath);//合并到上边中央
        //imageObj.mergeBothImageBottomcenter(negativeImagePath, additionImagePath, toPath);//合并到下边中央
        //imageObj.mergeBothImageLeftcenter(negativeImagePath, additionImagePath, toPath);//合并到左边中央
        //imageObj.mergeBothImageRightcenter(negativeImagePath, additionImagePath, toPath);//合并到右边中央
    	
    	/*
    	String srcImage = "D:/test/fileSource/001.jpg";
    	String toPath = "D:/test/desk/001-gray.jpg";
    	String imageFormat = "jpg";
    	imageObj.grayImage(srcImage, toPath, imageFormat);//图片灰化
    	 */    
    	
    	/*
    	String firstSrcImagePath = "D:/test/desk/003.jpg";
    	String secondSrcImagePath = "D:/test/desk/004.jpg";
    	String imageFormat = "jpg";
    	String toPath = "D:/test/desk/003-004-join.jpg";
    	imageObj.joinImagesHorizontal(firstSrcImagePath, secondSrcImagePath, imageFormat, toPath);//横向拼接图片
    	*/
    	
    	/*
    	String firstSrcImagePath = "D:/test/desk/001-002-join.jpg";
    	String secondSrcImagePath = "D:/test/desk/003-004-join.jpg";
    	String imageFormat = "jpg";
    	String toPath = "D:/test/desk/all-join.jpg";
    	imageObj.joinImagesVertical(firstSrcImagePath, secondSrcImagePath, imageFormat, toPath);//纵向拼接图片
    	*/
    	
    	/*String srcImagePath = "D:/test/fileSource/002.jpg";
    	int[] xPoints = {20,100,160,270,500}; 
    	int[] yPoints = {30,150,172,295,615};
    	int nPoints = 5; 
    	String toPath = "D:/test/desk/polygon-002.png";
    	imageObj.drawPolygon(srcImagePath, xPoints, yPoints, nPoints, Color.MAGENTA, "jpg", toPath); //根据坐标数组绘制多边形
    	*/

    	/*String srcImagePath = "D:/test/fileSource/004.jpg";
    	String appendImagePath = "D:/test/fileSource/005.jpg";
    	float alpha = 0.2F;
    	String  font = "宋体";
    	int fontStyle = Font.PLAIN;
    	int fontSize = 32;
    	Color color = Color.RED;
    	String inputWords = "图片上设置水印文字 ---- 宋体 普通字体 32号字 红色 透明度0.5";
    	int x = 20;
    	int y = 40;
    	String imageFormat = "jpg";
    	String toPath = "D:/test/desk/alphaI2I-001.png";*/
    	//imageObj.alphaWords2Image(srcImagePath, alpha, font, fontStyle, fontSize, color, inputWords, x, y, imageFormat, toPath); //设置文字水印
    	//imageObj.alphaImage2Image(srcImagePath, appendImagePath, alpha, x, y, 300, 200, imageFormat, toPath);//设置图片水印
    	
    	/*
    	String srcImagePath = "D:/test/fileSource/003.jpg";
    	int[] xPoints = {100,150,200,240,300};
    	int[] yPoints = {200,60,280,160,100};
    	int nPoints = 5;
    	Color lineColor = Color.RED;
    	String toPath = "D:/test/desk/polyline-003.jpg";
    	String imageFormat = "jpg";
    	imageObj.drawPolyline(srcImagePath, xPoints, yPoints, nPoints, lineColor,toPath, imageFormat);//画折线
    	 */    	
    	
    	/*
    	int x1 = 50;
    	int y1 = 100;
    	int x2 = 600;
    	int y2 = 150;
    	Color lineColor = Color.BLUE;
    	imageObj.drawLine(srcImagePath, x1, y1, x2, y2, lineColor,toPath, imageFormat);//画线段
    	 */    	
    	
    	/*
    	String srcImagePath = "D:/test/fileSource/002.jpg";
    	int x = 400;
    	int y = 500;
    	int width = 10;
    	int height = 10;
    	Color ovalColor = Color.RED;
    	String imageFormat = "jpg";
    	String toPath = "D:/test/desk/point-002.jpg";
    	imageObj.drawPoint(srcImagePath, x, y, width, height, ovalColor, imageFormat, toPath);//画一个圆点
    	*/
    	
    	/*List pointList = new ArrayList();
    	Point p1 = new Point(60,80);
    	pointList.add(p1);
    	Point p2 = new Point(160,80);
    	pointList.add(p2);
    	Point p3 = new Point(60,180);
    	pointList.add(p3);
    	Point p4 = new Point(260,180);
    	pointList.add(p4);
    	Point p5 = new Point(460,380);
    	pointList.add(p5);
    	String srcImagePath = "D:/test/fileSource/004.jpg";
    	int width = 10;
    	int height = 10;
    	Color ovalColor = Color.RED;
    	String imageFormat = "jpg";
    	String toPath = "D:/test/desk/points-004.jpg";
    	imageObj.drawPoints(srcImagePath, pointList, width, height, ovalColor, imageFormat, toPath);//画出一组（多个）点
    	 */   
    	
    	/*
    	int[] xPoints = {50,100,180,400,600};
    	int[] yPoints = {200,100,160,300,640};
    	int nPoints = 5;
    	Color lineColor = Color.PINK;
    	String srcImagePath = "D:/test/fileSource/003.jpg";
    	String toPath = "D:/test/desk/showpoints-003.jpg";
    	imageObj.drawPolylineShowPoints(srcImagePath, xPoints, yPoints, nPoints, lineColor, width, height, ovalColor, toPath, imageFormat);//画折线并突出显示点
    	 */   
    	
    	/*
    	String srcImagePath ="D:/test/fileSource/004.jpg"; 
    	int[] xPoints ={50,90,180,320,640};
    	int[] yPoints ={200,300,120,240,360};
    	int nPoints = 5;
    	Color polygonColor = Color.PINK;
    	float alpha = (float) 0.2;
    	String imageFormat ="jpg";
    	String toPath ="D:/test/desk/drawalpha-004.jpg";
    	imageObj.drawAndAlphaPolygon(srcImagePath, xPoints, yPoints, nPoints, polygonColor, alpha, imageFormat, toPath);
    	*/
    	/*
    	String negativeImagePath = "D:/test/fileSource/001.jpg";
    	String additionImagePath = "D:/test/fileSource/006.png";
    	String  toPath = "D:/test/fileSource/001.jpg";
    	long start = System.currentTimeMillis();
    	for(int i=0;i<1000;i++){
    		Random rand = new Random();
    		int x = rand.nextInt(1024);
    		int y =  rand.nextInt(768);
    		imageObj.mergeBothImage(negativeImagePath, additionImagePath, x, y, toPath);//每次附加合并一张图片(循环若干次)
    	}
    	long end = System.currentTimeMillis();
    	System.out.println(end-start);*/
    	//100 -- 45844
    	//1000 -- 411156
    	/*改进思路：将mergeBothImage方法 修改为mergeImageList方法，
    	通过将图片的坐标点装入list容器，然后再取出一来在方法中一次性与图片合并,
    	不再每次都打开底图、保存合成图片，关闭流*/

    	//叠加组合图像
    	/*String negativeImagePath = "D:/test/fileSource/001.jpg";
    	String  toPath = "D:/test/fileSource/001.jpg";
    	String additionImagePath = "D:/test/fileSource/007.png";
    	List additionImageList = new ArrayList();
    	int count = 0;
    	for(int i=0;i<100;i++){//为什么总是连续生成一样的随机数？？？
    		Random rand = new Random();
    		int x = rand.nextInt(1020);
    		String xStr = x+"";
    		int y =  rand.nextInt(760);
    		String yStr = y +"";
    		String[] str = {xStr,yStr,additionImagePath};
    		additionImageList.add(str);
    		count++;
    		//System.out.println(xStr+"   :     "+yStr);
    	}
    	System.out.println(count);
    	long start = System.currentTimeMillis();
    	imageObj.mergeImageList(negativeImagePath, additionImageList,"jpg", toPath);
    	long end = System.currentTimeMillis();
    	System.out.println(end-start);*/
    	//                                第一次        第二次      第三次
    	//100张耗时(毫秒)		--2003			1792			1869           1747        	1871        	1793
    	//1000张耗时(毫秒)	--15334			15200		15236         15903			16028		15545
    	//10000张耗时(毫秒)	--153010		153340 		152673       154978  		156506 		154854                               
    	//如果list.size()<=100,则调用此方法，
    	//如果list.size()>100,则调用Jmagick的方法。
    	
    	/*List iamgePathList = new ArrayList();		// D:/test/16a/
    	iamgePathList.add("D:/test/16a/12384_2492.jpg");
    	iamgePathList.add("D:/test/16a/12384_2493.jpg");
    	iamgePathList.add("D:/test/16a/12384_2494.jpg");
    	iamgePathList.add("D:/test/16a/12384_2495.jpg");
    	iamgePathList.add("D:/test/16a/12384_2496.jpg");
    	iamgePathList.add("D:/test/16a/12384_2497.jpg");
    	iamgePathList.add("D:/test/16a/12384_2498.jpg");
    	iamgePathList.add("D:/test/16a/12384_2499.jpg");
    	iamgePathList.add("D:/test/16a/12384_2500.jpg");
    	iamgePathList.add("D:/test/16a/12384_2501.jpg");
    	iamgePathList.add("D:/test/16a/12384_2502.jpg");*/
    	//String imageFormat = "jpg";
    	//String toPath = "D:/test/desk/16a_v1.jpg";
    	//imageObj.joinImageListHorizontal(iamgePathList, imageFormat, toPath);
    	
    	/*String imageFormat = "jpg";
    	String[] pics1 = {"D:/test/16a/12384_2502.jpg","D:/test/16a/12384_2501.jpg",
    			"D:/test/16a/12384_2500.jpg","D:/test/16a/12384_2499.jpg","D:/test/16a/12384_2498.jpg",
    			"D:/test/16a/12384_2497.jpg","D:/test/16a/12384_2496.jpg","D:/test/16a/12384_2495.jpg",
    			"D:/test/16a/12384_2494.jpg","D:/test/16a/12384_2493.jpg","D:/test/16a/12384_2492.jpg"};
    	
    	String[] pics2 = {"D:/test/16a/12385_2502.jpg","D:/test/16a/12385_2501.jpg",
    			"D:/test/16a/12385_2500.jpg","D:/test/16a/12385_2499.jpg","D:/test/16a/12385_2498.jpg",
    			"D:/test/16a/12385_2497.jpg","D:/test/16a/12385_2496.jpg","D:/test/16a/12385_2495.jpg",
    			"D:/test/16a/12385_2494.jpg","D:/test/16a/12385_2493.jpg","D:/test/16a/12385_2492.jpg"};
    	
    	String[] pics3 = {"D:/test/16a/12386_2502.jpg","D:/test/16a/12386_2501.jpg",
    			"D:/test/16a/12386_2500.jpg","D:/test/16a/12386_2499.jpg","D:/test/16a/12386_2498.jpg",
    			"D:/test/16a/12386_2497.jpg","D:/test/16a/12386_2496.jpg","D:/test/16a/12386_2495.jpg",
    			"D:/test/16a/12386_2494.jpg","D:/test/16a/12386_2493.jpg","D:/test/16a/12386_2492.jpg"};
    	
    	String[] pics4 = {"D:/test/16a/12387_2502.jpg","D:/test/16a/12387_2501.jpg",
    			"D:/test/16a/12387_2500.jpg","D:/test/16a/12387_2499.jpg","D:/test/16a/12387_2498.jpg",
    			"D:/test/16a/12387_2497.jpg","D:/test/16a/12387_2496.jpg","D:/test/16a/12387_2495.jpg",
    			"D:/test/16a/12387_2494.jpg","D:/test/16a/12387_2493.jpg","D:/test/16a/12387_2492.jpg"};
    	
    	String[] pics5 = {"D:/test/16a/12388_2502.jpg","D:/test/16a/12388_2501.jpg",
    			"D:/test/16a/12388_2500.jpg","D:/test/16a/12388_2499.jpg","D:/test/16a/12388_2498.jpg",
    			"D:/test/16a/12388_2497.jpg","D:/test/16a/12388_2496.jpg","D:/test/16a/12388_2495.jpg",
    			"D:/test/16a/12388_2494.jpg","D:/test/16a/12388_2493.jpg","D:/test/16a/12388_2492.jpg"};
    	
    	String[] pics6 = {"D:/test/16a/12389_2502.jpg","D:/test/16a/12389_2501.jpg",
    			"D:/test/16a/12389_2500.jpg","D:/test/16a/12389_2499.jpg","D:/test/16a/12389_2498.jpg",
    			"D:/test/16a/12389_2497.jpg","D:/test/16a/12389_2496.jpg","D:/test/16a/12389_2495.jpg",
    			"D:/test/16a/12389_2494.jpg","D:/test/16a/12389_2493.jpg","D:/test/16a/12389_2492.jpg"};
    	
    	String toPath1 = "D:/test/desk/16a_v1.jpg";
    	String toPath2 = "D:/test/desk/16a_v2.jpg";
    	String toPath3 = "D:/test/desk/16a_v3.jpg";
    	String toPath4 = "D:/test/desk/16a_v4.jpg";
    	String toPath5 = "D:/test/desk/16a_v5.jpg";
    	String toPath6 = "D:/test/desk/16a_v6.jpg";
    	
    	String[] pics7 = {toPath1,toPath2,toPath3,toPath4,toPath5,toPath6};
    	String toPath7 = "D:/test/desk/16a_h1.jpg";
    	
    	long start = System.currentTimeMillis();
    	imageObj.joinImageListVertical(pics1, imageFormat, toPath1);
    	imageObj.joinImageListVertical(pics2, imageFormat, toPath2);
    	imageObj.joinImageListVertical(pics3, imageFormat, toPath3);
    	imageObj.joinImageListVertical(pics4, imageFormat, toPath4);
    	imageObj.joinImageListVertical(pics5, imageFormat, toPath5);
    	imageObj.joinImageListVertical(pics6, imageFormat, toPath6);
    	
    	imageObj.joinImageListHorizontal(pics7, imageFormat, toPath7);
    	long end = System.currentTimeMillis();
    	System.out.println(end-start);*/
    	
    	String str = "北京\n上海\n广州\n深圳";
    	System.out.println(str);
    	String path = "c:/relevantdata.txt";
    	FileOutputStream fops = new FileOutputStream(path);
    	fops.write(str.getBytes());
    	
    	BufferedReader inputStream = new BufferedReader(new FileReader(new File(path)));
    	String mrMsg = "";
    	while((mrMsg = inputStream.readLine())!=null){
    	System.out.println(mrMsg);
    	}
    	}
    	//数量		11			11x6
    	//纵向		375		
    	//横向		391		3250
}
