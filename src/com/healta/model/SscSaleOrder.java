package com.healta.model;

import java.util.List;

import com.healta.model.base.BaseSscSaleOrder;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class SscSaleOrder extends BaseSscSaleOrder<SscSaleOrder> {
	public static final SscSaleOrder dao = new SscSaleOrder().dao();

	public Record getNewNum() {
		String sql="select count(*) AS num from ssc_sale_order t where   CONVERT(varchar(100),t.cteatetime,23)=CONVERT(varchar(100), GETDATE(), 23);";
		return Db.findFirst(sql);
	}

	public List<Record> getNodes(String status) {
		String sql="select t.id,t.sale_no  from ssc_sale_order t  where t.status='"+status+"' and deleted=0";
		List<Record> list=Db.find(sql);
		return list;
	}

	public Record findSscSaleOrder(String id) {
		String sql="select *  from ssc_sale_order t  where t.id='"+id+"'";
		return Db.findFirst(sql);
	}

	public List<Record> findSscSaleOrderLines(String id) {
		StringBuffer sb =new StringBuffer();
		sb.append(" select ssol.*,(select sum(onavailable) from stock s ");
		sb.append(" where s.goods_id=ssol.goods_id");
		sb.append("  and s.com_lot=ssol .com_lot");
		sb.append("  and s.efficiency_date=ssol.efficiency_date");
		sb.append("  and s.expire_date=ssol.expire_date) as onavailable,sale_qty as order_qty");
		sb.append(" from ssc_sale_order_lines ssol where ssol.ssc_sale_order_id='"+id+"'");
		return Db.find(sb.toString());
	}

	public boolean deleteSscSaleOrder(Integer id) {
		String sql="update ssc_sale_order  set deleted=1 where id='"+id+"'";
		int i=Db.update(sql);
		if(i==1) {
			return true;
		}
		return false;
	}
}