/**
 *    Copyright 2009-2019 the original author or authors.
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
package org.apache.ibatis.cache.decorators;

import org.apache.ibatis.cache.Cache;

/**
 * 实现定时清空整个缓存容器的缓存实现类
 * @author Clinton Begin
 */
public class ScheduledCache implements Cache {

  /**
   * 被装饰的 {@link Cache} 对象
   */
  private final Cache delegate;
  /**
   * 缓存容器清理间隔,单位:毫秒
   */
  protected long clearInterval;
  /**
   * 最后清理时间,单位:毫秒
   */
  protected long lastClear;

  public ScheduledCache(Cache delegate) {
    this.delegate = delegate;
    // 默认清理间隔:1小时
    this.clearInterval = 60 * 60 * 1000; // 1 hour
    this.lastClear = System.currentTimeMillis();
  }

  public void setClearInterval(long clearInterval) {
    this.clearInterval = clearInterval;
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    clearWhenStale(); // 判断是不是要全部清空
    return delegate.getSize();
  }

  @Override
  public void putObject(Object key, Object object) {
    clearWhenStale(); // 判断是不是要全部清空
    delegate.putObject(key, object);
  }

  @Override
  public Object getObject(Object key) {
    // 判断是不是要全部清空
    return clearWhenStale() ? null : delegate.getObject(key);
  }

  @Override
  public Object removeObject(Object key) {
    clearWhenStale(); // 判断是不是要全部清空
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    // 每次清理时记录最后清理时间
    lastClear = System.currentTimeMillis();
    delegate.clear();
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  /**
   * 检测距离上一次清理容器的时间间隔是否超过了设定的清理间隔,以决定是否需要清空容器
   * @return 是否需要清空缓存容器
   */
  private boolean clearWhenStale() {
    // 若距离左后清空时间间隔超过清理间隔时间,则清空容器
    if (System.currentTimeMillis() - lastClear > clearInterval) {
      clear();
      return true;
    }
    return false;
  }

}
