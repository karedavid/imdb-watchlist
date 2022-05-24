package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import data.Movie;
import data.MovieCollectionSearch;
import network.ConnectionManager;
import network.SearchEngine;
import network.SearchEngine.BadKeywordException;
import network.SearchEngine.NoResultException;

public class SearchWindow extends JDialog{
	
	private static final long serialVersionUID = -1240857411654211349L;

	//Data
	private MovieCollectionSearch data;
	private Movie selectedMovie;
	
	//Header
	private final JPanel 		searchPanel 	= new JPanel();
	private final JTextField 	inputField 		= new JTextField();
	private final JButton 		searchButton 	= new JButton();
	
	//Results
	private final JTable 		resultTable 	= new JTable();
	private final DefaultTableCellRenderer alignCenter 	= new DefaultTableCellRenderer();
	
	//Details
	private final JPanel 		detailPanel 	= new JPanel();
	private JLabel 				imageLabel;
	private final JTextArea 	detailTextArea 	= new JTextArea();
	private final JButton		addButton		= new JButton();
	
	
	public SearchWindow(JFrame parent) {
		super(parent, "Új film hozzáadása", true);
		data = new MovieCollectionSearch();
		initializeSearchButton();
		initializeInputField();
		initializeSearchPanel();
		initializeResultTable();
		initializeDetailTextArea();
		initializeAddButton();
		initializeDetailPanel();
		initializeDialog();
		
		this.setOnCloseAction();
		this.setVisible(true);
	}
	
	// Initialization
	
	private void initializeSearchButton() {
		// Search button
		searchButton.setText("Keresés");
		searchButton.setPreferredSize(new Dimension(80, 30));
		searchButton.addActionListener(searchButtonAction);
		searchButton.setBackground(MainWindow.SECONDARY_BACKGROUND_COLOR);
		searchButton.setForeground(Color.WHITE);
		searchButton.setBorder(BorderFactory.createEmptyBorder());
	}
	
	private void initializeInputField() {
		// Input field
		inputField.setColumns(20);
		inputField.setPreferredSize(new Dimension(300, 30));
		inputField.setHorizontalAlignment(SwingConstants.CENTER);
		inputField.setBackground(MainWindow.SECONDARY_BACKGROUND_COLOR);
		inputField.setSelectedTextColor(Color.BLACK);
		inputField.setSelectionColor(MainWindow.PRIMARY_ACCENT_COLOR);
		inputField.setForeground(Color.WHITE);
		inputField.setBorder(BorderFactory.createEmptyBorder());
		inputField.setCaretColor(MainWindow.PRIMARY_ACCENT_COLOR);
	}
	
