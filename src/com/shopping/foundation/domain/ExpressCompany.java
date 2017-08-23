package com.shopping.foundation.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 物流公司
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_express_company")
public class ExpressCompany extends IdEntity {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6688349848432902438L;
	//公司名称
	private String company_name;
	//公司标示
	private String company_mark;
	
	//公司序列
	@Column(columnDefinition = "int default 0")
	private int company_sequence;
	
	//公司类型
	@Column(columnDefinition = "varchar(255) default 'EXPRESS'")
	private String company_type;
	
	//公司状态
	@Column(columnDefinition = "int default 0")
	private int company_status;
	
	//订单
	@OneToMany(mappedBy = "ec")
	List<OrderForm> ofs = new ArrayList<OrderForm>();
	
	
	public List<OrderForm> getOfs() {
		return this.ofs;
	}

	public void setOfs(List<OrderForm> ofs) {
		this.ofs = ofs;
	}

	public String getCompany_name() {
		return this.company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getCompany_mark() {
		return this.company_mark;
	}

	public void setCompany_mark(String company_mark) {
		this.company_mark = company_mark;
	}

	public String getCompany_type() {
		return this.company_type;
	}

	public void setCompany_type(String company_type) {
		this.company_type = company_type;
	}

	public int getCompany_status() {
		return this.company_status;
	}

	public void setCompany_status(int company_status) {
		this.company_status = company_status;
	}

	public int getCompany_sequence() {
		return this.company_sequence;
	}

	public void setCompany_sequence(int company_sequence) {
		this.company_sequence = company_sequence;
	}
}
