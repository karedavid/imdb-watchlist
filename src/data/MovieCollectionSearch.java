package data;

public class MovieCollectionSearch extends MovieCollection{
	
	private static final long serialVersionUID = -5355453945793895847L;

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
			case 0: return "Cím";
			case 1: return "Év";
			case 2: return "Színészek";
			default: return "Típus";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex) {
			case 0: return String.class;
			case 1: return Integer.class;
			case 2: return String.class;
			default: return String.class;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Movie movie = getMovie(rowIndex);
		
		switch(columnIndex) {
			case 0: return movie.getTitle();
			case 1: return movie.getYear();
			case 2: return movie.getActor();
			default: return movie.getType();
		}
	}

}
