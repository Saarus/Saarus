package org.saarus.knime.uicomp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultTreeModel;

import org.saarus.client.ClientContext;
import org.saarus.client.RESTClient;
import org.saarus.knime.ServiceContext;
import org.saarus.knime.uicomp.JTreeItemSelector.TreeNodeItem;
import org.saarus.service.hadoop.dfs.DFSFile;
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskResult;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitResult;

public class JTreeDFSFileSelector extends JDialog {
  private static final long serialVersionUID = 1L;
  
  public JTreeDFSFileSelector() throws Exception {
    setLayout(new BorderLayout()) ;
    DFSFileNode  root = new DFSFileNode (getFile("/")) ;
    final JTreeItemSelector treeSelector = new JTreeItemSelector(root) ;
    treeSelector.setShowsRootHandles(true);
    
    setAlwaysOnTop(true) ;
    
    JButton okButton = new JButton("OK") ;
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        DFSFileNode selectObj = (DFSFileNode) treeSelector.getLastSelectedPathComponent() ;
        if(selectObj != null) {
          onClickOK((DFSFile) selectObj.getUserObject()) ;
        } else {
          onClickOK(null) ;
        }
      }
    }) ;
    
    add(new JScrollPane(treeSelector), BorderLayout.CENTER) ;
    add(okButton, BorderLayout.SOUTH) ;
  }
  
  public void onClickOK(DFSFile selectFile) {
    System.out.println(selectFile);
  }
  
  static class DFSFileNode extends TreeNodeItem {
    private static final long serialVersionUID = 1L;
    
    
    public DFSFileNode(DFSFile dfsFile) {
      super(dfsFile);
      setAllowsChildren(dfsFile.isDirectory()) ;
    }
    
    protected void loadChildren(final DefaultTreeModel model, final PropertyChangeListener progressListener) {
      if (loaded) return;
      final DFSFile dfsFile = (DFSFile) getUserObject() ;
      SwingWorker<List<TreeNodeItem>, Void> worker = new SwingWorker<List<TreeNodeItem>, Void>() {
        @Override
        protected List<TreeNodeItem> doInBackground() throws Exception {
          setProgress(0);
          List<TreeNodeItem> children = new ArrayList<TreeNodeItem>();
          try {
            DFSFile[] dfsFiles = listFiles(dfsFile.getPath()) ;
            for (int i = 0; i <dfsFiles.length; i++) {
              children.add(new DFSFileNode(dfsFiles[i]));
            }
          } catch(Exception ex) {
            ex.printStackTrace() ;
          }
          setProgress(1);
          return children;
        }

        @Override
        protected void done() {
          try {
            setChildren(get());
            model.nodeStructureChanged(DFSFileNode.this);
          } catch (Exception e) {
            e.printStackTrace();
          }
          super.done();
        }
      } ;
      if (progressListener != null) {
        worker.getPropertyChangeSupport().addPropertyChangeListener("progress", progressListener);
      }
      worker.execute();
    }
  }
  
  static DFSFile[] listFiles(String path) throws Exception {
    ClientContext context = ServiceContext.getInstance().getClientContext() ;
    RESTClient restClient = context.getBean(RESTClient.class) ;
    TaskUnit listTask = new TaskUnit("listFiles", null) ;
    listTask.setId("listFiles") ;
    listTask.getParameters().setString("path", path) ;
    TaskResult tresult = restClient.submitTask(new Task("DFSTaskHandler", listTask)) ;
    TaskUnitResult<DFSFile[]> listResult = 
        (TaskUnitResult<DFSFile[]>) tresult.getTaskUnitResult(listTask.getId()) ;
    DFSFile[] dfsFiles = listResult.getResult() ;
    return dfsFiles ;
  }
  
  static DFSFile getFile(String path) throws Exception {
    ClientContext context = ServiceContext.getInstance().getClientContext() ;
    RESTClient restClient = context.getBean(RESTClient.class) ;
    TaskUnit getTask = new TaskUnit("getFile", null) ;
    getTask.setId("getFile") ;
    getTask.getParameters().setString("path", path) ;
    Task task = new Task("DFSTaskHandler", getTask) ;
    TaskResult tresult = restClient.submitTask(task) ;
    TaskUnitResult<DFSFile> getResult = (TaskUnitResult<DFSFile>) tresult.getTaskUnitResult(getTask.getId()) ;
    return getResult.getResult() ;
  }
  
  
//--------------------------------------TEST------------------------------------------------
  
  public static void main(String[] args) throws Exception {
    //System.setProperty("saarus.rest.url", "http://hadoop1.saarus.org:7080/rest") ;
    System.setProperty("saarus.rest.url", "http://localhost:7080/rest") ;
    JTreeDFSFileSelector dfsFileSelector = new JTreeDFSFileSelector();
    
    dfsFileSelector.setSize(275, 300);
    dfsFileSelector.setVisible(true) ;
    
  }
}
