 package com.shopping.foundation.domain;
 
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.Entity;
 import javax.persistence.OneToMany;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_rolegroup")
 public class RoleGroup extends IdEntity
 {
   //名称
   private String name;
   //序列
   private int sequence;
   //类型
   private String type;
 
   //角色
   @OneToMany(mappedBy="rg")
   private List<Role> roles = new ArrayList();
 
   public String getName() {
     return this.name;
   }
 
   public void setName(String name) {
     this.name = name;
   }
 
   public int getSequence() {
     return this.sequence;
   }
 
   public void setSequence(int sequence) {
     this.sequence = sequence;
   }
 
   public List<Role> getRoles() {
     return this.roles;
   }
 
   public void setRoles(List<Role> roles) {
     this.roles = roles;
   }
 
   public String getType() {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }
 }



 
 