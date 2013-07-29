package org.saarus.util;

public interface AllocatorAgent<T extends Comparable<T>> {
  public T next() ;
  public T[] next(T[] holder) ;
  public T nextExclude(T[] excludes) ;
  public T[] getItems() ;
  public int available() ;
  public boolean hasItem(T object) ;
  
}