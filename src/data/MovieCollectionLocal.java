package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import utilities.ImageProcessor;

public class MovieCollectionLocal extends MovieCollection {

	private static final long serialVersionUID = 4962585391898625740L;

	@Override
	public int getColumnCount() {
		return 6;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
			case 0: return "Borító";
			case 1: return "Cím";
			case 2: return "Év";
			case 3: return "Színészek";
			case 4: return "Típus";
			default: return "Megtekintve";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex) {
			case 0: return Icon.class;
			case 1: return String.class;
			case 2: return Integer.class;
			case 3: return String.class;
			case 4: return String.class;
			default: return Boolean.class;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 5 ? true : false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Movie movie = getMovie(rowIndex);
		
		switch(columnIndex) {
			case 0: return new ImageIcon(ImageProcessor.resizeImage(movie.getCover(), 68, 100));
			case 1: return movie.getTitle();
			case 2: return movie.getYear();
			case 3: return movie.getActor();
			case 4: return movie.getType();
			default: return movie.getSeen();
			
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch(columnIndex) {
			case 5: getMovie(rowIndex).setSeen((Boolean)aValue);
					if((Boolean)aValue) {
						moveToEnd(getMovie(rowIndex));
					}else {
						moveToFirst(getMovie(rowIndex));
					}
					return;
		default: return;
		}
	}
	
	public int getUnseen() {
		int count = 0;
		for(Movie i : localDb) {
			if(!i.getSeen()) {
				count++;
			}
		}
		return count;
	}
	
	public void saveToJson(File fileToSave) throws JsonParseException, IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		FileWriter writer = new FileWriter(fileToSave);
		gson.toJson(localDb, writer);
		writer.close();
	}
	
	public void readFromJson(File fileToRead) throws JsonParseException, IOException {
		FileReader reader = new FileReader(fileToRead);
		ArrayList<Movie> newMovies = new Gson().fromJson(reader, new TypeToken<ArrayList<Movie>>() {}.getType());
		reader.close();
		
		Runnable background = new Runnable() {
	         public void run() {
	            for(Movie i : newMovies) {
					i.autoSetCover();
					
					addMovieBeginning(i);
	            }
	            sortBySeen();
	         }
	     };

	    new Thread(background).start();
	}
	
	public void updateCovers(){
		
		Runnable background = new Runnable() {
	         public void run() { 
		            for(Movie i : localDb) {
		            	i.autoSetCover();
					}
					fireTableDataChanged();
	        }
	     };

	    new Thread(background).start();
	    
	}
	
	public void serializeDatabase() throws IOException {
		
		File directory = new File("." + File.separator + "userdata");
		File subdirectory = new File("." + File.separator + "userdata" + File.separator + "covers");
		
	    if (! directory.exists()){
	        directory.mkdir();
	        subdirectory.mkdir();
	    }
	    
		FileOutputStream fos = new FileOutputStream("." + File.separator + "userdata" + File.separator + "database.dat");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(localDb);
		fos.close();
		oos.close();
		
		for(Movie i : localDb) {
			if(i.getMedia() != null) {
				ImageProcessor.saveImage(i.getCover(), i.getId());
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public void deserializeDatabase() throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream("." + File.separator + "userdata" + File.separator + "database.dat");
        ObjectInputStream ois = new ObjectInputStream(fis);
        localDb = (CopyOnWriteArrayList<Movie>) ois.readObject();
        ois.close();
        fis.close();
        
        for(Movie i : localDb) {
			i.setCover(ImageProcessor.loadImage(i.getId()));
		}
	}
	
	public void sortBySeen() {
		for (int i = 0 ; i < localDb.size(); i++) {
		    if(localDb.get(i).getSeen()) {
		    	moveToEnd(localDb.get(i));
		    }else {
		    	moveToFirst(localDb.get(i));
		    }
		}
	}
	
	public void removeMovie(Movie m) {
		for(Movie i : localDb) {
				if(m == i) {
					localDb.remove(m);
					
					File currentFile = new File("userdata" + File.separator + "covers" + File.separator + m.getId() + ".png");
					currentFile.delete();
				
					fireTableDataChanged();
					return;
				}
		}
		
		
	}
	
}
