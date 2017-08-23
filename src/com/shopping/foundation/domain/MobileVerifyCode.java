 package com.shopping.foundation.domain;
 
 import javax.persistence.Entity;
 import javax.persistence.Table;

import com.shopping.core.domain.IdEntity;
 
 @Entity
 @Table(name="shopping_mobileverifycode")
 public class MobileVerifyCode extends IdEntity
 {
   private String mobile;
   private String code;
 
   public String getMobile()
   {
     return this.mobile;
   }
 
   public void setMobile(String mobile) {
     this.mobile = mobile;
   }
 
   public String getCode() {
     return this.code;
   }
 
   public void setCode(String code) {
     this.code = code;
   }
 }



 
 