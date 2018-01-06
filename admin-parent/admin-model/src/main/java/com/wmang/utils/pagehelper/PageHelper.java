/*
	The MIT License (MIT)

	Copyright (c) 2014 abel533@gmail.com

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
 */

package com.wmang.utils.pagehelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foundationdb.sql.StandardException;

/**
 * Mybatis - 通用分页拦截器
 */
@Intercepts(@Signature(type = Executor.class, method = "query", args = {
		MappedStatement.class, Object.class, RowBounds.class,
		ResultHandler.class }))
public class PageHelper implements Interceptor {

	private static Logger logger = LoggerFactory.getLogger(PageHelper.class);

	public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
	public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
	public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(
			NullObject.class, DEFAULT_OBJECT_FACTORY,
			DEFAULT_OBJECT_WRAPPER_FACTORY);

	private static class NullObject {
	}

	/**
	 * 反射对象，增加对低版本Mybatis的支持
	 * 
	 * @param object
	 * @return
	 */
	public static MetaObject forObject(Object object) {
		return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY,
				DEFAULT_OBJECT_WRAPPER_FACTORY);
	}

	private static final String BOUND_SQL = "sqlSource.boundSql.sql";

	private static final ThreadLocal<Page> LOCAL_PAGE = new ThreadLocal<Page>();

	private static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<ResultMapping>(
			0);

	// 数据库方言
	/**mysql*/
//	private static String dialect = "mysql";
	/**hsqldb*/
