/*
 * SocialNetworksView.java
 */
package cl.b9.socialNetwork.gui;

import cl.b9.socialNetwork.jung.SNNodeLabeler;
import cl.b9.socialNetwork.jung.SNEdgeLabeler;
import cl.b9.socialNetwork.*;
import cl.b9.socialNetwork.GraphPanel;
import cl.b9.socialNetwork.persistence.ObjectManager;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;
import javax.swing.Timer;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Logger;

/**
 * The application's main frame.
 */
public class SocialNetworksView extends FrameView implements WindowListener{

    public GraphPanel snGraph;
    private static Logger logger = Logger.getLogger(SocialNetworksView.class);
    FileNameExtensionFilter filterPNG = new FileNameExtensionFilter("Archivo PNG", "png");
    FileNameExtensionFilter filterSNM = new FileNameExtensionFilter("Archivo SNM", "snm");
    FileNameExtensionFilter filterNET = new FileNameExtensionFilter("Archivo Pajek", "net");

    public SocialNetworksView(SingleFrameApplication app) {
        super(app);
        this.getFrame().addWindowListener(this);
        SNDirector director = SNDirector.getInstance();
        //inicializamos la conexion a la base de datos
       
        initComponents();
   
        snGraph = new GraphPanel(this.getFrame(),800,600);
        director.setMainGraph(snGraph);
        
 
        graphPanel.add(snGraph.getScrollPane());
        //graphPanel.add(snGraph.getSatellite());
   
        
        //se agregan los layouts a la barra de menu
        Iterator<LayoutContainer> it = SNDirector.getInstance().getLayouts().iterator();
        while(it.hasNext()){
            LayoutContainer lc = it.next();
            String name = lc.getName();
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(new LayoutMenuActionListener(lc.getLayout()));
            item.setToolTipText(lc.getDescription());
            layoutMenu.add(item);
            
        }


        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");

        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            }
        });
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        try {
            SNDirector.getInstance().redrawFromDatabase();
        }
        catch(Exception E){
            Popup.showError(this.getFrame(), "Error al recuperar desde la base de datos, se procedera a crear un nuevo archivo");
            SNDirector.getInstance().reset();
        }
      
        JPanel p = snGraph.getSatellite();
        satelite.setContentPane(p);
        satelite.setSize(p.getSize());
        //satelite.setVisible(true);
        WindowResizeListener l = new WindowResizeListener() {
            public void windowResized(WindowResizeEvent e) {
                snGraph.resizeSatellite(e.getNewSize(),e.getOldSize());
                
            }
        };
        WindowResizeMonitor.register(satelite, l);

        
        familiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel tcm = familiesTable.getColumnModel();
        TableColumn tc = tcm.getColumn(0);
        tc.setCellRenderer(new CellColorRenderer());
        tc.setPreferredWidth(10);
        tc = tcm.getColumn(1);
        tc.setPreferredWidth(125);
        familiesTable.setColumnSelectionAllowed(false);
        familiesTable.setRowSelectionAllowed(true);
        familiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        relationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tcm = relationsTable.getColumnModel();
        tc = tcm.getColumn(0);
        tc.setCellRenderer(new CellColorRenderer());
        tc.setPreferredWidth(10);
        tc = tcm.getColumn(1);
        tc.setPreferredWidth(125);
        relationsTable.setColumnSelectionAllowed(false);
        relationsTable.setRowSelectionAllowed(true);
        relationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        MouseListener tableMouseListener = new TableMouseListener();
        familiesTable.addMouseListener(tableMouseListener);
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = SocialNetworksApp.getApplication().getMainFrame();
            aboutBox = new SocialNetworksAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        SocialNetworksApp.getApplication().show(aboutBox);
    }

    /**
     * Carga un archivo externo
     * @param selectedFile
     */
    private void importar(File file) {
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        try {
            SNDirector.getInstance().load(file);
            progressBar.setVisible(false);
        } catch (Exception ex) {
            Popup.showError(this.getFrame(), ex.getLocalizedMessage());
            java.util.logging.Logger.getLogger(SocialNetworksView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        toolPanel = new javax.swing.JPanel();
        btnSearch = new javax.swing.JButton();
        btnNewRelationMode = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        familiesTable = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        relationsTable = new javax.swing.JTable();
        graphPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        importMenuItem = new javax.swing.JMenuItem();
        exportMenuItem = new javax.swing.JMenuItem();
        saveToImage = new javax.swing.JMenuItem();
        exportPajek = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        editUndo = new javax.swing.JMenuItem();
        showEdgeLabels = new javax.swing.JCheckBoxMenuItem();
        showActorNames = new javax.swing.JCheckBoxMenuItem();
        showRelationsAsNodes = new javax.swing.JCheckBoxMenuItem();
        adminFamilies = new javax.swing.JMenuItem();
        mnuSearchActor = new javax.swing.JMenuItem();
        debug = new javax.swing.JCheckBoxMenuItem();
        layoutMenu = new javax.swing.JMenu();
        mnuActor = new javax.swing.JMenu();
        btnNewType = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JMenu mnuHelp = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jComboBox1 = new javax.swing.JComboBox();
        fileChooser = new javax.swing.JFileChooser();
        buttonGroup1 = new javax.swing.ButtonGroup();
        satelite = new javax.swing.JDialog();

        mainPanel.setName("mainPanel"); // NOI18N

        toolPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        toolPanel.setName("toolPanel"); // NOI18N

        btnSearch.setIcon(null);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(cl.b9.socialNetwork.SocialNetworksApp.class).getContext().getResourceMap(SocialNetworksView.class);
        btnSearch.setText(resourceMap.getString("text")); // NOI18N
        btnSearch.setFocusable(false);
        btnSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSearch.setName(""); // NOI18N
        btnSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnNewRelationMode.setText(resourceMap.getString("btnNewRelationMode.text")); // NOI18N
        btnNewRelationMode.setName("btnNewRelationMode"); // NOI18N
        btnNewRelationMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewRelationModeActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        familiesTable.setModel(cl.b9.socialNetwork.gui.FamiliesTableModel.getInstance());
        familiesTable.setName("familiesTable"); // NOI18N
        jScrollPane1.setViewportView(familiesTable);

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        relationsTable.setModel(RelationsTableModel.getInstance());
        relationsTable.setName("relationsTable"); // NOI18N
        jScrollPane2.setViewportView(relationsTable);

        javax.swing.GroupLayout toolPanelLayout = new javax.swing.GroupLayout(toolPanel);
        toolPanel.setLayout(toolPanelLayout);
        toolPanelLayout.setHorizontalGroup(
            toolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(toolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                    .addComponent(btnNewRelationMode, 0, 0, Short.MAX_VALUE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
                .addContainerGap())
        );
        toolPanelLayout.setVerticalGroup(
            toolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNewRelationMode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        graphPanel.setBackground(resourceMap.getColor("graphPanel.background")); // NOI18N
        graphPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        graphPanel.setName("graphPanel"); // NOI18N
        graphPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(toolPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(graphPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
                    .addComponent(toolPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setMnemonic('a');
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jMenuItem1.setMnemonic('n');
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem1);

        importMenuItem.setMnemonic('a');
        importMenuItem.setText(resourceMap.getString("importMenuItem.text")); // NOI18N
        importMenuItem.setName("importMenuItem"); // NOI18N
        importMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(importMenuItem);

        exportMenuItem.setText(resourceMap.getString("exportMenuItem.text")); // NOI18N
        exportMenuItem.setName("exportMenuItem"); // NOI18N
        exportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exportMenuItem);

        saveToImage.setText(resourceMap.getString("saveToImage.text")); // NOI18N
        saveToImage.setName("saveToImage"); // NOI18N
        saveToImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveToImageActionPerformed(evt);
            }
        });
        fileMenu.add(saveToImage);

        exportPajek.setText(resourceMap.getString("exportPajek.text")); // NOI18N
        exportPajek.setName("exportPajek"); // NOI18N
        exportPajek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportPajekActionPerformed(evt);
            }
        });
        fileMenu.add(exportPajek);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(cl.b9.socialNetwork.SocialNetworksApp.class).getContext().getActionMap(SocialNetworksView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('E');
        editMenu.setText(resourceMap.getString("editMenu.text")); // NOI18N
        editMenu.setName("editMenu"); // NOI18N

        editUndo.setText(resourceMap.getString("editUndo.text")); // NOI18N
        editUndo.setEnabled(false);
        editUndo.setName("editUndo"); // NOI18N
        editMenu.add(editUndo);

        showEdgeLabels.setSelected(true);
        showEdgeLabels.setText(resourceMap.getString("showEdgeLabels.text")); // NOI18N
        showEdgeLabels.setName("showEdgeLabels"); // NOI18N
        showEdgeLabels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showEdgeLabelsActionPerformed(evt);
            }
        });
        editMenu.add(showEdgeLabels);

        showActorNames.setSelected(true);
        showActorNames.setText(resourceMap.getString("showActorNames.text")); // NOI18N
        showActorNames.setName("showActorNames"); // NOI18N
        showActorNames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showActorNamesActionPerformed(evt);
            }
        });
        editMenu.add(showActorNames);

        showRelationsAsNodes.setText(resourceMap.getString("showRelationsAsNodes.text")); // NOI18N
        showRelationsAsNodes.setName("showRelationsAsNodes"); // NOI18N
        showRelationsAsNodes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showRelationsAsNodesActionPerformed(evt);
            }
        });
        editMenu.add(showRelationsAsNodes);

        adminFamilies.setText(resourceMap.getString("adminFamilies.text")); // NOI18N
        adminFamilies.setName("adminFamilies"); // NOI18N
        adminFamilies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminFamiliesActionPerformed(evt);
            }
        });
        editMenu.add(adminFamilies);

        mnuSearchActor.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        mnuSearchActor.setText(resourceMap.getString("mnuSearchActor.text")); // NOI18N
        mnuSearchActor.setName("mnuSearchActor"); // NOI18N
        mnuSearchActor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSearchActorActionPerformed(evt);
            }
        });
        editMenu.add(mnuSearchActor);

        debug.setText(resourceMap.getString("debug.text")); // NOI18N
        debug.setName("debug"); // NOI18N
        debug.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugActionPerformed(evt);
            }
        });
        editMenu.add(debug);

        menuBar.add(editMenu);

        layoutMenu.setMnemonic('L');
        layoutMenu.setText(resourceMap.getString("layoutMenu.text")); // NOI18N
        layoutMenu.setName("layoutMenu"); // NOI18N
        menuBar.add(layoutMenu);

        mnuActor.setText(resourceMap.getString("mnuActor.text")); // NOI18N
        mnuActor.setName("mnuActor"); // NOI18N

        btnNewType.setText(resourceMap.getString("btnNewType.text")); // NOI18N
        btnNewType.setName("btnNewType"); // NOI18N
        btnNewType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewTypeActionPerformed(evt);
            }
        });
        mnuActor.add(btnNewType);

        jSeparator2.setName("jSeparator2"); // NOI18N
        mnuActor.add(jSeparator2);

        menuBar.add(mnuActor);

        mnuHelp.setText(resourceMap.getString("mnuHelp.text")); // NOI18N
        mnuHelp.setName("mnuHelp"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        mnuHelp.add(aboutMenuItem);

        menuBar.add(mnuHelp);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100%", "50%", "25%", "12.5%" }));
        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(statusMessageLabel)
                        .addGap(159, 159, 159))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 180, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addGap(148, 148, 148)
                        .addComponent(statusAnimationLabel))
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statusMessageLabel)
                            .addComponent(statusAnimationLabel))
                        .addGap(3, 3, 3))
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        fileChooser.setName("fileChooser"); // NOI18N

        satelite.setAlwaysOnTop(true);
        satelite.setName("satelite"); // NOI18N

        javax.swing.GroupLayout sateliteLayout = new javax.swing.GroupLayout(satelite.getContentPane());
        satelite.getContentPane().setLayout(sateliteLayout);
        sateliteLayout.setHorizontalGroup(
            sateliteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        sateliteLayout.setVerticalGroup(
            sateliteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void setFileFilter(JFileChooser fileChooser, FileNameExtensionFilter filter) {
       FileFilter[] d = fileChooser.getChoosableFileFilters();
       for(int i=0;i<d.length;i++){
           fileChooser.removeChoosableFileFilter(d[i]);
       }
       fileChooser.addChoosableFileFilter(filter);
    }
   

private void showEdgeLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showEdgeLabelsActionPerformed
    snGraph.getViewer().getRenderContext().setEdgeLabelTransformer(new SNEdgeLabeler(showEdgeLabels.getState()));
    snGraph.repaint();
}//GEN-LAST:event_showEdgeLabelsActionPerformed

private void importMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importMenuItemActionPerformed
//       fileChooser.addChoosableFileFilter(filterSNM);
    //fileChooser.setFileFilter(filterSNM);
    setFileFilter(fileChooser, filterSNM);
 
    int returnVal = fileChooser.showOpenDialog(this.getComponent());
    

    
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        importar(fileChooser.getSelectedFile());
    }
}//GEN-LAST:event_importMenuItemActionPerformed

