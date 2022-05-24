package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.regex.PatternSyntaxException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.google.gson.JsonParseException;

import data.Movie;
import data.MovieCollectionLocal;
import network.ConnectionManager;
import network.SearchEngine;
import utilities.ImageProcessor;

public class MainWindow extends JFrame{
	
	private static final long serialVersionUID = 8950065135712978682L;
		
	// Data
	private MovieCollectionLocal data;	
	private Movie activeMovie;
		
	// Menu bar
	private final JMenuBar		menuBar			= new JMenuBar();
		
	// Filter panel
	private final JPanel 		filterPanel 	= new JPanel();
	static JLabel				welcomeLabel;
	private final JTextField 	inputField 		= new JTextField();
	private final JButton 		filterButton 	= new JButton();
		
	// Result table
	private final JTable 		dataTable 		= new JTable();
	private final TableRowSorter<MovieCollectionLocal> sorter;
		
	// Detail panel
	private final JPanel 		detailPanel 	= new JPanel();
	private JLabel 				imageLabel;
	private final JTextArea 	detailTextArea 	= new JTextArea();
	private final JButton		deleteButton	= new JButton();
	private final JButton		imdbButton		= new JButton();
	
	// Color constants
	static final Color 		PRIMARY_BACKGROUND_COLOR 	= Color.decode("#121212");
	static final Color 		SECONDARY_BACKGROUND_COLOR 	= Color.decode("#383838");
	static final Color 		PRIMARY_ACCENT_COLOR 		= Color.decode("#f5c518");
	
	
	// Constructor
	public MainWindow() {

		initializeDatabase();
		sorter = new TableRowSorter<MovieCollectionLocal>(data);
		initializeFilterButton();
		initializeInputField();
		initializeFilterPanel();
		initializeDataTable();
		initializeDetailTextArea();
		initializeDeleteButton();
		initializeIMDBButton();
		initializeDetailPanel();
		initializeMenu();
		initializeFrame();
		updateUnseen();
		this.pack();
		this.setOnCloseAction();
		
	}
		
	// Initialization
	
	private void initializeMenu() {
		
		// Menu items
		
		final JMenu			menuFile		= new JMenu();
		final JMenu			menuTool		= new JMenu();
		final JMenu			menuHelp		= new JMenu();
		final JMenuItem 	itemNew			= new JMenuItem();
		final JMenuItem		itemImport		= new JMenuItem();
		final JMenuItem		itemExport		= new JMenuItem();
		final JMenuItem		itemUpdateCover = new JMenuItem();
		final JMenuItem		itemClear		= new JMenuItem();
		final JMenuItem		itemAbout		= new JMenuItem();
		final JMenuItem		itemHelp		= new JMenuItem();
		
		itemNew.		setText("Új film hozzáadása");
		itemImport.		setText("Importálás fájlból (JSON)");
		itemExport.		setText("Exportálás fájlba (JSON)");
		itemUpdateCover.setText("Borítóképek frissítése");
		itemClear.		setText("Lista törlése");
		itemHelp.		setText("Felhasználói kézikönyv");
		itemAbout.		setText("Névjegy");
		
		itemNew.		addActionListener(menuNewAction);
		itemExport.		addActionListener(menuExportAction);
		itemImport.		addActionListener(menuImportAction);
		itemUpdateCover.addActionListener(menuUpdateCoverAction);
		itemHelp.		addActionListener(menuDocAction);
		itemClear.		addActionListener(menuClearAction);
		itemAbout.		addActionListener(menuAboutAction);
		
		// File menu
		menuFile.setText("Fájl");
		menuFile.add(itemNew);
		
		// Tool menu
		menuTool.setText("Eszközök");
		menuTool.add(itemImport);
		menuTool.add(itemExport);
		menuTool.add(itemUpdateCover);
		menuTool.add(itemClear);
		
		// Help menu
		menuHelp.setText("Segítség");
		menuHelp.add(itemHelp);
		menuHelp.add(itemAbout);
		
		// Menu bar
		menuBar.add(menuFile);
		menuBar.add(menuTool);
		menuBar.add(menuHelp);
	}
		