//	private static String dialect = "hsqldb";
	/**oracle*/
	private static String dialect = "oracle";
	// RowBounds参数offset作为PageNum使用 - 默认不使用
	private static boolean offsetAsPageNum = false;
	// RowBounds是否进行count查询 - 默认不查询
	private static boolean rowBoundsWithCount = false;
	// 当设置为true的时候，如果pagesize设置为0（或RowBounds的limit=0），就不执行分页
	private static boolean pageSizeZero = false;

	/**
	 * 开始分页
	 *
	 * @param pageNum
	 * @param pageSize
	 */
	public static void startPage(int pageNum, int pageSize) {
		startPage(pageNum, pageSize, true);
	}

	/**
	 * 开始分页
	 *
	 * @param pageNum
	 * @param pageSize
	 */
	public static void startPage(int pageNum, int pageSize, boolean count) {
		LOCAL_PAGE.set(new Page(pageNum, pageSize, count));
	}

	/**
	 * 获取分页参数
	 *
	 * @param rowBounds
	 * @return
	 */
	private Page getPage(RowBounds rowBounds) {
		Page page = LOCAL_PAGE.get();
		// 移除本地变量
		LOCAL_PAGE.remove();

		if (page == null) {
			if (offsetAsPageNum) {
				page = new Page(rowBounds.getOffset(), rowBounds.getLimit(),
						rowBoundsWithCount);
			} else {
				page = new Page(rowBounds, rowBoundsWithCount);
			}
		}
		return page;
	}

	/**
	 * Mybatis拦截器方法
	 *
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		final Object[] args = invocation.getArgs();
		RowBounds rowBounds = (RowBounds) args[2];
		if (LOCAL_PAGE.get() == null && rowBounds == RowBounds.DEFAULT) {
			return invocation.proceed();
		} else {
			// 忽略RowBounds-否则会进行Mybatis自带的内存分页
			args[2] = RowBounds.DEFAULT;
			MappedStatement ms = (MappedStatement) args[0];
			Object parameterObject = args[1];
			BoundSql boundSql = ms.getBoundSql(parameterObject);

			// 分页信息
			Page page = getPage(rowBounds);
			// pageSizeZero的判断
			if (pageSizeZero && page.getPageSize() == 0) {
				// 执行正常（不分页）查询
				Object result = invocation.proceed();
				// 得到处理结果
				page.addAll((List) result);
				// 这种情况相当于pageSize=total
				page.setPageSize(page.size());
				// 仍然要设置total
				page.setTotal(page.size());
				// 返回结果仍然为Page类型 - 便于后面对接收类型的统一处理
				return page;
			}
			// 创建一个新的MappedStatement
			MappedStatement qs = newMappedStatement(ms, new BoundSqlSqlSource(
					boundSql));
			// 将参数中的MappedStatement替换为新的qs，防止并发异常
			args[0] = qs;
			MetaObject msObject = forObject(qs);
			String sql = (String) msObject.getValue(BOUND_SQL);
			// 简单的通过total的值来判断是否进行count查询
			if (page.isCount()) {
				// 求count - 重写sql
				msObject.setValue(BOUND_SQL, getCountSql(sql));
				logger.info("执行SQL：{}", getSql(ms.getConfiguration(), boundSql).replaceAll("\\s+", " "));
				// 查询总数
				Object result = invocation.proceed();
				// 设置总数
				page.setTotal((Integer) ((List) result).get(0));
				if (page.getTotal() == 0) {
					return page;
				}
			}
			// pageSize>0的时候执行分页查询，pageSize<=0的时候不执行相当于可能只返回了一个count
			if (page.getPageSize() > 0) {
				// 分页sql - 重写sql
				msObject.setValue(BOUND_SQL, getPageSql(sql, page));
				// 恢复类型
				msObject.setValue("resultMaps", ms.getResultMaps());
				// 执行分页查询
				logger.info("执行SQL：{}", getSql(ms.getConfiguration(), boundSql).replaceAll("\\s+", " "));
				Object result = invocation.proceed();
				// 得到处理结果
				page.addAll((List) result);
			}
			// 返回结果
			return page;
		}
	}

	/**
	 * 获取总数sql - 如果要支持其他数据库，修改这里就可以
	 *
	 * @param sql
	 * @return
	 */
	private String getCountSql(String sql) {
		CountSqlParser generator = new CountSqlParser();
		try {
			String newSql = generator.generate(sql);
			logger.info("原始sql：{}", sql.replaceAll("\\s+", " "));
			logger.info("新生成count sql：{}", newSql);
			return newSql;
		} catch (StandardException e) {
			logger.error("getCountSql生成CountSql失败", e);
		}
		return "select count(*) from (" + sql + ") tmp_count";
	}

	/**
	 * 获取分页sql - 如果要支持其他数据库，修改这里就可以
	 *
	 * @param sql
	 * @param page
	 * @return
	 */
	private String getPageSql(String sql, Page page) {
		StringBuilder pageSql = new StringBuilder(200);
		if ("mysql".equals(dialect)) {
			pageSql.append(sql);
			pageSql.append(" limit ").append(page.getStartRow()).append(",")
					.append(page.getPageSize());
			// pageSql.append("select * from (");
			// pageSql.append(sql);
			// pageSql.append(") as tmp_page limit " + page.getStartRow() + ","
			// + page.getPageSize());
		} else if ("hsqldb".equals(dialect)) {
			pageSql.append(sql);
			pageSql.append(" LIMIT " + page.getPageSize() + " OFFSET "
					+ page.getStartRow());
		} else if ("oracle".equals(dialect)) {
			if (page.getPageNum() == 0 && page.getPageSize() < 1000) {
				if (page.getPageSize() == 0) {
					pageSql.append("SELECT /*+first_rows(20) */* ");
				} else {
					pageSql.append("SELECT /*+first_rows(").append(page.getPageSize()).append(") */* ");
				}
				
			} else {
				pageSql.append("SELECT * ");
			}
			pageSql.append(" from ( select temp.*, rownum tmp_row_no from ( ");
			pageSql.append(sql);
			pageSql.append(" ) temp where rownum <= ").append(page.getEndRow());
			pageSql.append(") where tmp_row_no > ").append(page.getStartRow());
		}
		//logger.info("分页 sql：{}", pageSql);
		return pageSql.toString();
	}

	private class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

	/**
	 * 由于MappedStatement是一个全局共享的对象，因而需要复制一个对象来进行操作，防止并发访问导致错误
	 *
	 * @param ms
	 * @param newSqlSource
	 * @return
	 */
	private MappedStatement newMappedStatement(MappedStatement ms,
			SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(
				ms.getConfiguration(), ms.getId() + "_PageHelper",
				newSqlSource, ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
			StringBuilder keyProperties = new StringBuilder();
			for (String keyProperty : ms.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1,
					keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		// 由于resultMaps第一次需要返回int类型的结果，所以这里需要生成一个resultMap - 防止并发错误
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(),
				ms.getId(), int.class, EMPTY_RESULTMAPPING).build();
		resultMaps.add(resultMap);
		builder.resultMaps(resultMaps);
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	/**
	 * 只拦截Executor
	 *
	 * @param target
	 * @return
	 */
	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	/**
	 * 设置属性值
	 *
	 * @param p
	 */
	public void setProperties(Properties p) {
		dialect = p.getProperty("dialect");
		if (dialect == null || "".equals(dialect)) {
			throw new RuntimeException("Mybatis分页插件PageHelper无法获取dialect参数!");
		}
		// offset作为PageNum使用
		String offset = p.getProperty("offsetAsPageNum");
		if (offset != null && "TRUE".equalsIgnoreCase(offset)) {
			offsetAsPageNum = true;
		}
		// RowBounds方式是否做count查询
		String withcount = p.getProperty("rowBoundsWithCount");
		if (withcount != null && "TRUE".equalsIgnoreCase(withcount)) {
			rowBoundsWithCount = true;
		}
		// 当设置为true的时候，如果pagesize设置为0（或RowBounds的limit=0），就不执行分页
		String sizeZero = p.getProperty("pageSizeZero");
		if (sizeZero != null && "TRUE".equalsIgnoreCase(sizeZero)) {
			pageSizeZero = true;
		}
	}
	
	public static String getSql(Configuration configuration, BoundSql boundSql) {
		String sql = showSql(configuration, boundSql);
		StringBuilder str = new StringBuilder(100);
		str.append(sql);
		str.append(";");
		return sql;
	}
	private static String getParameterValue(Object obj) {
		String value = null;
		if (obj instanceof String) {
			value = "'" + obj.toString() + "'";
		} else if (obj instanceof Date) {
			DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
			value = "'" + formatter.format(new Date()) + "'";
		} else {
			if (obj != null) {
				value = obj.toString();
			} else {
				value = "";
			}
		}
		return value;
	}
	public static String showSql(Configuration configuration, BoundSql boundSql) {
		Object parameterObject = boundSql.getParameterObject();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
		if (parameterMappings.size() > 0 && parameterObject != null) {
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
				sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
			} else {
				MetaObject metaObject = configuration.newMetaObject(parameterObject);
				for (ParameterMapping parameterMapping : parameterMappings) {
					String propertyName = parameterMapping.getProperty();
					if (metaObject.hasGetter(propertyName)) {
						Object obj = metaObject.getValue(propertyName);
						sql = sql.replaceFirst("\\?", getParameterValue(obj));
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						Object obj = boundSql.getAdditionalParameter(propertyName);
						sql = sql.replaceFirst("\\?", getParameterValue(obj));
					}
				}
			}
		}
		return sql;
	}
}
