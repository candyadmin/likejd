package com.shopping.foundation.dao;

import com.shopping.core.base.GenericDAO;
import com.shopping.foundation.domain.Message;
import org.springframework.stereotype.Repository;

@Repository("messageDAO")
public class MessageDAO extends GenericDAO<Message>
{
}