private void adminFamiliesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminFamiliesActionPerformed
    FamilyAdministrator fa = new FamilyAdministrator(this.getFrame(),true);
    fa.setVisible(true);
    snGraph.repaint();
}//GEN-LAST:event_adminFamiliesActionPerformed

private void btnNewTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewTypeActionPerformed
    ActorFamilyDialog atp = new ActorFamilyDialog();
    atp.setVisible(true);
}//GEN-LAST:event_btnNewTypeActionPerformed

private void exportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportMenuItemActionPerformed
    //fileChooser.setFileFilter(filterSNM);
    setFileFilter(fileChooser, filterSNM);
    int returnVal = fileChooser.showSaveDialog(this.getComponent());
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        try {
            File dump = SNDirector.getInstance().getDump();
            InputStream in;
            OutputStream out = new FileOutputStream(fileChooser.getSelectedFile());
            GZIPOutputStream gzout = new GZIPOutputStream(out);
            byte[] buf = new byte[1024];
            int len;
            in = new FileInputStream(dump);
            while ((len = in.read(buf)) > 0){
              gzout.write(buf, 0, len);
            }
            in.close();
            gzout.close();
       }    catch (Exception ex) {
                Popup.showError(this.getFrame(), ex.getLocalizedMessage());
                java.util.logging.Logger.getLogger(SocialNetworksView.class.getName()).log(Level.SEVERE, null, ex);
            } 

    }
}//GEN-LAST:event_exportMenuItemActionPerformed

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    SNDirector.getInstance().reset();
}//GEN-LAST:event_jMenuItem1ActionPerformed

