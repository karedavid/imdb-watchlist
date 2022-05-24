package data;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.table.AbstractTableModel;

abstract class MovieCollection extends AbstractTableModel{
	
	private static final long serialVersionUID = -8325697813860692439L;
	
	//Thread safe database
	CopyOnWriteArrayList<Movie> localDb;
	
	//Constructor
	public MovieCollection() {
		///Initialize database
		localDb = new CopyOnWriteArrayList <>();
	}
	
	//Add new movie as first list element (default)
	public void addMovieBeginning(Movie m) {
		for(Movie i : localDb) {
			if(i.getId().equals(m.getId())) {
				return;
			}
		}
		localDb.add(0,m);
		fireTableDataChanged();
	}
	
	//Add movie to the end of the 
	public void addMovieEnd(Movie m) {
		localDb.add(m);
		fireTableDataChanged();
	}
	
	//Clear database
	public void clear() {
		localDb.clear();
		fireTableDataChanged();
	}
	
	//Get movie from persistent database
	public Movie getMovie(int i) {
		if(i >= localDb.size()) {
			return null;
		}else {
			return localDb.get(i);
		}
	}
	
	//Get size of database
	public int getSize() {
		return localDb.size();
	}
	
	//Remove move from database
	public void removeMovie(Movie m) {
		for(Movie i : localDb) {
				if(m == i) {
					localDb.remove(m);
					fireTableDataChanged();
					return;
				}
		}
	}
	
	//Move movie to end of list
	public void moveToEnd(Movie m) {
		removeMovie(m);
		addMovieEnd(m);
		fireTableDataChanged();
	}
	
	//Move movie to beginning of list
	public void moveToFirst(Movie m) {
		removeMovie(m);
		addMovieBeginning(m);
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return getSize();
	};
	

}
