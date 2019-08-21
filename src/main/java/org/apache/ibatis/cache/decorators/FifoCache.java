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

import java.util.Deque;
import java.util.LinkedList;

import org.apache.ibatis.cache.Cache;

/**
 * 基于先进先出淘汰机制的 {@link Cache} 实现类
 * FIFO (first in, first out) cache decorator.
 *
 * @author Clinton Begin
 */
public class FifoCache implements Cache {

  /**
   * 被装饰的 {@link Cache} 对象
   */
  private final Cache delegate;
  /**
   * 缓存 key 的双端队列,默认为 {@link LinkedList}
   */
  private final Deque<Object> keyList;
  /**
   * 缓存大小
   */
  private int size;

  public FifoCache(Cache delegate) {
    this.delegate = delegate;
    this.keyList = new LinkedList<>();
    this.size = 1024;
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

  public void setSize(int size) {
    this.size = size;
  }

  @Override
  public void putObject(Object key, Object value) {
    cycleKeyList(key);  // 循环 key 链表
    delegate.putObject(key, value);
  }

  @Override
  public Object getObject(Object key) {
    return delegate.getObject(key);
  }

  @Override
  public Object removeObject(Object key) {
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    delegate.clear(); // 清空缓存
    keyList.clear();  // 清空key链表
  }

  private void cycleKeyList(Object key) {
    // 将缓存 key 方法链表末尾
    keyList.addLast(key);
    // 如果缓存key的链表长度大于设定的长度 size,则移除最先加入的 key,同时清除此key对应的缓存
    if (keyList.size() > size) {
      Object oldestKey = keyList.removeFirst();// 移除链表头最旧的 key
      delegate.removeObject(oldestKey);
    }
  }

}
