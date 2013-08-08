package org.saarus.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.Expression;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.saarus.util.BeanInspector;

abstract public class BeanBindingJTable<T> extends JTable {
  private static final long serialVersionUID = 1L;

  private JPopupMenu rowPopupMenu ;
  protected List<T>  beans ;
  protected List<Expression> expressions = new ArrayList<Expression>() ;
  private String[]   beanProperty ;
  protected String[] columNames ;
  protected Class[]  columnType ;
  BeanInspector<T> beanInspector ;
  
  public BeanBindingJTable() {} 
  
  public BeanBindingJTable(String[] columns, String[]   beanProperty, List<T> beanList) {
    init(columns, beanProperty, beanList) ;
  }
  
  protected void init(String[] columns, String[]   beanProperty, List<T> beanList) {
    this.columNames = columns ;
    this.beanProperty = beanProperty ;
    this.beans = beanList;
    
    T sampleBean = newBean() ;
    beanInspector = BeanInspector.get(newBean().getClass()) ;
    
    try {
      columnType = new Class[beanProperty.length] ;
      for(int i = 0; i < columnType.length; i++) {
        PropertyDescriptor descriptor;
        descriptor = new PropertyDescriptor(beanProperty[i], sampleBean.getClass());
        columnType[i] = descriptor.getReadMethod().getReturnType() ;
        if(columnType[i] == boolean.class) columnType[i] = Boolean.class ;
        else if(columnType[i] == int.class) columnType[i] = Integer.class ;
        else if(columnType[i] == long.class) columnType[i] = Long.class ;
        else if(columnType[i] == double.class) columnType[i] = Double.class ;
        else if(columnType[i] == float.class) columnType[i] = Float.class ;
      }
    } catch (IntrospectionException e) {
      throw new RuntimeException(e) ;
    }
    
    final TableModel model = new AbstractTableModel() {
      public Class getColumnClass(int column) { return getBeanPropertyClass(column) ; }

      public String getColumnName(int column) { return columNames[column]; }

      public int getRowCount() { return beans.size() ;}
      public int getColumnCount() { return columNames.length ; }

      public Object getValueAt(int rowIndex, int columnIndex) {
        return getBeanValueAt(rowIndex, columnIndex);
      }

      public boolean isCellEditable(int row, int col) { return isBeanEditableAt(row, col) ; }

      public void setValueAt(Object aValue, int row, int col) {
        setBeanValueAt(aValue, row, col);
      }
    };
    setModel(model) ;
  }
  
  abstract protected T newBean() ;
  
  public Class getBeanPropertyClass(int column) { return columnType[column] ; }

  abstract protected boolean isBeanEditableAt(int row, int col) ;
  
  public Object getBeanValueAt(int row, int column) {
    T bean = beans.get(row) ;
    String property = beanProperty[column] ;
    return beanInspector.getValue(bean, property) ;
  }
  
  public void setBeanValueAt(Object value, int row, int column) {
    T bean = beans.get(row) ;
    String property = beanProperty[column] ;
    beanInspector.setValue(bean, property, value) ;
    onChangeBeanData(row, bean,  property, value) ;
    fireTableDataChanged() ;
  }
  
  public void onChangeBeanData(int row, T bean, String property, Object val) {
  }
  
  public boolean onAddRow() { return false;  }
  
  public boolean onRemoveRow(T bean, int row) { return false ; }
  
  public void fireTableDataChanged() {
    AbstractTableModel model = (AbstractTableModel) getModel() ;
    model.fireTableDataChanged() ;
  }

  public JPopupMenu createRowPopupMenu() {
    rowPopupMenu = new JPopupMenu();
    MouseListener popupListener = new MouseAdapter() {
      public void mouseReleased(MouseEvent e) {
        if(SwingUtilities.isRightMouseButton(e)) {
          rowPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    };
    addMouseListener(popupListener);
    
    JMenuItem addField = new JMenuItem("Add Row");
    addField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(onAddRow()) fireTableDataChanged();
      }
    });
    rowPopupMenu.add(addField);

    JMenuItem delField = new JMenuItem("Delete Row");
    delField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int row = getSelectedRow() ;
        T bean = beans.get(row) ;
        if(onRemoveRow(bean, row)) {
          fireTableDataChanged();
        }
      }
    });
    rowPopupMenu.add(delField);

    JMenuItem moveUp = new JMenuItem("Move Up");
    moveUp.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int row = getSelectedRow() ;
        if(row > 0) {
          T bean = beans.remove(row) ;
          beans.add(row - 1, bean) ;
          fireTableDataChanged();
        }
      }
    });
    rowPopupMenu.add(moveUp);

    JMenuItem moveDown = new JMenuItem("Move Down");
    moveDown.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int row = getSelectedRow() ;
        if(row < beans.size() - 1) {
          T bean = beans.remove(row) ;
          beans.add(row + 1, bean) ;
          fireTableDataChanged();
        }
      }
    });
    rowPopupMenu.add(moveDown) ;
    return rowPopupMenu ;
  }
  
  public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
    Component c = super.prepareRenderer(renderer, row, column);
    if (c instanceof JComponent) {
      JComponent jc = (JComponent) c;
      Object val = getValueAt(row, column) ;
      if(val != null) {
        jc.setToolTipText(val.toString());
      }
    }
    return c;
  }
}