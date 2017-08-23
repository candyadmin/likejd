package com.shopping.foundation.dao;

import com.shopping.core.base.GenericDAO;
import com.shopping.foundation.domain.Payment;
import org.springframework.stereotype.Repository;

@Repository("paymentDAO")
public class PaymentDAO extends GenericDAO<Payment>
{
}
