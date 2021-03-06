package com.thinkerwolf.hantis.type;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigDecimalTypeHandler extends AbstractTypeHandler<BigDecimal> {

	public BigDecimalTypeHandler() {
		super(BigDecimal.class);
	}
	
	@Override
	protected void setNotNullParameter(PreparedStatement stat, int pos, BigDecimal parameter) throws SQLException {
		stat.setBigDecimal(pos, parameter);
	}

	@Override
	protected BigDecimal getNotNullResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getBigDecimal(columnIndex);
	}

	@Override
	protected BigDecimal getNotNullResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getBigDecimal(columnName);
	}

}