private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jComboBox1ActionPerformed

private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
    String sub = jComboBox1.getSelectedItem().toString().substring(0,jComboBox1.getSelectedItem().toString().length()-1);
    int p = Integer.parseInt(sub);
    SNDirector.getInstance().setScale(1f*p/100f);
    
}//GEN-LAST:event_jComboBox1ItemStateChanged

private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
    JPanel p = snGraph.getSatellite();
    satelite.setContentPane(p);
    satelite.setSize(p.getSize());
    satelite.setVisible(true);
}//GEN-LAST:event_btnSearchActionPerformed

private void btnNewRelationModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewRelationModeActionPerformed
    SNDirector.getInstance().setNewRelationMode(btnNewRelationMode.isSelected());
}//GEN-LAST:event_btnNewRelationModeActionPerformed

private void mnuSearchActorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSearchActorActionPerformed
    SearchActorForm sa = new SearchActorForm(this.getFrame());
    sa.setVisible(true);
}//GEN-LAST:event_mnuSearchActorActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    SearchActorForm sa = new SearchActorForm(this.getFrame());
    sa.setVisible(true);
}//GEN-LAST:event_jButton1ActionPerformed

private void debugActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugActionPerformed
    SNDirector.getInstance().setDebug(this.debug.isSelected());
}//GEN-LAST:event_debugActionPerformed

