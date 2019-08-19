/**
 * Copyright 2009-2018 the original author or authors.
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
package org.apache.ibatis.reflection.invoker;

import org.apache.ibatis.reflection.Reflector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 方法调用者
 *
 * @author Clinton Begin
 */
public class MethodInvoker implements Invoker {

  /**
   * 方法的参数或返回值类型
   */
  private final Class<?> type;
  /**
   * 指定方法
   */
  private final Method method;

  public MethodInvoker(Method method) {
    this.method = method;

    // 方法只有一个参数时 type 为方法参数类型
    if (method.getParameterTypes().length == 1) {
      type = method.getParameterTypes()[0];
    }
    // 否则为方法返回值
    else {
      type = method.getReturnType();
    }
  }

  /**
   * 在目标对象 target 上调用 method 方法
   *
   * @param target 目标
   * @param args   参数
   * @return
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  @Override
  public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
    try {
      return method.invoke(target, args);
    } catch (IllegalAccessException e) {
      if (Reflector.canControlMemberAccessible()) {
        method.setAccessible(true);
        return method.invoke(target, args);
      } else {
        throw e;
      }
    }
  }

  @Override
  public Class<?> getType() {
    return type;
  }
}
