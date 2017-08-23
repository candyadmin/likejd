 package com.shopping.foundation.domain;
 
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Lob;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_message")
 public class Message extends IdEntity
 {
   //来自哪个用户
   @ManyToOne(fetch=FetchType.LAZY)
   private User fromUser;
   
   //抵达哪个用户
   @ManyToOne(fetch=FetchType.LAZY)
   private User toUser;
   //状态
   private int status;
 
   //回复状态
   @Column(columnDefinition="int default 0")
   private int reply_status;
   //标题
   private String title;
 
   //内容
   @Lob
   @Column(columnDefinition="LongText")
   private String content;
   
   //父信息
   @ManyToOne(fetch=FetchType.LAZY)
   private Message parent;
   //回复信息
   @OneToMany(mappedBy="parent", cascade={javax.persistence.CascadeType.REMOVE})
   List<Message> replys = new ArrayList();
   //信息类型
   private int type;
 
   public int getType()
   {
     return this.type;
   }
 
   public void setType(int type) {
     this.type = type;
   }
 
   public User getFromUser() {
     return this.fromUser;
   }
 
   public void setFromUser(User fromUser) {
     this.fromUser = fromUser;
   }
 
   public User getToUser() {
     return this.toUser;
   }
 
   public void setToUser(User toUser) {
     this.toUser = toUser;
   }
 
   public int getStatus() {
     return this.status;
   }
 
   public void setStatus(int status) {
     this.status = status;
   }
 
   public String getContent() {
     return this.content;
   }
 
   public void setContent(String content) {
     this.content = content;
   }
 
   public Message getParent() {
     return this.parent;
   }
 
   public void setParent(Message parent) {
     this.parent = parent;
   }
 
   public List<Message> getReplys() {
     return this.replys;
   }
 
   public void setReplys(List<Message> replys) {
     this.replys = replys;
   }
 
   public String getTitle() {
     return this.title;
   }
 
   public void setTitle(String title) {
     this.title = title;
   }
 
   public int getReply_status() {
     return this.reply_status;
   }
 
   public void setReply_status(int reply_status) {
     this.reply_status = reply_status;
   }
 }



 
 