	private void initializeSearchPanel() {
		// Search panel
		final JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("/images/imdb_logo.png")));
		searchPanel.add(iconLabel);
		searchPanel.add(inputField);
		searchPanel.add(searchButton);;
		searchPanel.setBackground(MainWindow.PRIMARY_BACKGROUND_COLOR);
		searchPanel.setBorder(BorderFactory.createMatteBorder(10,0,0,0, MainWindow.PRIMARY_BACKGROUND_COLOR));
	}
	
	private void initializeResultTable() {
		// Result table
		alignCenter.setHorizontalAlignment( SwingConstants.CENTER );
		alignCenter.setHorizontalAlignment( SwingConstants.CENTER );
		
		resultTable.setModel(data);
		resultTable.getColumnModel().getColumn(1).setCellRenderer( alignCenter );
		resultTable.getColumnModel().getColumn(3).setCellRenderer( alignCenter );
				
		resultTable.setFillsViewportHeight(true);
		resultTable.setAutoCreateRowSorter(true);
		resultTable.setBackground(MainWindow.SECONDARY_BACKGROUND_COLOR);
		resultTable.setForeground(Color.WHITE);
		resultTable.setSelectionBackground(MainWindow.PRIMARY_ACCENT_COLOR);
		resultTable.setSelectionForeground(Color.BLACK);
		resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				
		resultTable.getTableHeader().setBackground(MainWindow.SECONDARY_BACKGROUND_COLOR);
		resultTable.getTableHeader().setForeground(Color.WHITE);
		resultTable.setBorder(BorderFactory.createEmptyBorder());
		resultTable.getSelectionModel().addListSelectionListener(selectionEventAction);
	}
	
	private void initializeDetailTextArea() {
		// Detail panel text field
		detailTextArea.setBackground(MainWindow.SECONDARY_BACKGROUND_COLOR);
		detailTextArea.setSelectedTextColor(Color.BLACK);
		detailTextArea.setSelectionColor(MainWindow.PRIMARY_ACCENT_COLOR);
		detailTextArea.setForeground(Color.WHITE);
		detailTextArea.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		detailTextArea.setLineWrap(true);
	}
	
	private void initializeDetailPanel() {
		// Detail panel
		detailPanel.setLayout(new BorderLayout());
		detailPanel.setVisible(false);
		detailPanel.setBackground(MainWindow.SECONDARY_BACKGROUND_COLOR);
		detailPanel.setBorder(BorderFactory.createEmptyBorder());
		
		imageLabel = new JLabel();
		detailPanel.add(imageLabel, BorderLayout.WEST);
		detailPanel.add(detailTextArea, BorderLayout.CENTER);
		detailPanel.add(addButton, BorderLayout.SOUTH);
	}
	
	private void initializeAddButton() {
		// Add button
		addButton.addActionListener(addButtonAction);
		addButton.setText("Film hozzáadása");
		addButton.setPreferredSize(new Dimension(80, 30));
		addButton.setBackground(MainWindow.PRIMARY_ACCENT_COLOR);
		addButton.setForeground(Color.BLACK);
		addButton.setBorder(BorderFactory.createEmptyBorder());
	}
	
	private void initializeDialog() {
		// Frame
		this.getContentPane().setLayout(new BorderLayout());
		this.add(searchPanel, BorderLayout.NORTH);
		final JScrollPane 	scrollPanel 	= new JScrollPane(resultTable);
		scrollPanel.setViewportView(resultTable);
		scrollPanel.setBorder(BorderFactory.createMatteBorder(10,0,0,0, MainWindow.PRIMARY_BACKGROUND_COLOR));
		this.add(scrollPanel, BorderLayout.CENTER);
		this.add(detailPanel, BorderLayout.SOUTH);
		this.pack();
		this.setBackground(MainWindow.PRIMARY_BACKGROUND_COLOR);
		this.setLocation(200,200);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.getRootPane().setDefaultButton(searchButton);
		this.setMinimumSize(new Dimension(400, 400));
				
		// Loading application icon			
		try {
			this.setIconImage(ImageIO.read(getClass().getResource("/images/appicon.png")));
		} catch (IOException e) {
			// If not, exit, the files are damaged
			JOptionPane.showMessageDialog(this, "A program fájljai megsérültek.", "Hiba", JOptionPane.ERROR_MESSAGE );
			System.exit(1);
		}
				
		// Testing Internet connection
		try {
			ConnectionManager.isNetworkAvailable();
		}catch(IOException e) {
			// Not fatal error, continue after notification
			JOptionPane.showMessageDialog(this, "Nincs internet elérés", "Hiba", JOptionPane.ERROR_MESSAGE );
		}
	}

	// Listeners
	
	private final ActionListener searchButtonAction = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!inputField.getText().isBlank()) {
				
				Runnable background = new Runnable() {
			         public void run() {
			            // Doing search
						try {
							SearchEngine.search(inputField.getText());
							// Setting result table
							data = SearchEngine.getResults();
							resultTable.setModel(data);
							resultTable.getColumnModel().getColumn(1).setCellRenderer( alignCenter );
							resultTable.getColumnModel().getColumn(3).setCellRenderer( alignCenter );
						} catch (NoResultException e) {
							JOptionPane.showMessageDialog(SearchWindow.this, "Nincs találat", "Hiba", JOptionPane.WARNING_MESSAGE );
						} catch (IOException  | InterruptedException e) {
							JOptionPane.showMessageDialog(SearchWindow.this, "Nincs internet kapcsolat", "Hiba", JOptionPane.ERROR_MESSAGE );
						} catch (BadKeywordException e) {
							JOptionPane.showMessageDialog(SearchWindow.this, "Helytelen kifejezés", "Hiba", JOptionPane.ERROR_MESSAGE );
						}
			         }
			     };

			    new Thread(background).start();
				
			}
		}
	};
	
	// Movie selection listener
	private final ListSelectionListener selectionEventAction = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			// If selection is changing or selection lost, ignore
		    if (e.getValueIsAdjusting() || resultTable.getSelectedRow() < 0) {
		    	return;
		    }   
		    
		    selectedMovie = data.getMovie(resultTable.convertRowIndexToModel(resultTable.getSelectedRow()));
		    
		    // Download cover, if there is no, set default
		   
			selectedMovie.autoSetCover();
			
		    
		    detailPanel.remove(imageLabel);
		    detailTextArea.setText(selectedMovie.getSummary());
		    
			imageLabel = new JLabel(new ImageIcon(selectedMovie.getCover()));
			detailPanel.add(imageLabel, BorderLayout.WEST);
			
		    detailPanel.revalidate();
			detailPanel.setVisible(true);
		}
	};
	
	// Add selected movie to database
	private final ActionListener addButtonAction = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	};
	
	// On close
	private void setOnCloseAction() {
		addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	        	// User dismissed dialog, so no movie to return
	        	selectedMovie = null; 	
	       
	        }
	    });
	}
	
	// Return selected movie
	public Movie getSelectedMovie() {
		// Return selected movie
		return selectedMovie;
	}		
}
