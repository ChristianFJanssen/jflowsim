package jflowsim.view;

import jflowsim.controller.commands.UndoCommand;
import jflowsim.controller.commands.ZoomAllCommand;
import jflowsim.controller.commands.ZoomInCommand;
import jflowsim.controller.commands.ZoomOutCommand;
import java.awt.event.ActionEvent;
import jflowsim.model.ModelManager;
import jflowsim.controller.geobuilder.Geometry2DFactory;
import jflowsim.controller.commands.ClearAllCommand;
import jflowsim.controller.commands.RedoCommand;
import jflowsim.view.img.ImageUtilities;
import jflowsim.controller.mouseeventstrategy.BuildObjectStrategy;
import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.controller.solverbuilder.SolverBuilder;
import jflowsim.controller.solverbuilder.SolverFactory;
import jflowsim.model.numerics.utilities.Scalar;
import jflowsim.view.displaystyle.DisplayStyle;
import jflowsim.view.displaystyle.DisplayStyleManager;
import jflowsim.view.graphics.GraphicGrid;
import jflowsim.view.graphics.GraphicObject;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import jflowsim.view.configdialog.ConfigDialog;
import jflowsim.view.writer.Writer;

public class MainWindow extends javax.swing.JFrame {

    public GraphicViewer viewer;
    private JLabel statusField;
    private ModelManager modelManager;
    private MainWindow mainWindow;
    private DisplayStyleManager displayStyleManager;
    private ConfigDialog configDialog;
    private String _solverName;

    // Konstruktor
    public MainWindow(GraphicViewer viewer, ModelManager modelManager) {
        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }


        this.modelManager = modelManager;
        this.mainWindow = this;
        this.displayStyleManager = new DisplayStyleManager();

        this.configDialog = new ConfigDialog(modelManager, this);
        this.configDialog.setLocation(50, 50);

        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("JFlowSim");
        this.setIconImage(ImageUtilities.createImageIcon("swirl").getImage());

        this.viewer = viewer;
        this.add(viewer, BorderLayout.CENTER);

        this.createToolBarsAndMenus();

        this.createStatusPanel();

        this.pack();
        this.setSize(1024, 800);
        this.setMinimumSize(new Dimension(640, 480));