private void showRelationsAsNodesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showRelationsAsNodesActionPerformed
    SNDirector.getInstance().setBinaryRelationsAsNodes(this.showRelationsAsNodes.isSelected());
    
}//GEN-LAST:event_showRelationsAsNodesActionPerformed

private void exportPajekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportPajekActionPerformed
    setFileFilter(fileChooser,filterNET);

    int c = fileChooser.showSaveDialog(this.getFrame());
    if (c == JFileChooser.APPROVE_OPTION){
        File f = fileChooser.getSelectedFile();
        if (f != null ){
                try {
                    SNDirector.getInstance().writePajek(f);
                    JOptionPane.showMessageDialog(this.getFrame(), "Exportación existosa");
                } catch (IOException ex) {
                    Popup.showError(this.getFrame(), "Error al escribir en formato pajek : " + ex.getLocalizedMessage());
                }
        }
    }
}//GEN-LAST:event_exportPajekActionPerformed

private void saveToImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveToImageActionPerformed
    //fileChooser.setFileFilter(filterPNG);
    setFileFilter(fileChooser, filterPNG);
    int c = fileChooser.showSaveDialog(this.getFrame());
    if (c == JFileChooser.APPROVE_OPTION){
        File f = fileChooser.getSelectedFile();
        if (f != null ){
                try {
                    SNDirector.getInstance().writeImage(f);
                    JOptionPane.showMessageDialog(this.getFrame(), "Archivo almacenado exitosamente");
                } catch (Exception ex) {
                    Popup.showError(this.getFrame(), "Error al guardar imagen: " + ex.getLocalizedMessage());
                    logger.error(ex.getLocalizedMessage());
                } 
        }
    }
}//GEN-LAST:event_saveToImageActionPerformed

