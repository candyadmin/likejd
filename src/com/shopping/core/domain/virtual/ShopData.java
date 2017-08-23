package com.shopping.core.domain.virtual;

import java.util.Date;

public class ShopData
{
  private String name;
  private String phyPath;
  private double size;
  private int boundSize;
  private Date addTime;

  public String getName()
  {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhyPath() {
    return this.phyPath;
  }

  public void setPhyPath(String phyPath) {
    this.phyPath = phyPath;
  }

  public double getSize() {
    return this.size;
  }

  public void setSize(double size) {
    this.size = size;
  }

  public Date getAddTime() {
    return this.addTime;
  }

  public void setAddTime(Date addTime) {
    this.addTime = addTime;
  }

  public int getBoundSize() {
    return this.boundSize;
  }

  public void setBoundSize(int boundSize) {
    this.boundSize = boundSize;
  }
}