	private void initializeFilterButton() {
		// Search button
		filterButton.setText("Szűrés");
		filterButton.setPreferredSize(new Dimension(80, 30));
		filterButton.addActionListener(filterButtonAction);
		filterButton.setBackground(PRIMARY_BACKGROUND_COLOR);
		filterButton.setForeground(Color.WHITE);
		filterButton.setBorder(BorderFactory.createMatteBorder(1,1,1,1, PRIMARY_ACCENT_COLOR));
	}
			
	private void initializeInputField() {
		// Input field
		inputField.setColumns(20);
		inputField.setHorizontalAlignment(SwingConstants.CENTER);
		inputField.setBackground(PRIMARY_BACKGROUND_COLOR);
		inputField.setSelectedTextColor(Color.BLACK);
		inputField.setSelectionColor(PRIMARY_ACCENT_COLOR);
		inputField.setForeground(Color.WHITE);
		inputField.setBorder(BorderFactory.createMatteBorder(1,1,1,1, PRIMARY_ACCENT_COLOR));
		inputField.setCaretColor(PRIMARY_ACCENT_COLOR);
	}	
		
	private void initializeFilterPanel() {
		// Search panel
		welcomeLabel = new JLabel("", JLabel.CENTER);
		welcomeLabel.setForeground(Color.WHITE);
		
		final BorderLayout bl = new BorderLayout();
		bl.setHgap(10);
		bl.setVgap(10);
		filterPanel.setLayout(bl);
		filterPanel.add(welcomeLabel, BorderLayout.NORTH);
		
		// Set up IMDB icon
		try {
			final JLabel iconLabel = new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/images/imdb_logo.png"))));
			filterPanel.add(iconLabel, BorderLayout.WEST);
		} catch (IOException e) {
			//If not found, exit, the files are damaged
			JOptionPane.showMessageDialog(MainWindow.this, "A program fájljai megsérültekkkk.", "Hiba", JOptionPane.ERROR_MESSAGE );
			System.exit(1);
		}
		
		filterPanel.add(inputField, BorderLayout.CENTER);
		filterPanel.add(filterButton, BorderLayout.EAST);;
		filterPanel.setBackground(SECONDARY_BACKGROUND_COLOR);
		filterPanel.setBorder(BorderFactory.createMatteBorder(5,5,20,5, SECONDARY_BACKGROUND_COLOR));
	}
		
	private void initializeDataTable() {
		// Result table
		dataTable.setRowHeight(105);
		dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		dataTable.setModel(data);
		final DefaultTableCellRenderer alignCenter 	= new DefaultTableCellRenderer();
		alignCenter.setHorizontalAlignment( SwingConstants.CENTER );
		alignCenter.setHorizontalAlignment( SwingConstants.CENTER );
		dataTable.getColumnModel().getColumn(1).setCellRenderer( alignCenter );
		dataTable.getColumnModel().getColumn(2).setCellRenderer( alignCenter );
		dataTable.getColumnModel().getColumn(3).setCellRenderer( alignCenter );
		dataTable.getColumnModel().getColumn(4).setCellRenderer( alignCenter );
					
		dataTable.setFillsViewportHeight(true);
		dataTable.setBackground(SECONDARY_BACKGROUND_COLOR);
		dataTable.setForeground(Color.WHITE);
		dataTable.setSelectionBackground(PRIMARY_ACCENT_COLOR);
		dataTable.setSelectionForeground(Color.BLACK);
		dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dataTable.getSelectionModel().addListSelectionListener(selectionEventAction);	
		dataTable.getModel().addTableModelListener(dataChangeAction);
		
		dataTable.getTableHeader().setBackground(SECONDARY_BACKGROUND_COLOR);
		dataTable.getTableHeader().setForeground(Color.WHITE);
		dataTable.setBorder(BorderFactory.createEmptyBorder());
		
		dataTable.setRowSorter(sorter);		
	}
		