private void showActorNamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showActorNamesActionPerformed
    snGraph.getViewer().getRenderContext().setVertexLabelTransformer(new SNNodeLabeler(showActorNames.getState()));
    snGraph.repaint();
}//GEN-LAST:event_showActorNamesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem adminFamilies;
    private javax.swing.JToggleButton btnNewRelationMode;
    private javax.swing.JMenuItem btnNewType;
    private javax.swing.JButton btnSearch;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBoxMenuItem debug;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editUndo;
    private javax.swing.JMenuItem exportMenuItem;
    private javax.swing.JMenuItem exportPajek;
    private javax.swing.JTable familiesTable;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JPanel graphPanel;
    private javax.swing.JMenuItem importMenuItem;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JMenu layoutMenu;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu mnuActor;
    private javax.swing.JMenuItem mnuSearchActor;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTable relationsTable;
    private javax.swing.JDialog satelite;
    private javax.swing.JMenuItem saveToImage;
    private javax.swing.JCheckBoxMenuItem showActorNames;
    private javax.swing.JCheckBoxMenuItem showEdgeLabels;
    private javax.swing.JCheckBoxMenuItem showRelationsAsNodes;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JPanel toolPanel;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private JDialog aboutBox;

    public void windowOpened(WindowEvent arg0) {
    
    }

    public void windowClosing(WindowEvent arg0) {
             ObjectManager.getInstance().shutdown();   
             System.exit(0);
    }

    public void windowClosed(WindowEvent arg0) {

        
    }

    public void windowIconified(WindowEvent arg0) {
   
    }

    public void windowDeiconified(WindowEvent arg0) {
    
    }

    public void windowActivated(WindowEvent arg0) {

    }

    public void windowDeactivated(WindowEvent arg0) {

    }
    
    private class LayoutMenuActionListener implements ActionListener{
        private Class layout;
        public LayoutMenuActionListener(Class layout){
            this.layout = layout;
        }

        public void actionPerformed(ActionEvent e) {
            SNDirector.getInstance().changeLayout(layout);
        }
    }
}
