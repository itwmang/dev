package com.wmang.utils.pagehelper;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.FromBaseTable;
import com.foundationdb.sql.parser.FromSubquery;
import com.foundationdb.sql.parser.OrderByList;
import com.foundationdb.sql.parser.ParameterNode;
import com.foundationdb.sql.parser.ResultColumnList;
import com.foundationdb.sql.parser.SQLParser;
import com.foundationdb.sql.parser.StatementNode;
import com.foundationdb.sql.unparser.NodeToString;

public class CountSqlParser extends NodeToString {
	private final SQLParser PARSER = new SQLParser();
	private boolean isFirst = true;
	// 增加日志输出处理，获取失败日志。add by pengjingya 2016-02-18
	private static final Logger logger = LoggerFactory.getLogger(CountSqlParser.class);

	public String generate(String sql) throws StandardException {
		StatementNode stmt = PARSER.parseStatement(sql);
		return toString(stmt);
	}
	@Override
	protected String orderByList(OrderByList node) throws StandardException {
		return "";
	}
	@Override
	protected String resultColumnList(ResultColumnList node) throws StandardException {
		if (isFirst) {
			isFirst = false;
			return " count(*) ";
		} else {
			return super.resultColumnList(node);
		}
	}
	// @Override
	// protected String groupByList(GroupByList node) throws StandardException {
	// return "";
	// }
	@Override
	protected String fromBaseTable(FromBaseTable node) throws StandardException {
		String tn = toString(node.getOrigTableName());
		String n = node.getCorrelationName();
		if (n == null)
			return tn;
		else
			// return tn + " AS " + n;
			return tn + " " + n;
	}
	
	@Override
	protected String fromSubquery(FromSubquery node) throws StandardException {
        StringBuilder str = new StringBuilder(toString(node.getSubquery()));
        if (node.getOrderByList() != null) {
            str.append(' ');
            str.append(toString(node.getOrderByList()));
        }
        str.insert(0, '(');
        str.append(')');
        str.append(" ");
        str.append(node.getCorrelationName());
        if (node.getResultColumns() != null) {
            str.append('(');
            str.append(toString(node.getResultColumns()));
            str.append(')');
        }
        return str.toString();
    }
	
	@Override
	protected String parameterNode(ParameterNode node) throws StandardException {
		return " ? ";
	}
	/**
	 * 测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> ls = new ArrayList<String>();
		ls.add(" select tom.order_id, tom.order_flag, tom.order_status, tom.outer_order_id, tom.total_amount, tom.payment_amount, tom.pay_order_no, tom.platform_id, tom.shop_id, tom.order_type, tom.remain_time,tom.outer_create_time, tom.seller_memo, tom.buyer_memo, toa.receiver_name, toa.receiver_addr_detail, toa.receiver_mobile from t_order_main tom left join t_order_addr toa on toa.order_id = tom.order_id WHERE ( tom.shop_id in ( ? , ? , ? , ? , ? , ? ) and tom.order_status = ? and tom.refund_status = ? and tom.order_id in (select distinct toi.order_id from t_order_item toi where toi.store_id = ?) and tom.pay_time >= ? ) order by tom.outer_create_time desc");
		ls.add("SELECT * frOm dual");
		ls.add("Select C1,c2 From tb");
		ls.add("select c1,c2 from tb");
		ls.add("select count(*) from t1");
		ls.add("select c1,c2,c3 from t1 where condi1=1 ");
		ls.add("Select c1,c2,c3 From t1 Where condi1=1 ");
		ls.add("select c1,c2,c3 from t1,t2 where condi3=3 or condi4=5 order by o1,o2");
		ls.add("Select c1,c2,c3 from t1,t2 Where condi3=3 or condi4=5 Order by o1,o2");
		ls.add("select c1,c2,c3 from t1,t2,t3 where condi1=5 and condi6=6 or condi7=7 group by g1,g2");
		ls.add("Select c1,c2,c3 From t1,t2,t3 Where condi1=5 and condi6=6 or condi7=7 Group by g1,g2");
		ls.add("Select c1,c2,c3 From t1,t2,t3 Where condi1=5 and condi6=6 or condi7=7 Group by g1,g2,g3 order by g2,g3");
		ls.add("select * from m_shop a left join m_shop_store b on a.shop_id=b.shop_id where a.shop_id in (?,?,?,?) and a.sss=? group by a.shop_id  order by a.shop_id");
		ls.add("select * from m_shop a left join m_shop_store b on a.shop_id=b.shop_id  group by a.shop_id  order by a.shop_id");
		ls.add("select mi.* from sup_prod_type p1 join sup_prod_type p2 on p2.parent_node_id = p1.prod_code and p2.prod_level = '2' join sup_material_prod mp on mp.prod_code = p2.prod_code join sup_material_info mi on mp.material_id = mi.material_id where p1.prod_level = '1'");
		
		for (String sql : ls) {
			System.out.println("原始：" + sql);
			long start = System.currentTimeMillis();
			CountSqlParser parser = new CountSqlParser();
			try {
				System.out.println("生产Count：" + parser.generate(sql));
			} catch (StandardException e) {
				logger.error("解析报错", e);
			}
			System.out.println("耗时：" + (System.currentTimeMillis() - start)
					+ "ms");
			// System.out.println(sql);
		}
	}
}
