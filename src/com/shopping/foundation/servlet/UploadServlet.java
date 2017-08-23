 package com.shopping.foundation.servlet;
 
 import java.io.File;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.util.Iterator;
 import java.util.List;
 import java.util.UUID;
 import javax.servlet.ServletContext;
 import javax.servlet.ServletException;
 import javax.servlet.http.HttpServlet;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;
 import org.apache.commons.fileupload.FileItem;
 import org.apache.commons.fileupload.FileUploadException;
 import org.apache.commons.fileupload.disk.DiskFileItemFactory;
 import org.apache.commons.fileupload.servlet.ServletFileUpload;
 
 public class UploadServlet extends HttpServlet
 {
   public void doPost(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
   {
     String savePath = request.getSession().getServletContext()
       .getRealPath("/") + "space" + File.separator + 
       request.getParameter("path") + File.separator;
     File f1 = new File(savePath);
 
     if (!f1.exists()) {
       f1.mkdirs();
     }
     DiskFileItemFactory fac = new DiskFileItemFactory();
     ServletFileUpload upload = new ServletFileUpload(fac);
     upload.setHeaderEncoding("utf-8");
     List fileList = null;
     try {
       fileList = upload.parseRequest(request);
     } catch (FileUploadException ex) {
       return;
     }
     Iterator it = fileList.iterator();
     String name = "";
     String extName = "";
     while (it.hasNext()) {
       FileItem item = (FileItem)it.next();
       if (!item.isFormField()) {
         name = item.getName();
         long size = item.getSize();
         String type = item.getContentType();
         if ((name == null) || (name.trim().equals("")))
         {
           continue;
         }
         if (name.lastIndexOf(".") >= 0) {
           extName = name.substring(name.lastIndexOf("."));
         }
         File file = null;
         do
         {
           name = UUID.randomUUID().toString();
           file = new File(savePath + name + extName);
         }while (file.exists());
         File saveFile = new File(savePath + name + extName);
         try {
           item.write(saveFile);
         } catch (Exception e) {
           e.printStackTrace();
         }
       }
     }
     response.getWriter().print(name + extName);
   }
 }



 
 