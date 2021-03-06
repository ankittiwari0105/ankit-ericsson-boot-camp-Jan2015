package com.ericsson.raso.sef.core.db.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import com.ericsson.raso.sef.core.db.model.ContractState;

@MappedTypes(ContractState.class)
public class ContractStateTypeHandler extends BaseTypeHandler<ContractState> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, ContractState status, JdbcType jdbcType) throws SQLException {
        if ((jdbcType == null) || (jdbcType.equals(JdbcType.VARCHAR))) {
            preparedStatement.setString(i, status.getName());
        } else {
            throw new UnsupportedOperationException("Unable to convert Subscriber Status to " + jdbcType.toString());
        }
    }

    @Override
    public ContractState getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String name = rs.getString(columnName);
        return (name == null) ? null : ContractState.apiValue(name);
    }

    @Override
    public ContractState getNullableResult(ResultSet rs, int column) throws SQLException {
        String name = rs.getString(column);
        return (name == null) ? null : ContractState.apiValue(name);
    }

    @Override
    public ContractState getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String name = callableStatement.getString(i);
        return (name == null) ? null : ContractState.apiValue(name);
    }

}