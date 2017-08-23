package com.shopping.foundation.dao;

import com.shopping.core.base.GenericDAO;
import com.shopping.foundation.domain.Goods;
import org.springframework.stereotype.Repository;

@Repository("goodsDAO")
public class GoodsDAO extends GenericDAO<Goods>
{
}

