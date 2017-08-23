package com.shopping.foundation.dao;

import com.shopping.core.base.GenericDAO;
import com.shopping.foundation.domain.User;
import org.springframework.stereotype.Repository;

@Repository("userDAO")
public class UserDAO extends GenericDAO<User>
{
}

