/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 枚举类型处理器
 * 因为数据库不存在枚举类型，所以讲枚举类型持久化到数据库有两种方式，Enum.name <=> String 和 Enum.ordinal <=> int 。EnumTypeHandler 是前者，EnumOrdinalTypeHandler 是后者
 *
 * @author Clinton Begin
 */
public class EnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

  /**
   * 枚举类
   */
  private final Class<E> type;

  public EnumTypeHandler(Class<E> type) {
    if (type == null) {
      throw new IllegalArgumentException("Type argument cannot be null");
    }
    this.type = type;
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
    // 设置参数值为枚举类的 name 字符串值
    if (jdbcType == null) {
      ps.setString(i, parameter.name());
    } else {
      ps.setObject(i, parameter.name(), jdbcType.TYPE_CODE); // see r3589
    }
  }

  @Override
  public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
    // 获得该列的 String 值
    String s = rs.getString(columnName);
    // 将 String 转化为枚举
    return s == null ? null : Enum.valueOf(type, s);
  }

  @Override
  public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    // 获得该列的 String 值
    String s = rs.getString(columnIndex);
    // 将 String 转化为枚举
    return s == null ? null : Enum.valueOf(type, s);
  }

  @Override
  public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    // 获得该列的 String 值
    String s = cs.getString(columnIndex);
    // 将 String 转化为枚举
    return s == null ? null : Enum.valueOf(type, s);
  }
}
