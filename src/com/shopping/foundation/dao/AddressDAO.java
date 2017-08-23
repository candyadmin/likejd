package com.shopping.foundation.dao;

import com.shopping.core.base.GenericDAO;
import com.shopping.foundation.domain.Address;
import org.springframework.stereotype.Repository;

@Repository("addressDAO")
public class AddressDAO extends GenericDAO<Address>
{
}

