package com.shopping.foundation.dao;

import com.shopping.core.base.GenericDAO;
import com.shopping.foundation.domain.Favorite;
import org.springframework.stereotype.Repository;

@Repository("favoriteDAO")
public class FavoriteDAO extends GenericDAO<Favorite>
{
}

