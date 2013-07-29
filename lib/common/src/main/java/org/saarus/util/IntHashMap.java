package org.saarus.util;

public class IntHashMap<V> {
  private Entry<V>[] entries ;
  private int size ;
  
  public IntHashMap(int initSize) {
    this.entries = new Entry[initSize] ;
    this.size = 0 ;
  }
  
  public int size() { return size ; }
  
  final public V get(int key) {
    int idx = key % entries.length ;
    if(idx < 0) idx = -1*idx ;
    Entry<V> entry = entries[idx] ;
    while(entry != null) {
      if(entry.key == key) return entry.value ;
      entry = entry.next ;
    }
    return null ;
  }
  
  final public void put(int key, V value) {
    int idx = key % entries.length ;
    if(idx < 0) idx = -1*idx ;
    if(entries[idx] == null) {
      entries[idx] = new Entry<V>(key, value) ;
      size++ ;
    } else {
      boolean add = entries[idx].add(key, value) ;
      if(add) size++ ;
    }
    if(size == 2*entries.length) {
      optimize() ;
    }
  }
  
  final public V remove(int key) {
    int idx = key % entries.length ;
    if(idx < 0) idx = -1*idx ;
    if(entries[idx] == null) return null ;
    
    V retValue = null ;
    if(entries[idx].key == key) {
      Entry<V> removedEntry = entries[idx] ;
      entries[idx] = removedEntry.next ;
      size-- ;
      retValue =  removedEntry.value ;
    } else {
      Entry<V> pentry = entries[idx] ;
      Entry<V> centry = entries[idx].next ;
      while(centry != null) {
        if(centry.key == key) {
          pentry.next = centry.next ;
          size-- ;
          retValue = centry.value ;
          break ;
        } else {
          pentry = centry ;
          centry = centry.next ;
        }
      }
    }
    if(2*size < entries.length && entries.length > 10) {
      optimize() ;
    }
    return retValue ;
  }
  
  public Entry<V>[] entries() { return toArray(this.entries, this.size) ; }
  
  public V[] toArray(V[] holder) {
    int idx = 0 ;
    for(int i = 0; i < entries.length; i++) {
      if(entries[i] != null) {
        Entry<V> entry = entries[i] ;
        while(entry != null) {
          holder[idx] = entry.value ;
          entry = entry.next ;
          idx++ ;
        }
      }
    }
    return holder ;
  }
  
  public void clear() {
    this.entries = new Entry[15] ;
    this.size = 0 ;
  }
  
  private void optimize() {
    Entry<V>[] array = toArray(this.entries, this.size) ;
    this.entries = new Entry[size + size/2] ;
    this.size = 0 ;
    for(int i = 0; i < array.length; i++) {
      Entry<V> entry = array[i] ;
      entry.next = null ;
      int idx = entry.key % entries.length ;
      if(idx < 0) idx = -1*idx ;
      if(entries[idx] == null) {
        entries[idx] = entry ;
        size++ ;
      } else {
        boolean add = entries[idx].add(entry) ;
        if(add) size++ ;
      }
    }
  }

  
  private Entry<V>[] toArray(Entry<V>[] entries, int size) {
    Entry<V>[] array = new Entry[size] ;
    int idx = 0 ;
    for(int i = 0; i < entries.length; i++) {
      if(entries[i] != null) {
        Entry<V> entry = entries[i] ;
        while(entry != null) {
          array[idx] = entry ;
          entry = entry.next ;
          idx++ ;
        }
      }
    }
    return array ;
  }
  
  static public class Entry<V> {
    int key ;
    V value ;
    Entry<V> next ;
    
    Entry(int key, V value) {
      this.key = key ;
      this.value = value ;
    }
    
    public int getKey() { return key ; }
    public V getValue() { return value ;}
    
    boolean add(int key, V value) {
      if(this.key == key) {
        this.value = value ;
        return false ;
      } else if(next != null) {
        return this.next.add(key, value) ;
      } else {
        this.next = new Entry<V>(key, value) ;
        return true ;
      }
    }
    
    boolean add(Entry<V> entry) {
      if(this.key == entry.key) {
        this.value = entry.value ;
        return false ;
      } else if(next != null) {
        return this.next.add(entry) ;
      } else {
        this.next = entry ;
        return true ;
      }
    }
  }
}