	private void initializeDetailTextArea() {
		// Detail panel text field
		detailTextArea.setBackground(SECONDARY_BACKGROUND_COLOR);
		detailTextArea.setSelectedTextColor(Color.BLACK);
		detailTextArea.setSelectionColor(PRIMARY_ACCENT_COLOR);
		detailTextArea.setForeground(Color.WHITE);
		detailTextArea.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		detailTextArea.setLineWrap(true);
		detailTextArea.setMaximumSize(new Dimension(100,100));
	}
		
	private void initializeDetailPanel() {
		// Detail panel
		detailPanel.setLayout(new BorderLayout());
		detailPanel.setBackground(SECONDARY_BACKGROUND_COLOR);
		detailPanel.setBorder(BorderFactory.createMatteBorder(10,10,10,10, PRIMARY_BACKGROUND_COLOR));
			
		imageLabel = new JLabel();
		
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(deleteButton,BorderLayout.NORTH);
		buttonPanel.add(imdbButton,BorderLayout.SOUTH);
		
		detailPanel.add(filterPanel, BorderLayout.NORTH);
		detailPanel.add(imageLabel, BorderLayout.WEST);
		detailPanel.add(detailTextArea, BorderLayout.CENTER);
		detailPanel.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void initializeDeleteButton() {
		deleteButton.addActionListener(deleteButtonAction);
		deleteButton.setText("Film törlése");
		deleteButton.setPreferredSize(new Dimension(80, 30));
		deleteButton.setBackground(Color.RED);
		deleteButton.setForeground(Color.WHITE);
		deleteButton.setBorder(BorderFactory.createEmptyBorder());
		deleteButton.setVisible(false);
	}
		
	private void initializeIMDBButton() {
		imdbButton.addActionListener(imdbButtonAction);
		imdbButton.setText("Megnyitás IMDB-n");
		imdbButton.setPreferredSize(new Dimension(80, 30));
		imdbButton.setBackground(PRIMARY_ACCENT_COLOR);
		imdbButton.setForeground(Color.BLACK);
		imdbButton.setBorder(BorderFactory.createEmptyBorder());
		imdbButton.setVisible(false);
	}
		
	private void initializeFrame() {
		// Setting up frame
		this.getContentPane().setLayout(new GridLayout(1,2));
		final JScrollPane scrollPanel = new JScrollPane(dataTable);
		scrollPanel.setViewportView(dataTable);
		scrollPanel.setBorder(BorderFactory.createMatteBorder(10,0,10,10, PRIMARY_BACKGROUND_COLOR));
		this.add(detailPanel);
		this.add(scrollPanel);
		this.setJMenuBar(menuBar);
		this.pack();
		this.setBackground(PRIMARY_BACKGROUND_COLOR);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.getRootPane().setDefaultButton(filterButton);
		this.setTitle("IMDB Watchlist");
		this.setMinimumSize(new Dimension(800, 400));
		
		// Loading application icon			
		try {
			this.setIconImage(ImageIO.read(getClass().getResource("/images/appicon.png")));
		} catch (IOException e) {
			//If not, exit, the files are damaged
			JOptionPane.showMessageDialog(MainWindow.this, "A program fájljai megsérültek.", "Hiba", JOptionPane.ERROR_MESSAGE );
			System.exit(1);
		}
		
		// Testing Internet connection
		try {
			ConnectionManager.isNetworkAvailable();
		}catch(IOException e) {
			//Not fatal error, continue after notification
			JOptionPane.showMessageDialog(MainWindow.this, "Nincs internet elérés", "Hiba", JOptionPane.ERROR_MESSAGE );
		}
	}
	
	private void initializeDatabase() {
		data = new MovieCollectionLocal();
		
		// Loading existing data
		try {
			data.deserializeDatabase();
		}catch(Exception e) {
			//Not fatal error, continue after notification
			JOptionPane.showMessageDialog(MainWindow.this, "Nem sikerült betölteni az adatbázist.", "Hiba", JOptionPane.ERROR_MESSAGE );
		}
		
	}
	
	// Update unseen movie counter
	private void updateUnseen() {
		welcomeLabel.setText("Üdvözöllek! Jelenleg " + data.getUnseen() + " megnézetlen filmed van");
	}
	
	// On close
	private void setOnCloseAction() {
		addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	
            	// Serialize database
                try{
                	data.serializeDatabase();
		        }catch(Exception ex) {
		        	JOptionPane.showMessageDialog(MainWindow.this, "Hiba. Nem sikerült menteni az adatbázist.", "Hiba", JOptionPane.ERROR_MESSAGE );
		        }
            }
        });
	}	
	
	//Button listeners
	
	// Open movie on IMDB
	private final ActionListener imdbButtonAction = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// Open link in default web browser
			try {
				ConnectionManager.openLink(SearchEngine.generateUriFromId(activeMovie.getId()));
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(MainWindow.this, "Hiba. Ez a funkció nem támogatott", "Hiba", JOptionPane.ERROR_MESSAGE );
			}
		}
		
	};
	
	// Delete active movie from database
	private final ActionListener deleteButtonAction = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			
			// Confirm
			int reply = JOptionPane.showConfirmDialog(null, "Biztosan el szeretné távolítani a filmet?", "Eltávolítás", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
            	data.removeMovie(activeMovie);
            	deleteButton.setEnabled(false);
            }
		}
		
	};
	
	// Filter movie with given keyword
	private final ActionListener filterButtonAction = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			String keyword = inputField.getText();
			
			// If field is empty, remove filter, else apply filter
            if(keyword.length() == 0) {
               sorter.setRowFilter(null);
            } else {
               try {
                 sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
               } catch (PatternSyntaxException pse) {
            	   JOptionPane.showMessageDialog(MainWindow.this, "Hibás kifejezés", "Hiba", JOptionPane.ERROR_MESSAGE );
               }
            }
		}
	};
	
	// Movie selection listener
	private final ListSelectionListener selectionEventAction = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			// If selection is changing or selection lost, ignore
			if (e.getValueIsAdjusting() || dataTable.getSelectedRow() < 0) {
			   	return;
			}   
			
			// Save selected as active movie
			activeMovie = data.getMovie(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()));
			    	
			// Update detail panel with active movie
			detailPanel.remove(imageLabel);
			detailTextArea.setText(activeMovie.getSummary());
			imageLabel = new JLabel(new ImageIcon(ImageProcessor.resizeImage(activeMovie.getCover(), 150, 222)));
			imageLabel.setVerticalAlignment(JLabel.TOP);
			detailPanel.add(imageLabel, BorderLayout.WEST);
			
			// Make buttons available and visible
			deleteButton.setVisible(true);
			deleteButton.setEnabled(true);
			imdbButton.setVisible(true);
		}
	};
	
	// When user change seen property of movie, update unseen counter
	private final TableModelListener dataChangeAction = new TableModelListener() {
		public void tableChanged(TableModelEvent e) { 
			updateUnseen();
		} 
	};
	
	// Menu listeners
	
	// Add new movie
	private final ActionListener menuNewAction = new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
	    	// Make new search window and get selected
	    	SearchWindow searchWindow = new SearchWindow(MainWindow.this);
	    	Movie newMovie = searchWindow.getSelectedMovie();
	    	
	    	// If user selected a movie add to database and update seen counter
	    	if(newMovie != null) {
	    		data.addMovieBeginning(newMovie);
	    	}
			
	    }
	};
	
	// Export file
	private final ActionListener menuExportAction = new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
	    	// Creating dialog
	    	JFileChooser exportWindow = new JFileChooser();
	    	exportWindow.setDialogTitle("JSON fájl mentése");
	    	int userSelection = exportWindow.showSaveDialog(MainWindow.this);
	    	
	    	// If user selected a file, do export else ignore
	    	if (userSelection == JFileChooser.APPROVE_OPTION) {
	    	    File fileToSave = new File(exportWindow.getSelectedFile() + ".json");
	    	    
	    	    // Trying to export
	    	    try {
		    		data.saveToJson(fileToSave);
		    		JOptionPane.showMessageDialog(MainWindow.this, "A fájl mentése megtörtént.", "Exportálás", JOptionPane.INFORMATION_MESSAGE);
				} catch (JsonParseException e) {
					JOptionPane.showMessageDialog(MainWindow.this, "Hiba. Az adatbázis sérült.", "Hiba", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(MainWindow.this, "Hiba. Nem sikerült menteni a fájlt.", "Hiba", JOptionPane.ERROR_MESSAGE );
				}
	    
	    	}
	    }
	};
	
	// Import file (in background)
	private final ActionListener menuImportAction = new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
	    	// Creating dialog
	    	JFileChooser importWindow = new JFileChooser();
	    	importWindow.setDialogTitle("JSON fájl importálása");
	    	importWindow.setFileFilter(new FileNameExtensionFilter("*.json", "json"));
	    	int userSelection = importWindow.showOpenDialog(MainWindow.this);
	    	
	    	// If user selected a file, do import else ignore
	    	if (userSelection == JFileChooser.APPROVE_OPTION) {
	    	    File fileToSave = importWindow.getSelectedFile();
	    	
		    	try {
		    		data.readFromJson(fileToSave);
		    		JOptionPane.showMessageDialog(MainWindow.this, "A fájl importálása megkezdődött.", "Importálás", JOptionPane.INFORMATION_MESSAGE);
				} catch (JsonParseException e) {
					JOptionPane.showMessageDialog(MainWindow.this, "Hiba. A fájl sérült.", "Hiba", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(MainWindow.this, "Hiba. Nem sikerült megnyitni a fájlt.", "Hiba", JOptionPane.ERROR_MESSAGE );
				}
	    	}

	    }
	};
	
	// Update every cover from Internet (in background)
	private final ActionListener menuUpdateCoverAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				ConnectionManager.isNetworkAvailable();
				data.updateCovers();
			}catch(IOException ioe) {
				//Not fatal error, continue after notification
				JOptionPane.showMessageDialog(MainWindow.this, "Nincs internet elérés", "Hiba", JOptionPane.ERROR_MESSAGE );
			}
			
		};
	};
	
	// Delete every movie from database
	private final ActionListener menuClearAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			// Confirm
			int reply = JOptionPane.showConfirmDialog(null, "Biztosan törölni szeretne minden filmet?", "Eltávolítás", JOptionPane.YES_NO_OPTION);
	        if (reply == JOptionPane.YES_OPTION) {
	        	data.clear();
	    		deleteButton.setEnabled(false);
	        }
	       
		};
	};
	
	// Open documentation
	private final ActionListener menuDocAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				File docFile = new File(getClass().getResource("/documents/readme.pdf").toURI());
				Desktop.getDesktop().open(docFile);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(MainWindow.this, "Hiba. Nem sikerült megnyitni a fájlt.", "Hiba", JOptionPane.ERROR_MESSAGE );
			}
		};
	};
	
	// Open about
	private final ActionListener menuAboutAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(MainWindow.this, "IMDB Watchlist\n2020\nProgramozás alapjai 3\nNagy házi", "Névjegy", JOptionPane.INFORMATION_MESSAGE );
		};
	};
	
	// Main
	public static void main(String[] args){
		
		// Global UI properties
		UIManager.put("MenuItem.background", PRIMARY_BACKGROUND_COLOR);
		UIManager.put("MenuItem.foreground", Color.WHITE);
		UIManager.put("MenuItem.opaque", true);
		UIManager.put("Menu.foreground", Color.WHITE);
		UIManager.put("PopupMenu.border", BorderFactory.createMatteBorder(2,2,2,2, PRIMARY_ACCENT_COLOR));
		UIManager.put("Menu.selectionBackground", PRIMARY_ACCENT_COLOR);
		UIManager.put("Menu.selectionForeground", Color.BLACK);
		UIManager.put("MenuBar.background",SECONDARY_BACKGROUND_COLOR);
		UIManager.put("MenuBar.border", BorderFactory.createEmptyBorder());
		UIManager.put("MenuItem.selectionBackground", PRIMARY_ACCENT_COLOR);
		UIManager.put("MenuItem.selectionForeground", Color.BLACK);
		UIManager.put("ScrollBar.background", SECONDARY_BACKGROUND_COLOR);
		
		// Main window
	    MainWindow mainWindow = new MainWindow();
	    mainWindow.setVisible(true);
	}
				
}


