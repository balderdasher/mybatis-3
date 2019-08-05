/**
 * Copyright 2009-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.parsing;

/**
 * 通用Token解析器
 *
 * @author Clinton Begin
 */
public class GenericTokenParser {

  /**
   * 开始的 Token 字符串 如"{"
   */
  private final String openToken;
  /**
   * 结束的 Token 字符串 如"}"
   */
  private final String closeToken;
  private final TokenHandler handler;

  public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
    this.openToken = openToken;
    this.closeToken = closeToken;
    this.handler = handler;
  }

  public String parse(String text) {
    if (text == null || text.isEmpty()) {
      return "";
    }
    // search open token
    // 查找开始token的位置 未找到则返回待解析字符串text
    int start = text.indexOf(openToken);
    if (start == -1) {
      return text;
    }
    char[] src = text.toCharArray();
    int offset = 0;// 起始查找位置

    // 解析结果
    final StringBuilder builder = new StringBuilder();
    // 匹配到 openToken 和 closeToken 之间的表达式
    StringBuilder expression = null;
    // 循环查找
    while (start > -1) {
      // 转义字符"\"?
      if (start > 0 && src[start - 1] == '\\') {
        // this open token is escaped. remove the backslash and continue.
        // 因为 openToken 前面一个位置是 \ 转义字符，所以忽略 \
        // 解析结果添加openToken之前的内容和转义的openToken
        builder.append(src, offset, start - offset - 1).append(openToken);
        // 设置下一次开始查找的位置offset
        offset = start + openToken.length();
      }
      // 非转义字符
      else {
        // found open token. let's search close token.
        // 创建/重置表达式对象
        if (expression == null) {
          expression = new StringBuilder();
        } else {
          expression.setLength(0);
        }
        // 添加offset到 openToken之间的内容到builder
        builder.append(src, offset, start - offset);
        // 修改offset
        offset = start + openToken.length();
        // 查找结束token 的位置
        int end = text.indexOf(closeToken, offset);
        while (end > -1) {
          // 转义?
          if (end > offset && src[end - 1] == '\\') {
            // this close token is escaped. remove the backslash and continue.
            expression.append(src, offset, end - offset - 1).append(closeToken);
            offset = end + closeToken.length();
            // 继续寻找closeToken的位置
            end = text.indexOf(closeToken, offset);
          }
          // 非转义
          else {
            // 构造开始和结束token之间的表达式
            expression.append(src, offset, end - offset);
            break;
          }
        }
        // 拼接内容
        if (end == -1) {
          // close token was not found.
          // 未找到结束token,直接拼接
          builder.append(src, start, src.length - start);
          offset = src.length;
        } else {
          // 找到结束token时,将开始和结束token之间的表达式交给TokenHandler处理,并将结果添加到builder
          builder.append(handler.handleToken(expression.toString()));
          // 修改offset
          offset = end + closeToken.length();
        }
      }
      // 继续，寻找开始的 openToken 的位置
      start = text.indexOf(openToken, offset);
    }
    // 拼接剩余部分
    if (offset < src.length) {
      builder.append(src, offset, src.length - offset);
    }
    return builder.toString();
  }
}
