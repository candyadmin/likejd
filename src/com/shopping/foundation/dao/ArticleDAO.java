package com.shopping.foundation.dao;

import com.shopping.core.base.GenericDAO;
import com.shopping.foundation.domain.Article;
import org.springframework.stereotype.Repository;

@Repository("articleDAO")
public class ArticleDAO extends GenericDAO<Article>
{
}