        this.setVisible(true);
    }

    private void createToolBarsAndMenus() {

        JMenuBar menuBar = new JMenuBar();
        JPanel toolBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));


        AbstractAction action;
        // ------------------------------------------------- //
        // ------------------------------------------------- //
        JMenu fileMenu = new JMenu("File");
        //JToolBar fileToolBar = new JToolBar("File");
        fileMenu.add(new AbstractAction("Beenden", ImageUtilities.createImageIcon("stop", 22, 22)) {

            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        menuBar.add(fileMenu);
        // ------------------------------------------------- //
        // ------------------------------------------------- //
        // ------------------------------------------------- //
//        action = new AbstractAction("Save", ImageUtilities.createImageIcon("save", 22, 22)) {
//
//            public void actionPerformed(ActionEvent e) {
//                new SaveCommand(viewer, modelManager).execute();
//            }
//        };
//        fileMenu.add(action);
//        fileToolBar.add(action);//
//        // ------------------------------------------------- //
//        // ------------------------------------------------- //
//        action = new AbstractAction("Open", ImageUtilities.createImageIcon("open", 22, 22)) {
//
//            public void actionPerformed(ActionEvent e) {
//                new ClearAllCommand(viewer, modelManager).execute();
//                new OpenCommand(viewer, modelManager).execute();
//
//            }
//        };
//        fileMenu.add(action);
//        fileToolBar.add(action);
        // ------------------------------------------------- //
        menuBar.add(fileMenu);
        //       toolBarPanel.add(fileToolBar);
        // ------------------------------------------------- //



        JMenu viewMenu = new JMenu("View");
        JToolBar viewToolBar = new JToolBar("View");
        // ------------------------------------------------- //
        action = new AbstractAction("Zoom In", ImageUtilities.createImageIcon("zoomin", 22, 22)) {

            public void actionPerformed(ActionEvent e) {
                new ZoomInCommand(viewer).execute();
            }
        };
        viewMenu.add(action);
        viewToolBar.add(action);
        // ------------------------------------------------- //
        action = new AbstractAction("Zoom All", ImageUtilities.createImageIcon("zoomall", 22, 22)) {

            public void actionPerformed(ActionEvent e) {
                new ZoomAllCommand(viewer, modelManager).execute();
            }
        };
        viewMenu.add(action);
        viewToolBar.add(action);
        // ------------------------------------------------- //
        action = new AbstractAction("Zoom Out", ImageUtilities.createImageIcon("zoomout", 22, 22)) {

            public void actionPerformed(ActionEvent e) {
                new ZoomOutCommand(viewer).execute();
            }
        };
        viewMenu.add(action);
        viewToolBar.add(action);
        // ------------------------------------------------- //
        menuBar.add(viewMenu);
        toolBarPanel.add(viewToolBar);
        // ------------------------------------------------- //
        // ------------------------------------------------- //
        JMenu addMenu = new JMenu("Add");
        JToolBar addToolBar = new JToolBar("Add Geometry");
        ArrayList<String> keyList = Geometry2DFactory.getInstance().getKeySet();
        for (String key : keyList) {

            // Action Listener
            class CreateObjectAction implements ActionListener {

                private String objName;

                public CreateObjectAction(String objName) {
                    this.objName = objName;
                }

                public void actionPerformed(ActionEvent e) {
                    viewer.setMouseEventStrategy(new BuildObjectStrategy(objName, viewer, modelManager, mainWindow));
                }
            }

            CreateObjectAction listener = new CreateObjectAction(key);
            JButton button = new JButton(ImageUtilities.createImageIcon(key, 22, 22));
            button.setToolTipText(key);
            button.addActionListener(listener);
            JMenuItem item = new JMenuItem(key, ImageUtilities.createImageIcon(key, 22, 22));
            item.addActionListener(listener);
            addMenu.add(item);
            addToolBar.add(button);

        }
        menuBar.add(addMenu);
        toolBarPanel.add(addToolBar);
        // ------------------------------------------------- //



        JMenu simulationMenu = new JMenu("Simulation");

        ArrayList<String> solverKeyList = SolverFactory.getInstance().getKeySet();
        for (String solverName : solverKeyList) {

            JMenu solverMenuItem = new JMenu(solverName);


            ArrayList<String> testCases = SolverFactory.getInstance().getBuilder(solverName).getTestCaseKeySet();

            for (String testCaseName : testCases) {
                // Action Listener
                class CreateSolverAction implements ActionListener {

                    private String solverName;
                    private String testCaseName;

                    public CreateSolverAction(String solverName, String testCaseName) {
                        this.solverName = solverName;
                        this.testCaseName = testCaseName;
                    }

                    public void actionPerformed(ActionEvent e) {

                        if (!modelManager.hasGrid()) {
                            System.out.println("start solver:" + solverName + " testCase:" + testCaseName);

                            _solverName = solverName;

                            SolverBuilder builder = SolverFactory.getInstance().getBuilder(solverName);
                            UniformGrid grid = builder.createGrid(testCaseName);

                            GraphicGrid graphic = new GraphicGrid(grid, displayStyleManager);

                            modelManager.addGrid(grid);
                            viewer.addFirst(graphic);
                            new ZoomAllCommand(viewer, modelManager).execute();

                            Solver solver = builder.createSolver(grid);
                            //solver.addObserver(viewer);
                            solver.addObserver(modelManager);
                            modelManager.startSimulation(solver);

                            configDialog.update();
                        }
                    }
                }

                CreateSolverAction listener = new CreateSolverAction(solverName, testCaseName);
                JMenuItem testCaseMenuItem = new JMenuItem(testCaseName);
                testCaseMenuItem.addActionListener(listener);
                solverMenuItem.add(testCaseMenuItem);

            }
            simulationMenu.add(solverMenuItem);
        }

        simulationMenu.addSeparator();

        JToolBar simulationToolBar = new JToolBar("Simulation");

        action = new AbstractAction("delete simulation", ImageUtilities.createImageIcon("stop", 22, 22)) {

            public void actionPerformed(ActionEvent e) {
                modelManager.stopSimulation();

                for (GraphicObject graphics : viewer.getGraphicList()) {
                    if (graphics instanceof GraphicGrid) {
                        viewer.remove(graphics);
                        break;
                    }
                }

                modelManager.removeGrid();

                new ZoomAllCommand(viewer, modelManager).execute();

                repaint();
            }
        };

        simulationMenu.add(action);
        simulationToolBar.add(action);

        action = new AbstractAction("restart", ImageUtilities.createImageIcon("session", 22, 22)) {

            public void actionPerformed(ActionEvent e) {

                if (modelManager.hasGrid()) {

                    // 1. delete old simulation                    
                    String testCaseName = modelManager.grid.testcase;

                    System.out.println("restart: " + _solverName + " " + testCaseName);

                    modelManager.stopSimulation();

                    for (GraphicObject graphics : viewer.getGraphicList()) {
                        if (graphics instanceof GraphicGrid) {

                            viewer.remove(graphics);

                            break;
                        }
                    }

                    modelManager.removeGrid();

                    // 2. restart simulation
                    SolverBuilder builder = SolverFactory.getInstance().getBuilder(_solverName);
                    UniformGrid grid = builder.createGrid(testCaseName);

                    GraphicGrid graphic = new GraphicGrid(grid, displayStyleManager);

                    modelManager.addGrid(grid);
                    viewer.addFirst(graphic);

                    Solver solver = builder.createSolver(grid);
                    solver.addObserver(viewer);
                    modelManager.startSimulation(solver);

                    configDialog.update();

                    repaint();
                }
            }
        };

        simulationMenu.add(action);
        simulationToolBar.add(action);

        action = new AbstractAction("config", ImageUtilities.createImageIcon("config", 22, 22)) {

            public void actionPerformed(ActionEvent e) {
                if (modelManager.hasGrid()) {
                    configDialog.setVisible(true);
                }
            }
        };

        simulationMenu.add(action);
        simulationToolBar.add(action);



        action = new AbstractAction("pause", ImageUtilities.createImageIcon("pause", 22, 22)) {

            public void actionPerformed(ActionEvent e) {
                if (modelManager.hasGrid()) {
                    modelManager.stopSimulation();
                    repaint();
                }
            }
        };

        simulationMenu.add(action);
        simulationToolBar.add(action);


        action = new AbstractAction("continue", ImageUtilities.createImageIcon("continue", 22, 22)) {

            public void actionPerformed(ActionEvent e) {
                if (modelManager.solver == null && modelManager.hasGrid()) {

                    SolverBuilder builder = SolverFactory.getInstance().getBuilder(_solverName);
                    Solver solver = builder.createSolver(modelManager.grid);
                    solver.addObserver(viewer);
                    modelManager.startSimulation(solver);

                    configDialog.update();
                    repaint();

                }
            }
        };

        simulationMenu.add(action);
        simulationToolBar.add(action);


        menuBar.add(simulationMenu);
        toolBarPanel.add(simulationToolBar);
        // ------------------------------------------------- //




        JMenu displaystyleMenu = new JMenu("Display Style");

        // ItemListener
        class ScalarDisplayStyleListener implements ItemListener {

            private int index, scalar;

            public ScalarDisplayStyleListener(int index, int scalar) {
                this.index = index;
                this.scalar = scalar;
            }

            public void itemStateChanged(ItemEvent e) {

                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (scalar == Scalar.OFF) {
                        displayStyleManager.deactivateScalarStyle(index);
                    } else {
                        displayStyleManager.activateScalarStyle(index, scalar);
                    }
                    repaint();
                }
            }
        }

        // ** add scalar display styles ** //
        ArrayList<DisplayStyle> scalarStyles = displayStyleManager.getScalarStyles();

        for (int i = 0; i < scalarStyles.size(); i++) {

            JMenu style_menu = new JMenu(scalarStyles.get(i).getClass().getSimpleName());
            displaystyleMenu.add(style_menu);

            ButtonGroup bg = new ButtonGroup();
            String[] scalars = scalarStyles.get(i).getScalars();

            for (int j = 0; j < scalars.length; j++) {
                JRadioButtonMenuItem item = new JRadioButtonMenuItem(scalars[j]);
                bg.add(item);
                item.addItemListener(new ScalarDisplayStyleListener(i, j));
                style_menu.add(item);
                if (i == 0 && j == 0) {
                    item.setSelected(true);
                    displayStyleManager.activateScalarStyle(0, 0);
                } else if (i > 0 && j == scalars.length - 1) {
                } else if (i > 0 && j == scalars.length - 1) {
                    item.setSelected(true);
                }
            }
        }
        displaystyleMenu.addSeparator();


        // ItemListener
        class VectorDisplayStyleListener implements ItemListener {

            private int index;

            public VectorDisplayStyleListener(int index) {
                this.index = index;
            }

            public void itemStateChanged(ItemEvent e) {

                if (e.getStateChange() == ItemEvent.SELECTED) {
                    displayStyleManager.activateVecotrStyle(index);
                } else {
                    displayStyleManager.deactivateVecotrStyle(index);
                }

                repaint();
            }
        }

        // ** add vector display styles ** //
        ArrayList<DisplayStyle> vectorStyles = displayStyleManager.getVectorStyles();
        for (int i = 0; i < vectorStyles.size(); i++) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(vectorStyles.get(i).getClass().getSimpleName());
            displaystyleMenu.add(item);
            item.addItemListener(new VectorDisplayStyleListener(i));
        }
        displaystyleMenu.addSeparator();





        menuBar.add(displaystyleMenu);



        // WRITER MENU
        JMenu writerMenu = new JMenu("Writers");

        // ItemListener
        class WriterListener implements ItemListener {

            private int index, scalar;

            public WriterListener(int index, int scalar) {
                this.index = index;
                this.scalar = scalar;
            }

            public void itemStateChanged(ItemEvent e) {

                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (scalar == Scalar.OFF) {
                        modelManager.getWriterManager().deactivateWriter(index);
                        repaint();
                    } else {
                        modelManager.getWriterManager().activateWriter(index, scalar);
                        repaint();
                    }
                }
            }
        }

        // ** add scalar display styles ** //
        ArrayList<Writer> writers = modelManager.getWriterManager().getWriters();

        for (int i = 0; i < writers.size(); i++) {

            JMenu style_menu = new JMenu(writers.get(i).getClass().getSimpleName());
            writerMenu.add(style_menu);

            ButtonGroup bg = new ButtonGroup();
            String[] scalars = writers.get(i).getScalars();

            for (int j = 0; j < scalars.length; j++) {
                JRadioButtonMenuItem item = new JRadioButtonMenuItem(scalars[j]);
                bg.add(item);
                item.addItemListener(new WriterListener(i, j));
                style_menu.add(item);
//                if (i == 0 && j == 0) {
//                    item.setSelected(true);
//                    modelManager.getWriterManager().activateWriter(0, 0);
//                } else if (i > 0 && j == scalars.length - 1) {
//                    item.setSelected(true);
//                }
            }
        }

        menuBar.add(writerMenu);

        // ------------------------------------------------- //

        JMenu editMenu = new JMenu("Edit");
        JToolBar editToolBar = new JToolBar("Edit");
        action = new AbstractAction("Clear", ImageUtilities.createImageIcon("edit-clear", 22, 22)) {

            public void actionPerformed(ActionEvent e) {
                new ClearAllCommand(viewer, modelManager).execute();
            }
        };

        editToolBar.add(action);

        editMenu.add(action);
        // ------------------------------------------------- //
        action = new AbstractAction("Undo", ImageUtilities.createImageIcon("edit-undo", 22, 22)) {

            public void actionPerformed(ActionEvent e) {
                new UndoCommand().execute();
            }
        };

        editToolBar.add(action);

        editMenu.add(action);
        // ------------------------------------------------- //
        action = new AbstractAction("Redo", ImageUtilities.createImageIcon("edit-redo", 22, 22)) {

            public void actionPerformed(ActionEvent e) {
                new RedoCommand().execute();
            }
        };

        editToolBar.add(action);

        editMenu.add(action);
        // ------------------------------------------------- //
        action = new AbstractAction("Refresh", ImageUtilities.createImageIcon("view-refresh", 22, 22)) {

            public void actionPerformed(ActionEvent e) {
                new ZoomAllCommand(viewer, modelManager).execute();
            }
        };

        editToolBar.add(action);

        editMenu.add(action);
        // ------------------------------------------------- //

        menuBar.add(editMenu);

        toolBarPanel.add(editToolBar);
        // ------------------------------------------------- //
        // ------------------------------------------------- //
        this.add(toolBarPanel, BorderLayout.NORTH);


        this.setJMenuBar(menuBar);
    }

    private void createStatusPanel() {
        JPanel statusFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, FlowLayout.LEFT));
        statusField = new JLabel("0.000, 0.000", ImageUtilities.createImageIcon("Target", 18, 18), JLabel.LEFT);
        statusFieldPanel.add(statusField);
        this.add(statusFieldPanel, BorderLayout.SOUTH);
    }

    public void updateStatusField(String str) {
        statusField.setText(str);

    }
}
